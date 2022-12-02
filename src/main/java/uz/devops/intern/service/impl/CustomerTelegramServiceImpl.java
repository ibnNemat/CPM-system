package uz.devops.intern.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.redis.CustomerTelegramRedis;
import uz.devops.intern.redis.CustomerTelegramRedisRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.service.CustomerTelegramService;

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

    public CustomerTelegramServiceImpl(CustomerTelegramRepository customerTelegramRepository, CustomerTelegramRedisRepository customerTelegramRedisRepository, CustomersService customersService) {
        this.customerTelegramRepository = customerTelegramRepository;
        this.customerTelegramRedisRepository = customerTelegramRedisRepository;
        this.customersService = customersService;
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
            case "/start": startCommand(telegramUser, sendMessage, sendStringMessage); break;
            case "/help":
                return helpCommand(message);
        }

        sendMessage = registerCustomerClient(requestMessage, telegramUser, sendStringMessage, sendMessage);
        if (sendMessage != null){
            return sendMessage;
        }
        sendMessage = new SendMessage();
        sendMessage.setChatId(telegramUser.getId());
        sendMessage.setText("Hozicha hammasi joyida, dasturdan foydalanishingiz mumkin\n" +
            " Tizimni qolgani bitmagan, biroz kuting)");
        return sendMessage;
    }

    private SendMessage registerCustomerClient(String requestMessage, User telegramUser, String sendStringMessage, SendMessage sendMessage) {
        Optional<CustomerTelegram> customerTelegramOptional = customerTelegramRepository.findByTelegramId(telegramUser.getId());
        Optional<CustomerTelegramRedis> redisOptional = customerTelegramRedisRepository.findById(telegramUser.getId());


        if (!requestMessage.startsWith("+998")){
            if (customerTelegramOptional.isEmpty()){
                sendStringMessage += "Siz hali telegram botdan foydalanish uchun ro'yxatdan o'tmagansiz, iltimos telefon raqamingizni jo'natish" +
                    " uchun quyidagi tugmani bosing \uD83D\uDC47\n";
                KeyboardButton button = new KeyboardButton("\uD83D\uDCF1 Telefon raqam");
                button.setRequestContact(true);

                KeyboardRow row = new KeyboardRow();
                row.add(button);

                ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
                markup.setResizeKeyboard(true);
                markup.setKeyboard(List.of(row));

                sendMessage = TelegramUtil.sendMessage(String.valueOf(telegramUser.getId()), sendStringMessage, markup);
                return sendMessage;
            }
        }else{
            Customers customer = checkCustomerPhoneNumber(requestMessage);
            if (customer == null){
                sendStringMessage = "Kechirasiz, bu raqam ma'lumotlar omboridan topilmadi\n" +
                    "Tizim web-sahifasi orqali ro'yxatdan o'tishingizni so'raymiz!";

                sendMessage.setChatId(String.valueOf(telegramUser.getId()));
                sendMessage.setText(sendStringMessage);
                return sendMessage;
            }
            if (customerTelegramOptional.isEmpty()){
                saveCustomerTelegramToDatabase(telegramUser, requestMessage);
            }
            if (redisOptional.isEmpty()){
                CustomerTelegramRedis customerTelegramRedis = new CustomerTelegramRedis(telegramUser.getId(), telegramUser);
                customerTelegramRedisRepository.save(customerTelegramRedis);
            }
        }
        return null;
    }

    private Customers checkCustomerPhoneNumber(String phoneNumber) {
        Optional<Customers> customerOptional = customersService.findByPhoneNumber(phoneNumber);
        if (customerOptional.isEmpty()){
            return null;
        }
        return customerOptional.get();
    }


    public SendMessage startCommand(User user, SendMessage sendMessage, String sendStringMessage){
         sendStringMessage += "Assalomu alaykum " + user.getUserName() +
            ", CPM(nom qo'yiladi) to'lov tizimiga xush kelibsiz! \n";
        sendMessage.setText(sendStringMessage);
        sendMessage.setChatId(user.getId());

        return sendMessage;
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

        return TelegramUtil.sendMessage(String.valueOf(message.getFrom().getId()), newMessage);
    }

}
