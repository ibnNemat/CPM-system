package uz.devops.intern.telegram.bot.service.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ResourceBundle;

public abstract class State<T> {

    protected final Logger log = LoggerFactory.getLogger(State.class);

    protected T context;
    private final AdminFeign adminFeign;

    public State(T context, AdminFeign adminFeign){
        this.context = context;
        this.adminFeign = adminFeign;
    }

    abstract boolean doThis(Update update, CustomerTelegramDTO manager);

    protected void wrongValue(Long chatId, String message){
        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
        Update update = adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
            chatId, message, update);
    }

    protected void messageHasNotText(Long chatId, Update update){
        wrongValue(chatId, "Iltimos xabar yuboring\uD83D\uDE4F");
        log.warn("User hasn't send text, Chat id: {} | Update: {}", chatId, update);
    }

    protected boolean checkUpdateInside(Update update, Long managerId){
        if(!update.hasMessage()){
            wrongValue(managerId, "Iltimos ko'rsatilganlardan birini tanlang!");
            return false;
        }

        if(!update.getMessage().hasText()) {
            messageHasNotText(managerId, update);
            return false;
        }

        return true;
    }

    public boolean isManagerPressCancelButton(Update update, CustomerTelegramDTO manager){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasMessage() || !update.getMessage().hasText()){
            return false;
        }

        String messageText = update.getMessage().getText();
        if(!bundle.getString("bot.admin.keyboard.cancel.process").equals(messageText)){
            return false;
        }
        return true;
    }
}
