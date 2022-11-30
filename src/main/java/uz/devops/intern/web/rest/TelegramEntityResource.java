package uz.devops.intern.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devops.intern.repository.TelegramEntityRepository;
import uz.devops.intern.service.TelegramEntityService;
import uz.devops.intern.service.dto.TelegramEntityDTO;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.TelegramEntity}.
 */
@RestController
@RequestMapping("/api")
public class TelegramEntityResource {

    private final Logger log = LoggerFactory.getLogger(TelegramEntityResource.class);

    private static final String ENTITY_NAME = "telegramEntity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TelegramEntityService telegramEntityService;

    private final TelegramEntityRepository telegramEntityRepository;

    public TelegramEntityResource(TelegramEntityService telegramEntityService, TelegramEntityRepository telegramEntityRepository) {
        this.telegramEntityService = telegramEntityService;
        this.telegramEntityRepository = telegramEntityRepository;
    }

    /**
     * {@code POST  /telegram-entities} : Create a new telegramEntity.
     *
     * @param telegramEntityDTO the telegramEntityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new telegramEntityDTO, or with status {@code 400 (Bad Request)} if the telegramEntity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/telegram-entities")
    public ResponseEntity<TelegramEntityDTO> createTelegramEntity(@RequestBody TelegramEntityDTO telegramEntityDTO)
        throws URISyntaxException {
        log.debug("REST request to save TelegramEntity : {}", telegramEntityDTO);
        if (telegramEntityDTO.getId() != null) {
            throw new BadRequestAlertException("A new telegramEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TelegramEntityDTO result = telegramEntityService.save(telegramEntityDTO);
        return ResponseEntity
            .created(new URI("/api/telegram-entities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /telegram-entities/:id} : Updates an existing telegramEntity.
     *
     * @param id the id of the telegramEntityDTO to save.
     * @param telegramEntityDTO the telegramEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telegramEntityDTO,
     * or with status {@code 400 (Bad Request)} if the telegramEntityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the telegramEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/telegram-entities/{id}")
    public ResponseEntity<TelegramEntityDTO> updateTelegramEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TelegramEntityDTO telegramEntityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TelegramEntity : {}, {}", id, telegramEntityDTO);
        if (telegramEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telegramEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!telegramEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TelegramEntityDTO result = telegramEntityService.update(telegramEntityDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, telegramEntityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /telegram-entities/:id} : Partial updates given fields of an existing telegramEntity, field will ignore if it is null
     *
     * @param id the id of the telegramEntityDTO to save.
     * @param telegramEntityDTO the telegramEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telegramEntityDTO,
     * or with status {@code 400 (Bad Request)} if the telegramEntityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the telegramEntityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the telegramEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/telegram-entities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TelegramEntityDTO> partialUpdateTelegramEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TelegramEntityDTO telegramEntityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TelegramEntity partially : {}, {}", id, telegramEntityDTO);
        if (telegramEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telegramEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!telegramEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TelegramEntityDTO> result = telegramEntityService.partialUpdate(telegramEntityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, telegramEntityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /telegram-entities} : get all the telegramEntities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of telegramEntities in body.
     */
    @GetMapping("/telegram-entities")
    public List<TelegramEntityDTO> getAllTelegramEntities() {
        log.debug("REST request to get all TelegramEntities");
        return telegramEntityService.findAll();
    }

    /**
     * {@code GET  /telegram-entities/:id} : get the "id" telegramEntity.
     *
     * @param id the id of the telegramEntityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the telegramEntityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/telegram-entities/{id}")
    public ResponseEntity<TelegramEntityDTO> getTelegramEntity(@PathVariable Long id) {
        log.debug("REST request to get TelegramEntity : {}", id);
        Optional<TelegramEntityDTO> telegramEntityDTO = telegramEntityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(telegramEntityDTO);
    }

    /**
     * {@code DELETE  /telegram-entities/:id} : delete the "id" telegramEntity.
     *
     * @param id the id of the telegramEntityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/telegram-entities/{id}")
    public ResponseEntity<Void> deleteTelegramEntity(@PathVariable Long id) {
        log.debug("REST request to delete TelegramEntity : {}", id);
        telegramEntityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
