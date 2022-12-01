package uz.devops.intern.service;

import java.util.List;
import java.util.Optional;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.CustomerTelegram}.
 */
public interface CustomerTelegramService {
    /**
     * Save a customerTelegram.
     *
     * @param customerTelegramDTO the entity to save.
     * @return the persisted entity.
     */
    CustomerTelegramDTO save(CustomerTelegramDTO customerTelegramDTO);

    /**
     * Updates a customerTelegram.
     *
     * @param customerTelegramDTO the entity to update.
     * @return the persisted entity.
     */
    CustomerTelegramDTO update(CustomerTelegramDTO customerTelegramDTO);

    /**
     * Partially updates a customerTelegram.
     *
     * @param customerTelegramDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CustomerTelegramDTO> partialUpdate(CustomerTelegramDTO customerTelegramDTO);

    /**
     * Get all the customerTelegrams.
     *
     * @return the list of entities.
     */
    List<CustomerTelegramDTO> findAll();

    /**
     * Get the "id" customerTelegram.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CustomerTelegramDTO> findOne(Long id);

    /**
     * Delete the "id" customerTelegram.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
