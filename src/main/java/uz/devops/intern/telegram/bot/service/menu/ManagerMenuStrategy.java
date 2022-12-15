package uz.devops.intern.telegram.bot.service.menu;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

public interface ManagerMenuStrategy {

    boolean todo(Update update, CustomerTelegramDTO manager);

    String getSupportedText();
}
