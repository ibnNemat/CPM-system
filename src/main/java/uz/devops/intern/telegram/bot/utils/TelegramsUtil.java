package uz.devops.intern.telegram.bot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.redis.CustomerTelegramRedis;
import uz.devops.intern.service.impl.CustomerTelegramServiceImpl;

import java.util.List;
import java.util.Optional;

public class TelegramsUtil {
    private static final Logger log = LoggerFactory.getLogger(TelegramsUtil.class);

    public static CustomerTelegram createCustomerTelegramToSaveDatabase(User telegramUser, String phoneNumber) {
        return new CustomerTelegram()
            .isBot(telegramUser.getIsBot())
            .telegramId(telegramUser.getId())
            .canJoinGroups(telegramUser.getCanJoinGroups())
            .firstname(telegramUser.getFirstName())
            .username(telegramUser.getUserName())
            .languageCode(telegramUser.getLanguageCode())
            .step(1)
            .phoneNumber(phoneNumber)
            .isActive(true);
    }

    public static SendMessage checkCustomerTelegramIsEmpty(Optional<CustomerTelegram> customerTelegramOptional, User telegramUser){
        SendMessage sendMessage;
        String sendStringMessage = null;

        if (customerTelegramOptional.isEmpty()){
            sendStringMessage = "Siz hali telegram botdan foydalanish uchun ro'yxatdan o'tmagansiz, iltimos telefon raqamingizni jo'natish" +
                " uchun quyidagi tugmani bosing \uD83D\uDC47\n";
            KeyboardButton button = new KeyboardButton("\uD83D\uDCF1 Telefon raqam");
            button.setRequestContact(true);

            KeyboardRow row = new KeyboardRow();
            row.add(button);

            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
            markup.setResizeKeyboard(true);
            markup.setKeyboard(List.of(row));

            sendMessage = sendMessage(String.valueOf(telegramUser.getId()), sendStringMessage, markup);
            log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage);
            return sendMessage;
        }

        return null;
    }


    /**
     * param: String chatId
     * param: String text

     * return: SendMessage without buttons
     */
    public static SendMessage sendMessage(String chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        return sendMessage;
    }

    /**
     * param: String chatId
     * param: String text

     * return: SendMessage with buttons
     */
    public static SendMessage sendMessage(String chatId, String text, ReplyKeyboardMarkup keyboardMarkup){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }
}
