package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

//@Service
@Deprecated
public class Organization extends ManagerMenuAbs{
    private final String SUPPORTED_TEXT = "\uD83C\uDFE2 Yangi tashkilot";

    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();

    public Organization(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
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
            SUPPORTED_TEXTS.add(bundle.getString("bot.admin.keyboards.menu.new.organization"));
        }
    }


    public boolean todo(Update update, CustomerTelegramDTO manager){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        String newMessage = bundle.getString("bot.admin.send.organization.name.second");
        SendMessage sendMessage = sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        log.info("Admin is creating new organization, Manager id: {} | Message text: {} | Update: {}",
            manager.getTelegramId(), update.getMessage().getText(), update);
        manager.setStep(5);
        return true;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }

    @Override
    public List<String> getSupportedTexts() {
        return SUPPORTED_TEXTS;
    }
}
