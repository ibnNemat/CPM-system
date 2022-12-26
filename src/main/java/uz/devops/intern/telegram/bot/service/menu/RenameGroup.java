package uz.devops.intern.telegram.bot.service.menu;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.domain.User;
import uz.devops.intern.redis.GroupRedisDTO;
import uz.devops.intern.redis.GroupRedisRepository;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.OrganizationService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.*;

@Service
public class RenameGroup extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.CALLBACK_QUERY;
    private final String STATE = "MANAGER_RENAME_GROUP";
    private final Integer STEP = 10;
    private final Integer PREV_STEP = 9;
    private final Integer NEXT_STEP = 11;
    @Autowired
    private RefactorGroup refactorGroup;
    @Autowired
    private GroupRedisRepository groupRedisRepository;
    @Autowired
    private GroupsService groupsService;
    @Autowired
    private OrganizationService organizationService;
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
            update.getCallbackQuery().setData(update.getCallbackQuery().getData());
            refactorGroup.execute(update, manager);
            return true;
        }
        boolean result = removeInlineButtons(update.getCallbackQuery());
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }

        result = saveGroupToRedis(manager.getTelegramId(), callbackData);
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }

        if(callbackData.contains("GROUP_ORGANIZATION")){
            changeGroupOrganization(update.getCallbackQuery(), manager);
        }else if(callbackData.contains("GROUP_NAME")){
            renameGroup(manager, bundle);
        }
//        basicFunction(manager, bundle);
        return true;
    }

    private boolean saveGroupToRedis(Long managerId, String callback){
        Long groupId = Long.parseLong(callback.split(":")[0]);
        Optional<GroupsDTO> groupsOptional = groupsService.findOne(groupId);
        if(groupsOptional.isEmpty()){
            log.warn("Group is not found! Manager id: {} | Callback data: {}", managerId, callback);
            return false;
        }

        GroupRedisDTO groupRedisDTO = GroupRedisDTO.builder()
            .id(managerId).groupsDTO(groupsOptional.get()).build();
        groupRedisRepository.save(groupRedisDTO);
        return true;
    }

//    private boolean removeInlineButtons(CallbackQuery callback){
//        EditMessageDTO editMessageDTO = new EditMessageDTO(
//            String.valueOf(callback.getFrom().getId()),
//            callback.getMessage().getMessageId(),
//            callback.getInlineMessageId(),
//            new InlineKeyboardMarkup()
//        );
//        try {
//            adminFeign.editMessageReplyMarkup(editMessageDTO);
//            return true;
//        }catch (FeignException.FeignClientException e){
//            log.warn("Error while editing message when manager renamed group! Manager id: {} ", callback.getFrom().getId());
//            return false;
//        }
//    }

    public void basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.send.group.new.name");
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        log.info("User wants rename group! Manager id: {}", manager.getTelegramId());
        manager.setStep(NEXT_STEP);

    }

    private InlineKeyboardMarkup createOrganizationInlineButtons(List<OrganizationDTO> organizations, Long groupId){
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        for(OrganizationDTO organization: organizations){
            inlineKeyboardButtons.add(
                InlineKeyboardButton.builder()
                    .text(organization.getName())
                    .callbackData(organization.getId() + ":" + groupId)
                    .build()
            );
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(inlineKeyboardButtons));
        return markup;
    }

    private void renameGroup(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.send.group.new.name");
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        log.info("User wants rename group! Manager id: {}", manager.getTelegramId());
        manager.setStep(NEXT_STEP);
    }

    private void changeGroupOrganization(CallbackQuery callback, CustomerTelegramDTO manager){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(manager.getLanguageCode());
        String[] data = callback.getData().split(":");
        Long groupId = null;
        try {
            groupId = Long.parseLong(data[0]);
        }catch (NumberFormatException e){
            removeInlineButtons(callback);
            throwManagerToMenu(manager, bundle);
            return;
        }

        ResponseDTO<User> userResponse = getUserByCustomerTg(manager);
        if(Objects.isNull(userResponse.getResponseData())){
            return;
        }

        WebUtils.setUserToContextHolder(userResponse.getResponseData());
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUserLogin();
        if(organizations.isEmpty()){
            removeInlineButtons(callback);
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.organization.is.not.found"));
            return;
        }

        InlineKeyboardMarkup organizationsMarkup = createOrganizationInlineButtons(organizations, groupId);
        String newMessage = bundle.getString("bot.admin.send.choose.one.organization");
        EditMessageTextDTO dto = createEditMessage(callback, organizationsMarkup, callback.getMessage().getText() + "\n" + newMessage);
        try {
            adminFeign.editMessageText(dto);
        }catch (FeignException e){
            log.warn("Error while editing message text!");
            removeInlineButtons(callback);
            throwManagerToMenu(manager, bundle);
        }
        manager.setStep(16);

    }
}
