package uz.devops.intern.telegram.bot.service.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Service
public class ServicePeriodCountState extends State<ServiceFSM>{

//    @Autowired
    private ServicesRedisRepository servicesRedisRepository;
//    @Autowired
    private UserService userService;
    private CustomerTelegramService customerTelegramService;
//    @Autowired
    private GroupsService groupsService;
//    @Autowired
    private AdminFeign adminFeign;
    private AdminMenuKeys adminMenuKeys;

    private final ServiceGroupState serviceGroupState;

    public ServicePeriodCountState(ServiceFSM context, ServiceGroupState serviceGroupState) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.userService = context.getUserService();
        this.groupsService = context.getGroupsService();
        this.adminFeign = context.getAdminFeign();
        this.adminMenuKeys = context.getAdminMenuKeys();
        this.customerTelegramService = context.getCustomerTelegramService();
        this.serviceGroupState = serviceGroupState;
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO managerDTO) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(managerDTO.getLanguageCode());
        boolean isThereMessageInUpdate = checkUpdateInside(update, managerDTO.getTelegramId());
        if(!isThereMessageInUpdate) return false;

        boolean isTrue = isManagerPressCancelButton(update, managerDTO);
        if(isTrue){
            ReplyKeyboardMarkup menuMarkup = adminMenuKeys.createMenu(managerDTO.getLanguageCode());
            String newMessage = bundle.getString("bot.admin.service.process.is.canceled");
            SendMessage sendMessage = TelegramsUtil.sendMessage(managerDTO.getTelegramId(), newMessage, menuMarkup);
            adminFeign.sendMessage(sendMessage);

            managerDTO.setStep(7);
            customerTelegramService.update(managerDTO);
            context.changeState(new ServiceNameState(context));
            return false;
        }
        Message message = update.getMessage();
        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        if(messageText.length() > 2){
            wrongValue(managerId, bundle.getString("bot.admin.error.service.period.count.invalid"));
            log.warn("User send invalid value, Manager id: {} | Value: {}", managerId, messageText);
            return false;
        }

        Integer count = null;
        try{
            count = Integer.parseInt(messageText);
        }catch (NumberFormatException e){
            wrongValue(managerId, bundle.getString("bot.admin.error.value.contains.alphabet"));
            log.warn("There is alphabet in value that manager is send, Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }
        if(count <= 0){
            wrongValue(managerId, bundle.getString("bot.admin.error.value.should.not.below.zero"));
            log.warn("Value is below 0, Manager id: {} | Value: {}", managerId, messageText);
            return false;
        }

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setCountPeriod(count);
        servicesRedisRepository.save(redisDTO);

        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(managerDTO.getPhoneNumber());
        if(!responseDTO.getSuccess() && responseDTO.getResponseData() == null){
            log.warn("Manager is not found! Phone number: {} | Response: {}", managerDTO.getPhoneNumber(), responseDTO);
            return false;
        }

        WebUtils.setUserToContextHolder(responseDTO.getResponseData());
        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(managerId, bundle.getString("bot.admin.error.groups.are.not.attached.to.groups"));
            log.warn("Group is not found, Manager id: {} ", managerId);
            String newMessage = bundle.getString("bot.admin.main.menu");
            ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(managerDTO.getLanguageCode());
            SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
            adminFeign.sendMessage(sendMessage);
            managerDTO.setStep(7);
            customerTelegramService.update(managerDTO);
            return false;
        }
        removeReplyCancelButton(managerDTO);

        String newMessage = bundle.getString("bot.admin.send.groups.are.added.to.service");
        InlineKeyboardMarkup markup = createGroupButtons(groups, bundle);

        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        context.changeState(serviceGroupState);
        return true;
    }

    public InlineKeyboardMarkup createGroupButtons(List<GroupsDTO> groups, ResourceBundle bundle){
        List<List<InlineKeyboardButton>> label = new ArrayList<>();
        for(GroupsDTO group: groups){

            InlineKeyboardButton button = new InlineKeyboardButton(group.getName());
            button.setCallbackData(String.valueOf(group.getId()));

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            label.add(row);
        }

        InlineKeyboardButton button = new InlineKeyboardButton(bundle.getString("bot.admin.keyboard.cancel.process"));
        button.setCallbackData("CANCEL");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        label.add(row);


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(label);
        return markup;
    }

    private void removeReplyCancelButton(CustomerTelegramDTO manager){

        String messageForRemoveButton = "\uD83D\uDC65";
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), messageForRemoveButton, removeMarkup);
        adminFeign.sendMessage(sendMessage);
    }
}
