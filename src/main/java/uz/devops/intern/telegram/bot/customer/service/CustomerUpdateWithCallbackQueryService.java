package uz.devops.intern.telegram.bot.customer.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;

import java.net.URI;

public interface CustomerUpdateWithCallbackQueryService {
    /**
     *
     * @param callbackQuery to get callbackData of inlineButton and the user who sends request from bot
     * @param uri - URI to send message for telegram bot
     *
     * This method responds sendMessage when a callbackQuery request comes from client bot
     * @return SendMessage
     */
    SendMessage commandWithCallbackQuery(CallbackQuery callbackQuery, URI uri);
    SendMessage sendCustomerMenu(User userTelegram, CustomerTelegram customerTelegram);
    SendMessage whenPressingLeftOrRightToGetCustomerPayment(User telegramUser, CallbackQuery callbackQuery, CustomerTelegram customerTelegram, URI telegramURI);
    EditMessageTextDTO changeCustomerProfile(User telegramUser, CustomerTelegram customerTelegram, CallbackQuery callbackQuery);
    EditMessageTextDTO createMessageTextDTOWithEmptyInlineButton(String text, CallbackQuery callbackQuery, User telegramUser);
    EditMessageTextDTO showCurrentCustomerPayment(User telegramUser, CustomerTelegram customerTelegram, CallbackQuery callbackQuery);
    EditMessageTextDTO createMessageTextDTO(String text, CallbackQuery callbackQuery, User telegramUser);
    EditMessageTextDTO sendRequestPaymentSum(CustomerTelegram customerTelegram, User telegramUser, CallbackQuery callbackQuery);
}
