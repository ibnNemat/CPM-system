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
import uz.devops.intern.domain.User;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
public class RefactorGroup extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.CALLBACK_QUERY;
    private final String STATE = "MANAGER_REFACTORING_OWN_GROUPS";
    private final Integer STEP = 9;
    private final Integer PREV_STEP = 7;
    private final Integer NEXT_STEP = 10;

    @Autowired
    private AdminMenuKeys adminMenuKeys;
    @Autowired
    private GroupsService groupsService;
    @Autowired
    private UserService userService;

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

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());

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
        StringBuilder newMessage =
            new StringBuilder(bundle.getString("bot.admin.group.name") + " " + group.getName() + "\n" +
                bundle.getString("bot.admin.customers.count") + " " + group.getCustomers().size());

        int i = 1;
        ResponseDTO<List<User>> response = userService.getAllUsersByGroupId(group.getId());
        if(Objects.isNull(response.getResponseData())){
            log.warn("List of users is null! Response: {} ", response);
            return false;
        }

        for(User user: response.getResponseData()){
            newMessage.append(String.format("\n %d. %s %s", i++, user.getFirstName(), user.getLastName()));
        }

        boolean result = basicFunction(manager, update.getCallbackQuery(), newMessage.toString());
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
            InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.group.change.name")).callbackData(callback + ": GROUP_NAME").build()
        );
        List<InlineKeyboardButton> body = List.of(
            InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.group.change.organization")).callbackData(callback + ": GROUP_ORGANIZATION").build()
        );
        List<InlineKeyboardButton> footer = List.of(
            InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.back")).callbackData("BACK").build()
        );

        markup.setKeyboard(List.of(
            header, body, footer
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
