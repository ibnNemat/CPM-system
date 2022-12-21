package uz.devops.intern.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.feign.CustomerFeignClient;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

@Service
public abstract class BotStrategyAbs implements BotStrategy {

    public final Logger log = LoggerFactory.getLogger(BotStrategyAbs.class);
    public final String telegramAPI = "https://api.telegram.org/bot";
    @Autowired
    public AdminFeign adminFeign;
    @Autowired
    public CustomerFeignClient customerFeign;

    abstract public boolean execute(Update update, CustomerTelegramDTO manager);
    abstract public String getState();
    abstract public Integer getStep();

    public void wrongValue(Long chatId, String message){
        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
        Update update = adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
            chatId, message, update);
    }

    public void messageHasNotText(Long chatId, Update update){
        ResourceBundle bundle =
            ResourceBundleUtils.getResourceBundleByUserLanguageCode(update.getMessage().getFrom().getLanguageCode());
        wrongValue(chatId, bundle.getString("bot.admin.send.only.message.or.contact"));
        log.warn("User hasn't send text, Chat id: {} | Update: {}", chatId, update);
    }

    public void messageHasNotText(Long chatId, Update update, Boolean contact){
        wrongValue(chatId, "Iltimos xabar yoki kontakt yuboring\uD83D\uDE4F");
        log.warn("User hasn't send text, Chat id: {} | Update: {}", chatId, update);
    }

    public URI createCustomerURI(String token){
        try {
            return new URI(telegramAPI + token);
        } catch (URISyntaxException e) {
            log.error("{} | {}", e.getMessage(), e.getCause());
            return null;
        }
    }


}
