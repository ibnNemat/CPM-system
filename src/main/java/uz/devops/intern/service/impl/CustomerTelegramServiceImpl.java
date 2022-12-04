package uz.devops.intern.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.Authority;
import uz.devops.intern.domain.BotToken;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.redis.CustomerTelegramRedis;
import uz.devops.intern.redis.CustomerTelegramRedisRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;

import static uz.devops.intern.telegram.bot.utils.KeyboardUtil.sendMarkup;
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
    private final CustomerFeign customerFeign;
    private final BotTokenService botTokenService;
    public CustomerTelegramServiceImpl(CustomerTelegramRepository customerTelegramRepository, CustomerTelegramRedisRepository customerTelegramRedisRepository, CustomersService customersService, CustomerFeign customerFeign, BotTokenService botTokenService) {
        this.customerTelegramRepository = customerTelegramRepository;
        this.customerTelegramRedisRepository = customerTelegramRedisRepository;
        this.customersService = customersService;
        this.customerFeign = customerFeign;
        this.botTokenService = botTokenService;
    }

    public SendMessage checkBotToken(Chat chat){
        Optional<BotToken> optionalBotToken = botTokenService.findByChatId(chat.getId());
        if (optionalBotToken.isEmpty()){
            String sendStringMessage = "\uD83D\uDEAB Kechirasiz, sizdagi mavjud telegram guruhi tizimdan " +
                "foydalanish uchun ro'yxatdan o'tmagan. Guruh rahbaringiz telegram guruhni tizimdan ro'yxatdan o'tkazishi zarur!";

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chat.getId());
            sendMessage.setText(sendStringMessage);

            return sendMessage;
        }
        return null;
    }

    @Override
    public SendMessage botCommands(Update update) {
        Message message = update.getMessage();
        User telegramUser = message.getFrom();
        Chat chat = message.getChat();

//        SendMessage sendMessage = checkTelegramGroupIfExists(telegramUser, chat);
//        if (sendMessage != null){
//            return sendMessage;
//        }

        SendMessage sendMessage = checkBotToken(chat);
        if (sendMessage != null){
            return sendMessage;
        }

        sendMessage = new SendMessage();
        String requestMessage = message.getText();

        if (message.getText() == null){
            requestMessage = message.getContact().getPhoneNumber();
        }

        switch(requestMessage) {
            case "/start":
                startCommand(telegramUser, sendMessage); break;
            case "/help":
                return helpCommand(message);
        }

        Optional<CustomerTelegram> customerTelegramOptional = customerTelegramRepository.findByTelegramId(telegramUser.getId());
        if (customerTelegramOptional.isEmpty()){
            CustomerTelegram customerTelegram = createCustomerTelegramToSaveDatabase(telegramUser);
            customerTelegramRepository.save(customerTelegram);

            String responseString = "Quyidagi tillardan birini tanlang \uD83D\uDC47";
            ReplyKeyboardMarkup replyKeyboardMarkup = KeyboardUtil.language();
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                telegramUser.getId(), responseString, update);
            return sendMessage(telegramUser.getId(), responseString, replyKeyboardMarkup);
        }else {
            CustomerTelegram customerTelegram = customerTelegramOptional.get();
            Integer step = customerTelegram.getStep();

            switch (step){
                case 1:
                    return registerCustomerClientAndShowCustomerMenu(requestMessage, telegramUser, customerTelegram);
            }

            sendMessage = new SendMessage();
            sendMessage.setChatId(telegramUser.getId());
            sendMessage.setText("\uD83D\uDEAB Stepga kirmadi, nimadir nito\n" +
                "Tizimni qolgani bitmagan, biroz kuting)");
            return sendMessage;
        }
    }

    private SendMessage registerCustomerClientAndShowCustomerMenu(String requestMessage, User telegramUser, CustomerTelegram customerTelegram) {
        SendMessage sendMessage = new SendMessage();
        Optional<CustomerTelegramRedis> redisOptional = customerTelegramRedisRepository.findById(telegramUser.getId());

        if (!requestMessage.startsWith("+998")){
            sendMessage = checkPhoneNumberIsNull(customerTelegram, telegramUser);
            if (sendMessage != null){
                return sendMessage;
            }
        }else{
            Customers customer = checkCustomerPhoneNumber(requestMessage);
            Authority customerAuthority = new Authority();
            customerAuthority.setName("ROLE_CUSTOMER");
            if (customer == null){
                String sendStringMessage = "\uD83D\uDEAB Kechirasiz, bu raqam ma'lumotlar omboridan topilmadi\n" +
                    "Tizim web-sahifasi orqali ro'yxatdan o'tishingizni so'raymiz!";

                sendMessage = sendMessage(telegramUser.getId(), sendStringMessage, sendMarkup());
                log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage);
                return sendMessage;
            }else if(customer.getUser() == null || !customer.getUser().getAuthorities().contains(customerAuthority)){
                String sendStringMessage = "\uD83D\uDEAB Kechirasiz, bu raqamga 'Foydalanuvchi' huquqi berilmagan\n" +
                    "Boshqa raqam kiritishingizni so'raymiz!";

                sendMessage = sendMessage(telegramUser.getId(), sendStringMessage, sendMarkup());
                log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage);
                return sendMessage;
            }else{
                customerTelegram.customer(customer);
                customerTelegramRepository.save(customerTelegram);
            }

            if (customerTelegram.getPhoneNumber() == null) {
                customerTelegram.setPhoneNumber(requestMessage);
                customerTelegram.setStep(2);
                customerTelegramRepository.save(customerTelegram);
                log.info("Phone number successfully set to existing user! telegram user : {} | phoneNumber: {} ", customerTelegram, requestMessage);

                sendMessage.setChatId(telegramUser.getId());
                sendMessage.setText("Hurmatli foydalanuvchi " + telegramUser.getFirstName() +
                    ", tizimdan foydalanish uchun muvaffaqiyatli ro'yxatdan o'tdingiz ✅");

                customerFeign.sendMessage(sendMessage);
            }
            if (redisOptional.isEmpty()){
                CustomerTelegramRedis customerTelegramRedis = new CustomerTelegramRedis(telegramUser.getId(), telegramUser);
                customerTelegramRedisRepository.save(customerTelegramRedis);
                log.info("New telegram user successfully saved to redis! UserRedis : {}", customerTelegramRedis);
            }
        }

        return sendCustomerMenu(telegramUser);
    }

    // Customer can be entered to this menu after registration
    public SendMessage sendCustomerMenu(User userTelegram){
        KeyboardButton groupButton = new KeyboardButton("\uD83D\uDCAC Guruhlarim");
        groupButton.setRequestContact(true);

        KeyboardButton historyPaymentButton = new KeyboardButton("\uD83D\uDCC3 To'lovlar tarixi");
        historyPaymentButton.setRequestContact(true);

        KeyboardButton paymentButton = new KeyboardButton("\uD83D\uDCB0 Qarzdorligim");
        paymentButton.setRequestContact(true);

        KeyboardButton payButton = new KeyboardButton("\uD83D\uDCB8 To'lov qilish");
        payButton.setRequestContact(true);

        KeyboardButton changeCustomerButton = new KeyboardButton("✏️ Profilni o'zgartirish");
        changeCustomerButton.setRequestContact(true);

        KeyboardButton backButton = new KeyboardButton("⬅️ Ortga");
        backButton.setRequestContact(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(groupButton);
        row1.add(historyPaymentButton);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(paymentButton);
        row2.add(payButton);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(changeCustomerButton);
        row3.add(backButton);


        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(row1, row2, row3));

        return sendMessage(userTelegram.getId(), "Menu", markup);
    }

    private Customers checkCustomerPhoneNumber(String phoneNumber) {
        log.info("Checking customer phoneNumber : {}", phoneNumber);
        Optional<Customers> customerOptional = customersService.findByPhoneNumber(phoneNumber);
        if (customerOptional.isEmpty()){
            return null;
        }
        return customerOptional.get();
    }

    public void startCommand(User user, SendMessage sendMessage){
        String sendStringMessage = "Assalomu alaykum " + user.getUserName() +
            ", CPM(nom qo'yiladi) to'lov tizimiga xush kelibsiz! \n";
        sendMessage.setText(sendStringMessage);
        sendMessage.setChatId(user.getId());

        customerFeign.sendMessage(sendMessage);
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
