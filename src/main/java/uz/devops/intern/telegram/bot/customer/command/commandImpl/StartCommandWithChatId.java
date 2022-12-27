package uz.devops.intern.telegram.bot.customer.command.commandImpl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.TelegramGroup;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.telegram.bot.customer.command.ExecuteCommand;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.telegram.bot.customer.command.enumeration.CommandsName;
import uz.devops.intern.telegram.bot.customer.service.CustomerUpdateWithMessageService;
import uz.devops.intern.telegram.bot.dto.BotCommandDTO;
import uz.devops.intern.telegram.bot.dto.BotCommandsMenuDTO;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingCustomerTelegram;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingTelegramUser;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
@RequiredArgsConstructor
public class StartCommandWithChatId implements ExecuteCommand {
    @Value("${telegram.api}")
    private String telegramAPI;
    private static ResourceBundle resourceBundle;
    private static URI uri;
    private final CustomerTelegramService customerTelegramService;
    private final CustomerFeign customerFeign;
    private final CustomerUpdateWithMessageService customerUpdateWithMessageService;
    private final TelegramGroupService telegramGroupService;
    private final Logger log = LoggerFactory.getLogger(StartCommandWithChatId.class);
    @Override
    public SendMessage execute(Update update, URI uri) {
        setBotCommandsAndSendToBotCommands(uri);

        Message message = update.getMessage();
        String stringMessage = message.getText();

        SendMessage sendMessage = startCommandWithChatId(message.getFrom(), stringMessage, uri);
        if (sendMessage.getText()!= null)
            return sendMessage;

        return customerUpdateWithMessageService.responseFromStartCommandWithChatId(update, uri, stringMessage);
    }

    private void setBotCommandsAndSendToBotCommands(URI uri){
        List<BotCommandDTO> botCommandDTOList = List.of(
            new BotCommandDTO("/start", "start"),
            new BotCommandDTO("/help", "help"),
            new BotCommandDTO("/translate_uz","o'zbekchaga o'tkazish"),
            new BotCommandDTO("/translate_ru", "перевод на русский"),
            new BotCommandDTO("translate_en", "translate into english")
        );
        BotCommandsMenuDTO commandsMenuDTO = new BotCommandsMenuDTO();
        commandsMenuDTO.setCommands(botCommandDTOList);

        customerFeign.setMyCommands(uri, commandsMenuDTO);
        log.info("successfully send botCommands: {}", commandsMenuDTO);
    }

    @Override
    public String commandName() {
        return CommandsName.START_WITH_CHAT_ID.getCommandName();
    }

    public SendMessage startCommandWithChatId(User telegramUser, String requestMessage, URI telegramUri) {
        uri = telegramUri;
        resourceBundle = getResourceBundleUsingTelegramUser(telegramUser);
        log.info("started working method startCommandWithChatId.\n" +
            "Params telegramUser: {} | requestMessage: {} | telegramUri: {}", telegramUser, requestMessage, telegramUri);
        try {
            Long chatIdCreatedByManager = Long.parseLong(requestMessage.substring(7));
            Optional<TelegramGroup> telegramGroup = telegramGroupService.findByChatId(chatIdCreatedByManager);
            if (telegramGroup.isEmpty()) {
                log.warn("send not found message. Because there is no telegramGroup of telegram customer");
                return sendMessageIfNotExistsBotGroup(telegramUser);
            }

            Optional<CustomerTelegram> customerTelegramOptional = customerTelegramService.findCustomerByTelegramId(telegramUser.getId());
            if (customerTelegramOptional.isPresent()) startCommand(telegramUser, customerTelegramOptional.get());
            else startCommand(telegramUser);
        } catch (NumberFormatException numberFormatException) {
            log.error("Error parsing chatId to Long when bot started");
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_INVALID_CHAT_NUMBER));
        }
        return new SendMessage();
    }

    public void startCommand(User telegramUser, CustomerTelegram customerTelegram) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        String sayHello = resourceBundle.getString(BOT_SAY_HELLO) + " ";
        String welcomeToSystem = resourceBundle.getString(BOT_WELCOME_SYSTEM);
        String sendStringMessage = sayHello + customerTelegram.getUsername() +  welcomeToSystem;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(sendStringMessage);
        sendMessage.setChatId(telegramUser.getId());

        customerFeign.sendMessage(uri, sendMessage);
        log.info("successfully send welcome message to telegram customer. SendMessage: {}", sendMessage);
    }

    public void startCommand(User user) {
        resourceBundle = getResourceBundleUsingTelegramUser(user);
        String sayHello = resourceBundle.getString(BOT_SAY_HELLO) + " ";
        String welcomeToSystem = resourceBundle.getString(BOT_WELCOME_SYSTEM);
        String sendStringMessage = sayHello + user.getUserName() +  welcomeToSystem;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(sendStringMessage);
        sendMessage.setChatId(user.getId());

        customerFeign.sendMessage(uri, sendMessage);
        log.info("successfully send welcome message to telegram customer. SendMessage: {}", sendMessage);
    }

    public SendMessage sendMessageIfNotExistsBotGroup(User telegramUser) {
        resourceBundle = getResourceBundleUsingTelegramUser(telegramUser);
        String sendStringMessage = resourceBundle.getString(BOT_NOT_EXISTS_BOT_GROUP);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramUser.getId());
        sendMessage.setText(sendStringMessage);
        log.warn("send not found message. Because there is no telegramGroup of telegram customer");
        return sendMessage;
    }

    public SendMessage sendForbiddenMessage(Update update, URI telegramUri) {
        if (update.hasCallbackQuery())
            return sendMessageIfNotExistsBotGroup(update.getCallbackQuery().getFrom());

        return sendMessageIfNotExistsBotGroup(update.getMessage().getFrom());
    }
}
