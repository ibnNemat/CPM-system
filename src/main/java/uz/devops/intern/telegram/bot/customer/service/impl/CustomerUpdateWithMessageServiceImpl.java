package uz.devops.intern.telegram.bot.customer.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.*;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.redis.*;

import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.dto.PaymentHistoryDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.impl.CustomerTelegramServiceImpl;
import uz.devops.intern.service.mapper.BotTokenMapper;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.customer.service.CustomerUpdateWithCallbackQueryService;
import uz.devops.intern.telegram.bot.customer.service.CustomerUpdateWithMessageService;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.*;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
import static uz.devops.intern.constants.StringFormatConstants.*;
import static uz.devops.intern.service.utils.ResourceBundleUtils.*;
import static uz.devops.intern.telegram.bot.utils.KeyboardUtil.getLanguages;
import static uz.devops.intern.telegram.bot.utils.KeyboardUtil.sendMarkup;
import static uz.devops.intern.telegram.bot.utils.TelegramCustomerUtils.*;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.*;

@Service
@RequiredArgsConstructor
public class CustomerUpdateWithMessageServiceImpl implements CustomerUpdateWithMessageService {
    @Value("${telegram.api}")
    private String telegramAPI;
    private final EntityManager entityManager;
    private static URI uri;
    private static String backHomeMenuButton;
    private static final String DATA_INLINE_CHANGE_NAME_BUTTON = "change name";
    private static final String DATA_INLINE_CHANGE_PHONE_NUMBER_BUTTON = "change phone number";
    private static final String DATA_INLINE_CHANGE_EMAIL_BUTTON = "change email";
    private static final String DATA_INLINE_REPLENISH_BALANCE_BUTTON = "change the balance";
    private static final String DATA_BACK_TO_HOME = "back to menu";
    private static Long chatIdCreatedByManager;
    private final Logger log = LoggerFactory.getLogger(CustomerTelegramServiceImpl.class);
    private final CustomerTelegramRepository customerTelegramRepository;
    private final CustomerTelegramRedisRepository customerTelegramRedisRepository;
    private final CustomerPaymentRedisRepository customerPaymentRedisRepository;
    private final CustomersService customersService;
    private final CustomerFeign customerFeign;
    private final PaymentService paymentService;
    private final TelegramGroupService telegramGroupService;
    private final PaymentHistoryService paymentHistoryService;
    private final CallbackRedisRepository callbackRedisRepository;
    private final UserService userService;
    private final BotTokenService botTokenService;
    private final BotTokenMapper botTokenMapper;
    private final CustomerUpdateWithCallbackQueryService callbackQueryService;
    private static ResourceBundle resourceBundle;

    @Override
    public SendMessage responseFromStartCommandWithChatId(Update update, URI telegramURI, String chatId){
        log.info("response from startCommandWithChatId. Params update: {} | telegramURI: {} | chatId: {}", update, telegramURI, chatId);
        String botId = chatId.substring(7);
        chatIdCreatedByManager = Long.parseLong(botId);
        return commandWithUpdateMessage(update, telegramURI);
    }
    @Override
    public SendMessage commandWithUpdateMessage(Update update, URI telegramUri) {
        log.info("Started working method commandWithUpdateMessage. Params update: {} | telegramURI: {}", update, telegramUri);
        uri = telegramUri;
        Message message = update.getMessage();
        User telegramUser = message.getFrom();
        String requestMessage = message.getText();

        if (message.getText() == null) {
            requestMessage = message.getContact().getPhoneNumber();
            requestMessage = "+" + requestMessage;
        }

        return executeCommandStepByStep(telegramUser, requestMessage, update);
    }

    @Override
    public SendMessage executeCommandStepByStep(User telegramUser, String requestMessage, Update update) {
        log.info("Started working method executeCommandStepByStep. Params requestMessage: {}, telegramUser: {}, update: {}",
            requestMessage, telegramUser, update);
        Optional<CustomerTelegram> optional = customerTelegramRepository.findByTelegramId(telegramUser.getId());

        if (optional.isPresent() && optional.get().getManager()){
            log.warn("Send forbidden message. Not found 'Customer' role for CustomerTelegram: {}", optional.get());
            return forbiddenMessageWithoutButton(telegramUser);
        }

        if (optional.isPresent()) {
            CustomerTelegram customerTelegram = optional.get();
            customerTelegram.setIsActive(true);
            resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
            setCustomerTelegramStep(customerTelegram, requestMessage);

            return switch (customerTelegram.getStep()) {
                case 1 -> registerCustomerClientAndShowCustomerMenu(requestMessage, telegramUser, customerTelegram);
                case 2 -> mainCommand(requestMessage, telegramUser, customerTelegram, update.getMessage());
                case 3 -> payRequestForService(requestMessage, telegramUser, customerTelegram);
                case 4 -> changeEmail(requestMessage, telegramUser, customerTelegram);
                case 5 -> changePhoneNumber(requestMessage, telegramUser, customerTelegram);
                case 6 -> replenishBalance(requestMessage, telegramUser, customerTelegram);
                case 7 -> changeFullName(requestMessage, telegramUser, customerTelegram);
                default -> sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_UNKNOWN_COMMAND));
            };
        }

        if (!getLanguages().containsKey(requestMessage)){
            resourceBundle = getResourceBundleUsingTelegramUser(telegramUser);
            String responseString = resourceBundle.getString(BOT_CHOICE_LANGUAGE);
            ReplyKeyboardMarkup replyKeyboardMarkup = KeyboardUtil.language();

            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                telegramUser.getId(), responseString, update);
            return sendMessage(telegramUser.getId(), responseString, replyKeyboardMarkup);
        }

        CustomerTelegram customerTelegram = createCustomerTelegramToSaveDatabase(telegramUser);
        customerTelegram.setLanguageCode(getLanguages().get(requestMessage));

        entityManager.detach(customerTelegram);
        Optional<TelegramGroup> telegramGroupOptional = telegramGroupService.findByChatId(chatIdCreatedByManager);

        if (telegramGroupOptional.isPresent()) {
            TelegramGroup telegramGroup = new TelegramGroup();
            telegramGroup.setId(telegramGroupOptional.get().getId());
            customerTelegram.setTelegramGroups(Set.of(telegramGroup));
        }

        customerTelegramRepository.save(customerTelegram);
        return registerCustomerClientAndShowCustomerMenu(requestMessage, telegramUser, customerTelegram);
    }

    private void setCustomerTelegramStep(CustomerTelegram customerTelegram, String requestMessage){
        if (requestMessage.startsWith("/start ")) customerTelegram.setStep(1);
    }

    private SendMessage sendMessageIfPhoneNumberIsNull(User telegramUser, String message) {
        Map<String, String> languageMap = getLanguages();
        if (languageMap.containsKey(message)) resourceBundle = getResourceBundleUsingLanguageCode(languageMap.get(message));
        else resourceBundle = getResourceBundleUsingTelegramUser(telegramUser);
        String sendStringMessage = resourceBundle.getString(BOT_REQUEST_PHONE_NUMBER);

        SendMessage sendMessage = sendMessage(telegramUser.getId(), sendStringMessage, sendMarkup(telegramUser));
        log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage.getText());
        return sendMessage;
    }

    private Customers checkCustomerPhoneNumber(String phoneNumber) {
        log.info("Checking customer phoneNumber : {}", phoneNumber);
        Optional<Customers> customerOptional = customersService.findByPhoneNumber(phoneNumber);
        if (customerOptional.isEmpty()) {
            return null;
        }
        return customerOptional.get();
    }
    @Override
    public SendMessage registerCustomerClientAndShowCustomerMenu(String requestMessage, User telegramUser, CustomerTelegram customerTelegram) {
        log.info("Started working method registration of customers and show bot menu.\n" +
            "Params requestMessage: {}, telegramUser: {}, customerTelegram: {}", requestMessage, telegramUser, customerTelegram);
        SendMessage sendMessage = new SendMessage();
        Optional<CustomerTelegramRedis> redisOptional = customerTelegramRedisRepository.findById(telegramUser.getId());

        if (!requestMessage.startsWith("+998") && customerTelegram.getCustomer() == null) {
            log.warn("send not found message. Because there is null customer phone number");
            return sendMessageIfPhoneNumberIsNull(telegramUser, requestMessage);
        }
        // when customer entered from another telegram bot
        if (customerTelegram.getPhoneNumber() != null && chatIdCreatedByManager != null) {
            resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
            Set<TelegramGroup> customerTelegramGroups = customerTelegram.getTelegramGroups();
            entityManager.detach(customerTelegram);

            Optional<TelegramGroup> telegramGroupOptional = telegramGroupService.findByChatId(chatIdCreatedByManager);

            if (telegramGroupOptional.isPresent()) {
                Set<TelegramGroup> newCustomerTelegramGroups = new HashSet<>(customerTelegramGroups);
                TelegramGroup telegramGroup = new TelegramGroup();

                telegramGroup.setId(telegramGroupOptional.get().getId());
                newCustomerTelegramGroups.add(telegramGroup);
                customerTelegram.setTelegramGroups(newCustomerTelegramGroups);
                customerTelegramRepository.save(customerTelegram);
            }
            return callbackQueryService.sendCustomerMenu(telegramUser, customerTelegram);
        }

        Customers customer = checkCustomerPhoneNumber(requestMessage);
        if (customer == null) {
            String sendStringMessage = resourceBundle.getString(BOT_CUSTOMER_NOT_REGISTERED);
            sendMessage = sendMessage(telegramUser.getId(), sendStringMessage, sendMarkup(telegramUser));
            log.warn("send message customer not registered yet User id: {} | Message text: {}", telegramUser, sendMessage);
            return sendMessage;
        }

        if (checkRoleTelegramCustomer(customer)) return forbiddenMessage(telegramUser);

        customerTelegram.customer(customer);
        customerTelegramRepository.save(customerTelegram);

        customerTelegram.setPhoneNumber(requestMessage);
        customerTelegram.setStep(2);
        customerTelegramRepository.save(customerTelegram);
        log.info("Phone number successfully set to existing user! telegram user : {} | phoneNumber: {} ", customerTelegram, requestMessage);

        sendMessage.setChatId(telegramUser.getId());
        sendMessage.setText(resourceBundle.getString(BOT_DEAR_CUSTOMER) + " " + telegramUser.getFirstName() +
            resourceBundle.getString(BOT_SUCCESSFULLY_REGISTRATION));

        customerFeign.sendMessage(uri, sendMessage);

        if (redisOptional.isEmpty()) {
            CustomerTelegramRedis customerTelegramRedis = new CustomerTelegramRedis(telegramUser.getId(), telegramUser);
            customerTelegramRedisRepository.save(customerTelegramRedis);
            log.info("New telegram user successfully saved to redis! UserRedis : {}", customerTelegramRedis);
        }
        sendMessage = callbackQueryService.sendCustomerMenu(telegramUser, customerTelegram);
        log.info("successfully send customerMenu. SendMessage: {}", sendMessage);
        return sendMessage;
    }

    public Boolean checkRoleTelegramCustomer(Customers customer){
        Authority customerAuthority = new Authority();
        customerAuthority.setName("ROLE_CUSTOMER");
        return customer.getUser() == null || !customer.getUser().getAuthorities().contains(customerAuthority);
    }

    @Override
    public SendMessage forbiddenMessage(User telegramUser){
        ResourceBundle resourceBundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(telegramUser.getLanguageCode());
        String sendStringMessage = "\uD83D\uDEAB " + resourceBundle.getString( BOT_AUTHORITY_NOT_EXISTS);
        SendMessage sendMessage = sendMessage(telegramUser.getId(), sendStringMessage, sendMarkup(telegramUser));
        log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage);
        return sendMessage;
    }

    private SendMessage forbiddenMessageWithoutButton(User telegramUser){
        ResourceBundle resourceBundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(telegramUser.getLanguageCode());
        String sendStringMessage = "\uD83D\uDEAB " + resourceBundle.getString( BOT_AUTHORITY_NOT_EXISTS);
        SendMessage sendMessage = sendMessage(telegramUser.getId(), sendStringMessage);
        log.info("Message send successfully! User id: {} | Message text: {}", telegramUser, sendMessage);
        return sendMessage;
    }

    @Override
    public SendMessage sendCustomerGroups(User telegramUser, CustomerTelegram customerTelegram) {
        log.info("started working method sendCustomerGroups with params telegramUser: {} | customerTelegram: {}", telegramUser, customerTelegram);
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        SendMessage sendMessage;
        Customers customer = customerTelegram.getCustomer();
        if (customer == null || customer.getGroups() == null){
            log.warn("send data not found message. Because there is null customer or customer groups");
            return sendCustomerDataNotFoundMessage(telegramUser, customerTelegram);
        }

        StringBuilder buildCustomerGroups = new StringBuilder();
        for (Groups group : customer.getGroups()) {
            buildCustomerGroups.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_GROUP_NAME), group.getName()));
            buildCustomerGroups.append(String.format(STRING_FORMAT_TWO_TEXT + "\n", resourceBundle.getString(BOT_ORGANIZATION_NAME), group.getOrganization().getName()));
            buildCustomerGroups.append("------------------------------------\n");
            buildCustomerGroups.append(String.format("          <b>%s</b>\n\n", resourceBundle.getString(BOT_GROUP_PEOPLE)));

            for (Customers groupCustomer : group.getCustomers()) {
                buildCustomerGroups.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_PEOPLE_NAME), groupCustomer.getUsername()));
                buildCustomerGroups.append(String.format(STRING_FORMAT_TWO_TEXT + "\n",resourceBundle.getString(BOT_PEOPLE_PHONE_NUMBER), groupCustomer.getPhoneNumber()));
            }
            sendMessage = sendMessage(telegramUser.getId(), buildCustomerGroups.toString());
            sendMessage.enableHtml(true);
            customerFeign.sendMessage(uri, sendMessage);
            buildCustomerGroups = new StringBuilder();
        }

        log.info("successfully send customerGroups");
        return new SendMessage();
    }

    @Override
    // send Customer Payments where paid is false
    public SendMessage sendCustomerPayments(User telegramUser, CustomerTelegram customerTelegram, Message message) {
        log.info("started working method sendCustomerPayments with params telegramUser: {} | customerTelegram: {} | message: {}",
            telegramUser, customerTelegram, message);
        Customers customer = customerTelegram.getCustomer();
        if (customer == null || customer.getGroups() == null) {
            log.warn("send data not found message. Because there is null customer or customer groups");
            return sendCustomerDataNotFoundMessage(telegramUser, customerTelegram);
        }
        List<PaymentDTO> paymentDTOList = paymentService.getAllCustomerPaymentsPayedIsFalse(customer);
        if (paymentDTOList.size() == 0) {
            log.warn("send data not found message. Because there is null paymentDTOList");
            return sendCustomerDataNotFoundMessage(telegramUser, customerTelegram);
        }
        StringBuilder builderCustomerPayments = new StringBuilder();
        customerPaymentRedisRepository.save(new CustomerPaymentRedis(customerTelegram.getId(), paymentDTOList));
        buildCustomerPayments(paymentDTOList.get(0), builderCustomerPayments, customerTelegram.getLanguageCode());

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);

        customerFeign.sendMessage(uri, sendMessage(telegramUser.getId(), "💸", replyKeyboardRemove));
        int sizePaymentList = paymentDTOList.size();
        InlineKeyboardMarkup markup;

        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
        if (sizePaymentList > 1){
            Long indexOfLeftCustomerPayment = paymentDTOList.get(sizePaymentList-1).getId();
            Long indexOfRightCustomerPayment = paymentDTOList.get(1).getId();
            markup = paymentOfCustomerInlineMarkupWithLeftAndRightButton(paymentDTOList.get(0), indexOfLeftCustomerPayment, indexOfRightCustomerPayment, customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME);
        }else{
            markup = onlyOnePaymentOfCustomerInlineMarkup(paymentDTOList.get(0), customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME);
        }

        SendMessage sendMessage = sendMessage(telegramUser.getId(), builderCustomerPayments.toString(), markup);
        log.info("successfully send customerPayments message: {}", sendMessage);
        return sendMessage;
    }

    private void addCustomerTelegramToSecurityContextHolder(CustomerTelegram customerTelegram) {
        log.info("setting authentication to securityContextHolder for new telegram customer: {}", customerTelegram);
        Customers customer = customerTelegram.getCustomer();
        uz.devops.intern.domain.User authenticatedUser = customer.getUser();

        Iterator<Authority> authorityIterator = authenticatedUser.getAuthorities().iterator();
        Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = new HashSet<>();
        while (authorityIterator.hasNext()) {
            Authority authority = authorityIterator.next();
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority.getName());
            simpleGrantedAuthoritySet.add(simpleGrantedAuthority);
        }

        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(
            authenticatedUser.getLogin(), authenticatedUser.getPassword(), simpleGrantedAuthoritySet
        );

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityUser, "", simpleGrantedAuthoritySet);
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("Successfully set authentication to securityContextHolder for new telegram customer. Param customerTelegram: {}", customerTelegram);
    }
    @Override
    public SendMessage sendAllCustomerPayments(User telegramUser, CustomerTelegram customerTelegram) {
        log.info("started working method send customer all payments with params telegramUser: {} | customerTelegram: {}",
            telegramUser, customerTelegram);
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        Customers customer = customerTelegram.getCustomer();
        if (customer == null || customer.getGroups() == null) {
            log.warn("send data not found message. Because there is null customer or customer groups");
            return sendCustomerDataNotFoundMessage(telegramUser, customerTelegram);
        }
        addCustomerTelegramToSecurityContextHolder(customerTelegram);
        ResponseDTO<List<PaymentDTO>> listResponseDTO = paymentService.getAllCustomerPayments();
        if (listResponseDTO.getResponseData() == null) {
            log.warn("send data not found message. Because there is null paymentListResponse");
            return sendCustomerDataNotFoundMessage(telegramUser, customerTelegram);
        }
        List<PaymentDTO> paymentDTOList = listResponseDTO.getResponseData();
        for (PaymentDTO payment : paymentDTOList) {
            StringBuilder buildCustomerPayments = new StringBuilder();
            buildCustomerPayments(payment, buildCustomerPayments, customerTelegram.getLanguageCode());
            String status;
            if (payment.getIsPaid()) status = resourceBundle.getString(BOT_FULL_PAID);
            else if(payment.getPaidMoney().equals(0D)) status = resourceBundle.getString(BOT_NOT_PAID);
            else status = resourceBundle.getString(BOT_PARTIALLY_PAID);
            buildCustomerPayments.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_STATUS), status));

            customerFeign.sendMessage(uri, sendMessage(telegramUser.getId(), buildCustomerPayments.toString()));
        }
        log.info("successfully send customerPayments");
        return new SendMessage();
    }

    @Override
    public SendMessage sendCustomerPaymentsHistory(User telegramUser, CustomerTelegram customerTelegram) {
        log.info("started working method send customerPaymentsHistory with params telegramUser: {} | customerTelegram: {}",
            telegramUser, customerTelegram);
        Customers customer = customerTelegram.getCustomer();
        if (customer == null || customer.getGroups() == null) {
            log.warn("send data not found message. Because there is null customer or customer groups");
            return sendCustomerDataNotFoundMessage(telegramUser, customerTelegram);
        }

        List<PaymentHistory> paymentHistoryList = paymentHistoryService.getTelegramCustomerPaymentHistories(customer);
        if (paymentHistoryList.size() == 0) {
            log.warn("send data not found message. Because there is no customerPaymentsHistory");
            return sendCustomerDataNotFoundMessage(telegramUser, customerTelegram);
        }
        StringBuilder buildCustomerPaymentHistories = new StringBuilder();

        for (PaymentHistory paymentHistory : paymentHistoryList) {
            buildPaymentHistoryMessage(paymentHistory, buildCustomerPaymentHistories, resourceBundle);
            SendMessage sendMessage = sendMessage(telegramUser.getId(), buildCustomerPaymentHistories.toString());
            customerFeign.sendMessage(uri, sendMessage);
            buildCustomerPaymentHistories = new StringBuilder();
        }
        log.info("successfully send customerPaymentsHistory");
        return new SendMessage();
    }

    @Override
    public SendMessage showCustomerProfile(User telegramUser, CustomerTelegram customerTelegram) {
        log.info("started working method send showCustomerProfile with params telegramUser: {} | customerTelegram: {}",
            telegramUser, customerTelegram);
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        StringBuilder customerProfileBuilder = new StringBuilder();

        uz.devops.intern.domain.User jhi_user = customerTelegram.getCustomer().getUser();
        Customers customer = customerTelegram.getCustomer();

        customerProfileBuilder.append(String.format(STRING_FORMAT_ONE_TEXT + "\n", resourceBundle.getString(BOT_CUSTOMER_PROFILE)));
        customerProfileBuilder.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_CUSTOMER_FIRSTNAME), jhi_user.getFirstName()));
        customerProfileBuilder.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_CUSTOMER_SURNAME), jhi_user.getLastName()));
        customerProfileBuilder.append(String.format("<b>Email: </b> %s\n", jhi_user.getEmail()));
        customerProfileBuilder.append(String.format(STRING_FORMAT_TWO_TEXT,  resourceBundle.getString(BOT_PEOPLE_PHONE_NUMBER), customerTelegram.getPhoneNumber()));
        customerProfileBuilder.append(String.format(STRING_FORMAT_ONE_TEXT_ONE_FLOAT, resourceBundle.getString(BOT_CUSTOMER_BALANCE), customer.getBalance()));

        BotTokenDTO botTokenDTO = botTokenService.findByChatId(customerTelegram.getChatId());
        Optional<TelegramGroup> telegramGroupOptional = telegramGroupService.findByChatId(customerTelegram.getChatId());
        if (botTokenDTO != null && telegramGroupOptional.isPresent()) {
            BotToken botToken = botTokenMapper.toEntity(botTokenDTO);
            TelegramGroup telegramGroup = telegramGroupOptional.get();
            uz.devops.intern.domain.User managerUser = botToken.getCreatedBy();

            customerProfileBuilder.append(String.format("\n" + STRING_FORMAT_ONE_TEXT, resourceBundle.getString(BOT_TELEGRAM_GROUP)));
            customerProfileBuilder.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_GROUP_NAME), telegramGroup.getName()));
            customerProfileBuilder.append(String.format(STRING_FORMAT_THREE_TEXT, resourceBundle.getString(BOT_GROUP_MANAGER_NAME), managerUser.getFirstName(), managerUser.getLastName()));
            customerProfileBuilder.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_GROUP_LINK), botToken.getUsername()));
        }

        InlineKeyboardButton changeFullNameButton = new InlineKeyboardButton(resourceBundle.getString(BOT_CHANGE_NAME_BUTTON));
        changeFullNameButton.setCallbackData(DATA_INLINE_CHANGE_NAME_BUTTON);

        InlineKeyboardButton changeEmailButton = new InlineKeyboardButton(resourceBundle.getString(BOT_CHANGE_EMAIL_BUTTON));
        changeEmailButton.setCallbackData(DATA_INLINE_CHANGE_EMAIL_BUTTON);

        InlineKeyboardButton changePhoneNumberButton = new InlineKeyboardButton(resourceBundle.getString(BOT_CHANGE_PHONE_NUMBER_BUTTON));
        changePhoneNumberButton.setCallbackData(DATA_INLINE_CHANGE_PHONE_NUMBER_BUTTON);

        InlineKeyboardButton replenishBalanceButton = new InlineKeyboardButton(resourceBundle.getString(BOT_CHANGE_BALANCE_BUTTON));
        replenishBalanceButton.setCallbackData(DATA_INLINE_REPLENISH_BALANCE_BUTTON);

        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
        InlineKeyboardButton backHomeButton = new InlineKeyboardButton(backHomeMenuButton);
        backHomeButton.setCallbackData(DATA_BACK_TO_HOME);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(
                List.of(List.of(changeFullNameButton),
                List.of(changePhoneNumberButton),
                List.of(changeEmailButton, replenishBalanceButton),
                List.of(backHomeButton))
        );

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);

        customerFeign.sendMessage(uri, sendMessage(telegramUser.getId(), "\uD83D\uDE4B\u200D♂️", replyKeyboardRemove));
        log.info("successfully send customerProfile");
        return sendMessage(telegramUser.getId(), customerProfileBuilder.toString(), markup);
    }

    @Override
    public SendMessage mainCommand(String buttonMessage, User telegramUser, CustomerTelegram customerTelegram, Message message) {
        log.info("started working method mainCommand.\n" +
                "Params telegramUser: {} | customerTelegram: {} | buttonMessage: {}", telegramUser, customerTelegram, buttonMessage);
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        String paymentHistoryReplyButton = resourceBundle.getString(BOT_PAYMENT_HISTORY_BUTTON);
        String paymentReplyButton = resourceBundle.getString(BOT_PAYMENT_BUTTON);
        String payReplyButton = resourceBundle.getString(BOT_DEBTS_BUTTON);
        String myProfileReplyButton =  resourceBundle.getString(BOT_MY_PROFILE_BUTTON);
        String groupReplyButton = resourceBundle.getString(BOT_GROUP_BUTTON);
        String backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);


        if(groupReplyButton.equals(buttonMessage)) return sendCustomerGroups(telegramUser, customerTelegram);
        if(payReplyButton.equals(buttonMessage)) return sendCustomerPayments(telegramUser, customerTelegram, message);
        if(paymentReplyButton.equals(buttonMessage)) return  sendAllCustomerPayments(telegramUser, customerTelegram);
        if(paymentHistoryReplyButton.equals(buttonMessage)) return sendCustomerPaymentsHistory(telegramUser, customerTelegram);
        if(myProfileReplyButton.equals(buttonMessage)) return showCustomerProfile(telegramUser, customerTelegram);
        if(backHomeMenuButton.equals(buttonMessage)) return callbackQueryService.sendCustomerMenu(telegramUser, customerTelegram);

        log.warn("sendMessage unknown command");
        return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_UNKNOWN_COMMAND));
    }


    @Override
    public SendMessage payRequestForService(String paymentSum, User telegramUser, CustomerTelegram customerTelegram) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        SendMessage sendMessage;
        try {
            double requestPaymentSum = Double.parseDouble(paymentSum);
            if (requestPaymentSum < 1000)
                return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_SUM_LESS_THAN_ENOUGH));
            if (requestPaymentSum > 100_000_000)
                return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_SUM_GREATER_THAN_ENOUGH));

            Optional<CallbackDataRedis> callbackDataRedisOptional = callbackRedisRepository.findById(telegramUser.getId());
            if (callbackDataRedisOptional.isEmpty()) {
                sendMessage = sendMessage(telegramUser.getId(),  resourceBundle.getString(BOT_NOT_FOUND_FROM_REDIS));
                customerFeign.sendMessage(uri, sendMessage);
                log.warn("send not found message. Because there is no customer callback data redis ");
                return callbackQueryService.sendCustomerMenu(telegramUser, customerTelegram);
            }

            CallbackDataRedis redis = callbackDataRedisOptional.get();
            String stringId = redis.getCallbackDate().substring(8);
            Long id = Long.parseLong(stringId);

            Optional<PaymentDTO> paymentOptional = paymentService.findOne(id);
            if (paymentOptional.isEmpty()) {
                customerFeign.sendMessage(
                    uri, sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_NOT_FOUND_PAYMENT))
                );
                log.warn("send not found message. Because there is no customer payment");
                return callbackQueryService.sendCustomerMenu(telegramUser, customerTelegram);
            }

            PaymentDTO paymentDTO = paymentOptional.get();
            paymentDTO.setPaidMoney(requestPaymentSum);

            addCustomerTelegramToSecurityContextHolder(customerTelegram);
            ResponseDTO<PaymentHistoryDTO> responsePayment = paymentService.payForService(paymentDTO);
            backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
            if (responsePayment.getSuccess() && responsePayment.getCode().equals(0) && responsePayment.getResponseData() != null) {
                customerTelegram.setStep(2);
                customerTelegramRepository.save(customerTelegram);

                PaymentHistoryDTO paymentHistoryDTO = responsePayment.getResponseData();
                String successMessage = resourceBundle.getString(BOT_PAID_FOR_PAYMENT) + " ✅";

                String inlineButtonShowCurrentPayment = resourceBundle.getString(BOT_CURRENT_PAYMENT_BUTTON);
                InlineKeyboardButton showCurrentPaymentButton = new InlineKeyboardButton(inlineButtonShowCurrentPayment);
                showCurrentPaymentButton.setCallbackData("show current payment " + paymentHistoryDTO.getId());

                InlineKeyboardButton backHomeMenuInlineButton = new InlineKeyboardButton(backHomeMenuButton);
                backHomeMenuInlineButton.setCallbackData(DATA_BACK_TO_HOME);

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                inlineKeyboardMarkup.setKeyboard(List.of(List.of(showCurrentPaymentButton, backHomeMenuInlineButton)));
                customerTelegram.setStep(2);
                customerTelegramRepository.save(customerTelegram);

                sendMessage = sendMessage(telegramUser.getId(), successMessage, inlineKeyboardMarkup);
                log.info("send successfully paid message: {}", sendMessage);
                return sendMessage;
            }
            if (responsePayment.getCode().equals(-5)) {
                log.warn("send message not enough money while paying. entered money: {}", paymentSum);
                return sendMessage(telegramUser.getId(), "\uD83D\uDEAB " + resourceBundle.getString(BOT_NOT_ENOUGH_MONEY));
            }
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(backHomeMenuButton));

            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
            markup.setResizeKeyboard(true);
            markup.setKeyboard(List.of(keyboardRow));

            customerTelegram.setStep(2);
            customerTelegramRepository.save(customerTelegram);

            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_NOT_FOUND_MESSAGE), markup);
        } catch (NumberFormatException numberFormatException) {
            log.warn("send message wrong entered money while paying. entered money: {}", paymentSum);
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_WRONG_ENTERED_MONEY));
        }
    }
    @Override
    public SendMessage changeEmail(String email, User telegramUser, CustomerTelegram customerTelegram) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        if (!email.endsWith("@gmail.com")) {
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_INVALID_EMAIL));
        }

        Optional<uz.devops.intern.domain.User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            log.warn("send message already existing customer email. CustomerTelegram email: {}", email);
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_EXISTING_EMAIL));
        }
        Customers customer = customerTelegram.getCustomer();
        uz.devops.intern.domain.User jhi_user = customer.getUser();

        addCustomerTelegramToSecurityContextHolder(customerTelegram);
        userService.updateUser(jhi_user.getFirstName(), jhi_user.getLastName(), email, jhi_user.getLangKey(), jhi_user.getImageUrl());
        customerTelegram.setStep(2);
        customerTelegramRepository.save(customerTelegram);
        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
        SendMessage sendMessage = sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_EMAIL_CHANGED), backToMenuInlineButton(customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME));
        log.info("successfully changed email: {} | customer: {}", email, customer);
        return sendMessage;
    }

    @Override
    public SendMessage changePhoneNumber(String phoneNumber, User telegramUser, CustomerTelegram customerTelegram) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        int count = 0;
        for (char ch : phoneNumber.toCharArray()) {
            if (Character.isLetter(ch)) {
                log.warn("send invalid phone number message. Entered phone number: {}", phoneNumber);
                return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_INVALID_PHONE_NUMBER));
            }
            count++;
        }
        if (count != 13){
            log.warn("send invalid phone number message. Entered phone number: {}", phoneNumber);
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_INVALID_PHONE_NUMBER));
        }
        if (!phoneNumber.startsWith("+998")) {
            log.warn("send invalid phone number message. Entered phone number: {}", phoneNumber);
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_INVALID_PHONE_NUMBER));
        }

        Optional<Customers> customerOptional = customersService.findByPhoneNumber(phoneNumber);
        if (customerOptional.isPresent()) {
            log.warn("send message already existing phone number. Entered phone number: {}", phoneNumber);
            return sendMessage(telegramUser.getId(), "❌ " + resourceBundle.getString(BOT_EXISTING_PHONE_NUMBER));
        }
        Customers customer = customerTelegram.getCustomer();
        customer.setPhoneNumber(phoneNumber);
        customerTelegram.setStep(2);
        customerTelegram.setPhoneNumber(phoneNumber);
        customerTelegramRepository.save(customerTelegram);
        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
        log.info("successfully changed phone number: {} | customer: {}", phoneNumber, customer);
        return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_CHANGED_PHONE_NUMBER),
            backToMenuInlineButton(customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME));
    }

    private SendMessage replenishBalance(String summa, User telegramUser, CustomerTelegram customerTelegram) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        try {
            double money = Double.parseDouble(summa);
            if (money <= 0){
                log.warn("send message entered not enough money: {}", money);
                return sendMessage(telegramUser.getId(),  "❌ " + resourceBundle.getString(BOT_SEND_MONEY_GREATER_ZERO));
            }
            Customers customer = customerTelegram.getCustomer();
            customersService.replenishCustomerBalance(money, customer.getId());
            customerTelegram.setStep(2);
            customerTelegramRepository.save(customerTelegram);

            backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
            log.info("successfully replenished customer balance. Customer: {} | Entered money: {}", customer, money);
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_REPLENISH_BALANCE) +" ✅",
                backToMenuInlineButton(customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME));
        } catch (NumberFormatException e) {
            return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_WRONG_ENTERED_MONEY));
        }
    }

    @Override
    public SendMessage changeFullName(String fullName, User telegramUser, CustomerTelegram customerTelegram) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        String[] newFullName = fullName.split(" ");
        if (newFullName.length < 2) {
            log.warn("send message not entered full name: {}", fullName);
            return sendMessage(telegramUser.getId(), "❌ " + resourceBundle.getString(BOT_NOT_ENTERED_FULL_NAME));
        }
        Optional<uz.devops.intern.domain.User> userOptional = userService.findByFirstName(newFullName[0]);
        if (userOptional.isPresent()){
            log.warn("send message already existing full name. Entered name: {}", fullName);
            return sendMessage(telegramUser.getId(), "❌ " + resourceBundle.getString(BOT_EXISTING_NAME));
        }

        Customers customer = customerTelegram.getCustomer();
        uz.devops.intern.domain.User jhi_user = customer.getUser();
        addCustomerTelegramToSecurityContextHolder(customerTelegram);
        userService.updateUser(newFullName[0], newFullName[1], jhi_user.getEmail(), jhi_user.getLangKey(), jhi_user.getImageUrl());
        customerTelegram.setStep(2);
        customerTelegramRepository.save(customerTelegram);
        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
        log.info("successfully changed customer full name. Customer: {} | Entered name: {}", customer, fullName);
        return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_CHANGED_NAME) + " ✅",
            backToMenuInlineButton(customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME));
    }
}
