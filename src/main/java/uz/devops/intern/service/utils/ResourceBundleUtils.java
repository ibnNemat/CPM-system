package uz.devops.intern.service.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtils {
    private static final String RESOURCE_BUNDLE_NAME = "message";
    public static ResourceBundle getResourceBundleUsingTelegramUser(User telegramUser){
        Locale locale = new Locale(telegramUser.getLanguageCode());
        LocaleContextHolder.setLocale(locale);

        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,LocaleContextHolder.getLocale());
    }

    public static ResourceBundle getResourceBundleUsingCustomerTelegram(CustomerTelegram customerTelegram){
        Locale locale = new Locale(customerTelegram.getLanguageCode());
        LocaleContextHolder.setLocale(locale);
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,LocaleContextHolder.getLocale());
    }
}
