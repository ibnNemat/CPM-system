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
@RequestMapping("/customer-bot")
public class CustomerTelegramResource {

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramResource.class);

    private static final String ENTITY_NAME = "customerTelegram";

    private final TelegramClient telegramClient;
    private final CustomerTelegramService customerTelegramService;


    public CustomerTelegramResource(
        TelegramClient telegramClient, CustomerTelegramService customerTelegramService) {
        this.telegramClient = telegramClient;
        this.customerTelegramService = customerTelegramService;
    }

    @PostMapping
    public void sendMessage(@RequestBody Update update){
        SendMessage sendMessage = customerTelegramService.botCommands(update);
        telegramClient.sendMessage(sendMessage);
    }
}
