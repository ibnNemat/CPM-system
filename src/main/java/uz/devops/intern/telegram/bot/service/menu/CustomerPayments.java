package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class CustomerPayments extends ManagerMenuAbs{
    private final String SUPPORTED_TEXT = "\uD83D\uDCB8 Bolalar qarzdorliklari";

    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();
    @Autowired
    private PaymentService paymentService;

    public CustomerPayments(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
    }

    @PostConstruct
    public void fillSupportedTextsList(){
        List<String> languages = KeyboardUtil.availableLanguages();
        Map<String, String> languageMap = KeyboardUtil.getLanguages();
        for(String lang: languages){
            String languageCode = languageMap.get(lang);
            ResourceBundle bundle =
                ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);
            SUPPORTED_TEXTS.add(bundle.getString("bot.admin.keyboards.menu.show.payments"));
        }
    }


    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasMessage() && !update.getMessage().hasText()){
            Long userId = update.hasCallbackQuery()? update.getCallbackQuery().getFrom().getId() : null;
//            messageHasNotText(userId, update);
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.send.only.message.or.contact"));
            log.warn("User didn't send text! User id: {} | Update: {}", userId, update);
            return false;
        }

        Long managerId = update.getMessage().getFrom().getId();

        ResponseDTO<User> response = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!response.getSuccess()){
            wrongValue(managerId, bundle.getString("bot.admin.user.is.not.found"));
            log.warn("{} | Manager id: {} | Response: {}", response.getMessage(), managerId, response);
            return false;
        }
        setUserToContextHolder(response.getResponseData());

        List<PaymentDTO> payments = paymentService.getAllPaymentsCreatedByGroupManager();
        if(payments.isEmpty()){
            wrongValue(managerId, bundle.getString("bot.admin.manager.has.not.debt"));
            log.warn("Payments list is empty, Manager id: {} ", managerId);
            return false;
        }

        StringBuilder newMessage = new StringBuilder();
        for(PaymentDTO payment: payments){
            newMessage.append(payment + "\n\n");
        }

        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage.toString());
        adminFeign.sendMessage(sendMessage);
        return true;
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
