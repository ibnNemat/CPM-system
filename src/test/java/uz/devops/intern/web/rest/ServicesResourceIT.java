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
import uz.devops.intern.domain.Services;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.repository.ServicesRepository;
import uz.devops.intern.service.dto.ServicesDTO;
import uz.devops.intern.service.mapper.ServicesMapper;

/**
 * Integration tests for the {@link ServicesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServicesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 10000D;
    private static final Double UPDATED_PRICE = 10001D;

    private static final LocalDate DEFAULT_STARTED_PERIOD = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_STARTED_PERIOD = LocalDate.now(ZoneId.systemDefault());

    private static final PeriodType DEFAULT_PERIOD_TYPE = PeriodType.ONETIME;
    private static final PeriodType UPDATED_PERIOD_TYPE = PeriodType.DAY;

    private static final Integer DEFAULT_COUNT_PERIOD = 1;
    private static final Integer UPDATED_COUNT_PERIOD = 2;

    private static final String ENTITY_API_URL = "/api/services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private ServicesMapper servicesMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServicesMockMvc;

    private Services services;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Services createEntity(EntityManager em) {
        Services services = new Services()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .startedPeriod(DEFAULT_STARTED_PERIOD)
            .periodType(DEFAULT_PERIOD_TYPE)
            .countPeriod(DEFAULT_COUNT_PERIOD);
        return services;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Services createUpdatedEntity(EntityManager em) {
        Services services = new Services()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .startedPeriod(UPDATED_STARTED_PERIOD)
            .periodType(UPDATED_PERIOD_TYPE)
            .countPeriod(UPDATED_COUNT_PERIOD);
        return services;
    }

    @BeforeEach
    public void initTest() {
        services = createEntity(em);
    }

    @Test
    @Transactional
    void createServices() throws Exception {
        int databaseSizeBeforeCreate = servicesRepository.findAll().size();
        // Create the Services
        ServicesDTO servicesDTO = servicesMapper.toDto(services);
        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isCreated());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeCreate + 1);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServices.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testServices.getStartedPeriod()).isEqualTo(DEFAULT_STARTED_PERIOD);
        assertThat(testServices.getPeriodType()).isEqualTo(DEFAULT_PERIOD_TYPE);
        assertThat(testServices.getCountPeriod()).isEqualTo(DEFAULT_COUNT_PERIOD);
    }

    @Test
    @Transactional
    void createServicesWithExistingId() throws Exception {
        // Create the Services with an existing ID
        services.setId(1L);
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        int databaseSizeBeforeCreate = servicesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setName(null);

        // Create the Services, which fails.
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setPrice(null);

        // Create the Services, which fails.
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartedPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setStartedPeriod(null);

        // Create the Services, which fails.
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPeriodTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setPeriodType(null);

        // Create the Services, which fails.
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        // set the field null
        services.setCountPeriod(null);

        // Create the Services, which fails.
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        restServicesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        // Get all the servicesList
        restServicesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(services.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].startedPeriod").value(hasItem(DEFAULT_STARTED_PERIOD.toString())))
            .andExpect(jsonPath("$.[*].periodType").value(hasItem(DEFAULT_PERIOD_TYPE.toString())))
            .andExpect(jsonPath("$.[*].countPeriod").value(hasItem(DEFAULT_COUNT_PERIOD)));
    }

    @Test
    @Transactional
    void getServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        // Get the services
        restServicesMockMvc
            .perform(get(ENTITY_API_URL_ID, services.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(services.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.startedPeriod").value(DEFAULT_STARTED_PERIOD.toString()))
            .andExpect(jsonPath("$.periodType").value(DEFAULT_PERIOD_TYPE.toString()))
            .andExpect(jsonPath("$.countPeriod").value(DEFAULT_COUNT_PERIOD));
    }

    @Test
    @Transactional
    void getNonExistingServices() throws Exception {
        // Get the services
        restServicesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        // Update the services
        Services updatedServices = servicesRepository.findById(services.getId()).get();
        // Disconnect from session so that the updates on updatedServices are not directly saved in db
        em.detach(updatedServices);
        updatedServices
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .startedPeriod(UPDATED_STARTED_PERIOD)
            .periodType(UPDATED_PERIOD_TYPE)
            .countPeriod(UPDATED_COUNT_PERIOD);
        ServicesDTO servicesDTO = servicesMapper.toDto(updatedServices);

        restServicesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, servicesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(servicesDTO))
            )
            .andExpect(status().isOk());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServices.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testServices.getStartedPeriod()).isEqualTo(UPDATED_STARTED_PERIOD);
        assertThat(testServices.getPeriodType()).isEqualTo(UPDATED_PERIOD_TYPE);
        assertThat(testServices.getCountPeriod()).isEqualTo(UPDATED_COUNT_PERIOD);
    }

    @Test
    @Transactional
    void putNonExistingServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // Create the Services
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, servicesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(servicesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // Create the Services
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(servicesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // Create the Services
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(servicesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServicesWithPatch() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        // Update the services using partial update
        Services partialUpdatedServices = new Services();
        partialUpdatedServices.setId(services.getId());

        partialUpdatedServices.periodType(UPDATED_PERIOD_TYPE).countPeriod(UPDATED_COUNT_PERIOD);

        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServices.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServices))
            )
            .andExpect(status().isOk());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServices.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testServices.getStartedPeriod()).isEqualTo(DEFAULT_STARTED_PERIOD);
        assertThat(testServices.getPeriodType()).isEqualTo(UPDATED_PERIOD_TYPE);
        assertThat(testServices.getCountPeriod()).isEqualTo(UPDATED_COUNT_PERIOD);
    }

    @Test
    @Transactional
    void fullUpdateServicesWithPatch() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        // Update the services using partial update
        Services partialUpdatedServices = new Services();
        partialUpdatedServices.setId(services.getId());

        partialUpdatedServices
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .startedPeriod(UPDATED_STARTED_PERIOD)
            .periodType(UPDATED_PERIOD_TYPE)
            .countPeriod(UPDATED_COUNT_PERIOD);

        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServices.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServices))
            )
            .andExpect(status().isOk());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServices.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testServices.getStartedPeriod()).isEqualTo(UPDATED_STARTED_PERIOD);
        assertThat(testServices.getPeriodType()).isEqualTo(UPDATED_PERIOD_TYPE);
        assertThat(testServices.getCountPeriod()).isEqualTo(UPDATED_COUNT_PERIOD);
    }

    @Test
    @Transactional
    void patchNonExistingServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // Create the Services
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, servicesDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(servicesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // Create the Services
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(servicesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServices() throws Exception {
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();
        services.setId(count.incrementAndGet());

        // Create the Services
        ServicesDTO servicesDTO = servicesMapper.toDto(services);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServicesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(servicesDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Services in the database
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServices() throws Exception {
        // Initialize the database
        servicesRepository.saveAndFlush(services);

        int databaseSizeBeforeDelete = servicesRepository.findAll().size();

        // Delete the services
        restServicesMockMvc
            .perform(delete(ENTITY_API_URL_ID, services.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
