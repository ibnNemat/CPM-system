package uz.devops.intern.telegram.bot.service.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

@Service
public class MenuCommand extends BotCommandAbs {

    private final String COMMAND = "/menu";

    private final CustomerTelegramService customerTelegramService;


    protected MenuCommand(AdminFeign adminFeign, CustomerTelegramService customerTelegramService) {
        super(adminFeign);
        this.customerTelegramService = customerTelegramService;
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        ResponseDTO<CustomerTelegramDTO> response = customerTelegramService.findByTelegramId(userId);
        if(!response.getSuccess() && response.getResponseData() == null){
            wrongValue(update.getMessage().getFrom().getId(), "Foydalanuvchi topilmadi!");
            log.warn("User is not found! User id: {} | Response: {}", userId, response);
            return false;
        }

        CustomerTelegramDTO manager = response.getResponseData();

        if(manager.getPhoneNumber() == null){
            wrongValue(manager.getTelegramId(), "Siz /menu buyrug'ini ishlata olmaysiz, chunki siz verifikatsiyadan o'tmagansiz!");
            log.warn("User is not verified but send \"/menu\" command! Manager: {} ", manager);
            return false;
        }

        manager.setStep(4);
        customerTelegramService.update(manager);

        ReplyKeyboardMarkup markup = AdminKeyboards.createMenu();
        SendMessage sendMessage = TelegramsUtil.sendMessage(userId, "Asosiy menyu", markup);
        adminFeign.sendMessage(sendMessage);
        log.info("User is thrown to main menu, User id: {} | Update: {} ", userId, update);
        return true;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
