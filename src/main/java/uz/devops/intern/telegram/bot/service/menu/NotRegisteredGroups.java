package uz.devops.intern.telegram.bot.service.menu;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NotRegisteredGroups extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.CALLBACK_QUERY;
    private final String STATE = "TELEGRAM_GROUP_TO_GROUP";
    private final Integer STEP = 6;
    private final Integer NEXT_STEP = 7;

    private final GroupsService groupsService;
    private final UserService userService;
    private final AdminMenuKeys adminMenuKeys;
    private final CustomerTelegramService customerTelegramService;
    private final TelegramGroupService telegramGroupService;
    private final OrganizationService organizationService;
    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        String[] texts = update.getCallbackQuery().getData().split(":");
        if(texts.length != 2){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            log.warn("Callback data is split but array length is not equal to 2! Manager id: {} | Callback data: {} | Array: {}",
                manager.getTelegramId(), update.getCallbackQuery().getData(), Arrays.toString(texts));
            return false;
        }
        saveAsGroup(texts[1], texts[0], manager);
        boolean result = removeInlineButtons(update.getCallbackQuery(), bundle);
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }

        basicFunction(manager, bundle);
        return true;
    }

    private GroupsDTO saveAsGroup(String telegramGroupId, String organizationId,CustomerTelegramDTO manager){
        log.info("Telegram group id: {} | Organization id: {} | Manager phone number: {}", telegramGroupId, organizationId, manager.getPhoneNumber());
        Long tgGroupId = Long.parseLong(telegramGroupId);

        ResponseDTO<User> responseDTO = getUserByCustomerTg(manager);
        if(Objects.isNull(responseDTO.getResponseData())){
            return null;
        }

        List<CustomerTelegramDTO> customerTelegrams = customerTelegramService.findByTelegramGroupTelegramId(tgGroupId);
        Set<CustomersDTO> customersSet = new HashSet<>();
        if (customerTelegrams != null) {
            customerTelegrams.stream()
                .filter(customersTelegram -> customersTelegram.getCustomer() != null && !customersTelegram.getPhoneNumber().equals(manager.getPhoneNumber()))
                .forEach(customerTelegram -> customersSet.add(customerTelegram.getCustomer()));
        }

        Optional<TelegramGroupDTO> tgGroupOptional = telegramGroupService.findOne(tgGroupId);
        if(tgGroupOptional.isEmpty()){
            log.warn("Telegram group is not found! Telegram group id: {} | Manager phone number: {} ", tgGroupId, manager.getPhoneNumber());
            return null;
        }

        TelegramGroupDTO telegramGroup = tgGroupOptional.get();
        ResponseDTO<GroupsDTO> response = groupsService.findByName(telegramGroup.getName());
        String groupName = response.getSuccess()? telegramGroup.getName() + telegramGroup.getChatId(): telegramGroup.getName();

        Optional<OrganizationDTO> organizationOptional = organizationService.findOne(Long.parseLong(organizationId));
        if(organizationOptional.isEmpty()){
            log.warn("Organization is not found! Manager phone number: {} | Organization id: {} ", manager.getPhoneNumber(), organizationId);
            return null;
        }

        WebUtils.setUserToContextHolder(responseDTO.getResponseData());
        UUID uuid = UUID.randomUUID();

        GroupsDTO group = GroupsDTO.builder()
            .name(groupName + "_" + uuid)
            .customers(customersSet)
            .organization(organizationOptional.get())
            .build();

        group = groupsService.save(group);
        log.info("Group is saved successfully!");
        return group;
    }

    private boolean removeInlineButtons(CallbackQuery callback, ResourceBundle bundle){
        EditMessageTextDTO editMessageDTO = new EditMessageTextDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            String.valueOf(callback.getInlineMessageId()),
            new InlineKeyboardMarkup(),
            bundle.getString("bot.admin.send.groups.are.add.successfully"),
            "HTML",
            null
        );

        try {
            adminFeign.editMessageText(editMessageDTO);
            return true;
        } catch (FeignException e){
            log.error("Error while editing message! User id: {} | Exception: {}", callback.getFrom().getId(), e.getMessage());
            return false;
        }
    }

    public void basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.main.menu");
        ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(manager.getLanguageCode());
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(NEXT_STEP);
        log.info("User in main menu, User id: {}", manager.getTelegramId());
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public String messageOrCallback() {
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.choose.up");
    }
}
