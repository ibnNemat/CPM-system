package uz.devops.intern.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class AdminKeyboards {

    private final static List<String> periods = List.of("Yillik", "Oylik", "Haftalik", "Kunlik", "Bir martalik");

    public List<KeyboardButton> menuButtons = List.of(
        new KeyboardButton("\uD83C\uDFE2 Yangi tashkilot"),
        new KeyboardButton("\uD83D\uDC65 Guruh qo'shish"),
        new KeyboardButton("\uD83E\uDEC2 Xizmat qo'shish"),
        new KeyboardButton("\uD83D\uDCB8 Qarzdorliklarni ko'rish"),
        new KeyboardButton("\uD83D\uDC40 Guruhlarni ko'rish")
    );

    public static ReplyKeyboardMarkup createMenu(){
        KeyboardRow header = new KeyboardRow();
        header.add(new KeyboardButton("\uD83C\uDFE2 Yangi tashkilot"));
        header.add(new KeyboardButton("\uD83D\uDC65 Guruh qo'shish"));

        KeyboardRow body = new KeyboardRow();
        body.add(new KeyboardButton("\uD83E\uDEC2 Xizmat qo'shish"));
        body.add(new KeyboardButton("\uD83D\uDCB8 Qarzdorliklarni ko'rish"));

        KeyboardRow footer = new KeyboardRow();
        footer.add(new KeyboardButton("\uD83D\uDC40 Guruhlarni ko'rish"));

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(header, body, footer));

        return markup;
    }

    public static List<String> getPeriods(){
        return periods;
    }

    public static ReplyKeyboardMarkup getPeriodTypeButtons(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();

        KeyboardRow header = new KeyboardRow();
        header.add(new KeyboardButton("Yillik"));
        header.add(new KeyboardButton("Oylik"));

        KeyboardRow body = new KeyboardRow();
        body.add(new KeyboardButton("Haftalik"));
        body.add(new KeyboardButton("Kunlik"));

        KeyboardRow footer = new KeyboardRow();
        footer.add(new KeyboardButton("Bir martalik"));

        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(header, body, footer));
        return markup;
    }
}
