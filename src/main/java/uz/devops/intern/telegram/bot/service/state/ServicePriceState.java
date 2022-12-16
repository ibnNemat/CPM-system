package uz.devops.intern.telegram.bot.service.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

@Service
public class ServicePriceState extends State<ServiceFSM>{

//    @Autowired
    private ServicesRedisRepository servicesRedisRepository;
//    @Autowired
    private AdminFeign adminFeign;
    public ServicePriceState(ServiceFSM context) {
        super(context, context.getAdminFeign());
        this.servicesRedisRepository = context.getServicesRedisRepository();
        this.adminFeign = context.getAdminFeign();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        boolean firstCheckPoint = checkUpdateInside(update, manager.getTelegramId());
        if(!firstCheckPoint)return firstCheckPoint;

        Message message = update.getMessage();

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        Integer integerPrice = null;
        if(messageText.length() <= 7){
            try{
                integerPrice = Integer.parseInt(messageText);
            }catch (NumberFormatException e){
                wrongValue(message.getFrom().getId(), "Qiymat noto'g'ri!");
                log.warn("Throws NumberFormatException when user send cost of service, Manager id: {} | Value: {}",
                    managerId, messageText);
                return false;
            }
        }else{
            wrongValue(message.getFrom().getId(), "Qiymat noto'g'ri!");
            log.warn("Cost of service is out of range, Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }

        if(integerPrice <= 10000){
            wrongValue(managerId, "Xizmatlarning eng miqdordagi summasi 10 000");
            log.warn("Cost of service is below 10 000, Manager id: {} | Cost: {}", managerId, messageText);
            return false;
        }

        String newMessage = "Iltimos xizmatning boshlanadigan kunini kiriting.\n\nFormat: yil.oy.kun Misol: 2022.01.01";
        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage);
        adminFeign.sendMessage(sendMessage);

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setPrice(Double.parseDouble(String.valueOf(integerPrice)));
        servicesRedisRepository.save(redisDTO);
        context.changeState(new ServiceStartedTimeState(context));
        return true;
    }
}
