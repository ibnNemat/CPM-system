package uz.devops.intern.telegram.bot.service.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.time.LocalDate;
import java.util.ResourceBundle;

@Service
public class ServicePriceState extends State<ServiceFSM>{

//    @Autowired
    private ServicesRedisRepository servicesRedisRepository;
    @Autowired
    private ServiceStartedTimeState serviceStartedTimeState;
    private AdminFeign adminFeign;
    private AdminMenuKeys adminMenuKeys;
    private CustomerTelegramService customerTelegramService;
    public ServicePriceState(ServiceFSM context) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.adminFeign = context.getAdminFeign();
        this.adminMenuKeys = context.getAdminMenuKeys();
        this.customerTelegramService = context.getCustomerTelegramService();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        boolean firstCheckPoint = checkUpdateInside(update, manager.getTelegramId());
        if(!firstCheckPoint)return firstCheckPoint;

        boolean isTrue = isManagerPressCancelButton(update, manager);
        if(isTrue){
            ReplyKeyboardMarkup menuMarkup = adminMenuKeys.createMenu(manager.getLanguageCode());
            String newMessage = bundle.getString("bot.admin.service.process.is.canceled");
            SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, menuMarkup);
            adminFeign.sendMessage(sendMessage);
            manager.setStep(7);
            customerTelegramService.update(manager);
            context.changeState(new ServiceNameState(context));
            return false;
        }

        Message message = update.getMessage();

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        Integer integerPrice = null;
        if(messageText.length() <= 9){
            try{
                integerPrice = Integer.parseInt(messageText);
            }catch (NumberFormatException e){
                wrongValue(message.getFrom().getId(), bundle.getString("bot.admin.error.value.contains.alphabet"));
                log.warn("Throws NumberFormatException when user send cost of service, Manager id: {} | Value: {}",
                    managerId, messageText);
                return false;
            }
        }else{
            wrongValue(message.getFrom().getId(), bundle.getString("bot.admin.error.value.contains.alphabet"));
            log.warn("Cost of service is out of range, Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }

        if(integerPrice <= 10000){
            wrongValue(managerId, bundle.getString("bot.admin.send.service.min.price"));
            log.warn("Cost of service is below 10 000, Manager id: {} | Cost: {}", managerId, messageText);
            return false;
        }
        String newMessage = String.format(bundle.getString("bot.admin.send.service.started.time"), LocalDate.now().getYear(), LocalDate.now().getMonth().getValue(), LocalDate.now().getDayOfMonth());
//        ReplyKeyboardMarkup markup = TelegramsUtil.createCancelButton(bundle);
        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage);
        adminFeign.sendMessage(sendMessage);

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setPrice(Double.parseDouble(String.valueOf(integerPrice)));
        servicesRedisRepository.save(redisDTO);
        context.changeState(serviceStartedTimeState);
        return true;
    }
}
