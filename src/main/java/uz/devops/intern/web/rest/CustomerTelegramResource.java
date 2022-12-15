package uz.devops.intern.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.service.AdminTgService;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.telegram.bot.service.register.BotAddGroup;

import java.net.URI;
import java.net.URISyntaxException;

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
    private final BotTokenService botTokenService;
    private final BotAddGroup botAddGroup;

    public CustomerTelegramResource(
        CustomerFeign customerFeign, CustomerTelegramService customerTelegramService, AdminTgService adminService, BotTokenService botTokenService, BotAddGroup botAddGroup) {
        this.customerFeign = customerFeign;
        this.customerTelegramService = customerTelegramService;
        this.adminService = adminService;
        this.botTokenService = botTokenService;
        this.botAddGroup = botAddGroup;
    }

    @PostMapping("/new-message/{botId}")
    public void sendMessage(@RequestBody Update update, @PathVariable String botId) throws URISyntaxException {
        log.info("[REST] Bot id: {} | Update: {}", botId, update);
        long idBot = Long.parseLong(botId);
        BotTokenDTO botTokenDTO = botTokenService.findByChatId(idBot);
        if (botTokenDTO != null && botTokenDTO.getToken() != null) {
            URI uri = new URI("https://api.telegram.org/bot" + botTokenDTO.getToken());
            SendMessage sendMessage = customerTelegramService.botCommands(update, uri);
            if (sendMessage != null)
                customerFeign.sendMessage(uri, sendMessage);
        }
        botAddGroup.execute(update, botId);
    }
}
