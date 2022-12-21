package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.List;
import java.util.ResourceBundle;

@Service
public class UnknownMenuButton extends ManagerMenuAbs{
    private final String SUPPORTED_TEXT = "Unknown";

    private final List<String> SUPPORTED_TEXTS = List.of("UNKNOWN");

    public UnknownMenuButton(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
    }

    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        String newMessage = bundle.getString("bot.admin.error.message");
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value! User id: {} | Update: {}", manager.getTelegramId(), update);
        return false;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }

    @Override
    public List<String> getSupportedTexts() {
        return SUPPORTED_TEXTS;
    }
}
