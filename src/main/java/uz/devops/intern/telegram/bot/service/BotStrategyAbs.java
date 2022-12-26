package uz.devops.intern.telegram.bot.service;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.feign.CustomerFeignClient;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

@Service
public abstract class BotStrategyAbs implements BotStrategy {

    public final Logger log = LoggerFactory.getLogger(BotStrategyAbs.class);
    public final String telegramAPI = "https://api.telegram.org/bot";
    @Autowired
    public AdminFeign adminFeign;
    @Autowired
    public CustomerFeignClient customerFeign;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminMenuKeys adminMenuKeys;
    @Autowired
    private CustomerTelegramService customerTelegramService;

    abstract public boolean execute(Update update, CustomerTelegramDTO manager);
    abstract public String getState();
    abstract public Integer getStep();

    public void wrongValue(Long chatId, String message){
        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
        Update update = adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
            chatId, message, update);
    }

    public URI createCustomerURI(String token){
        try {
            return new URI(telegramAPI + token);
        } catch (URISyntaxException e) {
            log.error("{} | {}", e.getMessage(), e.getCause());
            return null;
        }
    }

    public ResponseDTO<User> getUserByCustomerTg(CustomerTelegramDTO manager){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        ResponseDTO<User> response = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!response.getSuccess()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.user.is.not.found"));
            log.warn("{} | Manager id: {} | Response: {}", response.getMessage(), manager.getTelegramId(), response);
            return response;
        }
        log.info("User by CustomerTelegram phone number, Phone number: {} | Response: {}", manager.getPhoneNumber(), response);
        return response;
    }

    protected void throwManagerToMenu(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.main.menu");
        ReplyKeyboardMarkup menuMarkup = adminMenuKeys.createMenu(manager.getLanguageCode());
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, menuMarkup);
        adminFeign.sendMessage(sendMessage);
        manager.setStep(7);
        customerTelegramService.update(manager);
    }

    protected EditMessageTextDTO createEditMessage(CallbackQuery callback, InlineKeyboardMarkup markup, String newMessage){
        return new EditMessageTextDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            callback.getInlineMessageId(),
            markup,
            newMessage,
            "HTML",
            null
        );
    }

    protected boolean removeInlineButtons(CallbackQuery callback){
        EditMessageDTO editMessageDTO = new EditMessageDTO(
            String.valueOf(callback.getFrom().getId()),
            callback.getMessage().getMessageId(),
            callback.getInlineMessageId(),
            new InlineKeyboardMarkup()
        );
        try {
            adminFeign.editMessageReplyMarkup(editMessageDTO);
            return true;
        }catch (FeignException.FeignClientException e){
            log.warn("Error while editing message when manager renamed group! Manager id: {} ", callback.getFrom().getId());
            return false;
        }
    }
}
