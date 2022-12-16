package uz.devops.intern.telegram.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

@Service
public abstract class BotCommandAbs implements BotCommand {

    protected final Logger log = LoggerFactory.getLogger(BotCommandAbs.class);

    protected final AdminFeign adminFeign;

    protected BotCommandAbs(AdminFeign adminFeign) {
        this.adminFeign = adminFeign;
    }

    protected void wrongValue(Long chatId, String message){
        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
        Update update = adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
            chatId, message, update);
    }
}
