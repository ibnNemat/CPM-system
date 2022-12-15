package uz.devops.intern.command.commandImpl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.command.ExecuteCommand;
import uz.devops.intern.service.CustomerTelegramService;

import javax.annotation.PostConstruct;
import java.net.URI;

import static uz.devops.intern.command.enumeration.CommandsName.COMMAND_WITH_CALLBACK_QUERY;


@Service
public class CommandWithCallbackQuery implements ExecuteCommand {

    @PostConstruct
    public void init(){
        System.out.println(getClass().getName() + " is created!");
    }
    private final CustomerTelegramService customerTelegramService;
    public CommandWithCallbackQuery(CustomerTelegramService customerTelegramService) {
        this.customerTelegramService = customerTelegramService;
    }

    @Override
    public SendMessage execute(Update update, URI uri) {
        return customerTelegramService.commandWithCallbackQuery(update.getCallbackQuery(), uri);
    }

    @Override
    public String commandName() {
        return COMMAND_WITH_CALLBACK_QUERY.getCommandName();
    }
}
