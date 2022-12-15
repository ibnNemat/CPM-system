package uz.devops.intern.telegram.bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommand {

    boolean executeCommand(Update update, Long userId);

    String getCommand();
}
