package uz.devops.intern.telegram.bot.service.payments;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.User;
import uz.devops.intern.repository.CustomersRepository;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.service.menu.CustomerPayments;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerPaymentsHistory extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.CALLBACK_QUERY;
    private final CustomersRepository customersRepository;
    private final String STATE = "CURRENT_CUSTOMER_PAYMENTS";
    private final Integer STEP = 14;
    private final Integer NEXT_STEP = 15;
    private final CustomerPayments customerPayments;
    private final GroupsService groupsService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final ServicesService servicesService;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasCallbackQuery()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.choose.up"));
            log.warn("User didn't pressed inline buttons! Manager id: {} | Update: {}", manager.getTelegramId(), update);
            return false;
        }

        String callbackData = update.getCallbackQuery().getData();
        if(callbackData.equals("BACK")){
            ResponseDTO<User> userResponse = getUserByCustomerTg(manager);
            if(!userResponse.getSuccess()){
                throwManagerToMenu(manager, bundle);
            }

            WebUtils.setUserToContextHolder(userResponse.getResponseData());
            List<GroupsDTO> managerGroups = groupsService.findOnlyManagerGroups();
            if(managerGroups.isEmpty()){
                wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.groups.are.not.found"));
                log.warn("Payments list is empty, Manager id: {} ", manager.getTelegramId());
                return false;
            }

            String newMessage = bundle.getString("bot.admin.send.group.payments");
            InlineKeyboardMarkup groupsMarkup = createGroupsInlineMarkup(managerGroups, bundle);
            EditMessageTextDTO editMessageTextDTO = createEditMessage(update.getCallbackQuery(), groupsMarkup, newMessage);
            adminFeign.editMessageText(editMessageTextDTO);
            log.info("User go back! Manager id: {} | Callback data: {}", manager.getTelegramId(), callbackData);
            manager.setStep(customerPayments.getNextStep());
            return true;
        }
        String customerLogin = callbackData;
        Customers customers = customersRepository.findByUsername(customerLogin).get();

        ResponseDTO<PaymentDTO> response = paymentService.getByCustomerId(customers.getId());

        if(!response.getSuccess() || response.getResponseData() == null){
            String newMessage = String.format(bundle.getString("bot.admin.error.groups.are.not.attached.to.service"), bundle.getString("bot.admin.keyboard.for.back"));
            EditMessageTextDTO editMessageTextDTO = createEditMessage(update.getCallbackQuery(), update.getCallbackQuery().getMessage().getReplyMarkup(), newMessage);
            adminFeign.editMessageText(editMessageTextDTO);
            log.warn("Customer's payment is not found! Manager id: {} | Customer login: {}", manager.getTelegramId(), customerLogin);
            return false;
        }

        PaymentDTO payment = response.getResponseData();
        String newMessage = createText(manager, payment, customers);
        if(Objects.isNull(newMessage)){
            return false;
        }
        InlineKeyboardMarkup markup = createInlineButtons(customerLogin, bundle);
        EditMessageTextDTO editMessageTextDTO = createEditMessage(update.getCallbackQuery(), markup, newMessage);
        try {
            adminFeign.editMessageText(editMessageTextDTO);
            manager.setStep(NEXT_STEP);
            return true;
        }catch (FeignException e){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            log.warn("Error while editing message text and buttons! Manager id: {} | Exception: {}", manager.getTelegramId(),e.getMessage());
            return false;
        }
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public String messageOrCallback() {
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.choose.up");
    }

    public Integer getNextStep(){
        return NEXT_STEP;
    }

    private InlineKeyboardMarkup createGroupsInlineMarkup(List<GroupsDTO> groups, ResourceBundle bundle){
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for(GroupsDTO group: groups){
            keyboardButtons.add(
                List.of(
                    InlineKeyboardButton.builder().text(group.getName()).callbackData(String.valueOf(group.getId())).build()
                )
            );
        }

        keyboardButtons.add(
            List.of(
                InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.back")).callbackData("BACK").build()
            )
        );

        return InlineKeyboardMarkup.builder().keyboard(keyboardButtons).build();
    }

    private InlineKeyboardMarkup createInlineButtons(String login, ResourceBundle bundle){
        List<List<InlineKeyboardButton>> keyboards = List.of(
//            List.of(
//                InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.back")).callbackData(login).build()
//            ),
            List.of(
                InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.menu")).callbackData("MENU").build()
            )
        );

        return InlineKeyboardMarkup.builder().keyboard(keyboards).build();
    }

    private String createText(CustomerTelegramDTO manager, PaymentDTO payment, Customers customer){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(manager.getLanguageCode());
        User user = customer.getUser();
        ServicesDTO service = payment.getService();

        return String.format(bundle.getString("bot.admin.send.text.customer.payment.history"),
            user.getFirstName() + " " + user.getLastName(),
            user.getCreatedBy(),
            service.getName(),
            payment.getStartedPeriod(),
            payment.getPaidMoney(),
            service.getPrice(),
            service.getPrice() - payment.getPaidMoney());
    }
}
