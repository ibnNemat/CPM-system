package uz.devops.intern.telegram.bot.service.payments;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import tech.jhipster.web.util.PaginationUtil;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.service.menu.CustomerPayments;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerPaymentsHistory extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.CALLBACK_QUERY;
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

        PageRequest pagination = PageRequest.of(0, 10);
        ResponseDTO<List<PaymentDTO>> response = paymentService.getByUserLogin(customerLogin, pagination);
        if(!response.getSuccess() || response.getResponseData().isEmpty()){
            String newMessage = String.format(bundle.getString("bot.admin.error.groups.are.not.attached.to.service"), bundle.getString("bot.admin.keyboard.for.back"));
            EditMessageTextDTO editMessageTextDTO = createEditMessage(update.getCallbackQuery(), update.getCallbackQuery().getMessage().getReplyMarkup(), newMessage);
            adminFeign.editMessageText(editMessageTextDTO);
            log.warn("Customer's payment is not found! Manager id: {} | Customer login: {}", manager.getTelegramId(), customerLogin);
            return false;
        }

        List<PaymentDTO> payment = response.getResponseData();
        boolean result = sendPaymentsListAsMessage(manager, payment, customerLogin);
        if(!result){
            return false;
        }
//        InlineKeyboardMarkup markup = createInlineButtons(customerLogin, bundle);
        EditMessageTextDTO editMessageTextDTO = createEditMessage(update.getCallbackQuery(), new InlineKeyboardMarkup(), update.getCallbackQuery().getMessage().getText());
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

    private boolean sendPaymentsListAsMessage(CustomerTelegramDTO manager, List<PaymentDTO> payments, String customerLogin){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(manager.getLanguageCode());
        ResponseDTO<User> response =
            userService.getUserByLogin(customerLogin);
        if(!response.getSuccess() || Objects.isNull(response.getResponseData())){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.user.is.not.found"));
            log.warn("Manager customer is not found! Manager id: {} | Customer login: {} ",
                manager.getTelegramId(), customerLogin);
            return false;
        }

        User user = response.getResponseData();

        PaymentDTO payment = payments.get(0);
        InlineKeyboardMarkup markup = createPaymentsHistoryPagination(bundle, payments, user.getLogin());
        ServicesDTO service = payment.getService();

        String messageText = String.format(bundle.getString("bot.admin.send.text.customer.payment.history"),
            user.getFirstName() + " " + user.getLastName(),
            user.getCreatedBy(),
            service.getName(),
            payment.getStartedPeriod(),
            payment.getPaidMoney(),
            service.getPrice(),
            service.getPrice() - payment.getPaidMoney());
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), messageText, markup);
        adminFeign.sendMessage(sendMessage);
        log.info("Customer's payments list send as message, Manager id: {} | Customer login: {}",
            manager.getTelegramId(), customerLogin);
        return true;
    }


    private ReplyKeyboardMarkup createMenuReplyButton(ResourceBundle bundle){
        KeyboardButton button = KeyboardButton.builder()
            .text(bundle.getString("bot.admin.keyboard.for.menu"))
            .build();
        KeyboardRow row = new KeyboardRow();
        row.add(button);

        return ReplyKeyboardMarkup.builder()
            .keyboard(
                List.of(
                    row
                ))
            .build();
    }

    private InlineKeyboardMarkup createPaymentsHistoryPagination(ResourceBundle bundle, List<PaymentDTO> payments, String customerLogin){

        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int i = 1;

        for(PaymentDTO payment: payments){
            row.add(
                InlineKeyboardButton.builder()
                    .text(String.valueOf(i))
                    .callbackData(payment.getId() + ":" + i++ + ":" + customerLogin)
                    .build()
            );

            if(row.size() == 5){
                inlineKeyboardButtons.add(row);
                row = new ArrayList<>();
            }
        }

        if(!row.isEmpty()){
            inlineKeyboardButtons.add(row);
        }

        List<InlineKeyboardButton> footer = List.of(
            InlineKeyboardButton.builder().text("⬅️").callbackData(0 + ":LEFT" + ":" + customerLogin).build(),
            InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.menu.only.text")).callbackData("HOME").build(),
            InlineKeyboardButton.builder().text("➡️").callbackData(1 + ":RIGHT" + ":" + customerLogin).build()
        );

        inlineKeyboardButtons.add(footer);

        return InlineKeyboardMarkup.builder().keyboard(inlineKeyboardButtons).build();
    }

}
