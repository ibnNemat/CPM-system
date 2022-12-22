package uz.devops.intern.telegram.bot.service.payments;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class ReturnFromCustomerPaymentsHistory extends BotStrategyAbs {

    private final String STATE = "MANAGER_RETURN_TO_BACK_OR_MENU";
    private final Integer STEP = 15;
    private final Integer NEXT_STEP = 7;

    private final GroupsService groupsService;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasCallbackQuery()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.choose.up"));
            log.warn("User didn't pressed inline buttons! Manager id: {} | Update: {}", manager.getTelegramId(), update);
            return false;
        }

        String callback = update.getCallbackQuery().getData();
        if(callback.equals("MENU")){
            // Return to main menu.
            removeInlineButtons(update.getCallbackQuery(), bundle);
            throwManagerToMenu(manager, bundle);
            manager.setStep(NEXT_STEP);
            return true;
        }
        Long customerId = null;
        try{
            customerId = Long.parseLong(callback);
        }catch (NumberFormatException e){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.choose.up"));
            log.warn("User didn't pressed shown inline buttons! Manager id: {} | Callback data: {}", manager.getTelegramId(), callback);
            return false;
        }
        ResponseDTO<GroupsDTO> response = groupsService.findByCustomerId(customerId);
        if(!response.getSuccess() || response.getResponseData() == null){
            log.warn("Group is not found! Manager id: {} | Customer id: {}",
                manager.getTelegramId(), customerId);
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            throwManagerToMenu(manager, bundle);
            return false;
        }

        String newMessage = bundle.getString("bot.admin.send.customers.payments");
        InlineKeyboardMarkup customersList = createCustomersInlineButtonsList(response.getResponseData(), bundle);
        EditMessageTextDTO editMessageTextDTO = createEditMessage(update.getCallbackQuery(), customersList, newMessage);
        try {
            adminFeign.editMessageText(editMessageTextDTO);
        }catch (FeignException e){
            log.warn("Error while editing message! Manager id: {} | EditMessageTextDTO: {} | Exception: {}",
                manager.getTelegramId(), editMessageTextDTO, e.getMessage());
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            throwManagerToMenu(manager, bundle);
            return false;
        }
        manager.setStep(13);
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

}
