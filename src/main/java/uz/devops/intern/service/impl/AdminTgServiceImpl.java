package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.*;
import uz.devops.intern.feign.TelegramClient;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.repository.UserRepository;
import uz.devops.intern.service.AdminTgService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.telegram.bot.dto.WebhookResponseDTO;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
//import org.telegram.telegrambots.meta.api.objects.User TgUser;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.*;

import java.util.Optional;
import java.util.Set;

@Service
public class AdminTgServiceImpl implements AdminTgService {

    private static String telegramAPI = "https://api.telegram.org/bot";
    private static String webhookAPI = "/setWebhook?url=https://eeca-89-236-218-41.in.ngrok.io/api/new-message";

    private final Logger log = LoggerFactory.getLogger(AdminTgServiceImpl.class);

    @Autowired
    private BotTokenRepository botTokenRepository;
    @Autowired
    private CustomerTelegramRepository customerTelegramRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TelegramClient feign;

    @Override
    public void main(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Optional<CustomerTelegram> customerOptional = customerTelegramRepository.findByTelegramId(userId);
        if(customerOptional.isEmpty()){
            // Bazadan user ma'lumotlar topilmasa shu if ichiga kiradi.
            String messageText = update.getMessage().getText();
            if(messageText.equals("/start")){
                pressedStartCommand(update.getMessage());
            }else if(messageText.equals("/help")){
                System.out.println("hello world!");
            }
        }else{
            CustomerTelegram customer = customerOptional.get();
            Integer step = customer.getStep();
            if(step == 1){
                // Verifikatsiyadan o'tishi kerak.
                getLanguage(update.getMessage(), customer);
                customerTelegramRepository.save(customer);
                log.info("User is updated successfully! User: {}", customer);
            }else if(step == 2){
                verifyAdminByPhoneNumber(update.getMessage(), customer);
            }else if(step == 3){
                getAdminBotToken(update.getMessage(), customer);
            }else if(step == 4){
                // Hozircha hish nima yo'q.
            }
        }

    }

    @Override
    public void pressedStartCommand(Message message) {
        Long userId = message.getFrom().getId();
        String messageText = message.getText();

        String newMessage = "Iltimos tilni tanlang\uD83D\uDC47";
        ReplyKeyboardMarkup markup = KeyboardUtil.language();
        SendMessage sendMessage = TelegramsUtil.sendMessage(String.valueOf(userId), newMessage, markup);
        Update update = feign.sendMessage(sendMessage);
        log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
            userId, messageText, update);
        CustomerTelegram customer = createCustomer(message.getFrom());
        customerTelegramRepository.save(customer);
    }

    @Override
    public void getLanguage(Message message, CustomerTelegram customer) {
        Long userId = message.getFrom().getId();
        String messageText = message.getText();
        if(messageText.equals("🇺🇿 O`zbekcha")){
            // O'zbechani tanladi.
            customer.setLanguageCode("uz");
            String newMessage = "Iltimos telefon raqamingizni jo'nating\uD83D\uDC47";
            ReplyKeyboardMarkup markup = KeyboardUtil.phoneNumber();
            SendMessage sendMessage = sendMessage(String.valueOf(userId), newMessage, markup);
            Update response = feign.sendMessage(sendMessage);
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                userId, messageText, response);
        }else if(messageText.equals("\uD83C\uDDF7\uD83C\uDDFA Русский")){
            // Ruschani tanladi.
            customer.setLanguageCode("ru");
            String newMessage = "Iltimos telefon raqamingizni jo'nating\uD83D\uDC47(Ruscha)";
            ReplyKeyboardMarkup markup = KeyboardUtil.phoneNumber();
            SendMessage sendMessage = sendMessage(String.valueOf(customer.getId()), newMessage, markup);
            Update response = feign.sendMessage(sendMessage);
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                userId, messageText, response);
        }
        customer.setStep(2);
        customerTelegramRepository.save(customer);
    }

    @Override
    public void verifyAdminByPhoneNumber(Message message, CustomerTelegram customer) {
        Long userId = message.getFrom().getId();
        String newMessage = null;

        if(message.hasContact() || message.hasText()){
            User user = checkPhoneNumber(message);
            if(user == null){
                newMessage = "Telefon raqam mos kelmayapti!";
                SendMessage sendMessage = sendMessage(String.valueOf(userId), newMessage);
                feign.sendMessage(sendMessage);
                log.warn("Data of user is not found! Customer: {} | Message: {}",
                    customer, message);

            }else {
                boolean isUserManager = checkUserRole(user);
                if (isUserManager) {
                    newMessage = "Iltimos botning tokenini tashlang.";
                    SendMessage sendMessage = sendMessage(String.valueOf(userId), newMessage);
                    feign.sendMessage(sendMessage);
                    log.info("User is verified, Phone number: {} | Customer: {}",
                        user.getCreatedBy(), customer);

                    customer.setPhoneNumber(user.getCreatedBy());
                    customer.setStep(3);
                    customerTelegramRepository.save(customer);
                } else {
                    // User manager emas.
                    newMessage = "Sizda boshqaruvchilik huquqi yo'q!";
                    SendMessage sendMessage = sendMessage(String.valueOf(userId), newMessage);
                    feign.sendMessage(sendMessage);
                    log.info("User hasn't \"Manager\" role! User: {} | Customer: {}", user, customer);
                }
            }
        }
    }

    @Override
    public void getAdminBotToken(Message message, CustomerTelegram customer) {
        Long userId = message.getFrom().getId();
        String newBotToken = message.getText();
        org.telegram.telegrambots.meta.api.objects.User bot = getBotData(newBotToken);
        if(bot != null) {

            WebhookResponseDTO response = setWebhookToNewBot(newBotToken, bot.getId());
            String result = checkWebhookResponse(response);
            if (result.equals("Ok")) {
                // Hammasi joyida
                String newMessage = "Tabriklaymiz, botning tokeni muvafaqiyatli saqlandi.";
                SendMessage sendMessage = sendMessage(String.valueOf(userId), newMessage);
                Update update = feign.sendMessage(sendMessage);
                User owner = getOwnerByPhoneNumber(customer.getPhoneNumber());
                BotToken botEntity = createBotEntity(bot, owner, newBotToken);
                botTokenRepository.save(botEntity);
                customer.setStep(4);
                customerTelegramRepository.save(customer);
                log.info("Bot saved successfully, Bot: {} | Customer: {}", botEntity, customer);
                // Botning tokenini saqlab qo'yish kere.
            } else {
                SendMessage sendMessage = sendMessage(String.valueOf(userId), result);
                Update update = feign.sendMessage(sendMessage);
                log.info("Setting webhook is failed, Response: {} | Bot token: {} | Customer: {}",
                    response, newBotToken, customer);
            }
        }
    }

    private CustomerTelegram createCustomer(org.telegram.telegrambots.meta.api.objects.User user){
        CustomerTelegram customer = new CustomerTelegram();
        customer.setTelegramId(user.getId());
        customer.setFirstname(user.getFirstName());
        customer.setLastname(user.getLastName());
        customer.setUsername(user.getUserName());
        customer.setCanJoinGroups(user.getCanJoinGroups());
        customer.setIsActive(true);
        customer.setLanguageCode(user.getLanguageCode());
        customer.setIsBot(user.getIsBot());
        customer.setStep(1);
        return customer;
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

    private User checkPhoneNumber(Message message){
        String contact = message.getContact() == null? message.getText(): message.getContact().getPhoneNumber();
        int contactLength = contact.length();
        if(contactLength != 12 && contactLength != 13){
            return null;
        }
        if(!contact.startsWith("+998") && !contact.startsWith("998")){
            return null;
        }
        return userRepository.findByCreatedBy(contact).orElse(null);
    }

    private boolean checkUserRole(User user){
        Set<Authority> authorities = user.getAuthorities();
        for(Authority auth: authorities){
            if(auth.getName().equals("ROLE_MANAGER"))return true;
        }
        return false;
    }

    private User getOwnerByPhoneNumber(String phoneNumber){
        Optional<User> userOptional = userRepository.findByCreatedBy(phoneNumber);
        return userOptional.orElse(null);
    }

    private org.telegram.telegrambots.meta.api.objects.User getBotData(String token){
        String url = telegramAPI + token + "/getMe";
        RestTemplate template = new RestTemplate();
        ResponseFromTelegram<org.telegram.telegrambots.meta.api.objects.User> response =
            template.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<ResponseFromTelegram<org.telegram.telegrambots.meta.api.objects.User>>() {}).getBody();
        if(response != null){
            org.telegram.telegrambots.meta.api.objects.User bot = response.getResult();
            log.info("Data of bot successfully got, Bot token: {} | Bot: {}", token, bot);
            return bot;
        }
        return null;
    }

    private BotToken createBotEntity(org.telegram.telegrambots.meta.api.objects.User bot, User owner,String token){
        BotToken entity = new BotToken();
        entity.setToken(token);
        entity.setCreatedBy(owner);
        entity.setTelegramId(bot.getId());
        entity.setUsername(bot.getUserName());

        return entity;
    }

    private WebhookResponseDTO setWebhookToNewBot(String token, Long botId){
        String url = telegramAPI + token + webhookAPI + "/" + botId;
        log.info("Url: {}", url);
        RestTemplate template = new RestTemplate();
        WebhookResponseDTO response =
            template.exchange(url, HttpMethod.GET, null, WebhookResponseDTO.class).getBody();
        log.info("Response from telegram server: {}", response);
        return response;
    }
}
