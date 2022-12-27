package uz.devops.intern.telegram.bot.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.mapper.CustomerTelegramsMapper;

import java.util.Optional;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.BOT_CHANGED_LANGUAGE;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.getResourceBundleByUserLanguageCode;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@RequiredArgsConstructor
@Service
public class TranslateCommandService {
    private final CustomerTelegramService customerTelegramService;
    private final CustomerTelegramsMapper customerTelegramMapper;
    public SendMessage changedLanguageMessage(Update update) {
        String languageCode = update.getMessage().getText();
        User telegramUser = update.getMessage().getFrom();
        Optional<CustomerTelegram> customerTelegramOptional = customerTelegramService.findCustomerByTelegramId(telegramUser.getId());
        if (customerTelegramOptional.isPresent()){
            ResourceBundle resourceBundle = getResourceBundleByUserLanguageCode(languageCode);
            CustomerTelegram customerTelegram = customerTelegramOptional.get();
            customerTelegram.setLanguageCode(languageCode);
            customerTelegramService.update(customerTelegramMapper.toDto(customerTelegram));

            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_CHANGED_LANGUAGE));
        }
        return new SendMessage();
    }
}
