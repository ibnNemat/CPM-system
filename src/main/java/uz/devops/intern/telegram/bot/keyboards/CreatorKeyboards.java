package uz.devops.intern.telegram.bot.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public interface CreatorKeyboards {

    public static Integer row = null;

    List<String> getTextsOfButtons(String languageCode);

    default ReplyKeyboardMarkup createReplyKeyboardMarkup(String languageCode, Integer buttonsCount){

        ReplyKeyboardMarkup menuMarkup = new ReplyKeyboardMarkup();
        menuMarkup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        List<String> textsOfButtons = getTextsOfButtons(languageCode);
        for(String text: textsOfButtons){
            row.add(new KeyboardButton(text));
            if(row.size() == buttonsCount){
                rows.add(row);
                row = new KeyboardRow();
            }
        }

        if(rows.size() > 0){ rows.add(row); }
        menuMarkup.setKeyboard(rows);
        return menuMarkup;
    }

    default Integer getRow() {
        return row;
    }

}
