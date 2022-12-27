package uz.devops.intern.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.dto.PaymentHistoryDTO;
import uz.devops.intern.service.dto.PaymentRequestParamDTO;
import uz.devops.intern.service.dto.ResponseDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.Payment}.
 */
public interface PaymentService {
    ResponseDTO<PaymentDTO> includePaymentToNewCustomer(PaymentRequestParamDTO requestParamDTO);
    List<PaymentDTO> findAllByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(Customers customers, Services service, Groups group, LocalDate startedPeriod);
    List<PaymentDTO> getAllCustomerPaymentsPayedIsFalse(Customers customer);
    ResponseDTO<List<PaymentDTO>> getAllCustomerPayments();
    List<PaymentDTO> getAllPaymentsCreatedByGroupManager();
    List<Payment> saveAll(List<Payment> paymentList);
    ResponseDTO<PaymentHistoryDTO> payForService(PaymentDTO paymentDTO);
    /**
     * Save a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    PaymentDTO save(PaymentDTO paymentDTO);

    /**
     * Updates a payment.
     *
     * @param paymentDTO the entity to update.
     * @return the persisted entity.
     */
    PaymentDTO update(PaymentDTO paymentDTO);

    /**
     * Partially updates a payment.
     *
     * @param paymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PaymentDTO> partialUpdate(PaymentDTO paymentDTO);

    /**
     * Get all the payments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PaymentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" payment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PaymentDTO> findOne(Long id);

    /**
     * Delete the "id" payment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    ResponseDTO<PaymentDTO> getByCustomerId(Long customerId);

    ResponseDTO<List<PaymentDTO>> getByUserLogin(String login);

    ResponseDTO<List<PaymentDTO>> getByUserLogin(String login, Pageable pageable);
}
