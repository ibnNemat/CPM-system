package uz.devops.intern.telegram.bot.service.menu;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.redis.GroupRedisDTO;
import uz.devops.intern.redis.GroupRedisRepository;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
public class RenameGroup extends BotStrategyAbs {
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
//            toBack(update.getCallbackQuery(), manager.getLanguageCode());
//            manager.setStep(PREV_STEP);
//            InlineKeyboardButton button =
//                update.getCallbackQuery().getMessage().getReplyMarkup().getKeyboard().get(0).get(0);
//            System.out.println(button.getCallbackData());
            update.getCallbackQuery().setData(update.getCallbackQuery().getData());
            refactorGroup.execute(update, manager);
            return true;
        }

        boolean result = removeInlineButtons(update.getCallbackQuery());
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }
        basicFunction(manager, bundle);
        result = saveGroupToRedis(manager.getTelegramId(), callbackData);
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }
        return true;
    }

    private boolean saveGroupToRedis(Long managerId, String callback){
        Long groupId = Long.parseLong(callback);
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

    private boolean removeInlineButtons(CallbackQuery callback){
        EditMessageDTO editMessageDTO = new EditMessageDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            callback.getInlineMessageId(),
            new InlineKeyboardMarkup()
        );
        try {
            adminFeign.editMessageReplyMarkup(editMessageDTO);
            return true;
        }catch (FeignException.FeignClientException e){
            log.warn("Error while editing message when manager renamed group! Manager id: {} ", callback.getFrom().getId());
            return false;
        }
    }

    public void basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.send.group.new.name");
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        log.info("User wants rename group! Manager id: {}", manager.getTelegramId());
        manager.setStep(NEXT_STEP);

    }
}
