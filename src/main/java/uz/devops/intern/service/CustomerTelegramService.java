package uz.devops.intern.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
public interface CustomersTelegramService {
    SendMessage botCommands(Update update);
}
