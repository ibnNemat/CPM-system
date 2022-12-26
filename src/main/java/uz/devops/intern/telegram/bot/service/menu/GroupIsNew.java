package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.redis.GroupRedisDTO;
import uz.devops.intern.redis.GroupRedisRepository;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.service.commands.MenuCommand;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.Optional;
import java.util.ResourceBundle;

@Service
public class GroupIsNew extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.MESSAGE;
    private final String STATE = "MANAGER_UPDATED_GROUP";
    private final Integer STEP = 11;
    private final Integer NEXT_STEP = 12;

    @Autowired
    private GroupRedisRepository groupRedisRepository;
    @Autowired
    private GroupsService groupsService;
    @Autowired
    private MenuCommand menuCommand;
    @Autowired
    private AdminMenuKeys adminMenuKeys;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasMessage() || !update.getMessage().hasText()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.send.only.text"));
            log.warn("User didn'y send text! Manager id: {} | Update: {}", manager.getTelegramId(), update);
            return false;
        }

        String nameOfGroup = update.getMessage().getText();
        Optional<GroupRedisDTO> groupRedisOptional = groupRedisRepository.findById(manager.getTelegramId());
        if(groupRedisOptional.isEmpty()){
            log.warn("Group is not found from Redis! Manager id: {}", manager.getTelegramId());
            menuCommand.executeCommand(update, manager.getTelegramId());
            return false;
        }

        GroupsDTO group = groupRedisOptional.get().getGroupsDTO();
        group.setName(nameOfGroup);
        groupsService.update(group);

        basicFunction(manager, bundle, group.getName());
        return true;
    }

    public boolean basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle, String groupName){
        String newMessage = bundle.getString("bot.admin.send.group.name.is.changed.successfully") + groupName;
        ReplyKeyboardMarkup menuMarkup = adminMenuKeys.createMenu(manager.getLanguageCode());

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, menuMarkup);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(7);
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

    @Override
    public String messageOrCallback() {
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.only.message");
    }
}
