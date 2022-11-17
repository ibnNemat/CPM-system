package uz.devops.intern.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devops.intern.repository.PaymentHistoryRepository;
import uz.devops.intern.service.PaymentHistoryService;
import uz.devops.intern.service.dto.PaymentHistoryDTO;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.PaymentHistory}.
 */
@RestController
@RequestMapping("/api")
public class PaymentHistoryResource {

    private final Logger log = LoggerFactory.getLogger(PaymentHistoryResource.class);

    private static final String ENTITY_NAME = "paymentHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentHistoryService paymentHistoryService;

    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentHistoryResource(PaymentHistoryService paymentHistoryService, PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentHistoryService = paymentHistoryService;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    @GetMapping("/getAllHistoryForEmail")
    public List<PaymentHistoryDTO> getAllHistory(){
        log.debug("Rest request to get all History for email");
        return paymentHistoryService.findAllForEmail();
    }

    /**
     * {@code POST  /payment-histories} : Create a new paymentHistory.
     *
     * @param paymentHistoryDTO the paymentHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentHistoryDTO, or with status {@code 400 (Bad Request)} if the paymentHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/payment-histories")
    public ResponseEntity<PaymentHistoryDTO> createPaymentHistory(@RequestBody PaymentHistoryDTO paymentHistoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save PaymentHistory : {}", paymentHistoryDTO);
        if (paymentHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PaymentHistoryDTO result = paymentHistoryService.save(paymentHistoryDTO);
        return ResponseEntity
            .created(new URI("/api/payment-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /payment-histories/:id} : Updates an existing paymentHistory.
     *
     * @param id the id of the paymentHistoryDTO to save.
     * @param paymentHistoryDTO the paymentHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the paymentHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/payment-histories/{id}")
    public ResponseEntity<PaymentHistoryDTO> updatePaymentHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PaymentHistoryDTO paymentHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PaymentHistory : {}, {}", id, paymentHistoryDTO);
        if (paymentHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PaymentHistoryDTO result = paymentHistoryService.update(paymentHistoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentHistoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /payment-histories/:id} : Partial updates given fields of an existing paymentHistory, field will ignore if it is null
     *
     * @param id the id of the paymentHistoryDTO to save.
     * @param paymentHistoryDTO the paymentHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the paymentHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/payment-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaymentHistoryDTO> partialUpdatePaymentHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PaymentHistoryDTO paymentHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PaymentHistory partially : {}, {}", id, paymentHistoryDTO);
        if (paymentHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paymentHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaymentHistoryDTO> result = paymentHistoryService.partialUpdate(paymentHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paymentHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /payment-histories} : get all the paymentHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentHistories in body.
     */
    @GetMapping("/payment-histories")
    public List<PaymentHistoryDTO> getAllPaymentHistories() {
        log.debug("REST request to get all PaymentHistories");
        return paymentHistoryService.findAll();
    }

    /**
     * {@code GET  /payment-histories/:id} : get the "id" paymentHistory.
     *
     * @param id the id of the paymentHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payment-histories/{id}")
    public ResponseEntity<PaymentHistoryDTO> getPaymentHistory(@PathVariable Long id) {
        log.debug("REST request to get PaymentHistory : {}", id);
        Optional<PaymentHistoryDTO> paymentHistoryDTO = paymentHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentHistoryDTO);
    }

    /**
     * {@code DELETE  /payment-histories/:id} : delete the "id" paymentHistory.
     *
     * @param id the id of the paymentHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/payment-histories/{id}")
    public ResponseEntity<Void> deletePaymentHistory(@PathVariable Long id) {
        log.debug("REST request to delete PaymentHistory : {}", id);
        paymentHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
