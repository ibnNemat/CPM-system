package uz.devops.intern.web.rest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.telegram.bot.customer.command.invoker.InvokerBotCommand;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.telegram.bot.service.register.BotAddGroup;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerTelegramResource {
    @Value("${telegram.api}")
    private String telegramAPI;
    private final Logger log = LoggerFactory.getLogger(CustomerTelegramResource.class);
    private final BotTokenService botTokenService;
    private final InvokerBotCommand invokerBotCommand;
    private final BotAddGroup botAddGroup;

    @PostMapping("/new-message/{botId}")
    public void sendMessage(@RequestBody Update update, @PathVariable String botId) throws URISyntaxException {
        log.info("[REST] Bot id: {} | Update: {}", botId, update);
        long idBot = Long.parseLong(botId);
        BotTokenDTO botTokenDTO = botTokenService.findByChatId(idBot);
        if(botTokenDTO != null) invokerBotCommand.invokerController(update, new URI(telegramAPI + botTokenDTO.getToken()));

        if(update.hasMessage()) botAddGroup.execute(update, botId);
    }
}
