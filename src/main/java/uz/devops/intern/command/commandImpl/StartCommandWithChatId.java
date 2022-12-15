package uz.devops.intern.command.commandImpl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.command.ExecuteCommand;
import uz.devops.intern.service.CustomerTelegramService;

import java.net.URI;

import static uz.devops.intern.command.enumeration.CommandsName.START_WITH_CHAT_ID;

@Service
public class StartCommandWithChatId implements ExecuteCommand {
    private final CustomerTelegramService customerTelegramService;

    public StartCommandWithChatId(CustomerTelegramService customerTelegramService) {
        this.customerTelegramService = customerTelegramService;
    }

    @Override
    public SendMessage execute(Update update, URI uri) {
        Message message = update.getMessage();
        String stringMessage = message.getText();

        SendMessage sendMessage = customerTelegramService.startCommandWithChatId(message.getFrom(), stringMessage, uri);
        if (sendMessage.getText()!= null)
            return sendMessage;

        return customerTelegramService.commandWithUpdateMessage(update, uri);
    }

    @Override
    public String commandName() {
        return START_WITH_CHAT_ID.getCommandName();
    }
}
