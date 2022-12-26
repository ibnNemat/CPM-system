package uz.devops.intern.telegram.bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

import java.util.ResourceBundle;

public interface BotStrategy {

    boolean execute(Update update, CustomerTelegramDTO manager);

    String getState();

    Integer getStep();

    String messageOrCallback();

    String getErrorMessage(ResourceBundle bundle);
}
