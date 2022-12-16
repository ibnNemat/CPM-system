package uz.devops.intern.telegram.bot.keyboards;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.telegram.bot.keyboards.menu.AdminMenu;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Component
public class AdminMenuKeys {

    private final HashMap<String, ReplyKeyboardMarkup> markups = new HashMap<>();
    private final List<AdminMenu> adminMenus;

    public AdminMenuKeys(List<AdminMenu> adminMenus) {
        this.adminMenus = adminMenus;
    }

    @PostConstruct
    public HashMap<String, ReplyKeyboardMarkup> createMarkupsMap(){
        for(AdminMenu menu: adminMenus){
            markups.put(menu.getLanguageCode(), menu.getMarkup());
        }

        return markups;
    }

    public ReplyKeyboardMarkup createMenu(String languageCode){
        return markups.getOrDefault(languageCode, null);
    }

}
