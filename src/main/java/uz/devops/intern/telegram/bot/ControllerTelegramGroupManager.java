package uz.devops.intern.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.AdminTgService;

@Component
public class ControllerTelegramGroupManager extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(ControllerTelegramGroupManager.class);
    private static final String BOT_USERNAME = "@iqroKitaab_bot";
    private static final String BOT_TOKEN = "5149475148:AAHfcyltnIyq7LCN0GamAcrHpxwxT1yV1EE";
    @Autowired
    private AdminFeign adminFeign;
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
