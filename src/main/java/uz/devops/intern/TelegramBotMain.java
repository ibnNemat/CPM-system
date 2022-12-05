package uz.devops.intern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBotMain extends TelegramLongPollingBot {

    private final Logger log = LoggerFactory.getLogger(TelegramBotMain.class);
    private final String BOT_TOKEN = "5543292898:AAGoR3GLOCOL7Lir7sjYyCFYS7BLiUwNbHA";
    private final String BOT_USERNAME = "devopsInternBot";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }
}
