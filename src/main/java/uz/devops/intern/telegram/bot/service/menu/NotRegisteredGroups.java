package uz.devops.intern.telegram.bot.service.menu;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.List;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class NotRegisteredGroups extends BotStrategyAbs {

    private final String STATE = "TELEGRAM_GROUP_TO_GROUP";
    private final Integer STEP = 6;
    private final Integer NEXT_STEP = 7;

    private final GroupsService groupsService;
    private final UserService userService;
    private final AdminMenuKeys adminMenuKeys;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
//        log.info("BotFSM is working! Manager: {}", manager);
//        return botFSM.execute(update, manager);
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasCallbackQuery()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.choose.up"));
            log.warn("User didn't press inline buttons! Manager id: {} | Update: {}", manager.getTelegramId(), update);
            return false;
        }

        Long organizationId = Long.parseLong(update.getCallbackQuery().getData());
        System.out.println("Callback data: " + organizationId);

        ResponseDTO<User> response = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!response.getSuccess()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            log.warn("User is not found! Manager phone number: {} ", manager.getPhoneNumber());
            return false;
        }
        WebUtils.setUserToContextHolder(response.getResponseData());
        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            log.warn("User is not found! Manager phone number: {} ", manager.getPhoneNumber());
            return false;
        }

        for(GroupsDTO group: groups){
            if(group.getOrganization() == null){
                OrganizationDTO organizationDTO = new OrganizationDTO();
                organizationDTO.setId(organizationId);
                group.setOrganization(organizationDTO);
                groupsService.update(group);
            }
        }
        boolean result = removeInlineButtons(update.getCallbackQuery());
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }

        basicFunction(manager, bundle);
        return true;
    }

    private boolean removeInlineButtons(CallbackQuery callback){
        EditMessageDTO editMessageDTO = new EditMessageDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            String.valueOf(callback.getInlineMessageId()),
            new InlineKeyboardMarkup()
        );

        try {
            adminFeign.editMessageReplyMarkup(editMessageDTO);
            return true;
        } catch (FeignException e){
            log.error("Error while editing message! User id: {} | Exception: {}", callback.getFrom().getId(), e.getMessage());
            return false;
        }
    }

    public void basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.send.groups.are.add.successfully");
        ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(manager.getLanguageCode());
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(NEXT_STEP);
        log.info("User in main menu, User id: {}", manager.getTelegramId());
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }
}
