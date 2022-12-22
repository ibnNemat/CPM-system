package uz.devops.intern.telegram.bot.service.state;


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
import uz.devops.intern.telegram.bot.keyboards.ServicePeriodsKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.time.LocalDate;
import java.util.ResourceBundle;

@Service
public class ServiceStartedTimeState extends State<ServiceFSM>{

//    @Autowired
    private final ServicesRedisRepository servicesRedisRepository;
//    @Autowired
    private AdminFeign adminFeign;
    private ServicePeriodsKeys servicePeriodsKeys;
    private final ServicePeriodTypeState servicePeriodTypeState;
    private final AdminMenuKeys adminMenuKeys;
    private CustomerTelegramService customerTelegramService;

    public ServiceStartedTimeState(ServiceFSM context, ServicePeriodTypeState servicePeriodTypeState) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.adminFeign = context.getAdminFeign();
        this.servicePeriodsKeys = context.getServicePeriodsKeys();
        this.servicePeriodTypeState = servicePeriodTypeState;
        this.adminMenuKeys = context.getAdminMenuKeys();
        this.customerTelegramService = context.getCustomerTelegramService();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        boolean isThereMessageInUpdate = checkUpdateInside(update, manager.getTelegramId());
        if(!isThereMessageInUpdate)return false;

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

        if(messageText.length() > 10){
            wrongValue(managerId, bundle.getString("bot.admin.error.start.time.is.invalid"));
            log.warn("User send invalid date! User id: {} | Message: {}", managerId, messageText);
            return false;
        }

        char[] elements = messageText.toCharArray();
        for(Character c: elements){
            if(c == '.')continue;
            try {
                Integer.parseInt(c + "");
            }catch (NumberFormatException e) {
                wrongValue(managerId, bundle.getString("bot.admin.error.value.contains.only.numbers.or.dot"));
                log.warn("There is alphabet in value that manager is send, Manager id: {} | Value: {}", managerId, messageText);
                return false;
            }
        }

        String[] partsOfText = messageText.split("\\.");
        if(partsOfText.length != 3){
            wrongValue(managerId, bundle.getString("bot.admin.error.start.time.is.invalid"));
            log.warn("Date is splited with \".\" but arrays length is not equal to 3! User id: {} | Message: {} | Arrays length: {}",
                managerId, messageText, partsOfText.length);
            return false;
        }

        int year = Integer.parseInt(partsOfText[0]);
        int month = Integer.parseInt(partsOfText[1]);
        int day = Integer.parseInt(partsOfText[2]);

        if(year < LocalDate.now().getYear()){
            wrongValue(managerId, bundle.getString("bot.admin.error.start.time.is.invalid.year"));
            log.warn("Date's year is invalid! User id: {} | Year: {}", managerId, year);
            return false;
        }
        if(!(month >= 1 && month <= 12)){
            wrongValue(managerId, bundle.getString("bot.admin.error.start.time.is.invalid.month"));
            log.warn("Date's month is invalid! User id: {} | Month: {}", managerId, month);
            return false;
        }
        if(!(day >= 1 && day <= 31)){
            wrongValue(managerId, bundle.getString("bot.admin.error.start.time.is.invalid.day"));
            log.warn("Date's day in invalid! User id: {} | Day: {}", managerId, day);
            return false;
        }

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setStartedPeriod(LocalDate.of(year, month, day));
        servicesRedisRepository.save(redisDTO);

        String newMessage = bundle.getString("bot.admin.send.service.period");
        ReplyKeyboardMarkup markup = servicePeriodsKeys.createReplyKeyboardMarkup(manager.getLanguageCode(), 2);

        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        context.changeState(servicePeriodTypeState);
        return true;
    }
}
