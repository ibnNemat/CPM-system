package uz.devops.intern.telegram.bot.service.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

@Service
public class GroupOrganizationState extends State<BotFSM>{

//    private final Logger log = LoggerFactory.getLogger(GroupOrganizationState.class);

    private TelegramGroupService telegramGroupService;
    private OrganizationService organizationService;
    private UserService userService;
    private CustomerTelegramService customerTelegramService;
    private GroupsService groupsService;
    private AdminFeign adminFeign;
    private AdminMenuKeys adminMenuKeys;

    public GroupOrganizationState(BotFSM context) {
        super(context, context.getAdminFeign());
        this.telegramGroupService = context.getTelegramGroupService();;
        this.organizationService = context.getOrganizationService();
        this.userService = context.getUserService();
        this.customerTelegramService = context.getCustomerTelegramService();
        this.groupsService = context.getGroupsService();
        this.adminFeign = context.getAdminFeign();
        this.adminMenuKeys = context.getAdminMenuKeys();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasCallbackQuery()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.message"));
            log.warn("Update: {}", update);
            return false;
        }

        String callbackData = update.getCallbackQuery().getData();
        String[] data = callbackData.split(":");
        Long telegramGroupId = Long.parseLong(data[0]);
        Long organizationId = Long.parseLong(data[1]);

        TelegramGroupDTO telegramGroup = telegramGroupService.findOneByChatId(telegramGroupId);
        OrganizationDTO organization = organizationService.findOne(organizationId).get();

        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!responseDTO.getSuccess()){
            log.warn("{} | Response: {}", responseDTO.getMessage(), responseDTO);
            return false;
        }

        WebUtils.setUserToContextHolder(responseDTO.getResponseData());

        GroupsDTO groupsDTO = new GroupsDTO();
        groupsDTO.setName(telegramGroup.getName());
        groupsDTO.setOrganization(organization);

        List<CustomerTelegramDTO> customerTelegramDTOS = customerTelegramService.findByTelegramGroupTelegramId(manager.getTelegramId());
        if (customerTelegramDTOS != null) {
            Set<CustomersDTO> customersSet = new HashSet<>();
            for (CustomerTelegramDTO customerTelegramDTO : customerTelegramDTOS) {
                CustomersDTO customersDTO = new CustomersDTO();
                customersDTO.setId(customerTelegramDTO.getCustomer().getId());
                customersSet.add(customersDTO);
            }
            groupsDTO.setCustomers(customersSet);
        }
        groupsService.save(groupsDTO);

        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();

        EditMessageTextDTO dto = createEditMessageText(update.getCallbackQuery(), inlineMarkup, "");
        adminFeign.editMessageText(dto);

        String newMessage = bundle.getString("bot.admin.main.menu");
        ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(manager.getLanguageCode());
        SendMessage sendMessage =
            TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        context.changeState(new GroupStates(context));
        manager.setStep(4);
        customerTelegramService.update(manager);
        return true;
    }
//
//    public void wrongValue(Long chatId, String message){
//        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
//        Update update = adminFeign.sendMessage(sendMessage);
//        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
//            chatId, message, update);
//    }

    private EditMessageTextDTO createEditMessageText(CallbackQuery callback, InlineKeyboardMarkup markup, String text){

        EditMessageTextDTO dto = new EditMessageTextDTO();
        dto.setMessageId(callback.getMessage().getMessageId());
        dto.setInlineMessageId(callback.getInlineMessageId());
        dto.setChatId(String.valueOf(callback.getFrom().getId()));
        dto.setReplyMarkup(markup);
        dto.setText(callback.getMessage().getText() + text);
        dto.setParseMode("HTML");

        return dto;
    }
}
