package uz.devops.intern.telegram.bot.customer.command.invoker;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.telegram.bot.customer.command.CommandContainer;
import uz.devops.intern.telegram.bot.customer.command.enumeration.CommandsName;

import javax.annotation.PostConstruct;
import java.net.URI;

import static uz.devops.intern.constants.ResourceBundleConstants.BOT_FORBIDDEN;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
@RequiredArgsConstructor
public class InvokerBotCommand {
    private final CustomerFeign customerFeign;
    private final CommandContainer commandContainer;
    private static final String prefixCommand = "/";
    private static final String startsWithTranslate = "/translate_";
    private final Logger logger = LoggerFactory.getLogger(InvokerBotCommand.class);
    @PostConstruct
    public void init(){
        logger.info("started working CommandContainer");
    }
    public SendMessage getMessageCommand(Update update, URI uri){
        Message message = update.getMessage();
        String requestMessage = message.getText();
        if (requestMessage != null && requestMessage.startsWith(startsWithTranslate)) {
            String languageCode = requestMessage.split(startsWithTranslate)[1];
            update.getMessage().setText(languageCode);
            return commandContainer.getCommand(languageCode).execute(update,uri);
        }

        if (requestMessage != null && requestMessage.startsWith(prefixCommand)) return commandContainer.getCommand(requestMessage.split("/start ")[0]).execute(update, uri);
        return commandContainer
            .getCommand(CommandsName.COMMAND_WITH_MESSAGE.getCommandName())
            .execute(update, uri);
    }

    public void invokerController(Update update, URI uri){
        SendMessage sendMessage = new SendMessage();
        if (update.hasMessage() && ifRequestFromBot(update)) sendMessage = getMessageCommand(update, uri);
        else if (update.hasCallbackQuery()) sendMessage = getCallbackQueryCommand(update, uri);

        if (sendMessage.getText() != null)
            customerFeign.sendMessage(uri, sendMessage);
    }

    public SendMessage getCallbackQueryCommand(Update update, URI uri){
        return commandContainer
            .getCommand(CommandsName.COMMAND_WITH_CALLBACK_QUERY.getCommandName())
            .execute(update, uri);
    }
    public SendMessage sendBotTokenNotFoundMessage(Update update){
        if (update.hasMessage()) return sendMessage(update.getMessage().getFrom().getId(), BOT_FORBIDDEN);
        return sendMessage(update.getCallbackQuery().getFrom().getId(), BOT_FORBIDDEN);
    }

    private Boolean ifRequestFromBot(Update update){
        return update.getMessage().getChat().getId().equals(update.getMessage().getFrom().getId());
    }
}
