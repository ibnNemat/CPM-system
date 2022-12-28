package uz.devops.intern.telegram.bot.service.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
public class ServiceTotalCountState extends State<ServiceFSM>{


    @Autowired
    private ServicesRedisRepository servicesRedisRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerTelegramService customerTelegramService;
    @Autowired
    private GroupsService groupsService;
    @Autowired
    private AdminFeign adminFeign;
    @Autowired
    private AdminMenuKeys adminMenuKeys;
    @Autowired
    private ServiceGroupState serviceGroupState;


    public ServiceTotalCountState(ServiceFSM context, AdminFeign adminFeign) {
        super(context, adminFeign);
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());

        if(!update.hasMessage() || !update.getMessage().hasText()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.only.message"));
            log.warn("User didn't send text! Manager id: {} | Update: {} ", manager.getTelegramId(), update);
            return false;
        }

        boolean didUserPressedCancelButton = isManagerPressCancelButton(update, manager);
        if(didUserPressedCancelButton){
            ReplyKeyboardMarkup menuMarkup = adminMenuKeys.createMenu(manager.getLanguageCode());
            String newMessage = bundle.getString("bot.admin.service.process.is.canceled");
            SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, menuMarkup);
            adminFeign.sendMessage(sendMessage);

            manager.setStep(7);
            customerTelegramService.update(manager);
            context.changeState(new ServiceNameState(context));
            return false;
        }

        Integer totalCount = null;
        try{
            totalCount = Integer.parseInt(update.getMessage().getText());
        }catch (NumberFormatException e){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.value.contains.alphabet"));
            log.warn("Manager send inavlid value! Manager id: {} | Value: {} ", manager.getTelegramId(), update.getMessage().getText());
            return false;
        }

        ServicesRedisDTO serviceRedis = servicesRedisRepository.findById(manager.getTelegramId()).get();
        serviceRedis.getServicesDTO().setTotalCountService(totalCount);
        servicesRedisRepository.save(serviceRedis);

        basicFuntion(manager, bundle);
        return true;
    }

    public boolean basicFuntion(CustomerTelegramDTO manager, ResourceBundle bundle){
        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!responseDTO.getSuccess() && responseDTO.getResponseData() == null){
            log.warn("Manager is not found! Phone number: {} | Response: {}", manager.getPhoneNumber(), responseDTO);
            return false;
        }

        WebUtils.setUserToContextHolder(responseDTO.getResponseData());
        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.groups.are.not.attached.to.groups"));
            log.warn("Group is not found, Manager id: {} ", manager.getTelegramId());
            String newMessage = bundle.getString("bot.admin.main.menu");
            ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(manager.getLanguageCode());
            SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
            adminFeign.sendMessage(sendMessage);
            manager.setStep(7);
            customerTelegramService.update(manager);
            return false;
        }
        removeReplyCancelButton(manager);

        String newMessage = bundle.getString("bot.admin.send.groups.are.added.to.service");
        InlineKeyboardMarkup markup = createGroupButtons(groups, bundle);

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        context.changeState(serviceGroupState);
        return true;
    }

    public InlineKeyboardMarkup createGroupButtons(List<GroupsDTO> groups, ResourceBundle bundle){
        List<List<InlineKeyboardButton>> label = new ArrayList<>();
        for(GroupsDTO group: groups){

            InlineKeyboardButton button = new InlineKeyboardButton(group.getName().split(" ")[0]);
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
