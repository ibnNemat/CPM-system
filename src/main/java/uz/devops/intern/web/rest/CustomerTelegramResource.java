package uz.devops.intern.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.TelegramClient;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.CustomerTelegramService;
/**
 * REST controller for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
@RestController
@RequestMapping("/api")
public class CustomerTelegramResource {

//    @Value("${jhipster.clientApp.name}")
//    private String applicationName;

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramResource.class);

//    private static final String ENTITY_NAME = "customerTelegram";
    private final TelegramClient telegramClient;
    private final CustomerTelegramService customerTelegramService;

    public CustomerTelegramResource(
        TelegramClient telegramClient, CustomerTelegramService customerTelegramService) {
        this.telegramClient = telegramClient;
        this.customerTelegramService = customerTelegramService;
    }

    @PostMapping("/new-message/{botId}")
    public void sendMessage(@RequestBody Update update, @PathVariable String botId){
        log.info("[REST] Bot id: {} | Message: {}", botId,update.getMessage());
        System.out.println("============ Men customerman: ==================\n" +
            "Message: " + update.getMessage());
        System.out.println("User: " + update.getMessage().getFrom());
        System.out.println("Message ChatID: " +update.getMessage().getChatId());
        System.out.println("Message chat" + update.getMessage().getChat().toString());
        System.out.println("================================================");

        SendMessage sendMessage = customerTelegramService.botCommands(update);
        if (sendMessage != null)
            telegramClient.sendMessage(sendMessage);
    }
}
