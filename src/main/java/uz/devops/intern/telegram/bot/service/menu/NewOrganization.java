package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.OrganizationService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.ResourceBundle;


@Service
public class NewOrganization extends BotStrategyAbs {

    private final String STATE = "NEW_ORGANIZATION_NAME";
    private final Integer STEP = 5;

    private final UserService userService;
    private final OrganizationService organizationService;

    public NewOrganization(UserService userService, OrganizationService organizationService) {
        this.userService = userService;
        this.organizationService = organizationService;
    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        ResponseDTO<User> response = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!response.getSuccess() && response.getResponseData() == null){
            log.warn("User is not found! Manager id: {} | Manager phone number: {} | Response: {}",
                manager.getTelegramId(), manager.getPhoneNumber(), response);
            return false;
        }
        if(!update.hasMessage() || !update.getMessage().hasText()){
            messageHasNotText(manager.getTelegramId(), update);
            return false;
        }

        Message message = update.getMessage();
        WebUtils.setUserToContextHolder(response.getResponseData());
        String messageText = message.getText();

        OrganizationDTO isOrganizationExists =
            organizationService.getOrganizationByName(messageText);

        if(isOrganizationExists != null){
            wrongValue(message.getFrom().getId(), bundle.getString("bot.admin.error.organization.is.already.exists"));
            log.warn("Organization is already exists, Organization: {}", isOrganizationExists);
            return false;
        }

        OrganizationDTO organization = new OrganizationDTO();
        organization.setName(messageText);
        organization = organizationService.save(organization);


        String newMessage = bundle.getString("bot.admin.send.organization.is.saved.successfully");
        ReplyKeyboardMarkup markup = AdminKeyboards.createMenu();
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        log.info("Manager is added new organization, Organization: {} | Manager id: {}: {} | Message text: {}",
            organization, manager.getTelegramId(), messageText);
        manager.setStep(4);
        return true;
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }
}
