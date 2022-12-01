package uz.devops.intern.telegram.bot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardUtil {

    public static ReplyKeyboardMarkup language(){
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("🇺🇿 O`zbekcha"));
        row.add(new KeyboardButton("\uD83C\uDDF7\uD83C\uDDFA Русский"));

        return new ReplyKeyboardMarkup(List.of(row));
    }
}
