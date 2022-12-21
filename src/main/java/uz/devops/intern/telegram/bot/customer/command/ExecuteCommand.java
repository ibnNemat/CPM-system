package uz.devops.intern.telegram.bot.customer.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;

public interface ExecuteCommand {
    SendMessage execute(Update update, URI uri);
    String commandName();
}
