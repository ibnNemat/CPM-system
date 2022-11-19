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
import uz.devops.intern.repository.PaymentRepository;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.mapper.PaymentMapper;

/**
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentResourceIT {

    private static final Double DEFAULT_PAYED_MONEY = 1D;
    private static final Double UPDATED_PAYED_MONEY = 2D;

    private static final Double DEFAULT_PAYMENT_FOR_PERIOD = 1D;
    private static final Double UPDATED_PAYMENT_FOR_PERIOD = 2D;

    private static final Boolean DEFAULT_IS_PAYED = false;
    private static final Boolean UPDATED_IS_PAYED = true;

    private static final LocalDate DEFAULT_STARTED_PERIOD = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_STARTED_PERIOD = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_FINISHED_PERIOD = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FINISHED_PERIOD = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private uz.devops.intern.domain.Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static uz.devops.intern.domain.Payment createEntity(EntityManager em) {
        uz.devops.intern.domain.Payment payment = new uz.devops.intern.domain.Payment()
            .payedMoney(DEFAULT_PAYED_MONEY)
            .paymentForPeriod(DEFAULT_PAYMENT_FOR_PERIOD)
            .isPayed(DEFAULT_IS_PAYED)
            .startedPeriod(DEFAULT_STARTED_PERIOD)
            .finishedPeriod(DEFAULT_FINISHED_PERIOD);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static uz.devops.intern.domain.Payment createUpdatedEntity(EntityManager em) {
        uz.devops.intern.domain.Payment payment = new uz.devops.intern.domain.Payment()
            .payedMoney(UPDATED_PAYED_MONEY)
            .paymentForPeriod(UPDATED_PAYMENT_FOR_PERIOD)
            .isPayed(UPDATED_IS_PAYED)
            .startedPeriod(UPDATED_STARTED_PERIOD)
            .finishedPeriod(UPDATED_FINISHED_PERIOD);
        return payment;
    }

    @BeforeEach
    public void initTest() {
        payment = createEntity(em);
    }

    @Test
    @Transactional
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        // Create the PaymentDTO
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isCreated());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        uz.devops.intern.domain.Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPayedMoney()).isEqualTo(DEFAULT_PAYED_MONEY);
        assertThat(testPayment.getPaymentForPeriod()).isEqualTo(DEFAULT_PAYMENT_FOR_PERIOD);
        assertThat(testPayment.getIsPayed()).isEqualTo(DEFAULT_IS_PAYED);
        assertThat(testPayment.getStartedPeriod()).isEqualTo(DEFAULT_STARTED_PERIOD);
        assertThat(testPayment.getFinishedPeriod()).isEqualTo(DEFAULT_FINISHED_PERIOD);
    }

    @Test
    @Transactional
    void createPaymentWithExistingId() throws Exception {
        // Create the PaymentDTO with an existing ID
        payment.setId(1L);
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        int databaseSizeBeforeCreate = paymentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPayedMoneyIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setPayedMoney(null);

        // Create the PaymentDTO, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentForPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setPaymentForPeriod(null);

        // Create the PaymentDTO, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsPayedIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setIsPayed(null);

        // Create the PaymentDTO, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartedPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setStartedPeriod(null);

        // Create the PaymentDTO, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFinishedPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        // set the field null
        payment.setFinishedPeriod(null);

        // Create the PaymentDTO, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].payedMoney").value(hasItem(DEFAULT_PAYED_MONEY.doubleValue())))
            .andExpect(jsonPath("$.[*].paymentForPeriod").value(hasItem(DEFAULT_PAYMENT_FOR_PERIOD.doubleValue())))
            .andExpect(jsonPath("$.[*].isPayed").value(hasItem(DEFAULT_IS_PAYED.booleanValue())))
            .andExpect(jsonPath("$.[*].startedPeriod").value(hasItem(DEFAULT_STARTED_PERIOD.toString())))
            .andExpect(jsonPath("$.[*].finishedPeriod").value(hasItem(DEFAULT_FINISHED_PERIOD.toString())));
    }

    @Test
    @Transactional
    void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payment.getId().intValue()))
            .andExpect(jsonPath("$.payedMoney").value(DEFAULT_PAYED_MONEY.doubleValue()))
            .andExpect(jsonPath("$.paymentForPeriod").value(DEFAULT_PAYMENT_FOR_PERIOD.doubleValue()))
            .andExpect(jsonPath("$.isPayed").value(DEFAULT_IS_PAYED.booleanValue()))
            .andExpect(jsonPath("$.startedPeriod").value(DEFAULT_STARTED_PERIOD.toString()))
            .andExpect(jsonPath("$.finishedPeriod").value(DEFAULT_FINISHED_PERIOD.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment
        uz.devops.intern.domain.Payment updatedPayment = paymentRepository.findById(payment.getId()).get();
        // Disconnect from session so that the updates on updatedPayment are not directly saved in db
        em.detach(updatedPayment);
        updatedPayment
            .payedMoney(UPDATED_PAYED_MONEY)
            .paymentForPeriod(UPDATED_PAYMENT_FOR_PERIOD)
            .isPayed(UPDATED_IS_PAYED)
            .startedPeriod(UPDATED_STARTED_PERIOD)
            .finishedPeriod(UPDATED_FINISHED_PERIOD);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        uz.devops.intern.domain.Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPayedMoney()).isEqualTo(UPDATED_PAYED_MONEY);
        assertThat(testPayment.getPaymentForPeriod()).isEqualTo(UPDATED_PAYMENT_FOR_PERIOD);
        assertThat(testPayment.getIsPayed()).isEqualTo(UPDATED_IS_PAYED);
        assertThat(testPayment.getStartedPeriod()).isEqualTo(UPDATED_STARTED_PERIOD);
        assertThat(testPayment.getFinishedPeriod()).isEqualTo(UPDATED_FINISHED_PERIOD);
    }

    @Test
    @Transactional
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(count.incrementAndGet());

        // Create the PaymentDTO
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(count.incrementAndGet());

        // Create the PaymentDTO
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(count.incrementAndGet());

        // Create the PaymentDTO
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        uz.devops.intern.domain.Payment partialUpdatedPayment = new uz.devops.intern.domain.Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment.paymentForPeriod(UPDATED_PAYMENT_FOR_PERIOD).finishedPeriod(UPDATED_FINISHED_PERIOD);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        uz.devops.intern.domain.Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPayedMoney()).isEqualTo(DEFAULT_PAYED_MONEY);
        assertThat(testPayment.getPaymentForPeriod()).isEqualTo(UPDATED_PAYMENT_FOR_PERIOD);
        assertThat(testPayment.getIsPayed()).isEqualTo(DEFAULT_IS_PAYED);
        assertThat(testPayment.getStartedPeriod()).isEqualTo(DEFAULT_STARTED_PERIOD);
        assertThat(testPayment.getFinishedPeriod()).isEqualTo(UPDATED_FINISHED_PERIOD);
    }

    @Test
    @Transactional
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        uz.devops.intern.domain.Payment partialUpdatedPayment = new uz.devops.intern.domain.Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .payedMoney(UPDATED_PAYED_MONEY)
            .paymentForPeriod(UPDATED_PAYMENT_FOR_PERIOD)
            .isPayed(UPDATED_IS_PAYED)
            .startedPeriod(UPDATED_STARTED_PERIOD)
            .finishedPeriod(UPDATED_FINISHED_PERIOD);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        uz.devops.intern.domain.Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPayedMoney()).isEqualTo(UPDATED_PAYED_MONEY);
        assertThat(testPayment.getPaymentForPeriod()).isEqualTo(UPDATED_PAYMENT_FOR_PERIOD);
        assertThat(testPayment.getIsPayed()).isEqualTo(UPDATED_IS_PAYED);
        assertThat(testPayment.getStartedPeriod()).isEqualTo(UPDATED_STARTED_PERIOD);
        assertThat(testPayment.getFinishedPeriod()).isEqualTo(UPDATED_FINISHED_PERIOD);
    }

    @Test
    @Transactional
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(count.incrementAndGet());

        // Create the PaymentDTO
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(count.incrementAndGet());

        // Create the PaymentDTO
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        payment.setId(count.incrementAndGet());

        // Create the PaymentDTO
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentDTO in the database
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeDelete = paymentRepository.findAll().size();

        // Delete the payment
        restPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, payment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<uz.devops.intern.domain.Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
