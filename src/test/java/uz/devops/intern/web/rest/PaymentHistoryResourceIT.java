package uz.devops.intern.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
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
import uz.devops.intern.domain.PaymentHistory;
import uz.devops.intern.repository.PaymentHistoryRepository;
import uz.devops.intern.service.dto.PaymentHistoryDTO;
import uz.devops.intern.service.mapper.PaymentHistoryMapper;

/**
 * Integration tests for the {@link PaymentHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentHistoryResourceIT {

    private static final String DEFAULT_ORGANIZATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORGANIZATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_SUM = 1D;
    private static final Double UPDATED_SUM = 2D;

    private static final LocalDate DEFAULT_CREATED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_AT = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/payment-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private PaymentHistoryMapper paymentHistoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentHistoryMockMvc;

    private PaymentHistory paymentHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentHistory createEntity(EntityManager em) {
        PaymentHistory paymentHistory = new PaymentHistory()
            .organizationName(DEFAULT_ORGANIZATION_NAME)
            .serviceName(DEFAULT_SERVICE_NAME)
            .groupName(DEFAULT_GROUP_NAME)
            .sum(DEFAULT_SUM)
            .createdAt(DEFAULT_CREATED_AT);
        return paymentHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentHistory createUpdatedEntity(EntityManager em) {
        PaymentHistory paymentHistory = new PaymentHistory()
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .serviceName(UPDATED_SERVICE_NAME)
            .groupName(UPDATED_GROUP_NAME)
            .sum(UPDATED_SUM)
            .createdAt(UPDATED_CREATED_AT);
        return paymentHistory;
    }

    @BeforeEach
    public void initTest() {
        paymentHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createPaymentHistory() throws Exception {
        int databaseSizeBeforeCreate = paymentHistoryRepository.findAll().size();
        // Create the PaymentHistory
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);
        restPaymentHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        PaymentHistory testPaymentHistory = paymentHistoryList.get(paymentHistoryList.size() - 1);
        assertThat(testPaymentHistory.getOrganizationName()).isEqualTo(DEFAULT_ORGANIZATION_NAME);
        assertThat(testPaymentHistory.getServiceName()).isEqualTo(DEFAULT_SERVICE_NAME);
        assertThat(testPaymentHistory.getGroupName()).isEqualTo(DEFAULT_GROUP_NAME);
        assertThat(testPaymentHistory.getSum()).isEqualTo(DEFAULT_SUM);
        assertThat(testPaymentHistory.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createPaymentHistoryWithExistingId() throws Exception {
        // Create the PaymentHistory with an existing ID
        paymentHistory.setId(1L);
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);

        int databaseSizeBeforeCreate = paymentHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPaymentHistories() throws Exception {
        // Initialize the database
        paymentHistoryRepository.saveAndFlush(paymentHistory);

        // Get all the paymentHistoryList
        restPaymentHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].organizationName").value(hasItem(DEFAULT_ORGANIZATION_NAME)))
            .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
            .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME)))
            .andExpect(jsonPath("$.[*].sum").value(hasItem(DEFAULT_SUM.doubleValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPaymentHistory() throws Exception {
        // Initialize the database
        paymentHistoryRepository.saveAndFlush(paymentHistory);

        // Get the paymentHistory
        restPaymentHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentHistory.getId().intValue()))
            .andExpect(jsonPath("$.organizationName").value(DEFAULT_ORGANIZATION_NAME))
            .andExpect(jsonPath("$.serviceName").value(DEFAULT_SERVICE_NAME))
            .andExpect(jsonPath("$.groupName").value(DEFAULT_GROUP_NAME))
            .andExpect(jsonPath("$.sum").value(DEFAULT_SUM.doubleValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPaymentHistory() throws Exception {
        // Get the paymentHistory
        restPaymentHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentHistory() throws Exception {
        // Initialize the database
        paymentHistoryRepository.saveAndFlush(paymentHistory);

        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();

        // Update the paymentHistory
        PaymentHistory updatedPaymentHistory = paymentHistoryRepository.findById(paymentHistory.getId()).get();
        // Disconnect from session so that the updates on updatedPaymentHistory are not directly saved in db
        em.detach(updatedPaymentHistory);
        updatedPaymentHistory
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .serviceName(UPDATED_SERVICE_NAME)
            .groupName(UPDATED_GROUP_NAME)
            .sum(UPDATED_SUM)
            .createdAt(UPDATED_CREATED_AT);
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(updatedPaymentHistory);

        restPaymentHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
        PaymentHistory testPaymentHistory = paymentHistoryList.get(paymentHistoryList.size() - 1);
        assertThat(testPaymentHistory.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testPaymentHistory.getServiceName()).isEqualTo(UPDATED_SERVICE_NAME);
        assertThat(testPaymentHistory.getGroupName()).isEqualTo(UPDATED_GROUP_NAME);
        assertThat(testPaymentHistory.getSum()).isEqualTo(UPDATED_SUM);
        assertThat(testPaymentHistory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingPaymentHistory() throws Exception {
        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();
        paymentHistory.setId(count.incrementAndGet());

        // Create the PaymentHistory
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentHistory() throws Exception {
        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();
        paymentHistory.setId(count.incrementAndGet());

        // Create the PaymentHistory
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentHistory() throws Exception {
        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();
        paymentHistory.setId(count.incrementAndGet());

        // Create the PaymentHistory
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentHistoryWithPatch() throws Exception {
        // Initialize the database
        paymentHistoryRepository.saveAndFlush(paymentHistory);

        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();

        // Update the paymentHistory using partial update
        PaymentHistory partialUpdatedPaymentHistory = new PaymentHistory();
        partialUpdatedPaymentHistory.setId(paymentHistory.getId());

        partialUpdatedPaymentHistory
            .serviceName(UPDATED_SERVICE_NAME)
            .groupName(UPDATED_GROUP_NAME)
            .sum(UPDATED_SUM)
            .createdAt(UPDATED_CREATED_AT);

        restPaymentHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentHistory))
            )
            .andExpect(status().isOk());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
        PaymentHistory testPaymentHistory = paymentHistoryList.get(paymentHistoryList.size() - 1);
        assertThat(testPaymentHistory.getOrganizationName()).isEqualTo(DEFAULT_ORGANIZATION_NAME);
        assertThat(testPaymentHistory.getServiceName()).isEqualTo(UPDATED_SERVICE_NAME);
        assertThat(testPaymentHistory.getGroupName()).isEqualTo(UPDATED_GROUP_NAME);
        assertThat(testPaymentHistory.getSum()).isEqualTo(UPDATED_SUM);
        assertThat(testPaymentHistory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdatePaymentHistoryWithPatch() throws Exception {
        // Initialize the database
        paymentHistoryRepository.saveAndFlush(paymentHistory);

        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();

        // Update the paymentHistory using partial update
        PaymentHistory partialUpdatedPaymentHistory = new PaymentHistory();
        partialUpdatedPaymentHistory.setId(paymentHistory.getId());

        partialUpdatedPaymentHistory
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .serviceName(UPDATED_SERVICE_NAME)
            .groupName(UPDATED_GROUP_NAME)
            .sum(UPDATED_SUM)
            .createdAt(UPDATED_CREATED_AT);

        restPaymentHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentHistory))
            )
            .andExpect(status().isOk());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
        PaymentHistory testPaymentHistory = paymentHistoryList.get(paymentHistoryList.size() - 1);
        assertThat(testPaymentHistory.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testPaymentHistory.getServiceName()).isEqualTo(UPDATED_SERVICE_NAME);
        assertThat(testPaymentHistory.getGroupName()).isEqualTo(UPDATED_GROUP_NAME);
        assertThat(testPaymentHistory.getSum()).isEqualTo(UPDATED_SUM);
        assertThat(testPaymentHistory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingPaymentHistory() throws Exception {
        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();
        paymentHistory.setId(count.incrementAndGet());

        // Create the PaymentHistory
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentHistory() throws Exception {
        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();
        paymentHistory.setId(count.incrementAndGet());

        // Create the PaymentHistory
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentHistory() throws Exception {
        int databaseSizeBeforeUpdate = paymentHistoryRepository.findAll().size();
        paymentHistory.setId(count.incrementAndGet());

        // Create the PaymentHistory
        PaymentHistoryDTO paymentHistoryDTO = paymentHistoryMapper.toDto(paymentHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentHistory in the database
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentHistory() throws Exception {
        // Initialize the database
        paymentHistoryRepository.saveAndFlush(paymentHistory);

        int databaseSizeBeforeDelete = paymentHistoryRepository.findAll().size();

        // Delete the paymentHistory
        restPaymentHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
        assertThat(paymentHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
