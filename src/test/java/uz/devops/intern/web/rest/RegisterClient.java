package uz.devops.intern.web.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.IntegrationTest;
import uz.devops.intern.config.Constants;
import uz.devops.intern.domain.Authority;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.User;
import uz.devops.intern.repository.AuthorityRepository;
import uz.devops.intern.repository.CustomersRepository;
import uz.devops.intern.repository.UserRepository;
import uz.devops.intern.web.rest.TestUtil;
import uz.devops.intern.web.rest.vm.ManagedUserVM;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@IntegrationTest
public class RegisterClient {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private MockMvc mockMvc;
    private static String [] logins = {"customer", "manager"};
    private static String login = logins[0];
    private static String [] emails = {"example1@gmail.com", "example2@gmail.com"};
    private static String email = emails[0];

    @BeforeEach
    public void postNewRoleToAuthorityTable(){
//        List<User> userList =  userRepository.findAll();
//        System.out.println(userList);

        Set<Authority> newAuthorities = new HashSet<>();
        Authority authorityCustomer = new Authority();
        authorityCustomer.setName("ROLE_CUSTOMER");
        newAuthorities.add(authorityCustomer);
        Authority authorityManager = new Authority();
        authorityManager.setName("ROLE_MANAGER");
        newAuthorities.add(authorityManager);

        authorityRepository.saveAllAndFlush(newAuthorities);
    }

    @ParameterizedTest
    @Transactional
    @ValueSource(strings = {"ROLE_CUSTOMER", "ROLE_MANAGER"})
    @DisplayName("check customer and manager registerValid")
    void testCustomerRegisterValid(String role) throws Exception {
        Set<String> authorities = new HashSet<>();
        authorities.add("ROLE_USER");
        authorities.add(role);

        ManagedUserVM validUser = new ManagedUserVM();
        validUser.setLogin(login);
        validUser.setPassword("12345");
        validUser.setFirstName("robot");
        validUser.setLastName("X");
        validUser.setEmail(email);
        validUser.setImageUrl("http://placehold.it/50x50");
        validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        validUser.setAuthorities(authorities);
        validUser.setActivated(true);
        validUser.setPhoneNumber("+998950645097");
        validUser.setAccount(150000D);
        assertThat(userRepository.findOneByLogin("robot")).isEmpty();

        mockMvc.perform(post("/api/test-register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());
        Optional<User> optionalUser = userRepository.findOneByLogin(login);
        Assertions.assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        Authority userAuthority = new Authority();
        userAuthority.setName(role);
        Assertions.assertTrue(user.getAuthorities().contains(userAuthority));
        if (role.equals("ROLE_CUSTOMER")){
            Optional<Customers> authenticatedCustomerOptional = customersRepository.findByUser(user);
            Assertions.assertTrue(authenticatedCustomerOptional.isPresent());
        }

        login = logins[1];
        email = emails[1];
    }
}
