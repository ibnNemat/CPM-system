package uz.devops.intern.telegram.bot.service.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.OrganizationService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Service
public class GroupStates extends State<BotFSM>{

    private final GroupsService groupsService;
    private final TelegramGroupService telegramGroupService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final AdminFeign adminFeign;

    @Autowired
    private GroupOrganizationState groupOrganizationState;

    public GroupStates(BotFSM context) {
        super(context, context.getAdminFeign());
        this.groupsService = context.getGroupsService();
        this.telegramGroupService = context.getTelegramGroupService();
        this.userService = context.getUserService();
        this.organizationService = context.getOrganizationService();
        this.adminFeign = context.getAdminFeign();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasCallbackQuery()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.message"));
            log.warn("Update: {}", update);
            return false;
        }

        CallbackQuery callback = update.getCallbackQuery();
        String callbackText = callback.getMessage().getReplyMarkup().getKeyboard().get(0).get(0).getText();
        if(!callbackText.equals(bundle.getString("bot.admin.send.attach.this.group")))return false;

        GroupsDTO groupsDTO =
            groupsService.findOneByTelegramId(Long.parseLong(callback.getData()));

        if(groupsDTO != null){
            wrongValue(callback.getFrom().getId(), bundle.getString("bot.admin.error.group.is.already.attached"));
            log.warn("Group is already exists, Group: {}", groupsDTO);
            return false;
        }

        Long groupChatId = Long.parseLong(callback.getData());
        TelegramGroupDTO telegramGroup = telegramGroupService.findOneByChatId(groupChatId);
        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!responseDTO.getSuccess()){
            log.warn("{}", responseDTO);
            return false;
        }

        WebUtils.setUserToContextHolder(responseDTO.getResponseData());
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUserLogin();
        if(organizations.isEmpty()){
            wrongValue(callback.getFrom().getId(), bundle.getString("bot.admin.error.organization.is.not.found"));
            log.warn("Organization is not found, Manager id: {}", callback.getFrom().getId());
            return false;
        }

        InlineKeyboardMarkup markup = createOrganizationButtons(organizations, telegramGroup.getChatId());
        EditMessageTextDTO dto = createEditMessageText(callback, markup, "\n" + bundle.getString("bot.admin.send.choose.organization"));
        adminFeign.editMessageText(dto);
        context.changeState(new GroupOrganizationState(context));
        return true;
    }

    private InlineKeyboardMarkup createOrganizationButtons(List<OrganizationDTO> organizations, Long telegramGroupId){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();

        for(OrganizationDTO organization: organizations){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(organization.getName());
            button.setCallbackData(telegramGroupId + ":" + organization.getId());

            keyboards.add(List.of(button));
        }

        markup.setKeyboard(keyboards);
        return markup;
    }
    private EditMessageTextDTO createEditMessageText(CallbackQuery callback, InlineKeyboardMarkup markup, String text){

        EditMessageTextDTO dto = new EditMessageTextDTO();
        dto.setMessageId(callback.getMessage().getMessageId());
        dto.setInlineMessageId(callback.getInlineMessageId());
        dto.setChatId(String.valueOf(callback.getFrom().getId()));
        dto.setReplyMarkup(markup);
        dto.setText(callback.getMessage().getText() + text);
        dto.setParseMode("HTML");

        return dto;
    }

//    public void wrongValue(Long chatId, String message){
//        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
//        Update update = adminFeign.sendMessage(sendMessage);
//        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
//            chatId, message, update);
//    }

}
