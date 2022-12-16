package uz.devops.intern.telegram.bot.keyboards.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface AdminMenu {

    String getLanguageCode();

    ReplyKeyboardMarkup getMarkup();
}
