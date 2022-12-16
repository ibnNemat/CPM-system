package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
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
public class AllGroups extends ManagerMenuAbs{
    private final String SUPPORTED_TEXT = "\uD83D\uDC40 Guruhlarni ko'rish";

    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();

    @Autowired
    private GroupsService groupsService;

    public AllGroups(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
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
            SUPPORTED_TEXTS.add(bundle.getString("bot.admin.keyboards.menu.add.group"));
        }
    }


    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!responseDTO.getSuccess()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.user.is.not.found"));
            log.warn("{} | Manager id: {} | Response: {}", responseDTO.getMessage(), manager.getTelegramId(), responseDTO);
            return false;
        }

        setUserToContextHolder(responseDTO.getResponseData());

        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.groups.are.not.attached.to.groups"));
            log.warn("Manager has not any groups! Manager: {}", manager);
            return false;
        }

        StringBuilder newMessage = new StringBuilder();
        for(GroupsDTO group: groups){
            newMessage.append(String.format(
                "%s <b>%s\n</b>" +
                    "%s <b>%s\n</b>" +
                    "%s <b>%d</b>\n\n",
                bundle.getString("bot.admin.group.name"),
                bundle.getString("bot.admin.organization.name"),
                bundle.getString("bot.admin.customers.count"),
                group.getName(), group.getOrganization().getName(), group.getCustomers().size()
            ));
        }

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage.toString());
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }

    @Override
    public List<String> getSupportedTexts(){ return SUPPORTED_TEXTS; }
}
