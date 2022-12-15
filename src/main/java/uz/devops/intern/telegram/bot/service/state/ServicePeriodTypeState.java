package uz.devops.intern.telegram.bot.service.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.List;

@Service
public class ServicePeriodTypeState extends State<ServiceFSM>{

//    @Autowired
    private ServicesRedisRepository servicesRedisRepository;
//    @Autowired
    private AdminFeign adminFeign;

    public ServicePeriodTypeState(ServiceFSM context) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.adminFeign = context.getAdminFeign();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        boolean isThereMessageInUpdate = checkUpdateInside(update, manager.getTelegramId());
        if(!isThereMessageInUpdate) return false;

        Message message = update.getMessage();
        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        List<String> periods = AdminKeyboards.getPeriods();
        boolean isPeriodExists = false;
        for(String p: periods){
            isPeriodExists = p.equals(messageText);
            if(isPeriodExists)break;
        }

        if(!isPeriodExists){
            wrongValue(managerId, "Iltimos ko'rsatilgan qiymatlardan birini tanlang");
            log.warn("User send invalid type of period, Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setPeriodType(getPeriodType(messageText));
        servicesRedisRepository.save(redisDTO);

        String newMessage = "Xizmat qancha davom etadi, son bilan kiriting. Misol: 4";
        SendMessage sendMessage =
            TelegramsUtil.sendMessage(managerId, newMessage, new ReplyKeyboardRemove(true));
        adminFeign.sendMessage(sendMessage);
        context.changeState(new ServicePeriodCountState(context));
        return true;
    }

    private PeriodType getPeriodType(String period){
        if(period.equals("Yillik")) return PeriodType.YEAR;
        if(period.equals("Oylik")) return PeriodType.MONTH;
        if(period.equals("Haftalik")) return PeriodType.WEEK;
        if(period.equals("Kunlik")) return PeriodType.DAY;
        if(period.equals("Bir martalik")) return PeriodType.ONETIME;
        return PeriodType.MONTH;
    }

}
