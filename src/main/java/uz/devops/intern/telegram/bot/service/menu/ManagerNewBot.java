package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

//@Service
@Deprecated
public class ManagerNewBot extends ManagerMenuAbs{

    private final String SUPPORTED_TEXT = "\uD83E\uDD16 Yangi bot qo'shish";
    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();

    public ManagerNewBot(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
    }

    @PostConstruct
    public void inject(){
        List<String> allLanguages = KeyboardUtil.availableLanguages();
        Map<String, String> languageMap = KeyboardUtil.getLanguages();
        for (String language: allLanguages){
            String languageCode = languageMap.get(language);
            ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);
            SUPPORTED_TEXTS.add(
                bundle.getString("bot.admin.keyboards.menu.new.bot")
            );
        }
    }

    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {

        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        String newMessage = bundle.getString("bot.admin.send.new.bot.token");
        ReplyKeyboardRemove markup = new ReplyKeyboardRemove(true);
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        Update responseFromTelegram = adminFeign.sendMessage(sendMessage);
        log.info("User's adding new bot, User id: {} | Update: {} ", manager.getTelegramId(), update);
        manager.setStep(3);
        customerTelegramService.update(manager);
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
