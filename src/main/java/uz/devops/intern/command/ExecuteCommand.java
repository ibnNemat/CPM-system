package uz.devops.intern.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ExecuteCommand {
    void execute(Update update);
}
