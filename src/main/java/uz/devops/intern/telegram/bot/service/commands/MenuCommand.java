package uz.devops.intern.telegram.bot.service.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ResourceBundle;

@Service
public class MenuCommand extends BotCommandAbs {

    private final String COMMAND = "/menu";

    private final CustomerTelegramService customerTelegramService;
    private final AdminMenuKeys adminMenuKeys;


    protected MenuCommand(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, AdminMenuKeys adminMenuKeys) {
        super(adminFeign);
        this.customerTelegramService = customerTelegramService;
        this.adminMenuKeys = adminMenuKeys;
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        ResponseDTO<CustomerTelegramDTO> response = customerTelegramService.findByTelegramId(userId);
        if(!response.getSuccess() && response.getResponseData() == null){
            ResourceBundle bundle = TelegramsUtil.getResourceBundleByUserLanguageCode(
                update.getMessage().getFrom().getLanguageCode()
            );

            wrongValue(update.getMessage().getFrom().getId(), bundle.getString("bot.admin.user.is.not.found"));
            log.warn("User is not found! User id: {} | Response: {}", userId, response);
            return false;
        }

        CustomerTelegramDTO manager = response.getResponseData();
        ResourceBundle bundle = TelegramsUtil.getResourceBundleByCustomerTgDTO(manager);

        if(manager.getPhoneNumber() == null){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.command.menu.user.is.not.verified"));
            log.warn("User is not verified but send \"/menu\" command! Manager: {} ", manager);
            return false;
        }

        manager.setStep(4);
        customerTelegramService.update(manager);

        ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(manager.getLanguageCode());
        SendMessage sendMessage = TelegramsUtil.sendMessage(userId, bundle.getString("bot.admin.main.menu"), markup);
        adminFeign.sendMessage(sendMessage);
        log.info("User is thrown to main menu, User id: {} | Update: {} ", userId, update);
        return true;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
