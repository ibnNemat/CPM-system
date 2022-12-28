package uz.devops.intern.telegram.bot.customer.command.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.telegram.bot.customer.command.ExecuteCommand;
import uz.devops.intern.telegram.bot.customer.command.enumeration.CommandsName;
import uz.devops.intern.telegram.bot.customer.service.TranslateCommandService;

import java.net.URI;
@Service
@RequiredArgsConstructor
public class TranslateCommandIntoUzbek implements ExecuteCommand {
    private final TranslateCommandService translateCommandService;
    @Override
    public SendMessage execute(Update update, URI uri) {
        return translateCommandService.changedLanguageMessage(update);
    }

    @Override
    public String commandName() {
        return CommandsName.TRANSLATE_INTO_UZBEK.getCommandName();
    }
}
