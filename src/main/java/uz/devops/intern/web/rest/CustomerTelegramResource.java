package uz.devops.intern.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.CustomerFeign;
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

    public CustomerTelegramResource(
        CustomerFeign customerFeign, CustomerTelegramService customerTelegramService) {
        this.customerFeign = customerFeign;
        this.customerTelegramService = customerTelegramService;
    }

    @PostMapping("/new-message/{botId}")
    public void sendMessage(@RequestBody Update update, @PathVariable String botId){
        log.info("[REST] Bot id: {} | Message: {}", botId,update.getMessage());

        SendMessage sendMessage = customerTelegramService.botCommands(update);
        if (sendMessage != null)
            customerFeign.sendMessage(sendMessage);
    }
}
