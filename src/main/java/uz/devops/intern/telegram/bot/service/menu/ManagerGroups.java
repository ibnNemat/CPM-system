package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class ManagerGroups extends ManagerMenuAbs{

    private final String SUPPORTED_TEXT = "\uD83D\uDC65 Guruh qo'shish";

    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();
    private final GroupsService groupsService;
    private final NewOrganization newOrganization;

    public ManagerGroups(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService, GroupsService groupsService, NewOrganization newOrganization) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
        this.groupsService = groupsService;
        this.newOrganization = newOrganization;
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
        if (!responseDTO.getSuccess() && responseDTO.getResponseData() == null) {
            log.warn("Manager is not found from jhi_user! Manager: {} | Response: {}", manager, responseDTO);
            return false;
        }

        newOrganization.basicFunction(manager, bundle);
        customerTelegramService.update(manager);
        return false;
//        setUserToContextHolder(responseDTO.getResponseData());
//
//        ResponseDTO<List<TelegramGroupDTO>> response = telegramGroupService.getTelegramGroupsByCustomer(manager.getId());
//        if(!response.getSuccess()){
//            wrongValue(manager.getTelegramId(), response.getMessage());
//            log.warn("{}, Response: {}", response.getMessage(), response);
//            return false;
//        }
//
//        if (response.getResponseData().isEmpty()) {
//            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.telegram.group.non"));
//            log.warn("Has no telegram group, Manager id: {} ", manager.getTelegramId());
//            return false;
//        }
//        List<TelegramGroupDTO> telegramGroups = response.getResponseData();
//        log.info("Manager telegram groups, Telegram groups count: {} | Telegram groups: {}", telegramGroups.size(), telegramGroups);
//
//        for (TelegramGroupDTO dto : telegramGroups) {
//            String newMessage = createGroupText(dto, bundle, manager.getTelegramId());
//            InlineKeyboardMarkup markup = createGroupButtons(dto.getChatId(), bundle);
//            SendMessage sendMessage =
//                TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
//            adminFeign.sendMessage(sendMessage);
//        }
//        manager.setStep(6);
//        return true;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }

    @Override
    public List<String> getSupportedTexts() {
        return SUPPORTED_TEXTS;
    }

    private String createGroupText(TelegramGroupDTO groupDTO, ResourceBundle bundle, Long managerId){
        ResponseDTO<List<CustomerTelegramDTO>> response =
            customerTelegramService.getCustomerTgByChatId(groupDTO.getId());

        StringBuilder text = new StringBuilder(String.format(
            "%s %s\n%s %d\n=========================\n",
            bundle.getString("bot.admin.group.name"),
            groupDTO.getName(),
            bundle.getString("bot.admin.customers.count"),
            response.getResponseData().size()
        ));

        List<CustomerTelegramDTO> customerTelegrams = response.getResponseData();
        int index = 1;
        for(CustomerTelegramDTO customer: customerTelegrams){
            if(customer.getTelegramId().equals(managerId))continue;
            text.append(String.format(
                "%d. %s\n", index++, customer.getFirstname()
            ));
        }
        return text.toString();
    }

    private InlineKeyboardMarkup createGroupButtons(Long chatId, ResourceBundle bundle){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton(bundle.getString("bot.admin.send.attach.this.group"));
        button.setCallbackData(String.valueOf(chatId));

        buttons.add(
            List.of(button)
        );

        return new InlineKeyboardMarkup(buttons);
    }

}
