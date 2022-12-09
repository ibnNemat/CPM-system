package uz.devops.intern.service.impl;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.*;
import uz.devops.intern.domain.User;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.feign.CustomerFeignClient;
import uz.devops.intern.redis.ServicesRedisDTO;
import uz.devops.intern.redis.ServicesRedisRepository;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.repository.UserRepository;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.WebhookResponseDTO;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;


import javax.persistence.EntityManager;

import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminTgServiceImpl implements AdminTgService {

    @Value("${ngrok.url}")
    private String WEBHOOK_URL;
    @Autowired
    private EntityManager entityManager;
    private static final String telegramAPI = "https://api.telegram.org/bot";
    private final Logger log = LoggerFactory.getLogger(AdminTgServiceImpl.class);
    private final TelegramGroupService telegramGroupService;
    private final OrganizationService organizationService;
    private final GroupsService groupsService;
    private final ServicesService servicesService;

    private final CustomerTelegramService customerTelegramService;
    private final BotTokenRepository botTokenRepository;
    private final BotTokenService botTokenService;
    private final CustomerTelegramRepository customerTelegramRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PaymentService paymentService;
    private final AdminFeign adminFeign;
    private final CustomerFeignClient customerFeign;
    private final ServicesRedisRepository servicesRedisRepository;

    public AdminTgServiceImpl(TelegramGroupService telegramGroupService, OrganizationService organizationService, GroupsService groupsService, ServicesService servicesService, CustomerTelegramService customerTelegramService, BotTokenRepository botTokenRepository, BotTokenService botTokenService, CustomerTelegramRepository customerTelegramRepository, UserRepository userRepository, UserService userService, PaymentService paymentService, AdminFeign adminFeign, CustomerFeignClient customerFeign, ServicesRedisRepository servicesRedisRepository) {
        this.telegramGroupService = telegramGroupService;
        this.organizationService = organizationService;
        this.groupsService = groupsService;
        this.servicesService = servicesService;
        this.customerTelegramService = customerTelegramService;
        this.botTokenRepository = botTokenRepository;
        this.botTokenService = botTokenService;
        this.customerTelegramRepository = customerTelegramRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.paymentService = paymentService;
        this.adminFeign = adminFeign;
        this.customerFeign = customerFeign;
        this.servicesRedisRepository = servicesRedisRepository;
    }

    @Override
    public void main(Update update) {
        Long userId = update.getMessage() != null?
            update.getMessage().getChatId():
            update.getCallbackQuery().getFrom().getId();
        Optional<CustomerTelegram> customerOptional = customerTelegramRepository.findByTelegramId(userId);
        if(customerOptional.isEmpty()){
            // Bazadan user ma'lumotlar topilmasa shu if ichiga kiradi.
            String messageText = update.getMessage().getText();
            if(messageText.equals("/start")){
                pressedStartCommand(update.getMessage());
            }else if(messageText.equals("/help")){
                System.out.println("hello world!");
            }else {
                wrongValue(update.getMessage().getFrom().getId(), "Mavjud bo'lamagan buyruq");
            }
        }else{
            CustomerTelegram customer = customerOptional.get();
            Integer step = customer.getStep();
            boolean isSuccess = false;
            if(update.hasMessage()) {
                if (step == 1) {
                    isSuccess = getLanguage(update.getMessage(), customer);
                    if (isSuccess) updateCustomerStep(customer, 2);
                } else if (step == 2) {
                    isSuccess = verifyAdminByPhoneNumber(update.getMessage(), customer);
                    if (isSuccess) updateCustomerStep(customer, 3);
                } else if (step == 3) {
                    isSuccess = getAdminBotToken(update.getMessage(), customer);
                    if (isSuccess) updateCustomerStep(customer, 4);
                } else if (step == 4) {
                    boolean isCorrect = menu(update.getMessage(), customer);
                    if (isCorrect) customerTelegramRepository.save(customer);
                } else if (step == 5) {
                    isSuccess = addOrganization(update.getMessage(), customer);
                    if (isSuccess) updateCustomerStep(customer, 4);
                } else if (step == 8){
                    isSuccess = getServiceName(update.getMessage());
                    if (isSuccess) updateCustomerStep(customer, 9);
                } else if (step == 9){
                    isSuccess = getServicePrice(update.getMessage());
                    if (isSuccess) updateCustomerStep(customer, 10);
                } else if (step == 10){
                    isSuccess = getServiceStartedTime(update.getMessage());
                    if (isSuccess) updateCustomerStep(customer, 11);
                } else if (step == 11){
                    isSuccess = getServicePeriodType(update.getMessage());
                    if (isSuccess) updateCustomerStep(customer, 12);
                } else if (step == 12){
                    isSuccess = getPeriodCount(update.getMessage(), customer.getPhoneNumber());
                    if (isSuccess) updateCustomerStep(customer, 13);
                }
            }else if(update.hasCallbackQuery()) {
                if (step == 6) {
                        isSuccess = addGroup(update.getCallbackQuery(), customer);
                        if (isSuccess) updateCustomerStep(customer, 7);
                } else if (step == 7) {
                        isSuccess = chooseOrganization(update.getCallbackQuery(), customer);
                        if (isSuccess) updateCustomerStep(customer, 4);
                } else if (step == 13){
                    isSuccess = markGroups(update.getCallbackQuery());
                    if (isSuccess) updateCustomerStep(customer, 4);
                }
            }
            if(isSuccess)customerTelegramRepository.save(customer);
        }
    }

    @Override
    public void pressedStartCommand(Message message) {
        Long userId = message.getFrom().getId();
        String messageText = message.getText();

        CustomerTelegramDTO manager =
            customerTelegramService.findByTelegramId(message.getFrom().getId());

        if(manager == null) {
            String newMessage = "Iltimos tilni tanlang\uD83D\uDC47";
            ReplyKeyboardMarkup markup = KeyboardUtil.language();
            SendMessage sendMessage = TelegramsUtil.sendMessage(userId, newMessage, markup);
            Update update = adminFeign.sendMessage(sendMessage);
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                userId, messageText, update);
            CustomerTelegram customer = createCustomerTelegramToSaveDatabase(message.getFrom());
            customerTelegramRepository.save(customer);
        }else {
            wrongValue(manager.getTelegramId(), "Foydalanuvchi ro'yxatdan o'tgan");
        }
    }

    @Override
    public Boolean getLanguage(Message message, CustomerTelegram customer) {
        if(!message.hasText()) {
            messageHasNotText(message.getFrom().getId(), message);
            return false;
        }

        Long userId = message.getFrom().getId();
        String messageText = message.getText();

        if(!KeyboardUtil.languages.contains(messageText)){
            wrongValue(userId, "Iltimos ko'rsatilgan tillardan birini tanlang\uD83D\uDE4F");
            log.info("User send invalid value while choosing language, Message: {} | Manager: {}", messageText, customer);
            return false;
        }

        customer.setLanguageCode(message.getFrom().getLanguageCode().equals("en")? "uz": "ru");
        String newMessage = "Iltimos telefon raqamingizni jo'nating\uD83D\uDC47";
        ReplyKeyboardMarkup markup = KeyboardUtil.phoneNumber();
        SendMessage sendMessage = sendMessage(userId, newMessage, markup);
        Update response = adminFeign.sendMessage(sendMessage);
        log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
            userId, messageText, response);
        return true;

    }

    private void updateCustomerStep(CustomerTelegram customer, Integer step){
        customer.setStep(step);
        customerTelegramRepository.save(customer);
        log.info("User is updated successfully! User: {}", customer);
    }

    @Override
    public Boolean verifyAdminByPhoneNumber(Message message, CustomerTelegram customer) {
        if(!message.hasContact() && !message.hasText()){
            messageHasNotText(message.getFrom().getId(), message, true);
            return false;
        }

        Long userId = message.getFrom().getId();
        String newMessage = null;

        User user = checkPhoneNumber(message);
        if(user == null){
            wrongValue(userId, "Telefon raqam noto'g'ri!");
            log.warn("Data of user is not found! Customer: {} | Message: {}",
                customer, message);
            return false;
        }

        boolean isUserManager = checkUserRole(user);
        if(!isUserManager){
            wrongValue(userId, "Sizda boshqaruvchilik huquqi yo'q!\uD83D\uDE45\uD83C\uDFFB");
            log.info("User hasn't \"Manager\" role! User: {} | Customer: {}", user, customer);
            return false;
        }

        newMessage = "Iltimos botning tokenini tashlang.";
        ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);
        SendMessage sendMessage = sendMessage(userId, newMessage, removeMarkup);
        adminFeign.sendMessage(sendMessage);
        log.info("User is verified, Phone number: {} | Customer: {}",
            user.getCreatedBy(), customer);

        customer.setPhoneNumber(user.getCreatedBy());
        return true;
    }

    @Override
    public Boolean getAdminBotToken(Message message, CustomerTelegram customer) {
        if(!message.hasText()) {
            messageHasNotText(message.getFrom().getId(), message);
            return false;
        }

        Long userId = message.getFrom().getId();
        String newBotToken = message.getText();

        if(botTokenRepository.existsByToken(newBotToken)){
            wrongValue(userId, "Bot mavjud!");
            log.warn("Bot is already exists, Bot token: {}", newBotToken);
            return false;
        }

        org.telegram.telegrambots.meta.api.objects.User bot = getBotData(newBotToken);
        if(bot == null){
            wrongValue(userId, "Botning tokeni yaroqsiz!♻️");
            log.warn("Bot token is invalid, Bot token: {} ", newBotToken);
            return false;
        }

        WebhookResponseDTO response = setWebhookToNewBot(newBotToken, bot.getId());
        String result = checkWebhookResponse(response);
        if (!result.equals("Ok")) {
            wrongValue(userId, result);
            log.info("Setting webhook is failed, Response: {} | Bot token: {} | Customer: {}",
                response, newBotToken, customer);
            return false;
        }
        saveBotEntity(bot, customer.getPhoneNumber(), newBotToken);
        String newMessage = "Tabriklaymiz, botning tokeni muvafaqiyatli saqlandi.Iltimos botni guruhga qo'shing";
        SendMessage sendMessage = sendMessage(userId, newMessage);
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    private void saveBotEntity(org.telegram.telegrambots.meta.api.objects.User bot, String phoneNumber, String newBotToken){
        UserDTO userDTO = userService.getUserByCreatedBy(phoneNumber);
        BotTokenDTO botTokenDTO = createBotEntity(bot, userDTO, newBotToken);
        botTokenService.save(botTokenDTO);
    }

    @Override
    public void checkIsBotInGroup(ChatMember user, Chat chat, String botId) {
        boolean isBot = user.getUser().getIsBot();
        BotTokenDTO botToken = botTokenService.findByChatId(Long.parseLong(botId));
        if(botToken != null){
            CustomerTelegram manager = customerTelegramRepository.findByBot(Long.parseLong(botId)).get();
            boolean isNotExistsInGroups = isNotExistsInGroups(manager.getTelegramGroups(), botToken);
            if(isNotExistsInGroups){
//                boolean isExists = telegramGroupService.getTelegramGroupRepository().existsInRelatedTableByChatId(chat.getId());
//                if (isBot && !isExists) {
                if(isBot) {
                    org.telegram.telegrambots.meta.api.objects.User bot = user.getUser();
                    sayThanksToManager(bot.getId(), manager);
                    sendInviteLink(bot, chat.getId());
                    saveToTelegramGroup(chat, manager);

//                } else if (!isBot) {
                }else{
                    wrongValue(user.getUser().getId(), "Hozirgi botni guruhga qo'shing!");
                    log.warn("Something goes wrong, Bot id: {} | Group id: {}", botId, chat.getId());
                }
            }else {
                wrongValue(manager.getTelegramId(), "Bot guruhda mavjud!");
            }
        }
    }

    @Override
    public boolean menu(Message message, CustomerTelegram customer) {
        if(!message.hasText())return false;
        String text = message.getText();
        String newMessage = null;

        if(text.equals("\uD83C\uDFE2 Yangi tashkilot")){
            newMessage = "Iltimos tashkilot nomini kiriting";
            SendMessage sendMessage = sendMessage(customer.getTelegramId(), newMessage);
            Update update = adminFeign.sendMessage(sendMessage);
            log.info("Admin is creating new organization, Manager id: {} | Message text: {} | Update: {}",
                customer.getTelegramId(), message.getText(), update);
            customer.setStep(5);
            return true;

        }else if(text.equals("\uD83D\uDC65 Guruh qo'shish")){
            CustomerTelegramDTO manager = customerTelegramService.findByTelegramId(customer.getTelegramId());
            List<TelegramGroupDTO> telegramGroups = manager.getTelegramGroups().stream().toList();

            if(!telegramGroups.isEmpty()){
                for(TelegramGroupDTO dto: telegramGroups){
                    dto = telegramGroupService.findOne(dto.getId()).get();
                    newMessage = createGroupText(dto);
                    InlineKeyboardMarkup markup = createGroupButtons(dto.getChatId());
                    SendMessage sendMessage =
                        TelegramsUtil.sendMessage(customer.getTelegramId(), newMessage, markup);
                    adminFeign.sendMessage(sendMessage);
                }
                customer.setStep(6);
                return true;
            }else {
                wrongValue(manager.getId(), "Telegram guruhlar mavjud emas!");
                log.warn("Has no telegram group, Manager id: {} ", customer.getTelegramId());
                return false;
            }
        }else if(text.equals("\uD83E\uDEC2 Xizmat qo'shish")){
            ReplyKeyboardRemove removeMarkup = new ReplyKeyboardRemove(true);

            newMessage = "Xizmat nomini kiriting!";
            SendMessage sendMessage = TelegramsUtil.sendMessage(customer.getTelegramId(), newMessage, removeMarkup);
            adminFeign.sendMessage(sendMessage);
            customer.setStep(8);
            return true;
        }else if(text.equals("\uD83D\uDCB8 Bolalar qarzdorliklari")){
            showPayments(message, customer);
        }else if(text.equals("\uD83D\uDC40 Guruhlarni ko'rish")){
            showGroups(message, customer);
        }else {
            wrongValue(message.getFrom().getId(), "Iltimos ko'rsatilganlardan birini tanlang\uD83D\uDE4F");
        }
        return false;
    }

    @Override
    public Boolean addOrganization(Message message, CustomerTelegram manager) {
        Optional<User> userOptional = userRepository.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!message.hasText() || userOptional.isEmpty()) return false;

        setUserToContextHolder(userOptional.get());
        String messageText = message.getText();

        OrganizationDTO isOrganizationExists =
            organizationService.getOrganizationByName(messageText);

        if(isOrganizationExists != null){
            wrongValue(message.getFrom().getId(), "Tashkilot avval saqlangan!");
            log.warn("Organization is already exists, Organization: {}", isOrganizationExists);
            return false;
        }

        OrganizationDTO organization = new OrganizationDTO();
        organization.setName(messageText);
        organization = organizationService.save(organization);


        String newMessage = "Tashkilot saqlandi";
        ReplyKeyboardMarkup markup = AdminKeyboards.createMenu();
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        log.info("Manager is added new organization, Organization: {} | Manager id: {}: {} | Message text: {}",
            organization, manager.getTelegramId(), messageText);
        return true;
    }

    @Override
    public Boolean addGroup(CallbackQuery callback, CustomerTelegram manager) {
        String callbackText = callback.getMessage().getReplyMarkup().getKeyboard().get(0).get(0).getText();
        if(!callbackText.equals("Shu guruhni qo'shish"))return false;

        GroupsDTO groupsDTO =
            groupsService.findOneByTelegramId(Long.parseLong(callback.getData()));

        if(groupsDTO != null){
            wrongValue(callback.getFrom().getId(), "Guruh tashkilotga biriktirilgan");
            log.warn("Group is already exists, Group: {}", groupsDTO);
            return false;
        }

        Long groupChatId = Long.parseLong(callback.getData());
        TelegramGroupDTO telegramGroup = telegramGroupService.findOneByChatId(groupChatId);
        Optional<User> userOptional = userRepository.getUserByPhoneNumber(manager.getPhoneNumber());
        setUserToContextHolder(userOptional.get());
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUserLogin();
        if(organizations.isEmpty()){
            wrongValue(callback.getFrom().getId(), "Tashkilotlar hozircha mavjud emas!");
            log.warn("Organization is not found, Manager id: {}", callback.getFrom().getId());
            return false;
        }
        InlineKeyboardMarkup markup = createGroupButtons(organizations, telegramGroup.getChatId());
        EditMessageTextDTO dto = createEditMessageText(callback, markup, "\n Iltimos tashkilotni tanlang\uD83D\uDC47");
        adminFeign.editMessageText(dto);
        return true;
    }

    private EditMessageDTO createEditMessage(CallbackQuery callback, InlineKeyboardMarkup markup){

        EditMessageDTO dto = new EditMessageDTO();
        dto.setChatId(String.valueOf(callback.getFrom().getId()));
        dto.setMessageId(callback.getMessage().getMessageId());
        dto.setInlineMessageId(String.valueOf(callback.getInlineMessageId()));
        dto.setReplyMarkup(markup);

        return dto;
    }

    private EditMessageTextDTO createEditMessageText(CallbackQuery callback, InlineKeyboardMarkup markup, String text){

        EditMessageTextDTO dto = new EditMessageTextDTO();
        dto.setMessageId(callback.getMessage().getMessageId());
        dto.setInlineMessageId(callback.getInlineMessageId());
        dto.setChatId(String.valueOf(callback.getFrom().getId()));
        dto.setReplyMarkup(markup);
        dto.setText(callback.getMessage().getText() + text);
        dto.setParseMode("HTML");

        return dto;
    }

    public Boolean chooseOrganization(CallbackQuery callbackQuery, CustomerTelegram manager) {
        String callbackData = callbackQuery.getData();
        String[] data = callbackData.split(":");
        Long telegramGroupId = Long.parseLong(data[0]);
        Long organizationId = Long.parseLong(data[1]);

        TelegramGroupDTO telegramGroup = telegramGroupService.findOneByChatId(telegramGroupId);
        OrganizationDTO organization = organizationService.findOne(organizationId).get();

        Optional<User> userOptional = userRepository.getUserByPhoneNumber(manager.getPhoneNumber());
        setUserToContextHolder(userOptional.get());

        GroupsDTO groupsDTO = new GroupsDTO();
        groupsDTO.setName(telegramGroup.getName());
        groupsDTO.setOrganization(organization);

        groupsService.save(groupsDTO);

        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();

        EditMessageTextDTO dto = createEditMessageText(callbackQuery, inlineMarkup, "");
        adminFeign.editMessageText(dto);

        String newMessage = "Asosiy menyu";
        ReplyKeyboardMarkup markup = AdminKeyboards.createMenu();
        SendMessage sendMessage =
            TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    @Override
    public void addServices(Message message, CustomerTelegram manager) {

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

    private org.telegram.telegrambots.meta.api.objects.User getBotData(String token){
        try {
            URI uri = new URI(telegramAPI + token);
            ResponseFromTelegram<org.telegram.telegrambots.meta.api.objects.User> responseFromTelegram = customerFeign.getMe(uri);
            log.info("Data of bot successfully got, Bot token: {} | Bot: {}", token, responseFromTelegram.getResult());
            return responseFromTelegram.getResult();

        } catch (URISyntaxException e) {
            log.error("Error while create URI, Exc. message: {} | Exc. cause: {}", e.getMessage(), e.getCause());
            return null;
        } catch (FeignException e){
            log.error("Error while sending request to server, Bot token: {} | API: {} | Exc. message: {} | Exc. cause: {}",
                token, telegramAPI + token + "/getMe", e.getMessage(), e.getCause());
            return null;
        }
    }

    private BotTokenDTO createBotEntity(org.telegram.telegrambots.meta.api.objects.User bot, UserDTO owner,String token){
        BotTokenDTO entity = new BotTokenDTO();
        entity.setToken(token);
        entity.setCreatedBy(owner);
        entity.setTelegramId(bot.getId());
        entity.setUsername(bot.getUserName());

        return entity;
    }

    private WebhookResponseDTO setWebhookToNewBot(String token, Long botId){
        String webhookAPI = "/setWebhook?url=" + WEBHOOK_URL + "/api/new-message";
        String url = telegramAPI + token + webhookAPI + "/" + botId;
        log.info("Url: {}", url);
        RestTemplate template = new RestTemplate();
        WebhookResponseDTO response =
            template.exchange(url, HttpMethod.GET, null, WebhookResponseDTO.class).getBody();
        log.info("Response from telegram server: {}", response);
        return response;
    }

    private void sayThanksToManager(Long botTelegramId, CustomerTelegram manager){
        String newMessage = "Raxmat☺";
        ReplyKeyboardMarkup menuMarkup = AdminKeyboards.createMenu();

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, menuMarkup);
        Update update = adminFeign.sendMessage(sendMessage);
        log.info("Thanks is send to manager, Bot id: {} | Manager id: {} | Update: {}",
            botTelegramId, manager.getTelegramId(), update);
    }

    private void sendInviteLink(org.telegram.telegrambots.meta.api.objects.User bot, Long groupId){
        String link = "https://t.me/" + bot.getUserName() + "?start=" + groupId;
        String newMessage = "Shu havola orqali botga start bering\uD83D\uDC49 " + link;

        SendMessage sendMessage = TelegramsUtil.sendMessage(groupId, newMessage);
        BotToken botToken = botTokenRepository.findByTelegramId(bot.getId()).get();
        URI uri = createCustomerURI(botToken.getToken());
        Update update = customerFeign.sendMessage(uri, sendMessage);
        log.info("Link is send successfully, Bot id: {} | Groupd id: {} | Uri: {}",
            bot.getId(), groupId, uri);
    }

    private URI createCustomerURI(String token){
        try {
            return new URI(telegramAPI + token);
        } catch (URISyntaxException e) {
            log.error("{} | {}", e.getMessage(), e.getCause());
            throw new RuntimeException(e);
        }
    }


    private void saveToTelegramGroup(Chat chat, CustomerTelegram manager){
        TelegramGroupDTO dto = new TelegramGroupDTO();
        dto.setChatId(chat.getId());
        dto.setName(chat.getTitle());

        dto = telegramGroupService.save(dto);
        System.out.println(dto);
        Set<TelegramGroup> telegramGroups = manager.getTelegramGroups() == null? new HashSet<>(): manager.getTelegramGroups();
        telegramGroups.add(telegramGroupService.mapToEntity(dto));
        entityManager.detach(manager);
        manager.setTelegramGroups(telegramGroups);

        customerTelegramRepository.save(manager);
        log.info("Telegram group is saved successfully, Chat id: {} | DTO: {}", chat.getId(), dto);
    }

    private void wrongValue(Long chatId, String message){
        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
        Update update = adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
            chatId, message, update);
    }

    private void messageHasNotText(Long chatId, Message message){
        wrongValue(chatId, "Iltimos xabar yuboring\uD83D\uDE4F");
        log.warn("User hasn't send text, Chat id: {} | Message: {}", chatId, message);
    }

    private void messageHasNotText(Long chatId, Message message, Boolean contact){
        wrongValue(chatId, "Iltimos xabar yoki kontakt yuboring\uD83D\uDE4F");
        log.warn("User hasn't send text, Chat id: {} | Message: {}", chatId, message);
    }

    private InlineKeyboardMarkup createGroupButtons(Long currentGroupId){
        InlineKeyboardButton current = new InlineKeyboardButton("Shu guruhni qo'shish");
        current.setCallbackData(String.valueOf(currentGroupId));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(List.of(current)));

        return markup;
    }

    private InlineKeyboardMarkup createGroupButtons(List<OrganizationDTO> organizations, Long telegramGroupId){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboards = new ArrayList<>();

        for(OrganizationDTO organization: organizations){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(organization.getName());
            button.setCallbackData(telegramGroupId + ":" + organization.getId());

            keyboards.add(List.of(button));
        }

        markup.setKeyboard(keyboards);
        return markup;
    }

    public InlineKeyboardMarkup createGroupButtons(List<GroupsDTO> groups){
        List<List<InlineKeyboardButton>> label = new ArrayList<>();
        for(GroupsDTO group: groups){

            InlineKeyboardButton button = new InlineKeyboardButton(group.getName());
            button.setCallbackData(String.valueOf(group.getId()));

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            label.add(row);
        }

        InlineKeyboardButton button = new InlineKeyboardButton("Shularni hammasi");
        button.setCallbackData("ENOUGH");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        label.add(row);


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(label);
        return markup;
    }

    private String createGroupText(TelegramGroupDTO telegramGroup){
        List<CustomerTelegram> customersList =
            customerTelegramRepository.getCountCustomersByChatId(telegramGroup.getChatId());

        String groupAsText = String.format(
            "Guruh nomi: %s\n" +
            "Guruh odamlari soni: %s\n" +
                "===========================\n",
            telegramGroup.getName(), customersList.size()
        );

        return groupAsText;
    }

    private void setUserToContextHolder(User user){
        Set<GrantedAuthority> authorities =
            user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName())).collect(Collectors.toSet());

        org.springframework.security.core.userdetails.User principal =
            new org.springframework.security.core.userdetails.User(user.getLogin(),
                user.getPassword() == null? "": user.getPassword(), authorities);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Boolean getServiceName(Message message){
        if(!message.hasText()) {
            messageHasNotText(message.getFrom().getId(), message);
            return false;
        }
        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        String newMessage = "Iltimos xizmat narxini kiriting.";
        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage);

        adminFeign.sendMessage(sendMessage);

        ServicesDTO dto = new ServicesDTO();
        dto.setName(messageText);

        ServicesRedisDTO redisDTO = new ServicesRedisDTO(managerId, dto);
        servicesRedisRepository.save(redisDTO);
        return true;
    }

    public Boolean getServicePrice(Message message){
        if(!message.hasText()) {
            messageHasNotText(message.getFrom().getId(), message);
            return false;
        }

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        Integer integerPrice = null;
        if(messageText.length() <= 7){
            try{
                integerPrice = Integer.parseInt(messageText);
            }catch (NumberFormatException e){
                wrongValue(message.getFrom().getId(), "Qiymat noto'g'ri!");
                log.warn("Throws NumberFormatException when user send cost of service, Manager id: {} | Value: {}",
                    managerId, messageText);
                return false;
            }
        }else{
            wrongValue(message.getFrom().getId(), "Qiymat noto'g'ri!");
            log.warn("Cost of service is out of range, Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }

        if(integerPrice <= 10000){
            wrongValue(managerId, "Xizmatlarning eng miqdordagi summasi 10 000");
            log.warn("Cost of service is below 10 000, Manager id: {} | Cost: {}", managerId, messageText);
            return false;
        }

        String newMessage = "Iltimos xizmatning boshlanadigan kunini kiriting.\n\nFormat: yil.oy.kun Misol: 2022.01.01";
        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage);
        adminFeign.sendMessage(sendMessage);

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setPrice(Double.parseDouble(String.valueOf(integerPrice)));
        servicesRedisRepository.save(redisDTO);
        return true;
    }

    public Boolean getServiceStartedTime(Message message){
        if(!message.hasText()) {
            messageHasNotText(message.getFrom().getId(), message);
            return false;
        }

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        if(messageText.length() != 10)return false;

        boolean isTextExists = false;
        List<Integer> numbers = List.of(1,2,3,4,5,6,7,8,9,0);

        char[] elements = messageText.toCharArray();
        for(Character c: elements){
            if(c == '.')continue;
            boolean isNumber = numbers.contains(Integer.parseInt(c + ""));
            if(!isNumber){
                isTextExists = true;
                break;
            }
        }

        if(isTextExists) {
            wrongValue(managerId, "Faqat sonlar va \".\" qatnashishi mumkun!");
            log.warn("There is alphabet in value that manager is send, Manager id: {} | Value: {}", managerId, messageText);
            return false;
        }
        String[] partsOfText = messageText.split("\\.");

        int year = Integer.parseInt(partsOfText[0]);
        int month = Integer.parseInt(partsOfText[1]);
        int day = Integer.parseInt(partsOfText[2]);

        if(partsOfText[0].length() != 4 || year < new Date().getYear())return false;
        if(partsOfText[1].length() != 2 || !(month >= 1 && month <= 12))return false;
        if(partsOfText[2].length() != 2 || !(day >= 1 && day <= 31)) return false;

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setStartedPeriod(LocalDate.of(year, month, day));
        servicesRedisRepository.save(redisDTO);

        String newMessage = "To'lov qilinish periyudini tanlang";
        ReplyKeyboardMarkup markup = AdminKeyboards.getPeriodTypeButtons();
        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    public Boolean getServicePeriodType(Message message){
        if(!message.hasText()) {
            messageHasNotText(message.getFrom().getId(), message);
            return false;
        }

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        List<String> periods = AdminKeyboards.getPeriods();
        boolean isPeriodExists = false;
        for(String p: periods){
            isPeriodExists = p.equals(messageText);
            if(isPeriodExists)break;
        }

        if(!isPeriodExists){
            wrongValue(managerId, "Iltimos ko'rsatilgan qiymatlardan birini tanlang");
            log.warn("User send invalid type of period, , Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setPeriodType(getPeriodType(messageText));
        servicesRedisRepository.save(redisDTO);

        String newMessage = "Xizmat qancha davom etadi, son bilan kiriting. Misol: 4";
        SendMessage sendMessage =
            TelegramsUtil.sendMessage(managerId, newMessage, new ReplyKeyboardRemove(true));
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    private PeriodType getPeriodType(String period){
        if(period.equals("Yillik")) return PeriodType.YEAR;
        if(period.equals("Oylik")) return PeriodType.MONTH;
        if(period.equals("Haftalik")) return PeriodType.WEEK;
        if(period.equals("Kunlik")) return PeriodType.DAY;
        if(period.equals("Bir martalik")) return PeriodType.ONETIME;
        return PeriodType.MONTH;
    }

    public Boolean getPeriodCount(Message message, String managerPhoneNumber){
        if(!message.hasText()) {
            messageHasNotText(message.getFrom().getId(), message);
            return false;
        }

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        if(messageText.length() > 2){
            wrongValue(managerId, "Qiymatning uzunligi 2 tadan uzun!");
            log.warn("User send invalid value, Manager id: {} | Value: {}", managerId, messageText);
            return false;
        }

        Integer count = null;
        try{
            count = Integer.parseInt(messageText);
        }catch (NumberFormatException e){
            wrongValue(managerId, "Qiymatga harf aralashmasligi kerak!");
            log.warn("There is alphabet in value that manager is send, Manager id: {} | Value: {}",
                managerId, messageText);
            return false;
        }
        if(count <= 0){
            wrongValue(managerId, "Qiymat 0 dan past bo'lmasligi kerak!");
            log.warn("Value is below 0, Manager id: {} | Value: {}", managerId, messageText);
            return false;
        }

        ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
        redisDTO.getServicesDTO().setCountPeriod(count);
        servicesRedisRepository.save(redisDTO);

        User manager = userRepository.findByCreatedBy(managerPhoneNumber).get();
        setUserToContextHolder(manager);
        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(managerId, "Sizda tashkilotga qo'shilgan guruhlar mavjud emas");
            log.warn("Group is not found, Manager id: {} ", managerId);
            String newMessage = "Asosiy menyu";
            ReplyKeyboardMarkup markup = AdminKeyboards.createMenu();
            SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
            adminFeign.sendMessage(sendMessage);
            return false;
        }

        String newMessage = "Bu xizmatga qaysi guruhlar qo'shiladi";
        InlineKeyboardMarkup markup = createGroupButtons(groups);

        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        return true;
    }


    public Boolean markGroups(CallbackQuery callback){
        String callbackData = callback.getData();
        Long managerId = callback.getFrom().getId();
            if(!callbackData.equals("ENOUGH")) {
            List<List<InlineKeyboardButton>> buttons = callback.getMessage().getReplyMarkup().getKeyboard();

            buttons.forEach(l -> l.forEach(k -> {
                if (k.getCallbackData().equals(callbackData)) {
                    k.setText(k.getText() + ": ✅");
//                    k.setCallbackData(k.getCallbackData() + ": true");
                }
            }));

            ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
            Set<GroupsDTO> groups = redisDTO.getServicesDTO().getGroups();

            GroupsDTO group = groupsService.findOneByTelegramId(Long.parseLong(callbackData));
            groups.add(group);
            servicesRedisRepository.save(redisDTO);

            EditMessageDTO editMessageDTO = new EditMessageDTO();
            editMessageDTO.setReplyMarkup(new InlineKeyboardMarkup(buttons));
            editMessageDTO.setInlineMessageId(callback.getInlineMessageId());
            editMessageDTO.setMessageId(callback.getMessage().getMessageId());
            editMessageDTO.setChatId(String.valueOf(callback.getFrom().getId()));

            adminFeign.editMessageReplyMarkup(editMessageDTO);

            return false;
        }else{
            ServicesRedisDTO redisDTO = servicesRedisRepository.findById(managerId).get();
            ServicesDTO servicesDTO = redisDTO.getServicesDTO();

            servicesService.save(servicesDTO);

            String newMessage = "Asosiy menyu";
            ReplyKeyboardMarkup markup = AdminKeyboards.createMenu();
            SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage, markup);
            adminFeign.sendMessage(sendMessage);
            return true;
        }
    }

    public void showPayments(Message message, CustomerTelegram manager){
        if(!message.hasText()) {
            messageHasNotText(message.getChatId(), message);
            return;
        }

        String messageText = message.getText();
        Long managerId = message.getFrom().getId();

        Optional<User> userOptional = userRepository.getUserByPhoneNumber(manager.getPhoneNumber());
        if(userOptional.isEmpty())return;
        setUserToContextHolder(userOptional.get());

        List<PaymentDTO> payments = paymentService.getAllPaymentsCreatedByGroupManager();
        if(payments.isEmpty()){
            wrongValue(managerId, "Hozircha sizda qarzdorliklar yo'q");
            log.warn("Payments list is empty, Manager id: {} ", managerId);
            return;
        }
        StringBuilder newMessage = new StringBuilder();
        for(PaymentDTO payment: payments){
            newMessage.append(payment + "\n\n");
        }

        SendMessage sendMessage = TelegramsUtil.sendMessage(managerId, newMessage.toString());
        adminFeign.sendMessage(sendMessage);
    }

    public void showGroups(Message message, CustomerTelegram manager){
        Optional<User> userOptional = userRepository.getUserByPhoneNumber(manager.getPhoneNumber());
        if(userOptional.isEmpty())return;
        setUserToContextHolder(userOptional.get());

        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(message.getFrom().getId(), "Tashkilotlarga biriktirilgan tashkilotlar hozircha mavjud emas!");
        }
        StringBuilder newMessage = new StringBuilder();
        for(GroupsDTO group: groups){
            newMessage.append(String.format(
                "Guruh nomi: %s\n" +
                    "Tashkilot nomi: %s\n" +
                    "Foydalanuvchilar soni: %d\n",
                group.getName(), group.getOrganization().getName(), group.getCustomers().size()
            ));
        }

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage.toString());
        adminFeign.sendMessage(sendMessage);
    }

    private boolean isNotExistsInGroups(Set<TelegramGroup> telegramGroups, BotTokenDTO botToken){
        for(TelegramGroup group: telegramGroups){
            URI uri = createCustomerURI(botToken.getToken());

            String chatId = String.valueOf(group.getChatId());
            String userId = String.valueOf(botToken.getTelegramId());

            ResponseFromTelegram<ChatMember> response =
                customerFeign.getChatMember(uri, chatId, userId);
            if(response.getOk()){
                log.info("Bot is already exists in other group, Bot token: {} | Group id: {} | Response: {} ",
                    botToken.getToken(), group.getChatId(), response);
                return false;
            }
        }

        return true;
    }
}
