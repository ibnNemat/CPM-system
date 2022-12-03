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
import uz.devops.intern.repository.TelegramGroupRepository;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.TelegramGroup}.
 */
@RestController
@RequestMapping("/api")
public class TelegramGroupResource {

    private final Logger log = LoggerFactory.getLogger(TelegramGroupResource.class);

    private static final String ENTITY_NAME = "telegramGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TelegramGroupService telegramGroupService;

    private final TelegramGroupRepository telegramGroupRepository;

    public TelegramGroupResource(TelegramGroupService telegramGroupService, TelegramGroupRepository telegramGroupRepository) {
        this.telegramGroupService = telegramGroupService;
        this.telegramGroupRepository = telegramGroupRepository;
    }

    /**
     * {@code POST  /telegram-groups} : Create a new telegramGroup.
     *
     * @param telegramGroupDTO the telegramGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new telegramGroupDTO, or with status {@code 400 (Bad Request)} if the telegramGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/telegram-groups")
    public ResponseEntity<TelegramGroupDTO> createTelegramGroup(@RequestBody TelegramGroupDTO telegramGroupDTO) throws URISyntaxException {
        log.debug("REST request to save TelegramGroup : {}", telegramGroupDTO);
        if (telegramGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new telegramGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TelegramGroupDTO result = telegramGroupService.save(telegramGroupDTO);
        return ResponseEntity
            .created(new URI("/api/telegram-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /telegram-groups/:id} : Updates an existing telegramGroup.
     *
     * @param id the id of the telegramGroupDTO to save.
     * @param telegramGroupDTO the telegramGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telegramGroupDTO,
     * or with status {@code 400 (Bad Request)} if the telegramGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the telegramGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/telegram-groups/{id}")
    public ResponseEntity<TelegramGroupDTO> updateTelegramGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TelegramGroupDTO telegramGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TelegramGroup : {}, {}", id, telegramGroupDTO);
        if (telegramGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telegramGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!telegramGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TelegramGroupDTO result = telegramGroupService.update(telegramGroupDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, telegramGroupDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /telegram-groups/:id} : Partial updates given fields of an existing telegramGroup, field will ignore if it is null
     *
     * @param id the id of the telegramGroupDTO to save.
     * @param telegramGroupDTO the telegramGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated telegramGroupDTO,
     * or with status {@code 400 (Bad Request)} if the telegramGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the telegramGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the telegramGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/telegram-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TelegramGroupDTO> partialUpdateTelegramGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TelegramGroupDTO telegramGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TelegramGroup partially : {}, {}", id, telegramGroupDTO);
        if (telegramGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, telegramGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!telegramGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TelegramGroupDTO> result = telegramGroupService.partialUpdate(telegramGroupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, telegramGroupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /telegram-groups} : get all the telegramGroups.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of telegramGroups in body.
     */
    @GetMapping("/telegram-groups")
    public List<TelegramGroupDTO> getAllTelegramGroups() {
        log.debug("REST request to get all TelegramGroups");
        return telegramGroupService.findAll();
    }

    /**
     * {@code GET  /telegram-groups/:id} : get the "id" telegramGroup.
     *
     * @param id the id of the telegramGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the telegramGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/telegram-groups/{id}")
    public ResponseEntity<TelegramGroupDTO> getTelegramGroup(@PathVariable Long id) {
        log.debug("REST request to get TelegramGroup : {}", id);
        Optional<TelegramGroupDTO> telegramGroupDTO = telegramGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(telegramGroupDTO);
    }

    /**
     * {@code DELETE  /telegram-groups/:id} : delete the "id" telegramGroup.
     *
     * @param id the id of the telegramGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/telegram-groups/{id}")
    public ResponseEntity<Void> deleteTelegramGroup(@PathVariable Long id) {
        log.debug("REST request to delete TelegramGroup : {}", id);
        telegramGroupService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
