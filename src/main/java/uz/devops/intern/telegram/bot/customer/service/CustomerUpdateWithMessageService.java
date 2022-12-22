package uz.devops.intern.telegram.bot.customer.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;

import java.net.URI;

public interface CustomerUpdateWithMessageService {
    /**
     *
     * @param telegramUser to get the user who sends request from bot
     * @param requestMessage - message sent by telegram customers
     * @param update to get Message and customer who sends request from telegram bot
     *
     * This method executes the bot commands, checking the client's step if an update message arrives.
     * @return SendMessage
     */
    SendMessage executeCommandStepByStep(User telegramUser, String requestMessage, Update update);

    /**
     *
     * @param update to get Message and customer who sends request from telegram bot
     * @param telegramUri - URI to send message for telegram bot
     *
     * This method will be working and sending message when arrives update message from telegram bot
     * @return
     */
    SendMessage commandWithUpdateMessage(Update update, URI telegramUri);
    SendMessage responseFromStartCommandWithChatId(Update update, URI telegramURI, String chatId);
    SendMessage registerCustomerClientAndShowCustomerMenu(String requestMessage, User telegramUser, CustomerTelegram customerTelegram);
    SendMessage sendCustomerGroups(User telegramUser, CustomerTelegram customerTelegram);
    SendMessage sendCustomerPayments(User telegramUser, CustomerTelegram customerTelegram, Message message);
    SendMessage showCustomerProfile(User telegramUser, CustomerTelegram customerTelegram);
    SendMessage sendCustomerPaymentsHistory(User telegramUser, CustomerTelegram customerTelegram);
    SendMessage sendAllCustomerPayments(User telegramUser, CustomerTelegram customerTelegram);
    SendMessage mainCommand(String buttonMessage, User telegramUser, CustomerTelegram customerTelegram, Message message);
    SendMessage payRequestForService(String paymentSum, User telegramUser, CustomerTelegram customerTelegram);
    SendMessage changeEmail(String email, User telegramUser, CustomerTelegram customerTelegram);
    SendMessage changeFullName(String fullName, User telegramUser, CustomerTelegram customerTelegram);
    SendMessage changePhoneNumber(String phoneNumber, User telegramUser, CustomerTelegram customerTelegram);
    SendMessage forbiddenMessage(User telegramUser);
}
