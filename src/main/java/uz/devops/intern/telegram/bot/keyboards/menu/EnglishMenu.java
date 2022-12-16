package uz.devops.intern.telegram.bot.keyboards.menu;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//@Getter
//@Service
public class EnglishMenu implements AdminMenu{
    private final String LANGUAGE = "en";
    private ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode("en");

    private ReplyKeyboardMarkup menuMarkup;
    private final List<String> textsOfButtons = List.of(
        bundle.getString("bot.admin.keyboards.menu.new.organization"),
        bundle.getString("bot.admin.keyboards.menu.add.group"),
        bundle.getString("bot.admin.keyboards.menu.add.services"),
        bundle.getString("bot.admin.keyboards.menu.show.payments"),
        bundle.getString("bot.admin.keyboards.menu.show.groups")
    );

    @PostConstruct
    public void inject(){
        menuMarkup = new ReplyKeyboardMarkup();
        menuMarkup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for(String text: textsOfButtons){
            row.add(new KeyboardButton(text));
            if(row.size() == 2){
                rows.add(row);
                row = new KeyboardRow();
            }
        }

        if(rows.size() > 0){ rows.add(row); }
        menuMarkup.setKeyboard(rows);
    }

    @Override
    public String getLanguageCode() {
        return LANGUAGE;
    }

    @Override
    public ReplyKeyboardMarkup getMarkup() {
        return menuMarkup;
    }
}

