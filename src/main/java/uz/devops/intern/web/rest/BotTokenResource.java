package uz.devops.intern.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.BotToken}.
 */
@RestController
@RequestMapping("/api")
public class BotTokenResource {

    private final Logger log = LoggerFactory.getLogger(BotTokenResource.class);

    private static final String ENTITY_NAME = "botToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BotTokenService botTokenService;

    private final BotTokenRepository botTokenRepository;

    public BotTokenResource(BotTokenService botTokenService, BotTokenRepository botTokenRepository) {
        this.botTokenService = botTokenService;
        this.botTokenRepository = botTokenRepository;
    }

    /**
     * {@code POST  /bot-tokens} : Create a new botToken.
     *
     * @param botTokenDTO the botTokenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new botTokenDTO, or with status {@code 400 (Bad Request)} if the botToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bot-tokens")
    public ResponseEntity<BotTokenDTO> createBotToken(@Valid @RequestBody BotTokenDTO botTokenDTO) throws URISyntaxException {
        log.debug("REST request to save BotToken : {}", botTokenDTO);
        if (botTokenDTO.getId() != null) {
            throw new BadRequestAlertException("A new botToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BotTokenDTO result = botTokenService.save(botTokenDTO);
        return ResponseEntity
            .created(new URI("/api/bot-tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bot-tokens/:id} : Updates an existing botToken.
     *
     * @param id the id of the botTokenDTO to save.
     * @param botTokenDTO the botTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated botTokenDTO,
     * or with status {@code 400 (Bad Request)} if the botTokenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the botTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bot-tokens/{id}")
    public ResponseEntity<BotTokenDTO> updateBotToken(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BotTokenDTO botTokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BotToken : {}, {}", id, botTokenDTO);
        if (botTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, botTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!botTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BotTokenDTO result = botTokenService.update(botTokenDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, botTokenDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bot-tokens/:id} : Partial updates given fields of an existing botToken, field will ignore if it is null
     *
     * @param id the id of the botTokenDTO to save.
     * @param botTokenDTO the botTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated botTokenDTO,
     * or with status {@code 400 (Bad Request)} if the botTokenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the botTokenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the botTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bot-tokens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BotTokenDTO> partialUpdateBotToken(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BotTokenDTO botTokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BotToken partially : {}, {}", id, botTokenDTO);
        if (botTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, botTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!botTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BotTokenDTO> result = botTokenService.partialUpdate(botTokenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, botTokenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bot-tokens} : get all the botTokens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of botTokens in body.
     */
    @GetMapping("/bot-tokens")
    public List<BotTokenDTO> getAllBotTokens() {
        log.debug("REST request to get all BotTokens");
        return botTokenService.findAll();
    }

    /**
     * {@code GET  /bot-tokens/:id} : get the "id" botToken.
     *
     * @param id the id of the botTokenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the botTokenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bot-tokens/{id}")
    public ResponseEntity<BotTokenDTO> getBotToken(@PathVariable Long id) {
        log.debug("REST request to get BotToken : {}", id);
        Optional<BotTokenDTO> botTokenDTO = botTokenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(botTokenDTO);
    }

    /**
     * {@code DELETE  /bot-tokens/:id} : delete the "id" botToken.
     *
     * @param id the id of the botTokenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bot-tokens/{id}")
    public ResponseEntity<Void> deleteBotToken(@PathVariable Long id) {
        log.debug("REST request to delete BotToken : {}", id);
        botTokenService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
