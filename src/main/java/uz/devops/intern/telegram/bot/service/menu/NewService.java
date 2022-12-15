package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

@Service
public class NewService extends ManagerMenuAbs{

    private final String SUPPORTED_TEXT = "\uD83E\uDEC2 Xizmat qo'shish";


    public NewService(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
    }

    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);

        String newMessage = "Xizmat nomini kiriting!";
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, removeMarkup);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(7);
        return true;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }
}
