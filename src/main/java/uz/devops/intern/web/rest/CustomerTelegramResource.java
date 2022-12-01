package uz.devops.intern.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.CustomerTelegramService;


/**
 * REST controller for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
@RestController
@RequestMapping("/customer-bot")
public class CustomerTelegramResource {

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramResource.class);

    private static final String ENTITY_NAME = "customerTelegram";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerTelegramService customerTelegramService;

    private final CustomerTelegramRepository customerTelegramRepository;

    public CustomerTelegramResource(
        CustomerTelegramService customerTelegramService,
        CustomerTelegramRepository customerTelegramRepository
    ) {
        this.customerTelegramService = customerTelegramService;
        this.customerTelegramRepository = customerTelegramRepository;
    }

    @PostMapping
    public void sendMessage(@RequestBody Update update){
        customerTelegramService.botCommands(update);
    }

}
