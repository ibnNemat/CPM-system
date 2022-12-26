package uz.devops.intern.telegram.bot.service.register;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.intern.domain.Authority;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
@RequiredArgsConstructor
public class VerifyManager extends BotStrategyAbs {

    private final String STATE = "MANAGER_VERIFICATION";
    private final Integer STEP = 2;
    private final Integer NEXT_STEP = 3;

//    @Autowired
    private final UserService userService;
    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        log.info("Verifying user by phone number, Customer: {}", manager);

        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasMessage() || (!update.getMessage().hasContact() && !update.getMessage().hasText())){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.send.only.message.or.contact"));
            log.warn("No message in update! Update: {} ", update);
            return false;
        }

        Message message = update.getMessage();
        Long userId = message.getFrom().getId();

        String phoneNumber = validatePhoneNumber(message);
        if(phoneNumber.length() != 12 && phoneNumber.length() != 13){
            log.info("{}", phoneNumber);
            wrongValue(userId, phoneNumber);
            return false;
        }

        ResponseDTO<Set<Authority>> response =
            userService.getUserAuthorityByCreatedBy(phoneNumber);

        if(!response.getSuccess()){
            log.warn("{}", response.getMessage());
            wrongValue(userId, bundle.getString("bot.admin.user.is.not.found"));
            return false;
        }

        boolean isManager = hasUserRoleManager(response.getResponseData());
        if(!isManager){
            log.warn("User hasn't \"ROLE_MANAGER\"! Customer id: {} | Customer: {}", userId, manager);
            wrongValue(userId, bundle.getString("bot.admin.user.has.not.role.manager"));
            return false;
        }

        manager.setPhoneNumber(phoneNumber);
        manager.setManager(true);
        basicFunction(manager, bundle);
        log.info("User is verified, Phone number: {} | Customer: {}",
            phoneNumber, manager);

        return true;
    }

    public void basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle){
        // Here bot should send tutorial telegraph about how to create bot.
        String newMessage = bundle.getString("bot.admin.send.new.bot.token");
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);
        SendMessage sendMessage = sendMessage(manager.getTelegramId(), newMessage, removeMarkup);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(NEXT_STEP);
    }

    private String validatePhoneNumber(Message message){
        String phoneNumber = message.hasContact()?
            message.getContact().getPhoneNumber():
            message.getText();

        if(phoneNumber.length() != 12 && phoneNumber.length() != 13){
            return "Phone number length is not equal to 12 or 13! Phone number: " + phoneNumber;
        }

        if(!phoneNumber.contains("+"))phoneNumber = "+" + phoneNumber;

        List<Integer> numbers = List.of(0,1,2,3,4,5,6,7,8,9);
        char[] elements = phoneNumber.toCharArray();
        for(Character c: elements){
            if(c == '+')continue;
            try{
                Integer.parseInt(String.valueOf(c));
            }catch (NumberFormatException e){
                return "There is alphabet in phone number! Phone number: " + phoneNumber;
            }
        }

        return phoneNumber;
    }

    private boolean hasUserRoleManager(Set<Authority> authorities){
        for(Authority auth: authorities){
            if(auth.getName().contains("MANAGER"))return true;
        }

        return false;
    }

}
