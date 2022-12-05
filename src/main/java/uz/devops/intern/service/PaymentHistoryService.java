package uz.devops.intern.service;

import java.util.List;
import java.util.Optional;

import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.PaymentHistory;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.dto.PaymentHistoryDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.PaymentHistory}.
 */
public interface PaymentHistoryService {
    List<PaymentHistory> getTelegramCustomerPaymentHistories(Customers customer);
    /**
     * Save a paymentHistory.
     *
     * @param paymentHistory the entity to save.
     * @return the persisted entity.
     */
    PaymentHistory save(PaymentHistory paymentHistory);

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
