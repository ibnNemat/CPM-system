package uz.devops.intern.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.TelegramClient;
import uz.devops.intern.service.AdminTgService;

@Component
public class ControllerTelegramGroupManager extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(ControllerTelegramGroupManager.class);
    private static final String BOT_USERNAME = "devopsInternBot";
    private static final String BOT_TOKEN = "5543292898:AAGoR3GLOCOL7Lir7sjYyCFYS7BLiUwNbHA";
    @Autowired
    private CustomerFeign customerFeign;
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
        System.out.println("============ Men Managerman: ==================\n" +
            "Message: " + update.getMessage());
        System.out.println("User: " + update.getMessage().getFrom());
        System.out.println("Message ChatID: " +update.getMessage().getChatId());
        System.out.println("Message chat" + update.getMessage().getChat().toString());
        System.out.println("================================================");
        adminService.main(update);
    }
}
