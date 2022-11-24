package uz.devops.intern.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.ResponseDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.Customers}.
 */
public interface CustomersService {
    CustomersDTO findByIdBalanceGreaterThen(Long customerId, Double account);
    /**
     * Save a customers.
     *
     * @param customersDTO the entity to save.
     * @return the persisted entity.
     */
    CustomersDTO save(CustomersDTO customersDTO);

    /**
     * Updates a customers.
     *
     * @param customersDTO the entity to update.
     * @return the persisted entity.
     */
    ResponseDTO<CustomersDTO> update(CustomersDTO customersDTO);

    /**
     * Partially updates a customers.
     *
     * @param customersDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CustomersDTO> partialUpdate(CustomersDTO customersDTO);

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CustomersDTO> findAll(Pageable pageable);

    /**
     * Get the "id" customers.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CustomersDTO> findOne(Long id);
    Optional<Customers> findByUsername(String username);
    void decreaseCustomerBalance(Double paidMoney, Long customerId);

    /**
     * Delete the "id" customers.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Page<CustomersDTO> findAllWithEagerRelationships(Pageable pageable);
}
