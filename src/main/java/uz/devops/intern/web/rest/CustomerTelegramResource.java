package uz.devops.intern.web.rest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.command.invoker.InvokerBotCommand;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.dto.BotTokenDTO;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerTelegramResource {
    private final Logger log = LoggerFactory.getLogger(CustomerTelegramResource.class);
    private final CustomerFeign customerFeign;
    private final BotTokenService botTokenService;
    private final InvokerBotCommand invokerBotCommand;

    @PostMapping("/new-message/{botId}")
    public void sendMessage(@RequestBody Update update, @PathVariable String botId) throws URISyntaxException {
        log.info("[REST] Bot id: {} | Update: {}", botId, update);

        long idBot = Long.parseLong(botId);
        BotTokenDTO botTokenDTO = botTokenService.findByChatId(idBot);
        SendMessage sendMessage = new SendMessage();

        if(update.hasCallbackQuery() && botTokenDTO != null)
            sendMessage = invokerBotCommand.getCallbackQueryCommand(update, new URI("https://api.telegram.org/bot" + botTokenDTO.getToken()));
        else if (update.hasMessage() && ifRequestFromBot(update) && botTokenDTO != null)
            sendMessage = invokerBotCommand.getMessageCommand(update, new URI("https://api.telegram.org/bot" + botTokenDTO.getToken()));

        if (sendMessage.getText() != null)
            customerFeign.sendMessage(new URI("https://api.telegram.org/bot" + botTokenDTO.getToken()), sendMessage);
    }

    private Boolean ifRequestFromBot(Update update){
        return update.getMessage().getChat().getId().equals(update.getMessage().getFrom().getId());
    }
}
