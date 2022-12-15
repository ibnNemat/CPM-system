package uz.devops.intern.telegram.bot.service.commands;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

@Service
public class HelpCommand extends BotCommandAbs {

    private final String COMMAND = "/help";

    protected HelpCommand(AdminFeign adminFeign) {
        super(adminFeign);
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        SendMessage sendMessage = TelegramsUtil.sendMessage(userId, "/start bosing!");
        adminFeign.sendMessage(sendMessage);
        return false;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
