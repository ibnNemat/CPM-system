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
    public PaymentHistoryResource(PaymentHistoryService paymentHistoryService) {
        this.paymentHistoryService = paymentHistoryService;
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
