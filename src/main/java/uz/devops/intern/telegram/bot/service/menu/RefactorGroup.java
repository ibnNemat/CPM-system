package uz.devops.intern.telegram.bot.service.menu;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
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
import uz.devops.intern.service.dto.UserDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
public class RefactorGroup extends BotStrategyAbs {
    private final String STATE = "MANAGER_REFACTORING_OWN_GROUPS";
    private final Integer STEP = 9;
    private final Integer PREV_STEP = 7;
    private final Integer NEXT_STEP = 10;

    @Autowired
    private AdminMenuKeys adminMenuKeys;
    @Autowired
    private GroupsService groupsService;

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasCallbackQuery()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.choose.up"));
            log.warn("User didn't press inline keyboard! Manager id: {} | Update: {}", manager.getTelegramId(), update);
            return false;
        }
        String callbackData = update.getCallbackQuery().getData();
        if(callbackData.equals("BACK")){
            toBack(update.getCallbackQuery(), manager.getLanguageCode());
            manager.setStep(PREV_STEP);
            return true;
        }

        Long groupId = Long.parseLong(callbackData);
        Optional<GroupsDTO> groupOptional = groupsService.findOne(groupId);
        if(groupOptional.isEmpty()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }

        GroupsDTO group = groupOptional.get();
        String newMessage =
            bundle.getString("bot.admin.group.name") + " " + group.getName() + "\n" +
            bundle.getString("bot.admin.customers.count") + " " + group.getCustomers().size();

        int i = 1;
        for(CustomersDTO customer: group.getCustomers()){
//            if(){}
            newMessage += String.format("\n %d. %s", i++, customer.getUsername());
        }

        boolean result = basicFunction(manager, update.getCallbackQuery(), newMessage);
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }
        manager.setStep(NEXT_STEP);
        return true;
    }

    private InlineKeyboardMarkup createGroupRefactoringButtons(String callback, ResourceBundle bundle){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> header = List.of(
            InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.group.change.name")).callbackData(callback).build()
        );
        List<InlineKeyboardButton> footer = List.of(
            InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.back")).callbackData("BACK").build()
        );

        markup.setKeyboard(List.of(
            header, footer
        ));
        System.out.println(markup);
        return markup;
    }

    public boolean basicFunction(CustomerTelegramDTO manager, CallbackQuery callback, String newText){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        InlineKeyboardMarkup refactoringButtonsMarkup = createGroupRefactoringButtons(callback.getData(), bundle);
        EditMessageTextDTO editMessageTextDTO = new EditMessageTextDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            callback.getInlineMessageId(),
            refactoringButtonsMarkup,
            newText,
            "HTML",
            null
        );

        try {
            adminFeign.editMessageText(editMessageTextDTO);
            return true;
        }catch (FeignException.FeignClientException e){
            log.error("Error while editing message text! User id: {} | Exception: {}", callback.getFrom().getId(), e.getMessage());
            return false;
        }
    }

    public boolean toBack(CallbackQuery callback, String managerLanguageCode){
        boolean result = editMessageAndRemoveButtons(callback);
        if(!result){
            return false;
        }
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(managerLanguageCode);
        String newMessage = bundle.getString("bot.admin.main.menu");
        ReplyKeyboardMarkup menuMarkup = adminMenuKeys.createMenu(managerLanguageCode);
        SendMessage sendMessage = TelegramsUtil.sendMessage(callback.getFrom().getId(), newMessage, menuMarkup);
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    private boolean editMessageAndRemoveButtons(CallbackQuery callback){
        EditMessageDTO editMessageDTO = new EditMessageDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            callback.getInlineMessageId(),
            new InlineKeyboardMarkup()
        );
        try {
            adminFeign.editMessageReplyMarkup(editMessageDTO);
            log.info("Message is edited successfully, Manager id: {}", callback.getFrom().getId());
            return true;
        }catch (FeignException e){
            log.error("Error while editing message. User wanted to go back when see all groups! Manager id: {}", callback.getFrom().getId());
            return false;
        }
    }

}
