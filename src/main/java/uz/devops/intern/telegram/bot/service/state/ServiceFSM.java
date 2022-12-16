package uz.devops.intern.telegram.bot.service.state;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

@Service
@Getter
public class ServiceFSM {

    private State<ServiceFSM> state;
    private final Logger log = LoggerFactory.getLogger(BotFSM.class);

    private final GroupsService groupsService;
    private final TelegramGroupService telegramGroupService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final AdminFeign adminFeign;
    private final CustomerTelegramService customerTelegramService;

    private final ServicesRedisRepository servicesRedisRepository;
    private final ServicesService servicesService;

    public ServiceFSM(GroupsService groupsService, TelegramGroupService telegramGroupService, UserService userService, OrganizationService organizationService, AdminFeign adminFeign, CustomerTelegramService customerTelegramService, ServicesRedisRepository servicesRedisRepository, ServicesService servicesService) {
        this.groupsService = groupsService;
        this.telegramGroupService = telegramGroupService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.adminFeign = adminFeign;
        this.customerTelegramService = customerTelegramService;
        this.servicesRedisRepository = servicesRedisRepository;
        this.servicesService = servicesService;
        this.state = new ServiceNameState(this);
    }

    public String changeState(State<ServiceFSM> state){
        this.state = state;
        return state.getClass().getName();
    }

    public boolean execute(Update update, CustomerTelegramDTO manager){
        log.info("State: {} | Update: {} | Manager: {}", state.getClass().getName(), update, manager);
        return state.doThis(update, manager);
    }

    public AdminFeign getAdminFeignObj(){ return adminFeign; }
}
