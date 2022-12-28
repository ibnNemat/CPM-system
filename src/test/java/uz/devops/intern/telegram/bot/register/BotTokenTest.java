package uz.devops.intern.telegram.bot.register;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.devops.intern.IntegrationTest;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.repository.UserRepository;
import uz.devops.intern.service.AdminTgService;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.telegram.bot.TestUtil;
import uz.devops.intern.web.rest.AccountResource;
import uz.devops.intern.web.rest.vm.ManagedUserVM;

import java.util.Optional;
import java.util.Set;

@IntegrationTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BotTokenTest {
//    @Value("${telegram.token}")
    protected String BOT_TOKEN = "5344114476:AAHjU99o-rTS_0OU-tDUk6zdUXmWj7pyc10";
    @Value("${admin.telegram-id}")
    protected Long userTgId;
    @Value("${admin.username}")
    protected String username;
    protected String PHONE_NUMBER = "+998999124625";
    @Value("${admin.language-code}")
    protected String languageCode;
    @Autowired
    private CustomerTelegramService customerTelegramService;
    @Autowired
    private BotTokenService botTokenService;
    @Autowired
    private CustomerTelegramRepository customerTelegramRepository;
    @Autowired
    private AccountResource accountResource;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AdminTgService adminTgService;

    protected User mockUser;
    protected Message mockMessage;
    protected Update update;

    @BeforeEach
    public void refill(){
        mockUser = new User();
        mockUser.setId(userTgId);
        mockUser.setUserName(username);
        mockUser.setLanguageCode(languageCode);

        Chat chat = new Chat();
        chat.setId(userTgId);

        mockMessage = new Message();
        mockMessage.setFrom(mockUser);
        mockMessage.setChat(chat);

        update = new Update();
        update.setMessage(mockMessage);
        saveToCustomerTelegram();
        saveUser();
//        saveToJhiUser();

    }

    public void saveToCustomerTelegram() {
        CustomerTelegramDTO manager = new CustomerTelegramDTO();
        manager.setTelegramId(userTgId);
        manager.setUsername(username);
        manager.setStep(3);
        manager.setPhoneNumber(PHONE_NUMBER);
        manager.setLanguageCode(languageCode);
        manager.setTelegramGroups(Set.of());

        customerTelegramService.update(manager);
    }

    public void saveUser(){
        ManagedUserVM vm = TestUtil.createUser();
        vm.setPassword(encoder.encode(vm.getPassword()));

        accountResource.registerAccount(vm);
        ResponseDTO<uz.devops.intern.domain.User> userResponse = userService.getUserByLogin(vm.getLogin());
        Assertions.assertNotNull(userResponse);
        Assertions.assertTrue(userResponse.getSuccess());
        Assertions.assertNotNull(userResponse.getResponseData());

        uz.devops.intern.domain.User admin = userResponse.getResponseData();
        admin.setActivated(true);
        admin.setCreatedBy(PHONE_NUMBER);

        userRepository.save(admin);
        System.out.println("[User] Admin: " + admin);
    }

    @Order(30)
    @Transactional
    @ParameterizedTest
    @ValueSource(strings = {"No token", "5344114476:AAHjU99o-rTS_0OU-tDUk6zdUXmWj7pyc1"})
    public void sendInvalidToken(String botToken){
        mockMessage.setText(botToken);

        adminTgService.main(update);
        ResponseDTO<CustomerTelegramDTO> managerResponse = customerTelegramService.findByTelegramId(userTgId);
        Assertions.assertNotNull(managerResponse);
        Assertions.assertTrue(managerResponse.getSuccess());
        Assertions.assertNotNull(managerResponse.getResponseData());

        CustomerTelegramDTO manager = managerResponse.getResponseData();
        Assertions.assertEquals(3, manager.getStep());

    }

    @Test
    @Order(31)
    @Transactional
    public void sendValidToken(){
        mockMessage.setText(BOT_TOKEN);
        adminTgService.main(update);

        ResponseDTO<BotTokenDTO> botTokenResponse = botTokenService.findByToken(BOT_TOKEN);
        Assertions.assertNotNull(botTokenResponse);
        Assertions.assertTrue(botTokenResponse.getSuccess());
        Assertions.assertNotNull(botTokenResponse.getResponseData());

        BotTokenDTO bot = botTokenResponse.getResponseData();
        Assertions.assertEquals(BOT_TOKEN, bot.getToken());

        ResponseDTO<CustomerTelegramDTO> managerResponse = customerTelegramService.findByTelegramId(userTgId);
        Assertions.assertNotNull(managerResponse);
        Assertions.assertTrue(managerResponse.getSuccess());
        Assertions.assertNotNull(managerResponse.getResponseData());

        CustomerTelegramDTO manager = managerResponse.getResponseData();
        Assertions.assertEquals(4, manager.getStep());

    }

    @Test
    @Order(32)
    @Transactional
    public void sendExistsToken(){
        sendValidToken();

        mockMessage.setText(BOT_TOKEN);
        adminTgService.main(update);

        ResponseDTO<CustomerTelegramDTO> managerResponse = customerTelegramService.findByTelegramId(userTgId);
        Assertions.assertNotNull(managerResponse);
        Assertions.assertTrue(managerResponse.getSuccess());
        Assertions.assertNotNull(managerResponse.getResponseData());

        CustomerTelegramDTO manager = managerResponse.getResponseData();
        Assertions.assertEquals(3, manager.getStep());

    }
}
