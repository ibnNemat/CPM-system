package uz.devops.intern.service;

import java.util.List;
import java.util.Optional;
import uz.devops.intern.service.dto.PaymentHistoryDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.PaymentHistory}.
 */
public interface PaymentHistoryService {
    /**
     * Save a paymentHistory.
     *
     * @param paymentHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    PaymentHistoryDTO save(PaymentHistoryDTO paymentHistoryDTO);

    /**
     * Updates a paymentHistory.
     *
     * @param paymentHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    PaymentHistoryDTO update(PaymentHistoryDTO paymentHistoryDTO);

    /**
     * Partially updates a paymentHistory.
     *
     * @param paymentHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PaymentHistoryDTO> partialUpdate(PaymentHistoryDTO paymentHistoryDTO);

    /**
     * Get all the paymentHistories.
     *
     * @return the list of entities.
     */
    List<PaymentHistoryDTO> findAll();

    /**
     * Get the "id" paymentHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PaymentHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" paymentHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

}
