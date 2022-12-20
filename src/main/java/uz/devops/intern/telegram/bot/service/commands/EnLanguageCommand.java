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

import java.util.ResourceBundle;

@Service
public class EnLanguageCommand extends BotCommandAbs {

    private final String COMMAND = "/en";
    private final String LANGUAGE_CODE = "en";

    protected EnLanguageCommand(AdminFeign adminFeign) {
        super(adminFeign);
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        ResponseDTO<CustomerTelegramDTO> response =
            customerTelegramService.getCustomerByTelegramId(userId);

        if(!response.getSuccess()){
            log.warn("User is not found! User id: {} | Message: {} ", userId, update.getMessage());
            return false;
        }

        CustomerTelegramDTO manager = response.getResponseData();
        manager.setLanguageCode(LANGUAGE_CODE);
        customerTelegramService.update(manager);

        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        String newMessage = bundle.getString("bot.admin.user.language.successfully.changed");
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        log.info("User's language is changed successfully, User id: {} | Language code: {}", userId, LANGUAGE_CODE);
        return true;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

}
