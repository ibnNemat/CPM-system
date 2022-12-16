package uz.devops.intern.telegram.bot.service.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;

import java.util.ResourceBundle;

@Service(value = "unknown-command")
public class UnknownCommand extends BotCommandAbs {

    private final String COMMAND = "/unknown";

    protected UnknownCommand(AdminFeign adminFeign) {
        super(adminFeign);
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        ResourceBundle bundle =
            ResourceBundleUtils.getResourceBundleByUserLanguageCode(update.getMessage().getFrom().getLanguageCode());
        wrongValue(userId, bundle.getString("bot.admin.send.command.help"));
        return false;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
