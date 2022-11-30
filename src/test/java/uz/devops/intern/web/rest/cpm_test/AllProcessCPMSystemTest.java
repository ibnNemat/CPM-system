package uz.devops.intern.web.rest.cpm_test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.IntegrationTest;
import uz.devops.intern.config.Constants;
import uz.devops.intern.domain.*;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.repository.*;
import uz.devops.intern.security.AuthoritiesConstants;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.mapper.*;

import uz.devops.intern.web.rest.TestUtil;
import uz.devops.intern.web.rest.vm.LoginVM;
import uz.devops.intern.web.rest.vm.ManagedUserVM;

import javax.persistence.EntityManager;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@AutoConfigureMockMvc
@IntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllProcessCPMSystemTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EntityManager entityManager;

    private Organization organizationEntity;
    private static Payment paymentEntity;
    private Services serviceEntity;

    private Groups groupEntity;
    private static String tokenCustomer;
    private static String tokenManager;
    private User savedUserCustomerEntity;
    private static Customers savedAuthenticatedCustomer;
    private static final String [] logins = {"customer", "manager"};
    private static final String [] emails = {"example1@gmail.com", "example2@gmail.com"};
    private static final String DEFAULT_CLIENT_NAME = "ROBOT";
    private static final String DEFAULT_PASSWORD = "12345";
    private static final String DEFAULT_PHONE_NUMBER = "+998950645097";
    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    private static final String ROLE_MANAGER = "ROLE_MANAGER";
    private static final String DEFAULT_SERVICE_NAME = "FOOD";
    private static final String DEFAULT_GROUP_NAME = "Group A";
    private static final String DEFAULT_ORG_NAME = "2 - MTM";
    private static final Double DEFAULT_SERVICE_PRICE = 100000D;
    private static final String PAYMENT_NOT_FOUND = "Looks like you already paid";
    private static final String NOT_ENOUGH = "Not enough money";
    private static final LocalDate DEFAULT_STARTED_PERIOD = LocalDate.of(2022, 12, 12);
    private static final LocalDate DEFAULT_FINISHED_PERIOD = LocalDate.of(2023, 1, 12);

    private static int index = 0;
    private static final PeriodType DEFAULT_PERIOD_TYPE = PeriodType.MONTH;
    private static final Integer DEFAULT_COUNT_PERIOD = 1;

    @BeforeEach
    public void init(){
        Set<Authority> newAuthorities = new HashSet<>();
        Authority authorityCustomer = new Authority();
        authorityCustomer.setName(ROLE_MANAGER);
        newAuthorities.add(authorityCustomer);

        Authority authorityManager = new Authority();
        authorityManager.setName(ROLE_CUSTOMER);
        newAuthorities.add(authorityManager);

        authorityRepository.saveAllAndFlush(newAuthorities);

        String [] roles = {ROLE_CUSTOMER, ROLE_MANAGER};
        List<User> users = new ArrayList<>();

        for (int i = 0; i < roles.length; i++) {
            users.add(buildUserEntity(roles[i], logins[i], emails[i]));
        }

        savedUserCustomerEntity = userRepository.saveAll(users).get(0);
        userRepository.flush();

        savedAuthenticatedCustomer = createEntityCustomers(logins[0], savedUserCustomerEntity);
        savedAuthenticatedCustomer = customersRepository.saveAndFlush(savedAuthenticatedCustomer);
    }

    public Customers createEntityCustomers(String customerName, User user){
        return new Customers()
            .username(customerName)
            .password(DEFAULT_PASSWORD)
            .balance(1_000_000D)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .user(user);
    }

    public Organization createEntityOrganization(){
        return new Organization()
            .name(DEFAULT_ORG_NAME)
            .orgOwnerName(logins[1]);
    }
    public Groups createEntityGroup(String groupName, Organization organizationEntity, Set<Customers> customersSet) {
        return new Groups()
            .name(groupName)
            .groupOwnerName(logins[1])
            .organization(organizationEntity)
            .customers(customersSet);
    }

    public Set<Groups> createEntityGroupsAndSave(Organization organizationEntity) {
        Set<Customers> customersSet = new HashSet<>();
        customersSet.add(savedAuthenticatedCustomer);
        String [] groupOwners = {logins[1], "owner 1", logins[1], "owner 3" ,logins[1]};
        String [] groupNames = {DEFAULT_GROUP_NAME, "group 2", "group 3", "group 4", "group 5"};
        Set<Groups> groups = new HashSet<>();

        for (int i = 0; i < groupOwners.length; i++) {
            Groups newGroup = new Groups()
                .name(groupNames[i])
                .groupOwnerName(groupOwners[i])
                .organization(organizationEntity)
                .customers(customersSet);
            groups.add(newGroup);
        }

        return new HashSet<>(groupsRepository.saveAllAndFlush(groups));
    }

    public Services createServiceEntity(Set<Groups> savedEntityGroups){
        return new Services()
            .name(DEFAULT_SERVICE_NAME)
            .groups(savedEntityGroups)
            .price(DEFAULT_SERVICE_PRICE)
            .periodType(DEFAULT_PERIOD_TYPE)
            .countPeriod(DEFAULT_COUNT_PERIOD)
            .startedPeriod(DEFAULT_STARTED_PERIOD);
    }

    public Payment createPaymentEntity(Groups savedGroupEntity, Services serviceEntity){
        return new Payment()
            .isPayed(false)
            .startedPeriod(DEFAULT_STARTED_PERIOD)
            .finishedPeriod(DEFAULT_FINISHED_PERIOD)
            .customer(savedAuthenticatedCustomer)
            .Group(savedGroupEntity)
            .Service(serviceEntity)
            .paymentForPeriod(DEFAULT_SERVICE_PRICE)
            .paidMoney(0D);
    }

    public List<Customers> createNewUserCustomerAndSave(){
        User newUser1 =  buildUserEntity("ROLE_CUSTOMER", "newUser1", "someGmail1@gmail.com");
        User newUser2 =  buildUserEntity("ROLE_CUSTOMER", "newUser2", "someGmail2@gmail.com");

        userRepository.saveAll(List.of(newUser1, newUser2));
        userRepository.flush();

        Customers newCustomer1 = createEntityCustomers("newUser1", newUser1);
        Customers newCustomer2 = createEntityCustomers("newUser2", newUser2);

        return customersRepository.saveAllAndFlush(List.of(newCustomer1, newCustomer2));

    }

    public List<Payment> createMultiplePayments(){
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        List<Customers> customers = createNewUserCustomerAndSave();
        Customers newCustomer1 = customers.get(0);
        Customers newCustomer2 = customers.get(1);

        groupEntity = createEntityGroup(
            DEFAULT_GROUP_NAME, organizationEntity,
            Set.of(savedAuthenticatedCustomer, newCustomer1, newCustomer2));
        groupsRepository.saveAndFlush(groupEntity);

        serviceEntity = createServiceEntity(Set.of(groupEntity));
        servicesRepository.saveAndFlush(serviceEntity);

        List<Payment> paymentList = new ArrayList<>();
        List<Customers> customersList = List.of(
            savedAuthenticatedCustomer, newCustomer2, savedAuthenticatedCustomer, newCustomer1, newCustomer2);

        for (Customers customer: customersList){
            Payment newPayment = new Payment()
                .isPayed(false)
                .startedPeriod(DEFAULT_STARTED_PERIOD)
                .finishedPeriod(DEFAULT_FINISHED_PERIOD)
                .customer(customer)
                .Group(groupEntity)
                .Service(serviceEntity)
                .paymentForPeriod(DEFAULT_SERVICE_PRICE)
                .paidMoney(0D);
            paymentList.add(newPayment);
        }
        return paymentList;
    }


    @Order(1)
    @Transactional
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username"})
    void checkRegisterInvalidate(String invalidData) throws Exception {
        String email = "valid@gmail.com";
        String password = "validPass";
        String username = "validUsername";

        switch (invalidData){
            case "email" -> email = "invalid";
            case "password" -> password = "few";
            case "username" -> username = "inva-lid)";
        }
        ManagedUserVM invalidUser = new ManagedUserVM();
        invalidUser.setLogin(username);
        invalidUser.setPassword(password);
        invalidUser.setFirstName(DEFAULT_CLIENT_NAME);
        invalidUser.setLastName("X");
        invalidUser.setEmail(email);
        invalidUser.setActivated(true);
        invalidUser.setImageUrl("http://placehold.it/50x50");
        invalidUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        invalidUser.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        mockMvc.perform(post("/api/test-register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin(username);
        assertThat(user).isEmpty();
    }

    @Order(2)
    @Test
    @Transactional
    void checkRegisterAlreadyExistingUser() throws Exception{
        assertThat(userRepository.findOneByLogin(logins[0])).isPresent();
        int sizeUserBefore = userRepository.findAll().size();

        // post request to create user with current user one more time
        ManagedUserVM validUser = buildDefaultManagedUser(ROLE_CUSTOMER, logins[0], emails[0]);

        mockMvc.perform(post("/api/test-register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(validUser)))
            .andExpect(status().isBadRequest());

        int sizeUserAfter = userRepository.findAll().size();
        Assertions.assertEquals(sizeUserAfter, sizeUserBefore);
    }
    private ManagedUserVM buildDefaultManagedUser(String role, String username, String email){
        Set<String> authorities = new HashSet<>();
        authorities.add("ROLE_USER");
        authorities.add(role);

        ManagedUserVM validUser = new ManagedUserVM();
        validUser.setLogin(username);
        validUser.setPassword(DEFAULT_PASSWORD);
        validUser.setFirstName(DEFAULT_CLIENT_NAME);
        validUser.setLastName("X");
        validUser.setEmail(email);
        validUser.setImageUrl("http://placehold.it/50x50");
        validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        validUser.setAuthorities(authorities);
        validUser.setActivated(true);
        validUser.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        validUser.setAccount(150000D);

        return validUser;
    }

//
//    @Order(3)
//    @ParameterizedTest
//    @Transactional
//    @ValueSource(strings = {ROLE_CUSTOMER, ROLE_MANAGER})
//    void testClientRegister(String role) throws Exception {
//        ManagedUserVM validUser = buildDefaultManagedUser(role, logins[index], emails[index]);
//
//        assertThat(userRepository.findOneByLogin(DEFAULT_CLIENT_NAME)).isEmpty();
//
//        mockMvc.perform(post("/api/test-register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsBytes(validUser)))
//            .andExpect(status().isCreated());
//
//        Optional<User> optionalUser = userRepository.findOneByLogin(logins[index++]);
//        Assertions.assertTrue(optionalUser.isPresent());
//        User user = optionalUser.get();
//        Authority userAuthority = new Authority();
//        userAuthority.setName(role);
//        Assertions.assertTrue(user.getAuthorities().contains(userAuthority));
//        if (role.equals(ROLE_CUSTOMER)) {
//            Optional<Customers> authenticatedCustomerOptional = customersRepository.findByUser(user);
//            Assertions.assertTrue(authenticatedCustomerOptional.isPresent());
//            savedAuthenticatedCustomer = authenticatedCustomerOptional.get();
//        }
//    }

    @Order(3)
    @ParameterizedTest
    @Transactional
    @ValueSource(strings = {ROLE_CUSTOMER, ROLE_MANAGER})
    void testGenerateTokenForClients(String role) throws Exception {
        LoginVM loginVM = new LoginVM();
        loginVM.setUsername(logins[index++]);
        loginVM.setPassword(DEFAULT_PASSWORD);

        String authorizationToken = (String) mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginVM)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(is(emptyString()))))
            .andReturn()
            .getResponse()
            .getHeaderValue("Authorization");

        if (role.equals(ROLE_CUSTOMER)){
            tokenCustomer = authorizationToken;
        }else{
            tokenManager = authorizationToken;
        }
    }

    private User buildUserEntity(String role, String username, String email){
        User user = new User();
        user.setLogin(username);
        user.setEmail(email);
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        Set<Authority> newAuthorities = new HashSet<>();
        Authority authority = new Authority();

        if (role.equals(ROLE_MANAGER)){
            authority.setName(ROLE_MANAGER);
        }else{
            authority.setName(ROLE_CUSTOMER);
        }

        newAuthorities.add(authority);
        user.setAuthorities(newAuthorities);
        return user;
    }

    @Order(4)
    @Test
    @Transactional
    void checkPermissionWhileCreatingOrganization() throws Exception {
        organizationEntity = createEntityOrganization();

        OrganizationDTO organizationDTO = OrganizationsMapper.toDtoWithGroups(organizationEntity);
        mockMvc.perform(
                post("/api/organizations")
                    .header("Authorization", tokenCustomer)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(organizationDTO))
            )
            .andDo(print())
            .andExpect(status().isForbidden());
    }
    @Test
    @Transactional
    @Order(5)
    void checkPermissionWhileCreatingGroup() throws Exception {
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        groupEntity = createEntityGroup(DEFAULT_GROUP_NAME, organizationEntity, Set.of(savedAuthenticatedCustomer));
        groupsRepository.saveAndFlush(groupEntity);

        GroupsDTO groupsDTO = GroupMapper.toDto(groupEntity);
        mockMvc.perform(
            post("/api/groups")
                .header("Authorization", tokenCustomer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(groupsDTO))
            )
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @Order(6)
    void testCreateNewCustomer() throws Exception {
        Customers customers = createEntityCustomers("Dovud", savedUserCustomerEntity);
        CustomersDTO customersDTO = CustomerMapper.toDtoForTest(customers);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(customersDTO))
                .header("Authorization", tokenCustomer)
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Order(7)
    @Test
    @Transactional
    void checkCreateExistingOrganization() throws Exception {
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);
        int databaseSizeBeforeCreate = organizationRepository.findAll().size();

        OrganizationDTO organizationDTO = OrganizationsMapper.toDtoWithGroups(organizationEntity);
        mockMvc.perform(
                post("/api/organizations")
                    .header("Authorization", tokenManager)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(organizationDTO))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());

        int databaseSizeAfterCreate = organizationRepository.findAll().size();
        Assertions.assertEquals(databaseSizeAfterCreate, databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @Order(8)
    void testCreateOrganization() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().size();
        organizationEntity = createEntityOrganization();

        OrganizationDTO organizationDTO = OrganizationsMapper.toDtoWithGroups(organizationEntity);
        mockMvc.perform(
                post("/api/organizations")
                    .header("Authorization", tokenManager)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(organizationDTO))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList)
            .isNotNull()
            .hasSize(databaseSizeBeforeCreate + 1);

        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        Assertions.assertEquals(testOrganization.getName(), DEFAULT_ORG_NAME);
        Assertions.assertEquals(testOrganization.getOrgOwnerName(), logins[1]);
    }

    @Test
    @Transactional
    @Order(9)
    void testCreateGroups() throws Exception {
        int databaseSizeBeforeCreate = groupsRepository.findAll().size();
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        groupEntity = createEntityGroup(DEFAULT_GROUP_NAME, organizationEntity, Set.of(savedAuthenticatedCustomer));

        GroupsDTO groupsDTO = GroupMapper.toDto(groupEntity);

        mockMvc.perform(post("/api/groups")
                .header("Authorization", tokenManager)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(groupsDTO)))
            .andDo(print())
            .andExpect(status().isCreated());

        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeCreate + 1);
        Groups testGroups = groupsList.get(groupsList.size() - 1);
        assertThat(testGroups.getName()).isEqualTo(DEFAULT_GROUP_NAME);
        assertThat(testGroups.getGroupOwnerName()).isEqualTo(logins[1]);
    }

    @Test
    @Transactional
    @Order(10)
    void testCreateServiceAndPaymentForCustomer() throws Exception {
        int databaseSizeBeforeCreate = servicesRepository.findAll().size();
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        groupEntity = createEntityGroup(DEFAULT_GROUP_NAME, organizationEntity, Set.of(savedAuthenticatedCustomer));
        groupsRepository.saveAndFlush(groupEntity);

        serviceEntity = createServiceEntity(Set.of(groupEntity));
        ServicesDTO servicesDTO = ServiceMapper.toDtoForSaveServiceMethod(serviceEntity);

        String response = mockMvc.perform(post("/api/services")
                .header("Authorization", tokenManager)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(servicesDTO)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeCreate + 1);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getName()).isEqualTo(DEFAULT_SERVICE_NAME);
        assertThat(testServices.getPrice()).isEqualTo(DEFAULT_SERVICE_PRICE);
        assertThat(testServices.getStartedPeriod()).isEqualTo(DEFAULT_STARTED_PERIOD);
        assertThat(testServices.getPeriodType()).isEqualTo(DEFAULT_PERIOD_TYPE);
        assertThat(testServices.getCountPeriod()).isEqualTo(DEFAULT_COUNT_PERIOD);

        ResponseDTO<ServicesDTO> responseDTO = objectMapper.readValue(response, new TypeReference<ResponseDTO<ServicesDTO>>() {});
        Assertions.assertNotNull(responseDTO);
        ServicesDTO responseServicesDTO = responseDTO.getResponseData();
        Assertions.assertNotNull(responseServicesDTO);

        // check payment customer
        List<Payment> paymentList = paymentRepository.findAll();
        Assertions.assertNotNull(paymentList);
        Payment payment = paymentList.get(0);
        Assertions.assertNotNull(payment);
        Assertions.assertEquals(payment.getService().getId(), responseServicesDTO.getId());
        Assertions.assertEquals(payment.getService().getName(), responseServicesDTO.getName());
        Assertions.assertEquals(payment.getService().getPrice(), responseServicesDTO.getPrice());
        Assertions.assertEquals(payment.getPaidMoney(), 0D);

        Optional<Payment> paymentOptional = paymentRepository.findByCustomerAndGroupAndServiceAndStartedPeriodAndIsPayedFalse(
            savedAuthenticatedCustomer, payment.getGroup(), payment.getService(), payment.getStartedPeriod()
        );

        assertThat(paymentOptional).isPresent();
        paymentEntity = paymentOptional.get();
    }

    @Order(11)
    @Test
    @Transactional
    void testCustomerPaymentForOnePeriod() throws Exception{
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        groupEntity = createEntityGroup(DEFAULT_GROUP_NAME, organizationEntity, Set.of(savedAuthenticatedCustomer));
        groupsRepository.saveAndFlush(groupEntity);

        serviceEntity = createServiceEntity(Set.of(groupEntity));
        servicesRepository.saveAndFlush(serviceEntity);

        paymentEntity = createPaymentEntity(groupEntity, serviceEntity);
        paymentEntity = paymentRepository.saveAndFlush(paymentEntity);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        entityManager.detach(paymentEntity);
        paymentEntity
            .paidMoney(55000D)
            .paymentForPeriod(DEFAULT_SERVICE_PRICE)
            .startedPeriod(DEFAULT_STARTED_PERIOD);
        PaymentDTO paymentDTO = PaymentsMapper.toDto(paymentEntity);

        mockMvc
            .perform(
                put(new URI("/api/payment/pay"))
                    .header("Authorization", tokenCustomer)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isOk());

        List<Payment> paymentList = paymentRepository.findAll();

        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);

        assertThat(testPayment.getPaidMoney()).isEqualTo(55000D);
        assertThat(testPayment.getPaymentForPeriod()).isEqualTo(DEFAULT_SERVICE_PRICE);
        assertThat(testPayment.getIsPayed()).isEqualTo(false);
        assertThat(testPayment.getStartedPeriod()).isEqualTo(DEFAULT_STARTED_PERIOD);
    }

    @Order(12)
    @ParameterizedTest
    @ValueSource(doubles = {2.5, 3.4, 6.5, 4.0})
    @Transactional
    void testCustomerPaymentWithMultipleMoneyGreaterThanPaymentForOnePeriod(double amount) throws Exception{
        int count = (int) amount;
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        groupEntity = createEntityGroup(DEFAULT_GROUP_NAME, organizationEntity, Set.of(savedAuthenticatedCustomer));
        groupsRepository.saveAndFlush(groupEntity);

        serviceEntity = createServiceEntity(Set.of(groupEntity));
        servicesRepository.saveAndFlush(serviceEntity);

        paymentEntity = createPaymentEntity(groupEntity, serviceEntity);
        paymentEntity = paymentRepository.saveAndFlush(paymentEntity);

        entityManager.detach(paymentEntity);
        paymentEntity
            .paidMoney(DEFAULT_SERVICE_PRICE * amount)
            .paymentForPeriod(DEFAULT_SERVICE_PRICE)
            .startedPeriod(DEFAULT_STARTED_PERIOD);
        PaymentDTO paymentDTO = PaymentsMapper.toDto(paymentEntity);

        mockMvc
            .perform(
                put(new URI("/api/payment/pay"))
                    .header("Authorization", tokenCustomer)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isOk());

        List<Payment> paymentList = paymentRepository.findAll();
        Payment remainingPayment = null;

        for (Payment p: paymentList){
            if (!p.getIsPayed()){
                remainingPayment = p;
                break;
            }
        }

        if (remainingPayment != null){
            Assertions.assertEquals(count, paymentList.size()-1);
            Assertions.assertNotNull(remainingPayment);
            double remainingMoney = (remainingPayment.getPaymentForPeriod() * amount) - (remainingPayment.getPaymentForPeriod() * count);
            Assertions.assertEquals(remainingPayment.getPaidMoney(), remainingMoney);
            assertThat(remainingPayment.getPaymentForPeriod()).isEqualTo(DEFAULT_SERVICE_PRICE);
        }else{
            Assertions.assertEquals(count, paymentList.size());
            assertThat(paymentList.get(0).getPaymentForPeriod()).isEqualTo(DEFAULT_SERVICE_PRICE);
        }
    }

    @Order(13)
    @Test
    @Transactional
    void checkCustomerPaymentWithNotEnoughMoney() throws Exception{
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        groupEntity = createEntityGroup(DEFAULT_GROUP_NAME, organizationEntity, Set.of(savedAuthenticatedCustomer));
        groupsRepository.saveAndFlush(groupEntity);

        serviceEntity = createServiceEntity(Set.of(groupEntity));
        servicesRepository.saveAndFlush(serviceEntity);

        paymentEntity = createPaymentEntity(groupEntity, serviceEntity);
        paymentEntity = paymentRepository.saveAndFlush(paymentEntity);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        entityManager.detach(paymentEntity);
        paymentEntity
            .paidMoney(2_000_000D)
            .paymentForPeriod(DEFAULT_SERVICE_PRICE)
            .startedPeriod(DEFAULT_STARTED_PERIOD);
        PaymentDTO paymentDTO = PaymentsMapper.toDto(paymentEntity);

        String response = mockMvc
            .perform(
                put(new URI("/api/payment/pay"))
                    .header("Authorization", tokenCustomer)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(paymentDTO))
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ResponseDTO responseDTO = objectMapper.readValue(response, new TypeReference<ResponseDTO>() {});

        Assertions.assertNotNull(responseDTO);
        Assertions.assertEquals(responseDTO.getCode(), -5);
        Assertions.assertFalse(responseDTO.getSuccess());
        Assertions.assertEquals(responseDTO.getMessage(), NOT_ENOUGH);

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @Order(14)
    void getAllManagerGroups() throws Exception {
        organizationEntity = createEntityOrganization();
        organizationRepository.saveAndFlush(organizationEntity);

        Set<Groups> savedEntityGroups = createEntityGroupsAndSave(organizationEntity);
        groupsRepository.saveAllAndFlush(savedEntityGroups);

        String response = mockMvc.perform(
            get("/api/manager-groups")
                .header("Authorization", tokenManager)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_GROUP_NAME)))
            .andExpect(jsonPath("$.[*].groupOwnerName").value(hasItem(logins[1])))
            .andReturn()
            .getResponse()
            .getContentAsString();

        List<Groups> responseGroupList = objectMapper.readValue(response, new TypeReference<List<Groups>>() {});
        Assertions.assertNotNull(responseGroupList);
        Assertions.assertEquals(3, responseGroupList.size());
    }

    @Order(15)
    @Test
    @Transactional
    void getAllCustomerPayments() throws Exception {
        List<Payment> paymentList = createMultiplePayments();
        paymentRepository.saveAllAndFlush(paymentList);

        String response = mockMvc
            .perform(
                get("/api/customer-payments")
                    .header("Authorization", tokenCustomer)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

        ResponseDTO<List<PaymentDTO>> responseDTO = objectMapper.readValue(response, new TypeReference<ResponseDTO<List<PaymentDTO>>>() {});
        Assertions.assertNotNull(responseDTO);
        Assertions.assertNotNull(responseDTO.getResponseData());
        Assertions.assertTrue(responseDTO.getSuccess());

        List<PaymentDTO> responsePaymentList = responseDTO.getResponseData();
        Assertions.assertEquals(2, responsePaymentList.size());

        for (PaymentDTO paymentDTO : responsePaymentList) {
            Assertions.assertEquals(savedAuthenticatedCustomer.getId(), paymentDTO.getCustomer().getId());
            Assertions.assertEquals(savedAuthenticatedCustomer.getUsername(), paymentDTO.getCustomer().getUsername());
        }
    }
}
