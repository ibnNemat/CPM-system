package uz.devops.intern.telegram.bot.service.state;

import feign.FeignException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.ServicesService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ServicesDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ServiceGroupState extends State<ServiceFSM>{
    private ServicesRedisRepository servicesRedisRepository;
    private ServicesService servicesService;
    private GroupsService groupsService;
    private AdminFeign adminFeign;
    private AdminMenuKeys adminMenuKeys;
    private CustomerTelegramService customerTelegramService;

    public ServiceGroupState(ServiceFSM context) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.servicesService = context.getServicesService();
        this.adminFeign = context.getAdminFeign();
        this.groupsService = context.getGroupsService();
        this.adminMenuKeys = context.getAdminMenuKeys();
        this.customerTelegramService = context.getCustomerTelegramService();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasCallbackQuery()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.send.organization.is.saved.successfully"));
            return false;
        }

        CallbackQuery callback = update.getCallbackQuery();
        String callbackData = callback.getData();
        Long managerId = callback.getFrom().getId();


        if(callbackData.equals("ENOUGH")) {
            ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
            ServicesDTO servicesDTO = redisDTO.getServicesDTO();
            if(servicesDTO.getGroups().isEmpty()){
                wrongValue(managerId, bundle.getString("bot.admin.error.please.connect.to.developer"));
                return false;
            }

            servicesService.save(servicesDTO);
            removeInlineButtons(callback, servicesDTO, bundle);
            String newMessage = bundle.getString("bot.admin.main.menu");
            ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(manager.getLanguageCode());
            SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
            adminFeign.sendMessage(sendMessage);
            context.changeState(new ServiceNameState(context));
            manager.setStep(7);
            customerTelegramService.update(manager);
            return true;

        }else if(callbackData.equals("CANCEL")){

            EditMessageTextDTO editMessageTextDTO = new EditMessageTextDTO(
                String.valueOf(callback.getFrom().getId()),
                callback.getMessage().getMessageId(),
                callback.getInlineMessageId(),
                new InlineKeyboardMarkup(),
                bundle.getString("bot.admin.service.process.is.canceled"),
                "HTML",
                null
            );

            adminFeign.editMessageText(editMessageTextDTO);
            String newMessage = bundle.getString("bot.admin.main.menu");
            ReplyKeyboardMarkup markup = adminMenuKeys.createMenu(manager.getLanguageCode());
            SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
            adminFeign.sendMessage(sendMessage);
            manager.setStep(7);
            customerTelegramService.update(manager);

            context.changeState(new ServiceNameState(context));
            return true;

        }else{
            List<List<InlineKeyboardButton>> buttons = callback.getMessage().getReplyMarkup().getKeyboard();
            AtomicBoolean isChanged = new AtomicBoolean(false);
            AtomicBoolean isExistsEnoughButton = new AtomicBoolean(false);
            buttons.forEach(l -> l.forEach(k -> {
                if (k.getCallbackData().equals(callbackData) && !k.getText().contains(" ✅")) {
                    k.setText(k.getText() + " ✅");
                    isChanged.set(true);
//                    k.setCallbackData(k.getCallbackData() + ": true");
                }
                if(!isExistsEnoughButton.get()){
                    isExistsEnoughButton.set(k.getText().equals(bundle.getString("bot.admin.send.that.is.all")));
                }
            }));

            if(!isChanged.get()){
                log.warn("User pressed marked button! User id: {} | Callback data: {}", manager.getTelegramId(), callbackData);
                return false;
            }

            if(!isExistsEnoughButton.get()){
                buttons.add(List.of(
                    InlineKeyboardButton.builder().text(bundle.getString("bot.admin.send.that.is.all")).callbackData("ENOUGH").build()
//                    ,
//                    InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.cancel.process")).callbackData("CANCEL").build()
                ));

            }

            ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();

            GroupsDTO group = groupsService.findOneByTelegramId(Long.parseLong(callbackData));

            redisDTO.getServicesDTO().getGroups().add(group);
            servicesRedisRepository.save(redisDTO);

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(buttons);

            EditMessageDTO editMessageDTO = new EditMessageDTO(
                String.valueOf(callback.getFrom().getId()),
                callback.getMessage().getMessageId(),
                callback.getInlineMessageId(),
                markup);

            adminFeign.editMessageReplyMarkup(editMessageDTO);

            return false;
        }
    }

    private boolean removeInlineButtons(CallbackQuery callback, ServicesDTO service, ResourceBundle bundle){
        StringBuilder newText = new StringBuilder(bundle.getString("bot.admin.send.service.is.add.successfully"));

        int i = 1;
        for(GroupsDTO groupsDTO: service.getGroups()){
            newText.append(String.format("\n%d. %s", i++, groupsDTO.getName()));
        }

        EditMessageTextDTO editMessageTextDTO = new EditMessageTextDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            callback.getInlineMessageId(),
            new InlineKeyboardMarkup(),
            newText.toString(),
            "HTML",
            null
        );
        try {
            adminFeign.editMessageText(editMessageTextDTO);
            return true;
        }catch (FeignException e){
            log.error("Error while editing message! Manager id: {} | New text: {}", callback.getFrom().getId(), newText.toString());
            return false;
        }
    }

}
