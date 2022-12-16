package uz.devops.intern.telegram.bot.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class AdminMenuKeys {

    public List<String> getTextsOfButtons(String languageCode){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);
        return List.of(
            bundle.getString("bot.admin.keyboards.menu.new.organization"),
            bundle.getString("bot.admin.keyboards.menu.add.group"),
            bundle.getString("bot.admin.keyboards.menu.add.services"),
            bundle.getString("bot.admin.keyboards.menu.show.payments"),
            bundle.getString("bot.admin.keyboards.menu.show.groups")
        );
    }

    public ReplyKeyboardMarkup createMenu(String languageCode){
        ReplyKeyboardMarkup menuMarkup = new ReplyKeyboardMarkup();
        menuMarkup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        List<String> textsOfButtons = getTextsOfButtons(languageCode);
        for(String text: textsOfButtons){
            row.add(new KeyboardButton(text));
            if(row.size() == 2){
                rows.add(row);
                row = new KeyboardRow();
            }
        }

        if(rows.size() > 0){ rows.add(row); }
        menuMarkup.setKeyboard(rows);
        return null;
    }

}
