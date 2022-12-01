package uz.devops.intern.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.IntegrationTest;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.mapper.CustomerTelegramMapper;

/**
 * Integration tests for the {@link CustomerTelegramResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomerTelegramResourceIT {

    private static final Boolean DEFAULT_IS_BOT = false;
    private static final Boolean UPDATED_IS_BOT = true;

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final Long DEFAULT_TELEGRAM_ID = 1L;
    private static final Long UPDATED_TELEGRAM_ID = 2L;

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Integer DEFAULT_STEP = 1;
    private static final Integer UPDATED_STEP = 2;

    private static final Boolean DEFAULT_CAN_JOIN_GROUPS = false;
    private static final Boolean UPDATED_CAN_JOIN_GROUPS = true;

    private static final String DEFAULT_LANGUAGE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/customer-telegrams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CustomerTelegramRepository customerTelegramRepository;

    @Autowired
    private CustomerTelegramMapper customerTelegramMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerTelegramMockMvc;

    private CustomerTelegram customerTelegram;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerTelegram createEntity(EntityManager em) {
        CustomerTelegram customerTelegram = new CustomerTelegram()
            .isBot(DEFAULT_IS_BOT)
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .username(DEFAULT_USERNAME)
            .telegramId(DEFAULT_TELEGRAM_ID)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .step(DEFAULT_STEP)
            .canJoinGroups(DEFAULT_CAN_JOIN_GROUPS)
            .languageCode(DEFAULT_LANGUAGE_CODE)
            .isActive(DEFAULT_IS_ACTIVE);
        return customerTelegram;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerTelegram createUpdatedEntity(EntityManager em) {
        CustomerTelegram customerTelegram = new CustomerTelegram()
            .isBot(UPDATED_IS_BOT)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .username(UPDATED_USERNAME)
            .telegramId(UPDATED_TELEGRAM_ID)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .step(UPDATED_STEP)
            .canJoinGroups(UPDATED_CAN_JOIN_GROUPS)
            .languageCode(UPDATED_LANGUAGE_CODE)
            .isActive(UPDATED_IS_ACTIVE);
        return customerTelegram;
    }

    @BeforeEach
    public void initTest() {
        customerTelegram = createEntity(em);
    }

    @Test
    @Transactional
    void createCustomerTelegram() throws Exception {
        int databaseSizeBeforeCreate = customerTelegramRepository.findAll().size();
        // Create the CustomerTelegram
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);
        restCustomerTelegramMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeCreate + 1);
        CustomerTelegram testCustomerTelegram = customerTelegramList.get(customerTelegramList.size() - 1);
        assertThat(testCustomerTelegram.getIsBot()).isEqualTo(DEFAULT_IS_BOT);
        assertThat(testCustomerTelegram.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testCustomerTelegram.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testCustomerTelegram.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testCustomerTelegram.getTelegramId()).isEqualTo(DEFAULT_TELEGRAM_ID);
        assertThat(testCustomerTelegram.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testCustomerTelegram.getStep()).isEqualTo(DEFAULT_STEP);
        assertThat(testCustomerTelegram.getCanJoinGroups()).isEqualTo(DEFAULT_CAN_JOIN_GROUPS);
        assertThat(testCustomerTelegram.getLanguageCode()).isEqualTo(DEFAULT_LANGUAGE_CODE);
        assertThat(testCustomerTelegram.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    void createCustomerTelegramWithExistingId() throws Exception {
        // Create the CustomerTelegram with an existing ID
        customerTelegram.setId(1L);
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);

        int databaseSizeBeforeCreate = customerTelegramRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerTelegramMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCustomerTelegrams() throws Exception {
        // Initialize the database
        customerTelegramRepository.saveAndFlush(customerTelegram);

        // Get all the customerTelegramList
        restCustomerTelegramMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerTelegram.getId().intValue())))
            .andExpect(jsonPath("$.[*].isBot").value(hasItem(DEFAULT_IS_BOT.booleanValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].telegramId").value(hasItem(DEFAULT_TELEGRAM_ID.intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].step").value(hasItem(DEFAULT_STEP)))
            .andExpect(jsonPath("$.[*].canJoinGroups").value(hasItem(DEFAULT_CAN_JOIN_GROUPS.booleanValue())))
            .andExpect(jsonPath("$.[*].languageCode").value(hasItem(DEFAULT_LANGUAGE_CODE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getCustomerTelegram() throws Exception {
        // Initialize the database
        customerTelegramRepository.saveAndFlush(customerTelegram);

        // Get the customerTelegram
        restCustomerTelegramMockMvc
            .perform(get(ENTITY_API_URL_ID, customerTelegram.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customerTelegram.getId().intValue()))
            .andExpect(jsonPath("$.isBot").value(DEFAULT_IS_BOT.booleanValue()))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.telegramId").value(DEFAULT_TELEGRAM_ID.intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.step").value(DEFAULT_STEP))
            .andExpect(jsonPath("$.canJoinGroups").value(DEFAULT_CAN_JOIN_GROUPS.booleanValue()))
            .andExpect(jsonPath("$.languageCode").value(DEFAULT_LANGUAGE_CODE))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingCustomerTelegram() throws Exception {
        // Get the customerTelegram
        restCustomerTelegramMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomerTelegram() throws Exception {
        // Initialize the database
        customerTelegramRepository.saveAndFlush(customerTelegram);

        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();

        // Update the customerTelegram
        CustomerTelegram updatedCustomerTelegram = customerTelegramRepository.findById(customerTelegram.getId()).get();
        // Disconnect from session so that the updates on updatedCustomerTelegram are not directly saved in db
        em.detach(updatedCustomerTelegram);
        updatedCustomerTelegram
            .isBot(UPDATED_IS_BOT)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .username(UPDATED_USERNAME)
            .telegramId(UPDATED_TELEGRAM_ID)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .step(UPDATED_STEP)
            .canJoinGroups(UPDATED_CAN_JOIN_GROUPS)
            .languageCode(UPDATED_LANGUAGE_CODE)
            .isActive(UPDATED_IS_ACTIVE);
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(updatedCustomerTelegram);

        restCustomerTelegramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerTelegramDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isOk());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
        CustomerTelegram testCustomerTelegram = customerTelegramList.get(customerTelegramList.size() - 1);
        assertThat(testCustomerTelegram.getIsBot()).isEqualTo(UPDATED_IS_BOT);
        assertThat(testCustomerTelegram.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testCustomerTelegram.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testCustomerTelegram.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testCustomerTelegram.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testCustomerTelegram.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCustomerTelegram.getStep()).isEqualTo(UPDATED_STEP);
        assertThat(testCustomerTelegram.getCanJoinGroups()).isEqualTo(UPDATED_CAN_JOIN_GROUPS);
        assertThat(testCustomerTelegram.getLanguageCode()).isEqualTo(UPDATED_LANGUAGE_CODE);
        assertThat(testCustomerTelegram.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingCustomerTelegram() throws Exception {
        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();
        customerTelegram.setId(count.incrementAndGet());

        // Create the CustomerTelegram
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerTelegramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerTelegramDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomerTelegram() throws Exception {
        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();
        customerTelegram.setId(count.incrementAndGet());

        // Create the CustomerTelegram
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerTelegramMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomerTelegram() throws Exception {
        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();
        customerTelegram.setId(count.incrementAndGet());

        // Create the CustomerTelegram
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerTelegramMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomerTelegramWithPatch() throws Exception {
        // Initialize the database
        customerTelegramRepository.saveAndFlush(customerTelegram);

        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();

        // Update the customerTelegram using partial update
        CustomerTelegram partialUpdatedCustomerTelegram = new CustomerTelegram();
        partialUpdatedCustomerTelegram.setId(customerTelegram.getId());

        partialUpdatedCustomerTelegram
            .isBot(UPDATED_IS_BOT)
            .telegramId(UPDATED_TELEGRAM_ID)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .step(UPDATED_STEP);

        restCustomerTelegramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerTelegram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomerTelegram))
            )
            .andExpect(status().isOk());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
        CustomerTelegram testCustomerTelegram = customerTelegramList.get(customerTelegramList.size() - 1);
        assertThat(testCustomerTelegram.getIsBot()).isEqualTo(UPDATED_IS_BOT);
        assertThat(testCustomerTelegram.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testCustomerTelegram.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testCustomerTelegram.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testCustomerTelegram.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testCustomerTelegram.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCustomerTelegram.getStep()).isEqualTo(UPDATED_STEP);
        assertThat(testCustomerTelegram.getCanJoinGroups()).isEqualTo(DEFAULT_CAN_JOIN_GROUPS);
        assertThat(testCustomerTelegram.getLanguageCode()).isEqualTo(DEFAULT_LANGUAGE_CODE);
        assertThat(testCustomerTelegram.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateCustomerTelegramWithPatch() throws Exception {
        // Initialize the database
        customerTelegramRepository.saveAndFlush(customerTelegram);

        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();

        // Update the customerTelegram using partial update
        CustomerTelegram partialUpdatedCustomerTelegram = new CustomerTelegram();
        partialUpdatedCustomerTelegram.setId(customerTelegram.getId());

        partialUpdatedCustomerTelegram
            .isBot(UPDATED_IS_BOT)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .username(UPDATED_USERNAME)
            .telegramId(UPDATED_TELEGRAM_ID)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .step(UPDATED_STEP)
            .canJoinGroups(UPDATED_CAN_JOIN_GROUPS)
            .languageCode(UPDATED_LANGUAGE_CODE)
            .isActive(UPDATED_IS_ACTIVE);

        restCustomerTelegramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerTelegram.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomerTelegram))
            )
            .andExpect(status().isOk());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
        CustomerTelegram testCustomerTelegram = customerTelegramList.get(customerTelegramList.size() - 1);
        assertThat(testCustomerTelegram.getIsBot()).isEqualTo(UPDATED_IS_BOT);
        assertThat(testCustomerTelegram.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testCustomerTelegram.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testCustomerTelegram.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testCustomerTelegram.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testCustomerTelegram.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCustomerTelegram.getStep()).isEqualTo(UPDATED_STEP);
        assertThat(testCustomerTelegram.getCanJoinGroups()).isEqualTo(UPDATED_CAN_JOIN_GROUPS);
        assertThat(testCustomerTelegram.getLanguageCode()).isEqualTo(UPDATED_LANGUAGE_CODE);
        assertThat(testCustomerTelegram.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingCustomerTelegram() throws Exception {
        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();
        customerTelegram.setId(count.incrementAndGet());

        // Create the CustomerTelegram
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerTelegramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customerTelegramDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomerTelegram() throws Exception {
        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();
        customerTelegram.setId(count.incrementAndGet());

        // Create the CustomerTelegram
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerTelegramMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomerTelegram() throws Exception {
        int databaseSizeBeforeUpdate = customerTelegramRepository.findAll().size();
        customerTelegram.setId(count.incrementAndGet());

        // Create the CustomerTelegram
        CustomerTelegramDTO customerTelegramDTO = customerTelegramMapper.toDto(customerTelegram);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerTelegramMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customerTelegramDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerTelegram in the database
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomerTelegram() throws Exception {
        // Initialize the database
        customerTelegramRepository.saveAndFlush(customerTelegram);

        int databaseSizeBeforeDelete = customerTelegramRepository.findAll().size();

        // Delete the customerTelegram
        restCustomerTelegramMockMvc
            .perform(delete(ENTITY_API_URL_ID, customerTelegram.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        assertThat(customerTelegramList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
