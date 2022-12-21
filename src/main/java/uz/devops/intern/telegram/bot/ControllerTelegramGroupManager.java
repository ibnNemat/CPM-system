package uz.devops.intern.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.AdminTgService;

@Component
public class ControllerTelegramGroupManager extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(ControllerTelegramGroupManager.class);
    @Value("${telegram.username}")
    private String BOT_USERNAME;

    @Value("${telegram.token}")
    private String BOT_TOKEN;
    @Autowired
    private AdminTgService adminService;
    @Override
    public String getBotUsername(){
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken(){
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update){
        adminService.main(update);
    }
}
