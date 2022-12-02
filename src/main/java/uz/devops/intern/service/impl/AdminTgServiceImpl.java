package uz.devops.intern.service.impl;

import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.Authority;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.feign.TelegramClient;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.AdminTgService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.telegram.bot.dto.WebhookResponseDTO;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramUtil;

import java.util.Optional;

@Service
public class AdminTgServiceImpl implements AdminTgService {

    private static String telegramAPI = "https://api.telegram.org/api/bot";
    private static String webhookAPI = "/setWebhook?url=https://8334-83-221-180-161.ap.ngrok.io/customer-bot";

    private final Logger log = LoggerFactory.getLogger(AdminTgServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerTelegramRepository customerTelegramRepository;
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
                verifyAdminByLogin(update.getMessage(), customer);
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
        SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(userId), newMessage, markup);
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
            String newMessage = "Iltimos veb saytdagi login ingizni kiriting";
            SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(customer.getId()), newMessage);
            Update response = feign.sendMessage(sendMessage);
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                userId, messageText, response);
        }else if(messageText.equals("\uD83C\uDDF7\uD83C\uDDFA Русский")){
            // Ruschani tanladi.
            customer.setLanguageCode("ru");
            String newMessage = "Iltimos veb saytdagi login ingizni kiriting(Ruscha bo'ladi)";
            SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(customer.getId()), newMessage);
            Update response = feign.sendMessage(sendMessage);
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                userId, messageText, response);
        }
        customer.setStep(2);
        customerTelegramRepository.save(customer);
    }

    @Override
    public void verifyAdminByLogin(Message message, CustomerTelegram customer) {
        Long userId = message.getFrom().getId();
        String newMessage = null;
        String userLogin = message.getText();
        Optional<uz.devops.intern.domain.User> userOptional = userService.getUserWithAuthoritiesByLogin(userLogin);
        if(userOptional.isEmpty()){
            newMessage = "Xato login!";
            SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(userId), newMessage);
            Update update = feign.sendMessage(sendMessage);
        }else {
            boolean isUserManager = false;
            uz.devops.intern.domain.User user = userOptional.get();
            for(Authority authority: user.getAuthorities()){
                if(authority.getName().equals("ROLE_MANAGER")){
                    isUserManager = true;
                    break;
                }
            }
            if(isUserManager){

                newMessage = "Tabriklaymiz, ma'lumotlar to'g'ri keldi. Endi shaxsiy botingizning tokennini yuboring.";
                SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(userId), newMessage);
                Update update = feign.sendMessage(sendMessage);
                log.info("User successfully authenticated, Login: {} | User: {} ", userLogin, customer);
                customer.setStep(3);
                customerTelegramRepository.save(customer);
            }else {
                newMessage = "Afsuski siz admin emassiz!";
                SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(userId), newMessage);
                Update update = feign.sendMessage(sendMessage);
                log.info("User has not \"ROLE_MANAGER\"! Login: {} | User: {}", userLogin, customer);
            }
        }
    }

    @Override
    public void getAdminBotToken(Message message, CustomerTelegram customer) {
        Long userId = message.getFrom().getId();
        String newBotToken = message.getText();

        String url = telegramAPI + newBotToken + webhookAPI;
        log.info("Url: {}", url);
        RestTemplate template = new RestTemplate();
        WebhookResponseDTO response =
            template.exchange(url, HttpMethod.GET, null, WebhookResponseDTO.class).getBody();
        log.info("Response from telegram server: {}", response);
        String result = checkWebhookResponse(response);
        if(result.equals("Ok")){
            // Hammasi joyida
            String newMessage = "Tabriklaymiz, botning tokeni muvafaqiyatli saqlandi.";
            SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(userId), newMessage);
            Update update = feign.sendMessage(sendMessage);
            log.info("BOT_TOKEN successfully saved, Bot token: {} | Customer: {} | Update: {}",
                newBotToken, customer, update);
            customer.setStep(4);
            customerTelegramRepository.save(customer);
            // Botning tokenini saqlab qo'yish kere.
        }else {
            SendMessage sendMessage = TelegramUtil.sendMessage(String.valueOf(userId), result);
            Update update = feign.sendMessage(sendMessage);
            log.info("Setting webhook is failed, Response: {} | Bot token: {} | Customer: {}",
                response, newBotToken, customer);
        }
    }

    private CustomerTelegram createCustomer(User user){
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
                return "Bu botdan oldi ishlatilingan.";
            }
        }else{
            if(response.getDescription().contains("Unauthorized")){
                return "Botning tokeni noto'g'ri.";
            }
        }
        return "Ok";
    }
}
