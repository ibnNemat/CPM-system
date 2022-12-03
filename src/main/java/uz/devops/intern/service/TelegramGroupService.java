package uz.devops.intern.service;

import java.util.List;
import java.util.Optional;
import uz.devops.intern.service.dto.TelegramGroupDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.TelegramGroup}.
 */
public interface TelegramGroupService {
    /**
     * Save a telegramGroup.
     *
     * @param telegramGroupDTO the entity to save.
     * @return the persisted entity.
     */
    TelegramGroupDTO save(TelegramGroupDTO telegramGroupDTO);

    /**
     * Updates a telegramGroup.
     *
     * @param telegramGroupDTO the entity to update.
     * @return the persisted entity.
     */
    TelegramGroupDTO update(TelegramGroupDTO telegramGroupDTO);

    /**
     * Partially updates a telegramGroup.
     *
     * @param telegramGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TelegramGroupDTO> partialUpdate(TelegramGroupDTO telegramGroupDTO);

    /**
     * Get all the telegramGroups.
     *
     * @return the list of entities.
     */
    List<TelegramGroupDTO> findAll();

    /**
     * Get the "id" telegramGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TelegramGroupDTO> findOne(Long id);

    /**
     * Delete the "id" telegramGroup.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
