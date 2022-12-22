package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.service.state.ServiceFSM;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class NewService extends ManagerMenuAbs{

    private final String SUPPORTED_TEXT = "\uD83E\uDEC2 Xizmat qo'shish";

    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();
    private final RegisterNewService registerNewService;

    public NewService(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService, RegisterNewService registerNewService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
        this.registerNewService = registerNewService;
    }

    @PostConstruct
    public void fillSupportedTextsList(){
        List<String> languages = KeyboardUtil.availableLanguages();
        Map<String, String> languageMap = KeyboardUtil.getLanguages();
        for(String lang: languages){
            String languageCode = languageMap.get(lang);
            ResourceBundle bundle =
                ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);
            SUPPORTED_TEXTS.add(bundle.getString("bot.admin.keyboards.menu.add.services"));
        }
    }

    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        update.getMessage();

//        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);
//
//        String newMessage = bundle.getString("bot.admin.send.service.name");
//        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, removeMarkup);
//        adminFeign.sendMessage(sendMessage);
//        manager.setStep(8);
//        registerNewService.rollbackStateToStart();
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
