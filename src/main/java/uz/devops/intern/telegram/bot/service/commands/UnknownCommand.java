package uz.devops.intern.telegram.bot.service.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;

@Service(value = "unknown-command")
public class UnknownCommand extends BotCommandAbs {

    private final String COMMAND = "/unknown";

    protected UnknownCommand(AdminFeign adminFeign) {
        super(adminFeign);
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        wrongValue(userId, "Iltimos /help ni bosing!");
        return false;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
