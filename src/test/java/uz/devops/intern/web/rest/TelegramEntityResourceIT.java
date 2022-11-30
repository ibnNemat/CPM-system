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
import uz.devops.intern.domain.TelegramEntity;
import uz.devops.intern.repository.TelegramEntityRepository;
import uz.devops.intern.service.dto.TelegramEntityDTO;
import uz.devops.intern.service.mapper.TelegramEntityMapper;

/**
 * Integration tests for the {@link TelegramEntityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TelegramEntityResourceIT {

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

    private static final Boolean DEFAULT_CAN_JOIN_GROUPS = false;
    private static final Boolean UPDATED_CAN_JOIN_GROUPS = true;

    private static final String DEFAULT_LANGUAGE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/telegram-entities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TelegramEntityRepository telegramEntityRepository;

    @Autowired
    private TelegramEntityMapper telegramEntityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTelegramEntityMockMvc;

    private TelegramEntity telegramEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelegramEntity createEntity(EntityManager em) {
        TelegramEntity telegramEntity = new TelegramEntity()
            .isBot(DEFAULT_IS_BOT)
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .username(DEFAULT_USERNAME)
            .telegramId(DEFAULT_TELEGRAM_ID)
            .canJoinGroups(DEFAULT_CAN_JOIN_GROUPS)
            .languageCode(DEFAULT_LANGUAGE_CODE)
            .isActive(DEFAULT_IS_ACTIVE);
        return telegramEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelegramEntity createUpdatedEntity(EntityManager em) {
        TelegramEntity telegramEntity = new TelegramEntity()
            .isBot(UPDATED_IS_BOT)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .username(UPDATED_USERNAME)
            .telegramId(UPDATED_TELEGRAM_ID)
            .canJoinGroups(UPDATED_CAN_JOIN_GROUPS)
            .languageCode(UPDATED_LANGUAGE_CODE)
            .isActive(UPDATED_IS_ACTIVE);
        return telegramEntity;
    }

    @BeforeEach
    public void initTest() {
        telegramEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createTelegramEntity() throws Exception {
        int databaseSizeBeforeCreate = telegramEntityRepository.findAll().size();
        // Create the TelegramEntity
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);
        restTelegramEntityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeCreate + 1);
        TelegramEntity testTelegramEntity = telegramEntityList.get(telegramEntityList.size() - 1);
        assertThat(testTelegramEntity.getIsBot()).isEqualTo(DEFAULT_IS_BOT);
        assertThat(testTelegramEntity.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testTelegramEntity.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testTelegramEntity.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testTelegramEntity.getTelegramId()).isEqualTo(DEFAULT_TELEGRAM_ID);
        assertThat(testTelegramEntity.getCanJoinGroups()).isEqualTo(DEFAULT_CAN_JOIN_GROUPS);
        assertThat(testTelegramEntity.getLanguageCode()).isEqualTo(DEFAULT_LANGUAGE_CODE);
        assertThat(testTelegramEntity.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    void createTelegramEntityWithExistingId() throws Exception {
        // Create the TelegramEntity with an existing ID
        telegramEntity.setId(1L);
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);

        int databaseSizeBeforeCreate = telegramEntityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTelegramEntityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTelegramEntities() throws Exception {
        // Initialize the database
        telegramEntityRepository.saveAndFlush(telegramEntity);

        // Get all the telegramEntityList
        restTelegramEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(telegramEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].isBot").value(hasItem(DEFAULT_IS_BOT.booleanValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].telegramId").value(hasItem(DEFAULT_TELEGRAM_ID.intValue())))
            .andExpect(jsonPath("$.[*].canJoinGroups").value(hasItem(DEFAULT_CAN_JOIN_GROUPS.booleanValue())))
            .andExpect(jsonPath("$.[*].languageCode").value(hasItem(DEFAULT_LANGUAGE_CODE)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getTelegramEntity() throws Exception {
        // Initialize the database
        telegramEntityRepository.saveAndFlush(telegramEntity);

        // Get the telegramEntity
        restTelegramEntityMockMvc
            .perform(get(ENTITY_API_URL_ID, telegramEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(telegramEntity.getId().intValue()))
            .andExpect(jsonPath("$.isBot").value(DEFAULT_IS_BOT.booleanValue()))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.telegramId").value(DEFAULT_TELEGRAM_ID.intValue()))
            .andExpect(jsonPath("$.canJoinGroups").value(DEFAULT_CAN_JOIN_GROUPS.booleanValue()))
            .andExpect(jsonPath("$.languageCode").value(DEFAULT_LANGUAGE_CODE))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingTelegramEntity() throws Exception {
        // Get the telegramEntity
        restTelegramEntityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTelegramEntity() throws Exception {
        // Initialize the database
        telegramEntityRepository.saveAndFlush(telegramEntity);

        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();

        // Update the telegramEntity
        TelegramEntity updatedTelegramEntity = telegramEntityRepository.findById(telegramEntity.getId()).get();
        // Disconnect from session so that the updates on updatedTelegramEntity are not directly saved in db
        em.detach(updatedTelegramEntity);
        updatedTelegramEntity
            .isBot(UPDATED_IS_BOT)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .username(UPDATED_USERNAME)
            .telegramId(UPDATED_TELEGRAM_ID)
            .canJoinGroups(UPDATED_CAN_JOIN_GROUPS)
            .languageCode(UPDATED_LANGUAGE_CODE)
            .isActive(UPDATED_IS_ACTIVE);
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(updatedTelegramEntity);

        restTelegramEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, telegramEntityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isOk());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
        TelegramEntity testTelegramEntity = telegramEntityList.get(telegramEntityList.size() - 1);
        assertThat(testTelegramEntity.getIsBot()).isEqualTo(UPDATED_IS_BOT);
        assertThat(testTelegramEntity.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testTelegramEntity.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testTelegramEntity.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testTelegramEntity.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testTelegramEntity.getCanJoinGroups()).isEqualTo(UPDATED_CAN_JOIN_GROUPS);
        assertThat(testTelegramEntity.getLanguageCode()).isEqualTo(UPDATED_LANGUAGE_CODE);
        assertThat(testTelegramEntity.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingTelegramEntity() throws Exception {
        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();
        telegramEntity.setId(count.incrementAndGet());

        // Create the TelegramEntity
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTelegramEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, telegramEntityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTelegramEntity() throws Exception {
        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();
        telegramEntity.setId(count.incrementAndGet());

        // Create the TelegramEntity
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTelegramEntity() throws Exception {
        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();
        telegramEntity.setId(count.incrementAndGet());

        // Create the TelegramEntity
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramEntityMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTelegramEntityWithPatch() throws Exception {
        // Initialize the database
        telegramEntityRepository.saveAndFlush(telegramEntity);

        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();

        // Update the telegramEntity using partial update
        TelegramEntity partialUpdatedTelegramEntity = new TelegramEntity();
        partialUpdatedTelegramEntity.setId(telegramEntity.getId());

        partialUpdatedTelegramEntity.isActive(UPDATED_IS_ACTIVE);

        restTelegramEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTelegramEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTelegramEntity))
            )
            .andExpect(status().isOk());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
        TelegramEntity testTelegramEntity = telegramEntityList.get(telegramEntityList.size() - 1);
        assertThat(testTelegramEntity.getIsBot()).isEqualTo(DEFAULT_IS_BOT);
        assertThat(testTelegramEntity.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testTelegramEntity.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testTelegramEntity.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testTelegramEntity.getTelegramId()).isEqualTo(DEFAULT_TELEGRAM_ID);
        assertThat(testTelegramEntity.getCanJoinGroups()).isEqualTo(DEFAULT_CAN_JOIN_GROUPS);
        assertThat(testTelegramEntity.getLanguageCode()).isEqualTo(DEFAULT_LANGUAGE_CODE);
        assertThat(testTelegramEntity.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateTelegramEntityWithPatch() throws Exception {
        // Initialize the database
        telegramEntityRepository.saveAndFlush(telegramEntity);

        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();

        // Update the telegramEntity using partial update
        TelegramEntity partialUpdatedTelegramEntity = new TelegramEntity();
        partialUpdatedTelegramEntity.setId(telegramEntity.getId());

        partialUpdatedTelegramEntity
            .isBot(UPDATED_IS_BOT)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .username(UPDATED_USERNAME)
            .telegramId(UPDATED_TELEGRAM_ID)
            .canJoinGroups(UPDATED_CAN_JOIN_GROUPS)
            .languageCode(UPDATED_LANGUAGE_CODE)
            .isActive(UPDATED_IS_ACTIVE);

        restTelegramEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTelegramEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTelegramEntity))
            )
            .andExpect(status().isOk());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
        TelegramEntity testTelegramEntity = telegramEntityList.get(telegramEntityList.size() - 1);
        assertThat(testTelegramEntity.getIsBot()).isEqualTo(UPDATED_IS_BOT);
        assertThat(testTelegramEntity.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testTelegramEntity.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testTelegramEntity.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testTelegramEntity.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testTelegramEntity.getCanJoinGroups()).isEqualTo(UPDATED_CAN_JOIN_GROUPS);
        assertThat(testTelegramEntity.getLanguageCode()).isEqualTo(UPDATED_LANGUAGE_CODE);
        assertThat(testTelegramEntity.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingTelegramEntity() throws Exception {
        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();
        telegramEntity.setId(count.incrementAndGet());

        // Create the TelegramEntity
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTelegramEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, telegramEntityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTelegramEntity() throws Exception {
        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();
        telegramEntity.setId(count.incrementAndGet());

        // Create the TelegramEntity
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTelegramEntity() throws Exception {
        int databaseSizeBeforeUpdate = telegramEntityRepository.findAll().size();
        telegramEntity.setId(count.incrementAndGet());

        // Create the TelegramEntity
        TelegramEntityDTO telegramEntityDTO = telegramEntityMapper.toDto(telegramEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramEntityMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(telegramEntityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TelegramEntity in the database
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTelegramEntity() throws Exception {
        // Initialize the database
        telegramEntityRepository.saveAndFlush(telegramEntity);

        int databaseSizeBeforeDelete = telegramEntityRepository.findAll().size();

        // Delete the telegramEntity
        restTelegramEntityMockMvc
            .perform(delete(ENTITY_API_URL_ID, telegramEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TelegramEntity> telegramEntityList = telegramEntityRepository.findAll();
        assertThat(telegramEntityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
