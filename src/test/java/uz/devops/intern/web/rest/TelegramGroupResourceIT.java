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
import uz.devops.intern.domain.TelegramGroup;
import uz.devops.intern.repository.TelegramGroupRepository;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.service.mapper.TelegramGroupMapper;

/**
 * Integration tests for the {@link TelegramGroupResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TelegramGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_CHAT_ID = 1L;
    private static final Long UPDATED_CHAT_ID = 2L;

    private static final String ENTITY_API_URL = "/api/telegram-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TelegramGroupRepository telegramGroupRepository;

    @Autowired
    private TelegramGroupMapper telegramGroupMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTelegramGroupMockMvc;

    private TelegramGroup telegramGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelegramGroup createEntity(EntityManager em) {
        TelegramGroup telegramGroup = new TelegramGroup().name(DEFAULT_NAME).chatId(DEFAULT_CHAT_ID);
        return telegramGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelegramGroup createUpdatedEntity(EntityManager em) {
        TelegramGroup telegramGroup = new TelegramGroup().name(UPDATED_NAME).chatId(UPDATED_CHAT_ID);
        return telegramGroup;
    }

    @BeforeEach
    public void initTest() {
        telegramGroup = createEntity(em);
    }

    @Test
    @Transactional
    void createTelegramGroup() throws Exception {
        int databaseSizeBeforeCreate = telegramGroupRepository.findAll().size();
        // Create the TelegramGroup
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);
        restTelegramGroupMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeCreate + 1);
        TelegramGroup testTelegramGroup = telegramGroupList.get(telegramGroupList.size() - 1);
        assertThat(testTelegramGroup.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTelegramGroup.getChatId()).isEqualTo(DEFAULT_CHAT_ID);
    }

    @Test
    @Transactional
    void createTelegramGroupWithExistingId() throws Exception {
        // Create the TelegramGroup with an existing ID
        telegramGroup.setId(1L);
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);

        int databaseSizeBeforeCreate = telegramGroupRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTelegramGroupMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTelegramGroups() throws Exception {
        // Initialize the database
        telegramGroupRepository.saveAndFlush(telegramGroup);

        // Get all the telegramGroupList
        restTelegramGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(telegramGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].chatId").value(hasItem(DEFAULT_CHAT_ID.intValue())));
    }

    @Test
    @Transactional
    void getTelegramGroup() throws Exception {
        // Initialize the database
        telegramGroupRepository.saveAndFlush(telegramGroup);

        // Get the telegramGroup
        restTelegramGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, telegramGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(telegramGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.chatId").value(DEFAULT_CHAT_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingTelegramGroup() throws Exception {
        // Get the telegramGroup
        restTelegramGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTelegramGroup() throws Exception {
        // Initialize the database
        telegramGroupRepository.saveAndFlush(telegramGroup);

        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();

        // Update the telegramGroup
        TelegramGroup updatedTelegramGroup = telegramGroupRepository.findById(telegramGroup.getId()).get();
        // Disconnect from session so that the updates on updatedTelegramGroup are not directly saved in db
        em.detach(updatedTelegramGroup);
        updatedTelegramGroup.name(UPDATED_NAME).chatId(UPDATED_CHAT_ID);
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(updatedTelegramGroup);

        restTelegramGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, telegramGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isOk());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
        TelegramGroup testTelegramGroup = telegramGroupList.get(telegramGroupList.size() - 1);
        assertThat(testTelegramGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTelegramGroup.getChatId()).isEqualTo(UPDATED_CHAT_ID);
    }

    @Test
    @Transactional
    void putNonExistingTelegramGroup() throws Exception {
        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();
        telegramGroup.setId(count.incrementAndGet());

        // Create the TelegramGroup
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTelegramGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, telegramGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTelegramGroup() throws Exception {
        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();
        telegramGroup.setId(count.incrementAndGet());

        // Create the TelegramGroup
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTelegramGroup() throws Exception {
        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();
        telegramGroup.setId(count.incrementAndGet());

        // Create the TelegramGroup
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramGroupMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTelegramGroupWithPatch() throws Exception {
        // Initialize the database
        telegramGroupRepository.saveAndFlush(telegramGroup);

        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();

        // Update the telegramGroup using partial update
        TelegramGroup partialUpdatedTelegramGroup = new TelegramGroup();
        partialUpdatedTelegramGroup.setId(telegramGroup.getId());

        restTelegramGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTelegramGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTelegramGroup))
            )
            .andExpect(status().isOk());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
        TelegramGroup testTelegramGroup = telegramGroupList.get(telegramGroupList.size() - 1);
        assertThat(testTelegramGroup.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTelegramGroup.getChatId()).isEqualTo(DEFAULT_CHAT_ID);
    }

    @Test
    @Transactional
    void fullUpdateTelegramGroupWithPatch() throws Exception {
        // Initialize the database
        telegramGroupRepository.saveAndFlush(telegramGroup);

        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();

        // Update the telegramGroup using partial update
        TelegramGroup partialUpdatedTelegramGroup = new TelegramGroup();
        partialUpdatedTelegramGroup.setId(telegramGroup.getId());

        partialUpdatedTelegramGroup.name(UPDATED_NAME).chatId(UPDATED_CHAT_ID);

        restTelegramGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTelegramGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTelegramGroup))
            )
            .andExpect(status().isOk());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
        TelegramGroup testTelegramGroup = telegramGroupList.get(telegramGroupList.size() - 1);
        assertThat(testTelegramGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTelegramGroup.getChatId()).isEqualTo(UPDATED_CHAT_ID);
    }

    @Test
    @Transactional
    void patchNonExistingTelegramGroup() throws Exception {
        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();
        telegramGroup.setId(count.incrementAndGet());

        // Create the TelegramGroup
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTelegramGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, telegramGroupDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTelegramGroup() throws Exception {
        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();
        telegramGroup.setId(count.incrementAndGet());

        // Create the TelegramGroup
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTelegramGroup() throws Exception {
        int databaseSizeBeforeUpdate = telegramGroupRepository.findAll().size();
        telegramGroup.setId(count.incrementAndGet());

        // Create the TelegramGroup
        TelegramGroupDTO telegramGroupDTO = telegramGroupMapper.toDto(telegramGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTelegramGroupMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(telegramGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TelegramGroup in the database
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTelegramGroup() throws Exception {
        // Initialize the database
        telegramGroupRepository.saveAndFlush(telegramGroup);

        int databaseSizeBeforeDelete = telegramGroupRepository.findAll().size();

        // Delete the telegramGroup
        restTelegramGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, telegramGroup.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TelegramGroup> telegramGroupList = telegramGroupRepository.findAll();
        assertThat(telegramGroupList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
