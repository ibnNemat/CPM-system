package uz.devops.intern.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
public interface CustomerTelegramService {
    ResponseDTO<CustomerTelegramDTO> findByTelegramId(Long telegramId);
    List<CustomerTelegramDTO> findByTelegramGroupTelegramId(Long telegramId);
    List<CustomerTelegramDTO> findAllByIsActiveTrue();
    List<CustomerTelegramDTO> findAll();
    void setFalseToTelegramCustomerProfile(List<Long> ids);
    void deleteAllTelegramCustomersIsActiveFalse(List<Long> ids);
    Optional<CustomerTelegram> findCustomerByTelegramId(Long telegramId);

    ResponseDTO<CustomerTelegramDTO> getCustomerByTelegramId(Long telegramId);

    ResponseDTO<CustomerTelegramDTO> update(CustomerTelegramDTO dto);

    ResponseDTO<CustomerTelegramDTO> findByBotTgId(Long botId);
    ResponseDTO<List<CustomerTelegramDTO>> getCustomerTgByChatId(Long chatId);

    Optional<CustomerTelegram> findByCustomer(Customers customer);
}
