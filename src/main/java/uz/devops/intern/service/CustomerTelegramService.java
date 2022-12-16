package uz.devops.intern.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;

import java.net.URI;
import java.util.List;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
public interface CustomerTelegramService {

    ResponseDTO<CustomerTelegramDTO> findByTelegramId(Long telegramId);

    CustomerTelegram findEntityByTelegramId(Long telegramId);

    List<CustomerTelegramDTO> findByTelegramGroupTelegramId(Long telegramId);
    List<CustomerTelegramDTO> findAllByIsActiveTrue();
    List<CustomerTelegramDTO> findAll();
    void setFalseToTelegramCustomerProfile(List<Long> ids);
    void deleteAllTelegramCustomersIsActiveFalse(List<Long> ids);

    ResponseDTO<CustomerTelegramDTO> getCustomerByTelegramId(Long telegramId);

    ResponseDTO<CustomerTelegramDTO> update(CustomerTelegramDTO dto);

    ResponseDTO<CustomerTelegramDTO> findByBotTgId(Long botId);
    SendMessage startCommandWithChatId(User telegramUser, String requestMessage, URI uri);
    SendMessage commandWithUpdateMessage(Update update, URI uri);
    SendMessage startCommandWithoutChatId(User telegramUser, URI uri);
    SendMessage helpCommand(Update update, Message message);
    SendMessage sendForbiddenMessage(Update update, URI telegramUri);
    SendMessage unknownCommand(User telegramUser, URI telegramURI);
    SendMessage commandWithCallbackQuery(CallbackQuery callbackQuery, URI uri);

    ResponseDTO<List<CustomerTelegramDTO>> getCustomerTgByChatId(Long chatId);
}
