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
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
@RestController
@RequestMapping("/api")
public class CustomerTelegramResource {

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramResource.class);

    private static final String ENTITY_NAME = "customerTelegram";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerTelegramService customerTelegramService;

    private final CustomerTelegramRepository customerTelegramRepository;

    public CustomerTelegramResource(
        CustomerTelegramService customerTelegramService,
        CustomerTelegramRepository customerTelegramRepository
    ) {
        this.customerTelegramService = customerTelegramService;
        this.customerTelegramRepository = customerTelegramRepository;
    }

    /**
     * {@code POST  /customer-telegrams} : Create a new customerTelegram.
     *
     * @param customerTelegramDTO the customerTelegramDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerTelegramDTO, or with status {@code 400 (Bad Request)} if the customerTelegram has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customer-telegrams")
    public ResponseEntity<CustomerTelegramDTO> createCustomerTelegram(@RequestBody CustomerTelegramDTO customerTelegramDTO)
        throws URISyntaxException {
        log.debug("REST request to save CustomerTelegram : {}", customerTelegramDTO);
        if (customerTelegramDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerTelegram cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CustomerTelegramDTO result = customerTelegramService.save(customerTelegramDTO);
        return ResponseEntity
            .created(new URI("/api/customer-telegrams/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /customer-telegrams/:id} : Updates an existing customerTelegram.
     *
     * @param id the id of the customerTelegramDTO to save.
     * @param customerTelegramDTO the customerTelegramDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerTelegramDTO,
     * or with status {@code 400 (Bad Request)} if the customerTelegramDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerTelegramDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customer-telegrams/{id}")
    public ResponseEntity<CustomerTelegramDTO> updateCustomerTelegram(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CustomerTelegramDTO customerTelegramDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CustomerTelegram : {}, {}", id, customerTelegramDTO);
        if (customerTelegramDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerTelegramDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerTelegramRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomerTelegramDTO result = customerTelegramService.update(customerTelegramDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerTelegramDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /customer-telegrams/:id} : Partial updates given fields of an existing customerTelegram, field will ignore if it is null
     *
     * @param id the id of the customerTelegramDTO to save.
     * @param customerTelegramDTO the customerTelegramDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerTelegramDTO,
     * or with status {@code 400 (Bad Request)} if the customerTelegramDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerTelegramDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerTelegramDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/customer-telegrams/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CustomerTelegramDTO> partialUpdateCustomerTelegram(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CustomerTelegramDTO customerTelegramDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CustomerTelegram partially : {}, {}", id, customerTelegramDTO);
        if (customerTelegramDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerTelegramDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerTelegramRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomerTelegramDTO> result = customerTelegramService.partialUpdate(customerTelegramDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerTelegramDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /customer-telegrams} : get all the customerTelegrams.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerTelegrams in body.
     */
    @GetMapping("/customer-telegrams")
    public List<CustomerTelegramDTO> getAllCustomerTelegrams() {
        log.debug("REST request to get all CustomerTelegrams");
        return customerTelegramService.findAll();
    }

    /**
     * {@code GET  /customer-telegrams/:id} : get the "id" customerTelegram.
     *
     * @param id the id of the customerTelegramDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerTelegramDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customer-telegrams/{id}")
    public ResponseEntity<CustomerTelegramDTO> getCustomerTelegram(@PathVariable Long id) {
        log.debug("REST request to get CustomerTelegram : {}", id);
        Optional<CustomerTelegramDTO> customerTelegramDTO = customerTelegramService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerTelegramDTO);
    }

    /**
     * {@code DELETE  /customer-telegrams/:id} : delete the "id" customerTelegram.
     *
     * @param id the id of the customerTelegramDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/customer-telegrams/{id}")
    public ResponseEntity<Void> deleteCustomerTelegram(@PathVariable Long id) {
        log.debug("REST request to delete CustomerTelegram : {}", id);
        customerTelegramService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
