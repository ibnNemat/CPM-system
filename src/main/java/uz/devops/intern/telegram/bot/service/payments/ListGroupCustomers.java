package uz.devops.intern.telegram.bot.service.payments;

import feign.FeignException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.Cache;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.keyboards.menu.AdminMenu;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.service.commands.MenuCommand;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
@Getter
public class ListGroupCustomers extends BotStrategyAbs {
    private final String STATE = "MANAGER_LIST_OF_CUSTOMER_IN_GROUP";
    private final Integer STEP = 13;
    private final Integer NEXT_STEP = 14;

    private final AdminMenuKeys adminMenuKeys;
    private final GroupsService groupsService;

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
            removeInlineButtons(update.getCallbackQuery(), bundle);
            throwManagerToMenu(manager, bundle);
            return true;
        }
        Long groupId = null;
        try {
            groupId = Long.parseLong(callbackData);
        }catch (NumberFormatException e){
            log.warn("Thrown error while parsing callback data to \"Long\"! Manager id: {} | Callback data: {} | Exception: {}",
                manager.getTelegramId(), callbackData, e.getMessage());
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            throwManagerToMenu(manager, bundle);
            return true;
        }

        return basicFunction(update.getCallbackQuery(), manager, bundle);
    }

    public boolean basicFunction(CallbackQuery callback, CustomerTelegramDTO manager, ResourceBundle bundle){
        Long groupId = Long.parseLong(callback.getData());
        Optional<GroupsDTO> groupOptional = groupsService.findOne(groupId);
        if(groupOptional.isEmpty()){
            log.warn("Group is not found! Manager id: {} | Group id: {}",
                manager.getTelegramId(), groupId);
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            throwManagerToMenu(manager, bundle);
            return false;
        }

        String newMessage = bundle.getString("bot.admin.send.customers.payments");
        InlineKeyboardMarkup customersList = createCustomersInlineButtonsList(groupOptional.get(), bundle);
        EditMessageTextDTO editMessageTextDTO = createEditMessage(callback, customersList, newMessage);
        try {
            adminFeign.editMessageText(editMessageTextDTO);
        }catch (FeignException e){
            log.warn("Error while editing message! Manager id: {} | EditMessageTextDTO: {} | Exception: {}",
                manager.getTelegramId(), editMessageTextDTO, e.getMessage());
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            throwManagerToMenu(manager, bundle);
            return false;
        }
        manager.setStep(NEXT_STEP);
        return true;
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    private void removeInlineButtons(CallbackQuery callback, ResourceBundle bundle){
        EditMessageDTO editMessageDTO = new EditMessageDTO();
        editMessageDTO.setChatId(String.valueOf(callback.getFrom().getId()));
        editMessageDTO.setMessageId(callback.getMessage().getMessageId());
        editMessageDTO.setInlineMessageId(callback.getInlineMessageId());
        editMessageDTO.setReplyMarkup(new InlineKeyboardMarkup());

        try {
            adminFeign.editMessageReplyMarkup(editMessageDTO);
            log.info("Inline buttons are removed, Manager id: {}", callback.getFrom().getId());
        }catch (FeignException e){
            log.error("Error while remove inline buttons! Manager id: {} | Exception: {}", callback.getFrom().getId(), e.getMessage());
            wrongValue(callback.getFrom().getId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
        }

    }

    private InlineKeyboardMarkup createCustomersInlineButtonsList(GroupsDTO group, ResourceBundle bundle){

        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for(CustomersDTO customer: group.getCustomers()){
            keyboardButtons.add(
                List.of(
                    InlineKeyboardButton.builder().text(customer.getUsername()).callbackData(String.valueOf(customer.getId())).build()
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


}