package uz.devops.intern.command.commandImpl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.command.ExecuteCommand;
import uz.devops.intern.service.CustomerTelegramService;

import java.net.URI;

import static uz.devops.intern.command.enumeration.CommandsName.UNKNOWN_COMMAND_WITH_SLASH;

@Service
public class UnknownCommand implements ExecuteCommand {
    private final CustomerTelegramService customerTelegramService;

    public UnknownCommand(CustomerTelegramService customerTelegramService) {
        this.customerTelegramService = customerTelegramService;
    }

    @Override
    public SendMessage execute(Update update, URI uri) {
        return customerTelegramService.unknownCommand(update.getMessage().getFrom(), uri);
    }

    @Override
    public String commandName() {
        return UNKNOWN_COMMAND_WITH_SLASH.getCommandName();
    }
}
