package uz.devops.intern.telegram.bot.customer.command.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.telegram.bot.customer.command.ExecuteCommand;
import uz.devops.intern.telegram.bot.customer.command.enumeration.CommandsName;
import uz.devops.intern.telegram.bot.customer.service.CustomerUpdateWithCallbackQueryService;

import java.net.URI;


@Service
@RequiredArgsConstructor
public class CommandWithCallbackQuery implements ExecuteCommand {

    private final CustomerUpdateWithCallbackQueryService callbackQueryService;

    @Override
    public SendMessage execute(Update update, URI uri) {
        return callbackQueryService.commandWithCallbackQuery(update.getCallbackQuery(), uri);
    }

    @Override
    public String commandName() {
        return CommandsName.COMMAND_WITH_CALLBACK_QUERY.getCommandName();
    }
}
