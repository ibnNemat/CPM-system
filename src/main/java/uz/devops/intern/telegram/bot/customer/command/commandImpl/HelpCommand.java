package uz.devops.intern.telegram.bot.customer.command.commandImpl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.telegram.bot.customer.command.ExecuteCommand;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.telegram.bot.customer.command.enumeration.CommandsName;

import java.net.URI;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
public class HelpCommand implements ExecuteCommand {

    @Override
    public SendMessage execute(Update update, URI uri) {
        return helpCommand(update, uri);
    }

    @Override
    public String commandName() {
        return CommandsName.HELP.getCommandName();
    }

    public SendMessage helpCommand(Update update, URI uri) {
        String newMessage = "Bu yerda platformadan qanday foydalanish kerakligi yozilgan bo'ladi.\n" +
            "\n" +
            "Komandalar nima qilishi: \n" +
            "1. \n" +
            "2. \n" +
            "3. \n" +
            "\n" +
            "Platformadan qanday foydalanish instruksiyasi: \n" +
            "1. \n" +
            "2. \n" +
            "3. \n" +
            "\n" +
            "Bosing: /start";

        return sendMessage(update.getMessage().getFrom().getId(), newMessage);
    }
}
