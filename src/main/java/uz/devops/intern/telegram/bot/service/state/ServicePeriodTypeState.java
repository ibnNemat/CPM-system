package uz.devops.intern.telegram.bot.service.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.keyboards.ServicePeriodsKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class ServicePeriodTypeState extends State<ServiceFSM>{

//    @Autowired
    private ServicesRedisRepository servicesRedisRepository;
//    @Autowired
    private AdminFeign adminFeign;
    private ServicePeriodsKeys servicePeriodsKeys;
    private final ServicePeriodCountState servicePeriodCountState;
    private final AdminMenuKeys adminMenuKeys;
    private CustomerTelegramService customerTelegramService;

    public ServicePeriodTypeState(ServiceFSM context, ServicePeriodCountState servicePeriodCountState) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.adminFeign = context.getAdminFeign();
        this.servicePeriodsKeys = context.getServicePeriodsKeys();
        this.servicePeriodCountState = servicePeriodCountState;
        this.adminMenuKeys = context.getAdminMenuKeys();
        this.customerTelegramService = context.getCustomerTelegramService();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        boolean isThereMessageInUpdate = checkUpdateInside(update, manager.getTelegramId());
        if(!isThereMessageInUpdate) return false;

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

        List<String> periods = servicePeriodsKeys.getTextsOfButtons(manager.getLanguageCode());
        boolean isPeriodExists = false;
        for(String p: periods){
            isPeriodExists = p.equals(messageText);
            if(isPeriodExists)break;
        }

        if(!isPeriodExists){
            wrongValue(managerId, bundle.getString("bot.admin.error.message"));
            log.warn("User send invalid type of period, Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setPeriodType(getPeriodType(bundle, messageText));
        servicesRedisRepository.save(redisDTO);

        ReplyKeyboardMarkup markup = TelegramsUtil.createCancelButton(bundle);
        String newMessage = bundle.getString("bot.admin.send.service.how.long");
        SendMessage sendMessage =
            TelegramsUtil.sendMessage(managerId, newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        context.changeState(servicePeriodCountState);
        return true;
    }

    private PeriodType getPeriodType(ResourceBundle bundle, String period){
        Map<String, PeriodType> periodsEnum = Map.of(
            bundle.getString("bot.admin.keyboards.service.period.year"), PeriodType.YEAR,
            bundle.getString("bot.admin.keyboards.service.period.month"), PeriodType.MONTH,
            bundle.getString("bot.admin.keyboards.service.period.week"), PeriodType.WEEK,
            bundle.getString("bot.admin.keyboards.service.period.day"), PeriodType.DAY,
            bundle.getString("bot.admin.keyboards.service.period.one.time"), PeriodType.ONETIME
        );

        return periodsEnum.getOrDefault(period, PeriodType.MONTH);
    }

}
