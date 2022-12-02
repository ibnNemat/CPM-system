package uz.devops.intern.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
public interface CustomerTelegramService {
    SendMessage botCommands(Update update);
}
