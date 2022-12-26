package uz.devops.intern.telegram.bot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

@ControllerAdvice
public class AdminExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(AdminExceptionHandler.class);
    @Autowired
    private AdminFeign adminFeign;
    @Value("${telegram.admin}")
    private Long adminTelegramId;

    @ExceptionHandler({Exception.class})
    public void warningAdminAboutError(Exception e){
        log.error("Bot throws error while working!");
        String newMessage = String.format("Bot throw exception while working! Exception class name: {} | Exception message: {} | Exception cause: {}",
            e.getClass().getSimpleName(), e.getMessage(), e.getCause().toString());
        SendMessage sendMessage = TelegramsUtil.sendMessage(adminTelegramId, newMessage);
        adminFeign.sendMessage(sendMessage);
        log.info("Admin is warned about error!");
    }
}
