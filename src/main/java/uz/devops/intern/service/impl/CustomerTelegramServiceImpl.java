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
import uz.devops.intern.feign.TelegramClient;
import uz.devops.intern.redis.CustomerTelegramRedis;
import uz.devops.intern.redis.CustomerTelegramRedisRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.telegram.bot.utils.TelegramUtil;
//import static uz.devops.intern.telegram.bot.utils.TelegramUtil;
/**
 * Service Implementation for managing {@link CustomerTelegram}.
 */
@Service
@Transactional
public class CustomerTelegramServiceImpl implements CustomerTelegramService {

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramServiceImpl.class);
    private final CustomerTelegramRepository customerTelegramRepository;
    private final TelegramClient telegramClient;
    private final CustomerTelegramRedisRepository customerTelegramRedisRepository;
    private final CustomersService customersService;

    public CustomerTelegramServiceImpl(CustomerTelegramRepository customerTelegramRepository, TelegramClient telegramClient, CustomerTelegramRedisRepository customerTelegramRedisRepository, CustomersService customersService) {
        this.customerTelegramRepository = customerTelegramRepository;
        this.telegramClient = telegramClient;
        this.customerTelegramRedisRepository = customerTelegramRedisRepository;
        this.customersService = customersService;
    }

    @Override
    public Message botCommands(Update update) {
        Message message = update.getMessage();
        User customerTelegram = message.getFrom();

        switch(message.getText()){
            case "/start" -> startCommand(customerTelegram);
            case "/help" -> helpCommand(message);
        }

        if (message.getText().startsWith("+998")){
            checkCustomerPhoneNumber(customerTelegram);
        }

        return message;
    }

    private boolean checkCustomerPhoneNumber(User customerTelegram) {
//        Optional<Customers> custemerOptional = customersService.findOne();
//        if (){
//
//        }
        return true;
    }

    @Override
    public void saveCustomerTelegramToDatabase(Update update) {
        User telegramUser = update.getMessage().getFrom();
        CustomerTelegram customerTelegram = new CustomerTelegram()
            .isBot(telegramUser.getIsBot())
            .telegramId(telegramUser.getId())
            .canJoinGroups(telegramUser.getCanJoinGroups())
            .firstname(telegramUser.getFirstName())
            .username(telegramUser.getUserName())
            .languageCode(telegramUser.getLanguageCode())
            .isActive(true);

        customerTelegramRepository.save(customerTelegram);
    }

    public SendMessage startCommand(User user){
        SendMessage sendMessage = null;
        String newMessage = "Assalomu alaykum " + user.getUserName() +
            ", CPM(nom qo'yiladi) to'lov tizimiga xush kelibsiz! \n";

        Optional<CustomerTelegramRedis> redisOptional = customerTelegramRedisRepository.findById(user.getId());
        Optional<CustomerTelegram> customerTelegramOptional = customerTelegramRepository.findByTelegramId(user.getId());

        if (customerTelegramOptional.isEmpty()){
            newMessage += "Siz hali telegram botdan foydalanish uchun ro'yxatdan o'tmagansiz, iltimos telefon raqamingizni jo'natish\n" +
                " uchun quyidagi tugmani bosing \uD83D\uDC47\n";
            KeyboardButton button = new KeyboardButton("\uD83D\uDCF1 Telefon raqam");
            button.setRequestContact(true);

            KeyboardRow row = new KeyboardRow();
            row.add(button);

            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
            markup.setResizeKeyboard(true);
            markup.setKeyboard(List.of(row));

            sendMessage = TelegramUtil.sendMessage(String.valueOf(user.getId()), newMessage, markup);
            return sendMessage;
        }else if (redisOptional.isEmpty()){
            CustomerTelegramRedis customerTelegramRedis = new CustomerTelegramRedis(user.getId(), user);
            customerTelegramRedisRepository.save(customerTelegramRedis);
        }else{

        }
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
