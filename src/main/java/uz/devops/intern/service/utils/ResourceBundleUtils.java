package uz.devops.intern.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtils {
    private static final Logger log = LoggerFactory.getLogger(ResourceBundleUtils.class);
    private static final String RESOURCE_BUNDLE_NAME = "message";
    public static ResourceBundle getResourceBundleUsingTelegramUser(User telegramUser){
        Locale locale = new Locale(telegramUser.getLanguageCode());
        LocaleContextHolder.setLocale(locale);

        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,LocaleContextHolder.getLocale());
    }

    public static ResourceBundle getResourceBundleUsingLanguageCode(String languageCode){
        log.info("Language code: {} ", languageCode);
        Locale locale = new Locale(languageCode);
        LocaleContextHolder.setLocale(locale);
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, LocaleContextHolder.getLocale());
    }

    public static ResourceBundle getResourceBundleUsingCustomerTelegram(CustomerTelegram customerTelegram){
        Locale locale = new Locale(customerTelegram.getLanguageCode());
        LocaleContextHolder.setLocale(locale);
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,LocaleContextHolder.getLocale());
    }

    public static ResourceBundle getResourceBundleByCustomerTgDTO(CustomerTelegramDTO customerTelegramDTO){
        Locale locale = new Locale(customerTelegramDTO.getLanguageCode());
        LocaleContextHolder.setLocale(locale);
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, LocaleContextHolder.getLocale());
    }

    public static ResourceBundle getResourceBundleByUserLanguageCode(String languageCode){
//        log.info("Language code: {} ", languageCode);
        languageCode = languageCode.equals("ru_RU") || languageCode.equals("ru")? "ru":
            languageCode.equals("uz_UZ") || languageCode.equals("uz")? "uz": "en";
        Locale locale = new Locale(languageCode);
        LocaleContextHolder.setLocale(locale);
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,LocaleContextHolder.getLocale());
    }
}
