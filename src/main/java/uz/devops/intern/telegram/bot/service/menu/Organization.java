package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
public class Organization extends ManagerMenuAbs{
    private final String SUPPORTED_TEXT = "\uD83C\uDFE2 Yangi tashkilot";

    public Organization(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
    }

    public boolean todo(Update update, CustomerTelegramDTO manager){
        String newMessage = "Iltimos tashkilot nomini kiriting";
        SendMessage sendMessage = sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        log.info("Admin is creating new organization, Manager id: {} | Message text: {} | Update: {}",
            manager.getTelegramId(), update.getMessage().getText(), update);
        manager.setStep(5);
        return true;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }
}
