package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.*;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.feign.CustomerFeignClient;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.repository.TelegramGroupRepository;
import uz.devops.intern.repository.UserRepository;
import uz.devops.intern.service.AdminTgService;
import uz.devops.intern.service.OrganizationService;
import uz.devops.intern.service.ServicesService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.telegram.bot.dto.WebhookResponseDTO;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;


import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminTgServiceImpl implements AdminTgService {

    private static String telegramAPI = "https://api.telegram.org/bot";
    private static String webhookAPI = "/setWebhook?url=https://b243-83-221-180-161.in.ngrok.io/api/new-message";
    private final Logger log = LoggerFactory.getLogger(AdminTgServiceImpl.class);
    @Autowired
    private TelegramGroupService telegramGroupService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ServicesService servicesService;
    @Autowired
    private BotTokenRepository botTokenRepository;
    @Autowired
    private CustomerTelegramRepository customerTelegramRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminFeign adminFeign;
    @Autowired
    private CustomerFeignClient customerFeign;

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
                boolean isCorrect = menu(update.getMessage(), customer);
                if(isCorrect) customerTelegramRepository.save(customer);
            }else if(step == 5){
                addOrganization(update.getMessage(), customer);
            }else if(step == 6){
                showOtherGroup(update.getCallbackQuery(), customer);
            }
        }
    }

    @Override
    public void pressedStartCommand(Message message) {
        Long userId = message.getFrom().getId();
        String messageText = message.getText();

        String newMessage = "Iltimos tilni tanlang\uD83D\uDC47";
        ReplyKeyboardMarkup markup = KeyboardUtil.language();
        SendMessage sendMessage = TelegramsUtil.sendMessage(userId, newMessage, markup);
        Update update = adminFeign.sendMessage(sendMessage);
        log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
            userId, messageText, update);
        CustomerTelegram customer = createCustomerTelegramToSaveDatabase(message.getFrom());
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
            SendMessage sendMessage = sendMessage(userId, newMessage, markup);
            Update response = adminFeign.sendMessage(sendMessage);
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                userId, messageText, response);
        }else if(messageText.equals("\uD83C\uDDF7\uD83C\uDDFA Русский")){
            // Ruschani tanladi.
            customer.setLanguageCode("ru");
            String newMessage = "Iltimos telefon raqamingizni jo'nating\uD83D\uDC47(Ruscha)";
            ReplyKeyboardMarkup markup = KeyboardUtil.phoneNumber();
            SendMessage sendMessage = sendMessage(customer.getTelegramId(), newMessage, markup);
            Update response = adminFeign.sendMessage(sendMessage);
            log.info("Message send successfully! User id: {} | Message text: {} | Update: {}",
                userId, messageText, response);
        }else {

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
                SendMessage sendMessage = sendMessage(userId, newMessage);
                adminFeign.sendMessage(sendMessage);
                log.warn("Data of user is not found! Customer: {} | Message: {}",
                    customer, message);

            }else {
                boolean isUserManager = checkUserRole(user);
                if (isUserManager) {
                    newMessage = "Iltimos botning tokenini tashlang.";
                    SendMessage sendMessage = sendMessage(userId, newMessage);
                    adminFeign.sendMessage(sendMessage);
                    log.info("User is verified, Phone number: {} | Customer: {}",
                        user.getCreatedBy(), customer);

                    customer.setPhoneNumber(user.getCreatedBy());
                    customer.setStep(3);
                    customerTelegramRepository.save(customer);
                } else {
                    // User manager emas.
                    newMessage = "Sizda boshqaruvchilik huquqi yo'q!";
                    SendMessage sendMessage = sendMessage(userId, newMessage);
                    adminFeign.sendMessage(sendMessage);
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
                SendMessage sendMessage = sendMessage(userId, newMessage);
                Update update = adminFeign.sendMessage(sendMessage);
                User owner = getOwnerByPhoneNumber(customer.getPhoneNumber());
                BotToken botEntity = createBotEntity(bot, owner, newBotToken);
                botTokenRepository.save(botEntity);
                customer.setStep(4);
                customerTelegramRepository.save(customer);
                log.info("Bot saved successfully, Bot: {} | Customer: {}", botEntity, customer);
                newMessage = "Iltimos botni guruhga qo'shing";
                sendMessage =
                    TelegramsUtil.sendMessage(message.getFrom().getId(), newMessage);
                adminFeign.sendMessage(sendMessage);
                // Botning tokenini saqlab qo'yish kere.
            } else {
                SendMessage sendMessage = sendMessage(userId, result);
                Update update = adminFeign.sendMessage(sendMessage);
                log.info("Setting webhook is failed, Response: {} | Bot token: {} | Customer: {}",
                    response, newBotToken, customer);
            }
        }
    }

    @Override
    public void checkIsBotInGroup(Message message, String botId) {
        // Todo o'zini boti kirdimi yomi shuni tekshirish kere.
        List<org.telegram.telegrambots.meta.api.objects.User> telegramUsers = message.getNewChatMembers();
        boolean isBot = false;
        org.telegram.telegrambots.meta.api.objects.User bot = null;
//        isBotExistsIntoGroup(message.getChat(), botId);
        for(org.telegram.telegrambots.meta.api.objects.User user: telegramUsers){
            if(user.getIsBot() && String.valueOf(user.getId()).equals(botId)){
                bot = user;
                isBot = true;
            }
        }

        CustomerTelegram manager = customerTelegramRepository.findByBot(Long.parseLong(botId)).get();
        String userId = String.valueOf(manager.getTelegramId());
        if(isBot){
            sayThanksToManager(bot);
            sendInviteLink(bot, message.getChat().getId());
            saveToTelegramGroup(message.getChat());

        }else {
            String newMessage = "Iltimos hozirgi botni qo'shing";
            SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage);
            adminFeign.sendMessage(sendMessage);
            log.warn("Something goes wrong, Bot id: {} | Group id: {}", botId, message.getChat().getId());
        }
    }

    @Override
    public void checkIsBotAdmin(ChatMemberUpdated member){
        boolean isBot = member.getNewChatMember().getUser().getIsBot();
        if(isBot){
            String status = member.getNewChatMember().getStatus().toUpperCase();
            if(status.equals("ADMINISTRATOR")){
                org.telegram.telegrambots.meta.api.objects.User bot = member.getNewChatMember().getUser();
                sayThanksToManager(bot);
                sendInviteLink(bot, member.getChat().getId());
            }else {
                log.warn("Bot is not administrator, Bot status: {}", member.getNewChatMember().getStatus());
            }
        }else{
            log.warn("Something goes wrong, New member username and id: {} {} | Group id: {}",
                member.getFrom().getUserName(), member.getFrom().getId(), member.getChat().getId());
        }
    }

    @Override
    public boolean menu(Message message, CustomerTelegram customer) {
        if(message.hasText()){
            String text = message.getText();
            String newMessage = null;
            if(text.equals("Organisatsiya qo'shish")){
                newMessage = "Iltimos tashkilot nomini kiriting";
                SendMessage sendMessage = sendMessage(customer.getTelegramId(), newMessage);
                Update update = adminFeign.sendMessage(sendMessage);
                log.info("Admin is creating new organization, Manager id: {} | Message text: {} | Update: {}",
                    customer.getTelegramId(), message.getText(), update);
                customer.setStep(5);
                return true;
            }else if(text.equals("Guruh qo'shish")){
                List<TelegramGroupDTO> telegramGroups = telegramGroupService.findAll();
                if(!telegramGroups.isEmpty()){
                    TelegramGroupDTO telegramGroupDTO = telegramGroups.get(0);
                    newMessage = createGroupText(telegramGroupDTO);
                    InlineKeyboardMarkup markup = createGroupButtons(telegramGroupDTO.getChatId(),
                        null,
                        telegramGroups.get(1) == null? null: telegramGroups.get(1).getChatId());
                    SendMessage sendMessage = TelegramsUtil.sendMessage(customer.getTelegramId(), newMessage, markup);
                    adminFeign.sendMessage(sendMessage);
                    log.info("Bot send group as text, Manager id: {} | Telegram groups count: {} | Group: {}",
                        customer.getTelegramId(), telegramGroups.size(), telegramGroupDTO);
                    customer.setStep(6);
                    return true;
                }else {
                    newMessage = "Telegram gruppalar yo'q";
                    SendMessage sendMessage = TelegramsUtil.sendMessage(customer.getTelegramId(), newMessage);
                    adminFeign.sendMessage(sendMessage);
                    log.warn("Has no telegram group, Manager id: {} | Telegram group count: {}",
                        customer.getTelegramId(), telegramGroups.size());
                }
            }else if(text.equals("Servis qo'shish")){

            }

        }else{
            wrongValue(message.getFrom().getId());
        }
        return false;
    }

    @Override
    public void addOrganization(Message message, CustomerTelegram manager) {
        if(message.hasText()){
            String messageText = message.getText();
            OrganizationDTO organization = new OrganizationDTO();
            organization.setName(messageText);
            organization = organizationService.save(organization);
            String newMessage = "Tashkilot saqlandi";
            ReplyKeyboardMarkup markup = createMenu();
            SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
            adminFeign.sendMessage(sendMessage);
            log.info("Manager is added new organization, Organization: {} | Manager id: {}: {} | Message text: {}",
                organization, manager.getTelegramId(), messageText);
            manager.setStep(4);
            customerTelegramRepository.save(manager);
        }else {
            wrongValue(manager.getTelegramId());
//            adminFeign.sendMessage(sendMessage);
//            log.warn("Manager didn't send text while creating new organization, Manager id: {} | Message: {}",
//                manager.getTelegramId(), message);
        }
    }

    private void showOtherGroup(CallbackQuery callback, CustomerTelegram manager){
        String callbackText = callback.getMessage().getText();
        String callbackData = callback.getData();

        if(callbackData != null){
            TelegramGroupDTO group =
                telegramGroupService.findOne(Long.parseLong(callbackData)).get();

            String groupAsText = createGroupText(group);
//
//            createGroupButtons(group.getChatId(), )

        }
    }

    @Override
    public void addGroup(Message message, CustomerTelegram manager) {
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

    private void sayThanksToManager(org.telegram.telegrambots.meta.api.objects.User bot){
        CustomerTelegram manager = customerTelegramRepository.findByBot(bot.getId()).get();
        String newMessage = "Raxmat☺";
        String userId = String.valueOf(manager.getTelegramId());
        ReplyKeyboardMarkup menuMarkup = createMenu();

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, menuMarkup);
        Update update = adminFeign.sendMessage(sendMessage);
        log.info("Thanks is send to manager, Bot id: {} | Manager id: {} | Update: {}",
            bot.getId(), manager.getTelegramId(), update);
    }

    private void sendInviteLink(org.telegram.telegrambots.meta.api.objects.User bot, Long groupId){
        String link = "https://t.me/" + bot.getUserName() + "?start=" + groupId;
        String newMessage = "Shu havola orqali botga start bering\uD83D\uDC49 " + link;

        SendMessage sendMessage = TelegramsUtil.sendMessage(groupId, newMessage);
        BotToken botToken = botTokenRepository.findByTelegramId(bot.getId()).get();
        Update update = sendRequestWithFeign(botToken.getToken(), sendMessage);
        log.info("Link is send successfully, Bot id: {} | Groupd id: {} | Link: {}",
            bot.getId(), groupId, link);
    }


    private Update sendRequestWithFeign(String token, SendMessage sendMessage){
        try {
            URI uri = new URI(telegramAPI + token);
            Update update = customerFeign.sendMessage(uri, sendMessage);
            log.info("{}", update);
            return update;
        } catch (URISyntaxException e) {
            log.error("{} | {}", e.getMessage(), e.getCause());
            throw new RuntimeException(e);

        }finally {
            return null;
        }
    }

    private void saveToTelegramGroup(Chat chat){
        TelegramGroupDTO dto = new TelegramGroupDTO();
        dto.setChatId(chat.getId());
        dto.setName(chat.getTitle());

        dto = telegramGroupService.save(dto);
        log.info("Telegram group is saved successfully, Chat id: {} | DTO: {}", chat.getId(), dto);
    }

//    private User isBotExistsIntoGroup(Chat chat, String botId){
//        BotToken botToken = botTokenRepository.findByTelegramId(Long.parseLong(botId)).get();
//
//        String url = telegramAPI + botToken.getToken() + "/getChatMember?chat_id=" + chat.getId() + "&user_id=" + botToken.getTelegramId();
//        RestTemplate template = new RestTemplate();
//        ResponseEntity<ChatMember> response =
//            template.exchange(url, HttpMethod.GET, null, ChatMember.class);
//        if(response.getBody() != null){
//            ChatMember chatMember = response.getBody();
//            return chatMember.getUser().equals(botToken.getTelegramId());
//        }else {
//            return false;
//        }
//    }

    private ReplyKeyboardMarkup createMenu(){
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Organisatsiya qo'shish"));
        row.add(new KeyboardButton("Guruh qo'shish"));

        KeyboardRow line = new KeyboardRow();
        line.add(new KeyboardButton("Servis qo'shish"));

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(row, line));

        return markup;
    }

    private void wrongValue(Long chatId){
        SendMessage sendMessage = TelegramsUtil.wrongChoice(chatId);
        Update update = adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value, Chat id: {} | Update: {}", chatId, update);
    }

    private InlineKeyboardMarkup createGroupButtons(Long currentId, Long previousId, Long nextId){
        InlineKeyboardButton previous = new InlineKeyboardButton("⬅");
        previous.setCallbackData(String.valueOf(previousId));

        InlineKeyboardButton current = new InlineKeyboardButton("✋");
        current.setCallbackData(String.valueOf(currentId));

        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setCallbackData(String.valueOf(nextId));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(List.of(previous, current, next)));

        return markup;
    }

    private String createGroupText(TelegramGroupDTO telegramGroup){
        List<CustomerTelegram> customersList =
            customerTelegramRepository.getCountCustomersByChatId(telegramGroup.getChatId());

        StringBuilder builder = new StringBuilder(String.format(
            "Guruh nomi: %s\n" +
            "Guruh odamlari soni: %s\n" +
                "===========================\n",
            telegramGroup.getName(), customersList.size()
        ));

        for(CustomerTelegram customer: customersList){
            builder.append(customer.getUsername()).append("\n");
        }

        return builder.toString();
    }


}
