package uz.devops.intern.telegram.bot.service.menu;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.OrganizationService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;

import java.util.Optional;
import java.util.ResourceBundle;

@Service
public class ChangeGroupOrganization extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.CALLBACK_QUERY;
    private final String STATE = "";
    private final Integer STEP = 16;

    @Autowired
    private GroupsService groupsService;
    @Autowired
    private OrganizationService organizationService;


    @Override
    public String messageOrCallback() {
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.choose.up");
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
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        // 0 - index: Organization id
        // 1 - index: Group id
        String[] callbackData = update.getCallbackQuery().getData().split(":");
        if(callbackData.length != 2){
            removeInlineButtons(update.getCallbackQuery());
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }

        Long organizationId = null, groupId = null;
        try{
            organizationId = Long.parseLong(callbackData[0]);
            groupId = Long.parseLong(callbackData[1]);
        }catch (NumberFormatException e){
            removeInlineButtons(update.getCallbackQuery());
            throwManagerToMenu(manager, bundle);
            return false;
        }

        Optional<GroupsDTO> groupOptional = groupsService.findOne(groupId);
        if(groupOptional.isEmpty()){
            removeInlineButtons(update.getCallbackQuery());
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.groups.are.not.found"));
            return false;
        }

        Optional<OrganizationDTO> organizationOptional = organizationService.findOne(organizationId);
        if(organizationOptional.isEmpty()){
            removeInlineButtons(update.getCallbackQuery());
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.organization.is.not.found"));
            return false;
        }

        GroupsDTO group = groupOptional.get();
        OrganizationDTO organization = organizationOptional.get();

        group.setOrganization(organization);
        groupsService.update(group);

        String newMessage =String.format("<b>%s</b> %s\n<b>%s</b> %s",
            bundle.getString("bot.admin.group.name"), group.getName(),
            bundle.getString("bot.admin.organization.name"), organization.getName());

        EditMessageTextDTO dto = createEditMessage(update.getCallbackQuery(), new InlineKeyboardMarkup(), newMessage);
        try{
            adminFeign.editMessageText(dto);
            log.info("Group's organization is changed, Manager id: {} | Group id: {} | Organization id: {}",
                manager.getTelegramId(), group.getId(), organization.getId());
            throwManagerToMenu(manager, bundle);

            return true;
        }catch (FeignException e){
            removeInlineButtons(update.getCallbackQuery());
            throwManagerToMenu(manager, bundle);
            return false;
        }
    }

}
