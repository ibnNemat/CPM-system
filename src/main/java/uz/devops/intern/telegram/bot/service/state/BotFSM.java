package uz.devops.intern.telegram.bot.service.state;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;

@Service
@Getter
public class BotFSM {
    private State<BotFSM> state;
    private final Logger log = LoggerFactory.getLogger(BotFSM.class);

    private final GroupsService groupsService;
    private final TelegramGroupService telegramGroupService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final AdminFeign adminFeign;
    private final CustomerTelegramService customerTelegramService;
    private final AdminMenuKeys adminMenuKeys;

    public BotFSM(GroupsService groupsService, TelegramGroupService telegramGroupService, UserService userService, OrganizationService organizationService, AdminFeign adminFeign, CustomerTelegramService customerTelegramService, AdminMenuKeys adminMenuKeys) {
        this.groupsService = groupsService;
        this.telegramGroupService = telegramGroupService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.adminFeign = adminFeign;
        this.customerTelegramService = customerTelegramService;
        this.adminMenuKeys = adminMenuKeys;
        this.state = new GroupStates(this);
    }

    public String changeState(State<BotFSM> state){
        this.state = state;
        return state.getClass().getName();
    }

    public boolean execute(Update update, CustomerTelegramDTO manager){
        log.info("State: {} | Update: {} | Manager: {}", state.getClass().getName(), update, manager);
        return state.doThis(update, manager);
    }

    public AdminFeign getFeign(){return adminFeign;}
}
