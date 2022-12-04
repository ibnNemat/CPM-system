package uz.devops.intern.telegram.bot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardUtil {

    public static ReplyKeyboardMarkup language(){
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("\uD83C\uDDFA\uD83C\uDDFF O`zbekcha"));
        row.add(new KeyboardButton("\uD83C\uDDF7\uD83C\uDDFA Русский"));

        ReplyKeyboardMarkup markup =  new ReplyKeyboardMarkup(List.of(row));
        markup.setResizeKeyboard(true);
        return markup;
    }

    public static ReplyKeyboardMarkup phoneNumber(){
        KeyboardButton button = new KeyboardButton("\uD83D\uDCF2 Telefon raqamni jo'natish");
        button.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(button);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(List.of(row));
        markup.setResizeKeyboard(true);
        return markup;
    }

    public static SendMessage sendKeyboardButtonToMarkPhoneNumber(User telegramUser){
        String sendStringMessage = "❗️ Siz hali telegram botdan foydalanish uchun ro'yxatdan o'tmagansiz, iltimos telefon raqamingizni jo'natish" +
            " uchun quyidagi tugmani bosing \uD83D\uDC47\n";

        return TelegramsUtil.sendMessage(telegramUser.getId(), sendStringMessage, sendMarkup());
    }

    public static ReplyKeyboardMarkup sendMarkup(){
        KeyboardButton button = new KeyboardButton("\uD83D\uDCF1 Telefon raqam");
        button.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(button);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(row));

        return markup;
    }
}
