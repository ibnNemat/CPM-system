package uz.devops.intern.telegram.bot.service.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ResourceBundle;

@Service(value = "help-command")
public class HelpCommand extends BotCommandAbs {

    private final String COMMAND = "/help";
    private final CustomerTelegramService customerTelegramService;
    protected HelpCommand(AdminFeign adminFeign, CustomerTelegramService customerTelegramService) {
        super(adminFeign);
        this.customerTelegramService = customerTelegramService;
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        ResponseDTO<CustomerTelegramDTO> response = customerTelegramService.getCustomerByTelegramId(userId);
        if(!response.getSuccess() && response.getResponseData() == null){
            String languageCode = update.getMessage().getFrom().getLanguageCode();

            ResourceBundle resourceBundle = TelegramsUtil.getResourceBundleByUserLanguageCode(languageCode);
            wrongValue(userId, resourceBundle.getString("bot.admin.command.help"));
            log.warn("User is not found! User id: {} | Response: {}", userId, response);
            return false;
        }

        ResourceBundle bundle = TelegramsUtil.getResourceBundleByCustomerTgDTO(response.getResponseData());
        SendMessage sendMessage = TelegramsUtil.sendMessage(userId, bundle.getString("bot.admin.command.help"));
        adminFeign.sendMessage(sendMessage);
        log.info("User pressed command \"/help\", User id: {} | Message: {} | Response: {}",
            userId, update.getMessage().getText(), response);
        return true;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
