package uz.devops.intern.telegram.bot.dto;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum UpdateType {

    MESSAGE,
    CALLBACK_QUERY,
    BOTH_OF_THEM,
    UNKNOWN;

}
