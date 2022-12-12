package uz.devops.intern.telegram.bot.service.register;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.service.CommandHalfImpl;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
public class UserLanguage extends CommandHalfImpl {

    private final String STATE = "MANAGER_LANGUAGE";
    private final Integer STEP = 1;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        if(!update.hasMessage()){
            Long userId = update.hasCallbackQuery()? update.getCallbackQuery().getFrom().getId(): null;
            messageHasNotText(userId, update);
            return false;
        }

        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        String messageText = message.getText();

        if(!KeyboardUtil.availableLanguages().contains(messageText)){
            wrongValue(userId, "Iltimos ko'rsatilganlaridan birini tanlang");
            log.warn("User choose not shown language, User id: {} | Message: {}", userId, message);
            return false;
        }

        manager.setLanguageCode(message.getFrom().getLanguageCode().equals("en")? "uz": "ru");
        manager.setStep(2);

        String newMessage = "Iltimos telefon raqamingizni jo'nating\uD83D\uDC47";
        ReplyKeyboardMarkup markup = KeyboardUtil.phoneNumber();
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
}
