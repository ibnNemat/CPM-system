package uz.devops.intern.telegram.bot.service.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ServicesDTO;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

@Service
public class ServiceNameState extends State<ServiceFSM> {

//    private final Logger log = LoggerFactory.getLogger(ServiceNameState.class);

    //    @Autowired
    private AdminFeign adminFeign;

    //    @Autowired
    private ServicesRedisRepository servicesRedisRepository;

    public ServiceNameState(ServiceFSM context) {
        super(context, context.getAdminFeign());
        this.adminFeign = context.getAdminFeign();
        this.servicesRedisRepository = context.getServicesRedisRepository();
    }

    @Override
    boolean doThis(Update update, CustomerTelegramDTO manager) {
        boolean isThereMessageInUpdate = checkUpdateInside(update, manager.getTelegramId());
        if (!isThereMessageInUpdate) return isThereMessageInUpdate;

        Message message = update.getMessage();
        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        String newMessage = "Iltimos xizmat narxini kiriting.";
        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage);

        adminFeign.sendMessage(sendMessage);

        ServicesDTO dto = new ServicesDTO();
        dto.setName(messageText);

        ServicesRedisDTO redisDTO = new ServicesRedisDTO(managerId, dto);
        servicesRedisRepository.save(redisDTO);
        context.changeState(new ServicePriceState(context));
        return true;
    }


}
