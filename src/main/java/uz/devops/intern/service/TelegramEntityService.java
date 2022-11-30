package uz.devops.intern.service;

import java.util.List;
import java.util.Optional;
import uz.devops.intern.service.dto.TelegramEntityDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.TelegramEntity}.
 */
public interface TelegramEntityService {
    /**
     * Save a telegramEntity.
     *
     * @param telegramEntityDTO the entity to save.
     * @return the persisted entity.
     */
    TelegramEntityDTO save(TelegramEntityDTO telegramEntityDTO);

    /**
     * Updates a telegramEntity.
     *
     * @param telegramEntityDTO the entity to update.
     * @return the persisted entity.
     */
    TelegramEntityDTO update(TelegramEntityDTO telegramEntityDTO);

    /**
     * Partially updates a telegramEntity.
     *
     * @param telegramEntityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TelegramEntityDTO> partialUpdate(TelegramEntityDTO telegramEntityDTO);

    /**
     * Get all the telegramEntities.
     *
     * @return the list of entities.
     */
    List<TelegramEntityDTO> findAll();

    /**
     * Get the "id" telegramEntity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TelegramEntityDTO> findOne(Long id);

    /**
     * Delete the "id" telegramEntity.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
