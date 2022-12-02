package uz.devops.intern.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.feign.TelegramClient;
import uz.devops.intern.redis.CustomerTelegramRedis;
import uz.devops.intern.redis.CustomerTelegramRedisRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.service.CustomerTelegramService;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.*;

/**
 * Service Implementation for managing {@link CustomerTelegram}.
 */
@Service
@Transactional
public class CustomerTelegramServiceImpl implements CustomerTelegramService {

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramServiceImpl.class);
    private final CustomerTelegramRepository customerTelegramRepository;
    private final CustomerTelegramRedisRepository customerTelegramRedisRepository;
    private final CustomersService customersService;
    private final TelegramClient telegramClient;

    public CustomerTelegramServiceImpl(CustomerTelegramRepository customerTelegramRepository, CustomerTelegramRedisRepository customerTelegramRedisRepository, CustomersService customersService, TelegramClient telegramClient) {
        this.customerTelegramRepository = customerTelegramRepository;
        this.customerTelegramRedisRepository = customerTelegramRedisRepository;
        this.customersService = customersService;
        this.telegramClient = telegramClient;
    }

    @Override
    public SendMessage botCommands(Update update) {
        Message message = update.getMessage();
        User telegramUser = message.getFrom();
        String sendStringMessage = "";
        SendMessage sendMessage = new SendMessage();
        String requestMessage = message.getText();
        System.out.println(requestMessage);

        switch(requestMessage){
            case "/start": startCommand(telegramUser, sendMessage, sendStringMessage);
            case "/help":
                return helpCommand(message);
        }

        sendMessage = registerCustomerClient(requestMessage, telegramUser);
        if (sendMessage != null){
            return sendMessage;
        }

//        sendMessage = new SendMessage();
//        sendMessage.setChatId(telegramUser.getId());
//        sendMessage.setText("Hozicha hammasi joyida, dasturdan foydalanishingiz mumkin\n" +
//            " Tizimni qolgani bitmagan, biroz kuting)");
        return sendMessage;
    }

    private SendMessage registerCustomerClient(String requestMessage, User telegramUser) {
        SendMessage sendMessage = new SendMessage();
        Optional<CustomerTelegram> customerTelegramOptional = customerTelegramRepository.findByTelegramId(telegramUser.getId());
        Optional<CustomerTelegramRedis> redisOptional = customerTelegramRedisRepository.findById(telegramUser.getId());

        if (!requestMessage.startsWith("+998")){
            sendMessage = checkCustomerTelegramIsEmpty(customerTelegramOptional, telegramUser);
            if (sendMessage != null){
                return sendMessage;
            }
        }else{
            Customers customer = checkCustomerPhoneNumber(requestMessage);
            if (customer == null){
                String sendStringMessage = "Kechirasiz, bu raqam ma'lumotlar omboridan topilmadi\n" +
                    "Tizim web-sahifasi orqali ro'yxatdan o'tishingizni so'raymiz!";

                sendMessage.setChatId(String.valueOf(telegramUser.getId()));
                sendMessage.setText(sendStringMessage);
                log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage);
                return sendMessage;
            }

            if (customerTelegramOptional.isEmpty()){
                CustomerTelegram customerTelegram = createCustomerTelegramToSaveDatabase(telegramUser, requestMessage);
                customerTelegramRepository.save(customerTelegram);
                log.info("New telegram user successfully saved to Database! telegram user : {}", customerTelegram);
            }
            if (redisOptional.isEmpty()){
                CustomerTelegramRedis customerTelegramRedis = new CustomerTelegramRedis(telegramUser.getId(), telegramUser);
                customerTelegramRedisRepository.save(customerTelegramRedis);
                log.info("New telegram user successfully saved to redis! UserRedis : {}", customerTelegramRedis);
            }
        }
        return null;
    }

    private Customers checkCustomerPhoneNumber(String phoneNumber) {
        log.info("Checking customer phoneNumber : {}", phoneNumber);
        Optional<Customers> customerOptional = customersService.findByPhoneNumber(phoneNumber);
        if (customerOptional.isEmpty()){
            return null;
        }
        return customerOptional.get();
    }


    public void startCommand(User user, SendMessage sendMessage, String sendStringMessage){
         sendStringMessage += "Assalomu alaykum " + user.getUserName() +
            ", CPM(nom qo'yiladi) to'lov tizimiga xush kelibsiz! \n";
        sendMessage.setText(sendStringMessage);
        sendMessage.setChatId(user.getId());

        telegramClient.sendMessage(sendMessage);
    }

    public SendMessage helpCommand(Message message){
        String newMessage = "Bu yerda platformadan qanday foydalanish kerakligi yozilgan bo'ladi.\n" +
            "\n" +
            "Komandalar nima qilishi: \n" +
            "1. \n" +
            "2. \n" +
            "3. \n" +
            "\n" +
            "Platformadan qanday foydalanish instruksiyasi: \n" +
            "1. \n" +
            "2. \n" +
            "3. \n" +
            "\n" +
            "Bosing: /start";

        return sendMessage(String.valueOf(message.getFrom().getId()), newMessage);
    }

}
