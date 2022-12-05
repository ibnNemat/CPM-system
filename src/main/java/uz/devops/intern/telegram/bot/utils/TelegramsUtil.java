package uz.devops.intern.telegram.bot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.intern.domain.CustomerTelegram;

import static uz.devops.intern.telegram.bot.utils.KeyboardUtil.sendKeyboardButtonToMarkPhoneNumber;


public class TelegramsUtil {
    private static final Logger log = LoggerFactory.getLogger(TelegramsUtil.class);

    public static SendMessage checkTelegramGroupIfExists(User telegramUser, Chat chat){
        if (telegramUser.getId().equals(chat.getId())){
            String sendStringMessage = "\uD83D\uDEAB Kechirasiz, tizimdan foydalanishingiz uchun sizda telegram" +
                " guruhi mavjud bo'lishi kerak. Avval, telegram guruhga qo'shilishingizni so'raymiz!";

            return sendMessage(telegramUser.getId(), sendStringMessage);
        }
        return null;
    }

    public static CustomerTelegram createCustomerTelegramToSaveDatabase(User telegramUser) {
        return new CustomerTelegram()
            .isBot(telegramUser.getIsBot())
            .telegramId(telegramUser.getId())
            .canJoinGroups(telegramUser.getCanJoinGroups())
            .firstname(telegramUser.getFirstName())
            .lastname(telegramUser.getFirstName())
            .username(telegramUser.getUserName())
            .languageCode(telegramUser.getLanguageCode())
            .step(1)
            .isActive(true);
    }

    public static SendMessage checkPhoneNumberIsNull(CustomerTelegram customerTelegram, User telegramUser){
        SendMessage sendMessage;

        if (customerTelegram.getPhoneNumber() == null){
            sendMessage = sendKeyboardButtonToMarkPhoneNumber(telegramUser);

            log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage);
            return sendMessage;
        }

        return null;
    }

    /**
     * param: Long chatId
     * param: String text

     * return: SendMessage without buttons
     */
    public static SendMessage sendMessage(Long chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableHtml(true);

        return sendMessage;
    }

    /**
     * param: Long chatId
     * param: String text

     * return: SendMessage with buttons
     */
    public static SendMessage sendMessage(Long chatId, String text, ReplyKeyboardMarkup keyboardMarkup){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.enableHtml(true);

        return sendMessage;
    }

    public static SendMessage sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboardMarkup){
        /**
         * param: Long chatId
         * param: String text
         * param: InlineKeyboardMarkup
         *
         * return SendMessage with buttons
         */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.enableHtml(true);
        return sendMessage;
    }
//
//    public static SendMessage sendMessage(Long chatId, String text, ReplyKeyboardRemove replyKeyboardRemove){
//        /**
//         * param: Long chatId
//         * param: String text
//         * param: ReplyKeyboardRemove
//         *
//         * return SendMessage with buttons
//         */
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(text);
//        sendMessage.setReplyMarkup(replyKeyboardRemove);
//        sendMessage.enableHtml(true);
//        return sendMessage;
//    }
//
//    public static SendMessage sendMessage(Long chatId, String text, InlineKeyboardMarkup inlineMarkup){
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(text);
//        sendMessage.setReplyMarkup(inlineMarkup);
//
//        return sendMessage;
//    }
//
//    public static SendMessage sendMessage(Long chatId, String text, ReplyKeyboardRemove removeMarkup){
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(text);
//        sendMessage.setReplyMarkup(removeMarkup);
//
//        return sendMessage;
//    }


    public static SendMessage wrongChoice(Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Noto'g'ri qiymat");
        sendMessage.setChatId(chatId);

        return sendMessage;
    }

}
