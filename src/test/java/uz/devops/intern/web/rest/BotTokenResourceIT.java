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
import uz.devops.intern.domain.BotToken;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.service.mapper.BotTokenMapper;

/**
 * Integration tests for the {@link BotTokenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BotTokenResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final Long DEFAULT_TELEGRAM_ID = 1L;
    private static final Long UPDATED_TELEGRAM_ID = 2L;

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bot-tokens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BotTokenRepository botTokenRepository;

    @Autowired
    private BotTokenMapper botTokenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBotTokenMockMvc;

    private BotToken botToken;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BotToken createEntity(EntityManager em) {
        BotToken botToken = new BotToken().username(DEFAULT_USERNAME).telegramId(DEFAULT_TELEGRAM_ID).token(DEFAULT_TOKEN);
        return botToken;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BotToken createUpdatedEntity(EntityManager em) {
        BotToken botToken = new BotToken().username(UPDATED_USERNAME).telegramId(UPDATED_TELEGRAM_ID).token(UPDATED_TOKEN);
        return botToken;
    }

    @BeforeEach
    public void initTest() {
        botToken = createEntity(em);
    }

    @Test
    @Transactional
    void createBotToken() throws Exception {
        int databaseSizeBeforeCreate = botTokenRepository.findAll().size();
        // Create the BotToken
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);
        restBotTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(botTokenDTO)))
            .andExpect(status().isCreated());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeCreate + 1);
        BotToken testBotToken = botTokenList.get(botTokenList.size() - 1);
        assertThat(testBotToken.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testBotToken.getTelegramId()).isEqualTo(DEFAULT_TELEGRAM_ID);
        assertThat(testBotToken.getToken()).isEqualTo(DEFAULT_TOKEN);
    }

    @Test
    @Transactional
    void createBotTokenWithExistingId() throws Exception {
        // Create the BotToken with an existing ID
        botToken.setId(1L);
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        int databaseSizeBeforeCreate = botTokenRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBotTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(botTokenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = botTokenRepository.findAll().size();
        // set the field null
        botToken.setUsername(null);

        // Create the BotToken, which fails.
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        restBotTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(botTokenDTO)))
            .andExpect(status().isBadRequest());

        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTelegramIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = botTokenRepository.findAll().size();
        // set the field null
        botToken.setTelegramId(null);

        // Create the BotToken, which fails.
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        restBotTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(botTokenDTO)))
            .andExpect(status().isBadRequest());

        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = botTokenRepository.findAll().size();
        // set the field null
        botToken.setToken(null);

        // Create the BotToken, which fails.
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        restBotTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(botTokenDTO)))
            .andExpect(status().isBadRequest());

        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBotTokens() throws Exception {
        // Initialize the database
        botTokenRepository.saveAndFlush(botToken);

        // Get all the botTokenList
        restBotTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(botToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].telegramId").value(hasItem(DEFAULT_TELEGRAM_ID.intValue())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)));
    }

    @Test
    @Transactional
    void getBotToken() throws Exception {
        // Initialize the database
        botTokenRepository.saveAndFlush(botToken);

        // Get the botToken
        restBotTokenMockMvc
            .perform(get(ENTITY_API_URL_ID, botToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(botToken.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.telegramId").value(DEFAULT_TELEGRAM_ID.intValue()))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN));
    }

    @Test
    @Transactional
    void getNonExistingBotToken() throws Exception {
        // Get the botToken
        restBotTokenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBotToken() throws Exception {
        // Initialize the database
        botTokenRepository.saveAndFlush(botToken);

        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();

        // Update the botToken
        BotToken updatedBotToken = botTokenRepository.findById(botToken.getId()).get();
        // Disconnect from session so that the updates on updatedBotToken are not directly saved in db
        em.detach(updatedBotToken);
        updatedBotToken.username(UPDATED_USERNAME).telegramId(UPDATED_TELEGRAM_ID).token(UPDATED_TOKEN);
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(updatedBotToken);

        restBotTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, botTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(botTokenDTO))
            )
            .andExpect(status().isOk());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
        BotToken testBotToken = botTokenList.get(botTokenList.size() - 1);
        assertThat(testBotToken.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testBotToken.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testBotToken.getToken()).isEqualTo(UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void putNonExistingBotToken() throws Exception {
        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();
        botToken.setId(count.incrementAndGet());

        // Create the BotToken
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBotTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, botTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(botTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBotToken() throws Exception {
        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();
        botToken.setId(count.incrementAndGet());

        // Create the BotToken
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(botTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBotToken() throws Exception {
        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();
        botToken.setId(count.incrementAndGet());

        // Create the BotToken
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotTokenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(botTokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBotTokenWithPatch() throws Exception {
        // Initialize the database
        botTokenRepository.saveAndFlush(botToken);

        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();

        // Update the botToken using partial update
        BotToken partialUpdatedBotToken = new BotToken();
        partialUpdatedBotToken.setId(botToken.getId());

        partialUpdatedBotToken.username(UPDATED_USERNAME).telegramId(UPDATED_TELEGRAM_ID).token(UPDATED_TOKEN);

        restBotTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBotToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBotToken))
            )
            .andExpect(status().isOk());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
        BotToken testBotToken = botTokenList.get(botTokenList.size() - 1);
        assertThat(testBotToken.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testBotToken.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testBotToken.getToken()).isEqualTo(UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void fullUpdateBotTokenWithPatch() throws Exception {
        // Initialize the database
        botTokenRepository.saveAndFlush(botToken);

        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();

        // Update the botToken using partial update
        BotToken partialUpdatedBotToken = new BotToken();
        partialUpdatedBotToken.setId(botToken.getId());

        partialUpdatedBotToken.username(UPDATED_USERNAME).telegramId(UPDATED_TELEGRAM_ID).token(UPDATED_TOKEN);

        restBotTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBotToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBotToken))
            )
            .andExpect(status().isOk());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
        BotToken testBotToken = botTokenList.get(botTokenList.size() - 1);
        assertThat(testBotToken.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testBotToken.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testBotToken.getToken()).isEqualTo(UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void patchNonExistingBotToken() throws Exception {
        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();
        botToken.setId(count.incrementAndGet());

        // Create the BotToken
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBotTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, botTokenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(botTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBotToken() throws Exception {
        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();
        botToken.setId(count.incrementAndGet());

        // Create the BotToken
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(botTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBotToken() throws Exception {
        int databaseSizeBeforeUpdate = botTokenRepository.findAll().size();
        botToken.setId(count.incrementAndGet());

        // Create the BotToken
        BotTokenDTO botTokenDTO = botTokenMapper.toDto(botToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotTokenMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(botTokenDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BotToken in the database
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBotToken() throws Exception {
        // Initialize the database
        botTokenRepository.saveAndFlush(botToken);

        int databaseSizeBeforeDelete = botTokenRepository.findAll().size();

        // Delete the botToken
        restBotTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, botToken.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BotToken> botTokenList = botTokenRepository.findAll();
        assertThat(botTokenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
