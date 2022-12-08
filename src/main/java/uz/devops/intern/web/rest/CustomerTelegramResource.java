package uz.devops.intern.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.service.AdminTgService;
import uz.devops.intern.service.CustomerTelegramService;
/**
 * REST controller for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
@RestController
@RequestMapping("/api")
public class CustomerTelegramResource {

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramResource.class);

    private final CustomerFeign customerFeign;
    private final CustomerTelegramService customerTelegramService;

    private final AdminTgService adminService;

    public CustomerTelegramResource(
        CustomerFeign customerFeign, CustomerTelegramService customerTelegramService, AdminTgService adminService) {
        this.customerFeign = customerFeign;
        this.customerTelegramService = customerTelegramService;
        this.adminService = adminService;
    }

    @PostMapping("/new-message/{botId}")
    public void sendMessage(@RequestBody Update update, @PathVariable String botId){
        log.info("[REST] Bot id: {} | Message: {}", botId,update.getMessage());
        if(update.hasMessage() &&
            update.getMessage().getText() != null &&
            update.getMessage().getFrom().getId().equals(update.getMessage().getChatId())
        ) {
            SendMessage sendMessage = customerTelegramService.botCommands(update);
            if (sendMessage != null)
                customerFeign.sendMessage(sendMessage);
        }

        if(update.getMyChatMember() != null) {
            if (update.getMyChatMember().getNewChatMember() != null) {
                // Shu joyda botni gruppaga add qiganini bilsa bo'ladi
                adminService.checkIsBotInGroup(update.getMyChatMember().getNewChatMember(), update.getMyChatMember().getChat(),botId);
            }
        }
//        else if(update.getMyChatMember() != null &&
//                update.getMyChatMember().getNewChatMember().getUser().getIsBot()){
//                adminService.checkIsBotAdmin(update.getMyChatMember());
//
//        }
    }
}
