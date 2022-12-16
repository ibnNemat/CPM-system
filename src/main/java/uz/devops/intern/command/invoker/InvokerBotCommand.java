package uz.devops.intern.command.invoker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.command.CommandContainerVersion;

import java.net.URI;
import static uz.devops.intern.command.enumeration.CommandsName.*;

@Service
@RequiredArgsConstructor
public class InvokerBotCommand {

    private final CommandContainerVersion commandContainerVersion;
    private static final String prefixCommand = "/";


    public SendMessage getMessageCommand(Update update, URI uri){
        Message message = update.getMessage();
        if (message.getText() != null && message.getText().startsWith(prefixCommand)){
            String requestMessage = message.getText();
            return commandContainerVersion.getCommand(requestMessage.split("/start ")[0]).execute(update, uri);
        }

        return commandContainerVersion
            .getCommand(COMMAND_WITH_MESSAGE.getCommandName())
            .execute(update, uri);
    }

    public SendMessage getCallbackQueryCommand(Update update, URI uri){
        return commandContainerVersion
            .getCommand(COMMAND_WITH_CALLBACK_QUERY.getCommandName())
            .execute(update, uri);
    }
}
