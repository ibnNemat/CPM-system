package uz.devops.intern.telegram.bot.service.menu;

import feign.FeignException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
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
    private final Integer NEXT_STEP = 13;
    private final List<String> SUPPORTED_TEXTS = new ArrayList<>();
    private final GroupsService groupsService;

    public CustomerPayments(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService, GroupsService groupsService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
        this.groupsService = groupsService;
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

    public Integer getNextStep(){
        return NEXT_STEP;
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

//        Long managerId = update.getMessage().getFrom().getId();
        boolean result = removeMenuButtons(manager, bundle);
        basicFunction(manager, bundle);
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

    public boolean basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle){
        ResponseDTO<User> response = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!response.getSuccess()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.user.is.not.found"));
            log.warn("{} | Manager id: {} | Response: {}", response.getMessage(), manager.getTelegramId(), response);
            return false;
        }
        setUserToContextHolder(response.getResponseData());
        List<GroupsDTO> managerGroups = groupsService.findOnlyManagerGroups();
        if(managerGroups.isEmpty()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.groups.are.not.found"));
            log.warn("Payments list is empty, Manager id: {} ", manager.getTelegramId());
            return false;
        }

        String newMessage = bundle.getString("bot.admin.send.group.payments");
        InlineKeyboardMarkup groupsMarkup = createGroupsInlineMarkup(managerGroups, bundle);
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, groupsMarkup);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(NEXT_STEP);
        customerTelegramService.update(manager);
        return true;
    }

    private boolean removeMenuButtons(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = "\uD83D\uDCB8";
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, removeMarkup);
        try {
            adminFeign.sendMessage(sendMessage);
            log.info("Menu buttons are removed! Manager id: {}", manager.getTelegramId());
            return true;
        }catch (FeignException e){
            log.error("Error while sending message for removing menu buttons! Manager id: {} | Exception: {}", manager.getTelegramId(), e.getMessage());
            return false;
        }
    }

    private InlineKeyboardMarkup createGroupsInlineMarkup(List<GroupsDTO> groups, ResourceBundle bundle){
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for(GroupsDTO group: groups){
            keyboardButtons.add(
                List.of(
                    InlineKeyboardButton.builder().text(group.getName()).callbackData(String.valueOf(group.getId())).build()
                )
            );
        }

        keyboardButtons.add(
            List.of(
                InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.back")).callbackData("BACK").build()
            )
        );

        return InlineKeyboardMarkup.builder().keyboard(keyboardButtons).build();
    }
}
