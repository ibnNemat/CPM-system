package uz.devops.intern.telegram.bot.service.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.Enumeration;
import java.util.ResourceBundle;

@Service
public class CommandsCommand extends BotCommandAbs {
    private final String COMMAND = "/commands";
    private final String KEY = "bot.admin.description.command.";

    protected CommandsCommand(AdminFeign adminFeign) {
        super(adminFeign);
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        ResponseDTO<CustomerTelegramDTO> response = customerTelegramService.findByTelegramId(userId);

        if(!response.getSuccess()){
            ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode("ru");
            wrongValue(update.getMessage().getFrom().getId(), bundle.getString("bot.admin.user.is.not.found"));
            return false;
        }

        CustomerTelegramDTO manager = response.getResponseData();
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());

        StringBuilder newMessage = new StringBuilder();
        Enumeration<String> keysEnumeration = bundle.getKeys();
        while (keysEnumeration.hasMoreElements()){
            String key = keysEnumeration.nextElement();
            if(key.contains(KEY)){
                String command = key.substring(KEY.length());
                String description = bundle.getString(key);

                command = command.replace('.', '_');
                newMessage.append(String.format(
                   "/%s - %s\n", command, description
                ));
            }
        }

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage.toString());
        adminFeign.sendMessage(sendMessage);
        log.info("Show all commands to user, User id: {} | Message: {} | Text: {}",
            manager.getTelegramId(), update.getMessage(), newMessage.toString());
        return true;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
