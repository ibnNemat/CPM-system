package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class ManagerGroups extends ManagerMenuAbs{

    private final String SUPPORTED_TEXT = "\uD83D\uDC65 Guruh qo'shish";

    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();
    private final NewOrganization newOrganization;

    public ManagerGroups(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService, NewOrganization newOrganization) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
        this.newOrganization = newOrganization;
    }

    @PostConstruct
    public void fillSupportedTextsList(){
        List<String> languages = KeyboardUtil.availableLanguages();
        Map<String, String> languageMap = KeyboardUtil.getLanguages();
        for(String lang: languages){
            String languageCode = languageMap.get(lang);
            ResourceBundle bundle =
                ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);
            SUPPORTED_TEXTS.add(bundle.getString("bot.admin.keyboards.menu.add.group"));
        }
    }

    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        ResponseDTO<User> responseDTO = getUserByCustomerTg(manager);
        if (!responseDTO.getSuccess() || responseDTO.getResponseData() == null) {
            return false;
        }

        newOrganization.basicFunction(manager, bundle);
        customerTelegramService.update(manager);
        return false;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }

    @Override
    public List<String> getSupportedTexts() {
        return SUPPORTED_TEXTS;
    }


    private InlineKeyboardMarkup createGroupButtons(Long chatId, ResourceBundle bundle){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton(bundle.getString("bot.admin.send.attach.this.group"));
        button.setCallbackData(String.valueOf(chatId));

        buttons.add(
            List.of(button)
        );

        return new InlineKeyboardMarkup(buttons);
    }

}
