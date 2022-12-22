package uz.devops.intern.telegram.bot.customer.command.commandImpl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.telegram.bot.customer.command.ExecuteCommand;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.telegram.bot.customer.command.enumeration.CommandsName;

import java.net.URI;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.BOT_FORBIDDEN;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingTelegramUser;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
public class StartCommandWithoutChatId implements ExecuteCommand {

    @Override
    public SendMessage execute(Update update, URI uri) {
        User telegramUser = update.getMessage().getFrom();
        ResourceBundle resourceBundle = getResourceBundleUsingTelegramUser(telegramUser);
        return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_FORBIDDEN));
    }

    @Override
    public String commandName() {
        return CommandsName.START_WITHOUT_CHAT_ID.getCommandName();
    }
}
