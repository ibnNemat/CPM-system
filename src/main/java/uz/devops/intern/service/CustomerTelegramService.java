package uz.devops.intern.service;

import org.glassfish.jersey.server.Uri;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
public interface CustomerTelegramService {
    SendMessage botCommands(Update update, URI telegramUri) throws URISyntaxException;
}
