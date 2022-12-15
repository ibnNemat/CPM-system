package uz.devops.intern.command.commandImpl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.command.ExecuteCommand;
import uz.devops.intern.service.CustomerTelegramService;

import java.net.URI;

import static uz.devops.intern.command.enumeration.CommandsName.START_WITHOUT_CHAT_ID;

@Service
public class StartCommandWithoutChatId implements ExecuteCommand {

    private final CustomerTelegramService customerTelegramService;

    public StartCommandWithoutChatId(CustomerTelegramService customerTelegramService) {
        this.customerTelegramService = customerTelegramService;
    }

    @Override
    public SendMessage execute(Update update, URI uri) {
        return customerTelegramService.startCommandWithoutChatId(update.getMessage().getFrom(), uri);
    }

    @Override
    public String commandName() {
        return START_WITHOUT_CHAT_ID.getCommandName();
    }
}
