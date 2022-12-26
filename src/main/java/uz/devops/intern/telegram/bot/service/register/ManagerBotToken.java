package uz.devops.intern.telegram.bot.service.register;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.glassfish.jersey.server.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.dto.UserDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.dto.WebhookResponseDTO;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.net.URI;
import java.util.Objects;
import java.util.ResourceBundle;

@Service
//@RequiredArgsConstructor
public class ManagerBotToken extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.MESSAGE;
    private final String telegramAPI = "https://api.telegram.org/bot";
    private final String STATE = "MANAGER_NEW_BOT_TOKEN";
    private final Integer STEP = 3;
    private final Integer NEXT_STEP = 4;

    @Value("${ngrok.url}")
    private String WEBHOOK_URL;

    private final BotTokenService botTokenService;
    private final UserService userService;

    public ManagerBotToken(BotTokenService botTokenService, UserService userService) {
        this.botTokenService = botTokenService;
        this.userService = userService;
    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());

        String newBotToken = update.getMessage().getText();

        ResponseDTO<BotTokenDTO> responseDTO = botTokenService.findByToken(newBotToken);
        if(responseDTO.getSuccess() && responseDTO.getMessage().equals("OK")){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.bot.is.already.exists"));
            log.warn("Bot is already exists! Bot token: {} | BotTokenDTO: {} | Manager: {}",
                newBotToken, responseDTO.getResponseData(), manager);
            return false;
        }

        URI uri = createCustomerURI(newBotToken);
        if(uri == null){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.bot.token.is.invalid"));
            log.warn("Bot token is invalid! User id: {} | Token: {}", manager.getTelegramId(), newBotToken);
            return false;
        }

        ResponseFromTelegram<User> telegramResponse = null;
        try{
            telegramResponse = customerFeign.getMe(uri);
        } catch (FeignException e){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.bot.token.is.invalid"));
            log.error("Error while sending request to server, Bot token: {} | API: {} | Exc. message: {} | Exc. cause: {}",
                newBotToken, telegramAPI + newBotToken + "/getMe", e.getMessage(), e.getCause());
            return false;
        }

        if(!telegramResponse.getOk() || telegramResponse.getResult() == null){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.bot.token.is.invalid"));
            log.warn("Given bot token is invalid! Bot token: {} | Manager: {} | Response from telegram: {}",
                newBotToken, manager, telegramResponse);
            return false;
        }
        boolean isDeletedWebhook = deleteWebhook(newBotToken);
        if(!isDeletedWebhook){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }
        User bot = telegramResponse.getResult();
        WebhookResponseDTO webhookResponse = setWebhookToNewBot(newBotToken, bot.getId());
        String result = checkWebhookResponse(webhookResponse);

        if (!result.equals("Ok")) {
            wrongValue(manager.getTelegramId(), result);
            log.info("Setting webhook is failed, Response: {} | Bot token: {} | Customer: {}",
                webhookResponse, newBotToken, manager);
            return false;
        }

        saveBotEntity(bot, manager, newBotToken);
        basicFunction(manager, bundle);
        return true;
    }

    public void basicFunction(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.bot.successfully.saved");
        newMessage = newMessage + "\n" + bundle.getString("bot.admin.send.organization.name");
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(NEXT_STEP);
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public String messageOrCallback() {
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.only.message");
    }

    private WebhookResponseDTO setWebhookToNewBot(String token, Long botId){
        URI uri = createCustomerURI(token);
        log.info("URI: {}", uri);
        String webhookAPI = WEBHOOK_URL + "/api/new-message/" + botId;
        WebhookResponseDTO webhookResponseDTO = customerFeign.setWebhook(uri, webhookAPI);
        log.info("Response from telegram server: {}", webhookResponseDTO);
        return webhookResponseDTO;
    }

    private String checkWebhookResponse(WebhookResponseDTO response){
        if(response.getResult()){
            if(response.getDescription().contains("is already set")){
                return "Bu botdan oldin ishlatilingan.";
            }
        }else{
            if(response.getDescription().contains("Unauthorized")){
                return "Botning tokeni noto'g'ri.";
            }
        }
        return "Ok";
    }

    private void saveBotEntity(User bot, CustomerTelegramDTO manager, String newBotToken){
        ResponseDTO<uz.devops.intern.domain.User> response = getUserByCustomerTg(manager);
        if(Objects.isNull(response.getResponseData())){
            return;
        }

        UserDTO dto = new UserDTO();
        dto.setId(response.getResponseData().getId());
        dto.setLogin(response.getResponseData().getLogin());

        BotTokenDTO botTokenDTO = BotTokenDTO.builder()
            .username(bot.getUserName()).telegramId(bot.getId()).token(newBotToken).createdBy(dto).build();
        botTokenService.save(botTokenDTO);
    }

    private boolean deleteWebhook(String token){
        URI uri = createCustomerURI(token);
        try {
            WebhookResponseDTO response = customerFeign.deleteWebhook(uri, true);
            log.info("Webhook is deleted! Bot token: {} | Response: {}", token, response);
            return true;
        }catch (FeignException e){
            log.error("Error while deleting webhook! Token: {} | Exception: {}", token, e.getMessage());
            return false;
        }
    }
}
