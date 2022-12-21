package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
@Service
public class AllGroups extends ManagerMenuAbs{
    private final String SUPPORTED_TEXT = "\uD83D\uDC40 Guruhlarni ko'rish";

    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();

    private final Integer NEXT_STEP = 9;

    @Autowired
    private GroupsService groupsService;

    public AllGroups(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
    }

    @PostConstruct
    public void fillSupportedTextsList(){
        List<String> languages = KeyboardUtil.availableLanguages();
        Map<String, String> languageMap = KeyboardUtil.getLanguages();
        for(String lang: languages){
            String languageCode = languageMap.get(lang);
            ResourceBundle bundle =
                ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);
            SUPPORTED_TEXTS.add(bundle.getString("bot.admin.keyboards.menu.show.groups"));
        }
    }


    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!responseDTO.getSuccess()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.user.is.not.found"));
            log.warn("{} | Manager id: {} | Response: {}", responseDTO.getMessage(), manager.getTelegramId(), responseDTO);
            return false;
        }

        setUserToContextHolder(responseDTO.getResponseData());

        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.groups.are.not.attached.to.groups"));
            log.warn("Manager has not any groups! Manager: {}", manager);
            return false;
        }
        removeMenuButtons(manager, bundle, update.getMessage().getText());
        basicFunction(manager, bundle, groups);
        return true;
    }

    private boolean removeMenuButtons(CustomerTelegramDTO manager, ResourceBundle bundle, String messageText){
        String newMessage = bundle.getString("bot.admin.send.all.groups.text");
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, removeMarkup);
        adminFeign.sendMessage(sendMessage);
        log.info("User wants to see all groups. Menu buttons are removed! Manager id: {} | Message: {}",
            manager.getTelegramId(), messageText);
        return true;
    }

    public void basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle, List<GroupsDTO> groups){
        String newMessage = bundle.getString("bot.admin.send.choose.one.from.your.groups");
        InlineKeyboardMarkup markup = createGroupsButton(groups, bundle);
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        log.info("List of groups is send as message, Manager id: {}", manager.getTelegramId());
        manager.setStep(NEXT_STEP);
        customerTelegramService.update(manager);
    }

    private InlineKeyboardMarkup createGroupsButton(List<GroupsDTO> groups, ResourceBundle bundle){
        String buttonForBack = bundle.getString("bot.admin.keyboard.for.back");
        String callbackData = "BACK";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for(GroupsDTO group: groups){
            rows.add(
                List.of(
                    InlineKeyboardButton.builder().text(group.getName()).callbackData(String.valueOf(group.getId())).build()
                )
            );
        }
        rows.add(
            List.of(InlineKeyboardButton.builder().text(buttonForBack).callbackData(callbackData).build())
        );
        return new InlineKeyboardMarkup(rows);
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }

    @Override
    public List<String> getSupportedTexts(){ return SUPPORTED_TEXTS; }
}
