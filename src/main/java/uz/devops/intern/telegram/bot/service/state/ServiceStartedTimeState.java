package uz.devops.intern.telegram.bot.service.state;


import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ServiceStartedTimeState extends State<ServiceFSM>{

//    @Autowired
    private ServicesRedisRepository servicesRedisRepository;
//    @Autowired
    private AdminFeign adminFeign;

    public ServiceStartedTimeState(ServiceFSM context) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.adminFeign = context.getAdminFeign();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        boolean isThereMessageInUpdate = checkUpdateInside(update, manager.getTelegramId());
        if(!isThereMessageInUpdate)return false;

        Message message = update.getMessage();

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        if(messageText.length() != 10)return false;

        boolean isTextExists = false;
        List<Integer> numbers = List.of(1,2,3,4,5,6,7,8,9,0);

        char[] elements = messageText.toCharArray();
        for(Character c: elements){
            if(c == '.')continue;
            boolean isNumber = numbers.contains(Integer.parseInt(c + ""));
            if(!isNumber){
                isTextExists = true;
                break;
            }
        }

        if(isTextExists) {
            wrongValue(managerId, "Faqat sonlar va \".\" qatnashishi mumkun!");
            log.warn("There is alphabet in value that manager is send, Manager id: {} | Value: {}", managerId, messageText);
            return false;
        }
        String[] partsOfText = messageText.split("\\.");

        int year = Integer.parseInt(partsOfText[0]);
        int month = Integer.parseInt(partsOfText[1]);
        int day = Integer.parseInt(partsOfText[2]);

        if(partsOfText[0].length() != 4 || year < new Date().getYear())return false;
        if(partsOfText[1].length() != 2 || !(month >= 1 && month <= 12))return false;
        if(partsOfText[2].length() != 2 || !(day >= 1 && day <= 31)) return false;

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setStartedPeriod(LocalDate.of(year, month, day));
        servicesRedisRepository.save(redisDTO);

        String newMessage = "To'lov qilinish periyudini tanlang";
        ReplyKeyboardMarkup markup = AdminKeyboards.getPeriodTypeButtons();
        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        context.changeState(new ServicePeriodTypeState(context));
        return true;
    }
}
