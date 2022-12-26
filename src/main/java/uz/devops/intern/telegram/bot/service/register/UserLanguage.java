package uz.devops.intern.telegram.bot.service.register;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ResourceBundle;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
public class UserLanguage extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.MESSAGE;
    private final String STATE = "MANAGER_LANGUAGE";
    private final Integer STEP = 1;
    private final Integer NEXT_STEP = 2;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        String messageText = message.getText();

        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!KeyboardUtil.availableLanguages().contains(messageText)){
            wrongValue(userId, bundle.getString("bot.admin.error.message"));
            log.warn("User choose not shown language, User id: {} | Message: {}", userId, message);
            return false;
        }

        String languageCode = KeyboardUtil.getLanguages().get(messageText);
        bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);
        manager.setLanguageCode(languageCode);
        manager.setStep(NEXT_STEP);

        String newMessage = bundle.getString("bot.admin.send.contact");
        ReplyKeyboardMarkup markup = KeyboardUtil.phoneNumber(manager.getLanguageCode());
        SendMessage sendMessage = sendMessage(userId, newMessage, markup);
        Update response = adminFeign.sendMessage(sendMessage);
        log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
            userId, messageText, response);
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
    public String messageOrCallback(){
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.only.message");
    }
}
