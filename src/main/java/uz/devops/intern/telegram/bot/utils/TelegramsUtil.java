package uz.devops.intern.telegram.bot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static uz.devops.intern.constants.ResourceBundleConstants.BOT_CHANGED_LANGUAGE;
import static uz.devops.intern.telegram.bot.utils.KeyboardUtil.sendMarkup;


public class TelegramsUtil {
    private static final Logger log = LoggerFactory.getLogger(TelegramsUtil.class);
    private static final String RESOURCE_BUNDLE_NAME = "message";

    public static ResourceBundle getResourceBundleByCustomerTgDTO(CustomerTelegramDTO customerTelegramDTO){
        Locale locale = new Locale(customerTelegramDTO.getLanguageCode());
        LocaleContextHolder.setLocale(locale);
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, LocaleContextHolder.getLocale());
    }


    public static ResourceBundle getResourceBundleByUserLanguageCode(String languageCode){
        log.info("Language code: {} ", languageCode);
        languageCode = languageCode.equals("ru_RU") || languageCode.equals("ru")? "ru":
            languageCode.equals("uz_UZ") || languageCode.equals("uz")? "uz": "en";
        Locale locale = new Locale(languageCode);
        LocaleContextHolder.setLocale(locale);
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, LocaleContextHolder.getLocale());
    }

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
            .manager(false)
            .isActive(true);
    }

    public static CustomerTelegramDTO createCustomerTelegramDTO(User telegramUser, Long userId) {
        return CustomerTelegramDTO.builder()
            .id(userId)
            .isBot(telegramUser.getIsBot())
            .telegramId(telegramUser.getId())
            .canJoinGroups(telegramUser.getCanJoinGroups())
            .firstname(telegramUser.getFirstName())
            .lastname(telegramUser.getFirstName())
            .username(telegramUser.getUserName())
            .languageCode(telegramUser.getLanguageCode())
            .isManager(false)
            .step(1)
            .isActive(true)
            .telegramGroups(Set.of())
            .build();
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

    public static SendMessage sendMessage(Long chatId, String text, ReplyKeyboardRemove replyKeyboardRemove){
        /**
         * param: Long chatId
         * param: String text
         * param: ReplyKeyboardRemove
         *
         * return SendMessage with buttons
         */
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboardRemove);
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public static ReplyKeyboardMarkup createCancelButton(ResourceBundle bundle){
        String buttonText = bundle.getString("bot.admin.keyboard.cancel.process");
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        KeyboardRow row = new KeyboardRow();
        row.add(
            new KeyboardButton(buttonText)
        );

        markup.setKeyboard(List.of(row));
        return markup;
    }
}
