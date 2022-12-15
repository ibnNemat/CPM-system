package uz.devops.intern.telegram.bot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyboardUtil {
    public static final String UZ_LANGUAGE = "\uD83C\uDDFA\uD83C\uDDFF Uzbek";
    public static final String RU_LANGUAGE = "\uD83C\uDDF7\uD83C\uDDFA Русский";
    public static final String EN_LANGUAGE = "\uD83C\uDDEC\uD83C\uDDE7 English";

    private final static List<String> LANGUAGES = List.of(
        UZ_LANGUAGE, RU_LANGUAGE, EN_LANGUAGE
    );

    private static final Map<String, String> languageMap = Map.of(
        UZ_LANGUAGE, "uz",
        RU_LANGUAGE,"ru",
        EN_LANGUAGE ,"en"
    );

    public static Map<String, String> getLanguages(){
        return languageMap;
    }
    public static List<String> availableLanguages(){
        return LANGUAGES;
    }

    public static ReplyKeyboardMarkup language(){
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        for(String language: LANGUAGES){
            row.add(new KeyboardButton(language));
            if(row.size() == 2){
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        if(!row.isEmpty()) rows.add(row);

        row.add(new KeyboardButton(UZ_LANGUAGE));
        row.add(new KeyboardButton(RU_LANGUAGE));
        row.add(new KeyboardButton(EN_LANGUAGE));

        ReplyKeyboardMarkup markup =  new ReplyKeyboardMarkup(rows);
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

    public static ReplyKeyboardMarkup sendBackHomeButton(String text){
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(text));
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow));
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
