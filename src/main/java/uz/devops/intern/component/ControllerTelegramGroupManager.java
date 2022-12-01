package uz.devops.intern.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.feign.TelegramClient;

@Component
public class ControllerTelegramGroupManager extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(ControllerTelegramGroupManager.class);
    private static final String BOT_USERNAME = "devopsInternBot";
    private static final String BOT_TOKEN = "5543292898:AAGoR3GLOCOL7Lir7sjYyCFYS7BLiUwNbHA";
    @Autowired
    private TelegramClient telegramClient;

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
        Long userChatId = update.getMessage().getChatId();

        String newMessage = "Hello telegram bot!";
        log.info("Message: {} | User chat id: {}",
                update.getMessage().getText(),
                update.getMessage().getFrom().getId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChatId);
        sendMessage.setText(newMessage);

        User user = update.getMessage().getFrom();

        try {
//            execute(sendMessage);
            telegramClient.sendMessage(sendMessage);
        }catch (Exception e){
            log.error("Error while sending message");
            e.printStackTrace();
        }
    }
}
