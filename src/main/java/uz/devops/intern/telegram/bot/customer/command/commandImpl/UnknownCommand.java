package uz.devops.intern.telegram.bot.customer.command.commandImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.telegram.bot.customer.command.ExecuteCommand;
import uz.devops.intern.telegram.bot.customer.command.enumeration.CommandsName;

import java.net.URI;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.BOT_UNKNOWN_COMMAND;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingTelegramUser;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
public class UnknownCommand implements ExecuteCommand {
    private final Logger log = LoggerFactory.getLogger(UnknownCommand.class);
    @Override
    public SendMessage execute(Update update, URI uri) {
        return unknownCommand(update.getMessage().getFrom());
    }

    @Override
    public String commandName() {
        return CommandsName.UNKNOWN_COMMAND_WITH_SLASH.getCommandName();
    }


    public SendMessage unknownCommand(User telegramUser) {
        log.warn("started working method unknownCommand with param telegramUser: {}", telegramUser);
        ResourceBundle resourceBundle = getResourceBundleUsingTelegramUser(telegramUser);
        return sendMessage(telegramUser.getId(),  resourceBundle.getString(BOT_UNKNOWN_COMMAND));
    }
}
