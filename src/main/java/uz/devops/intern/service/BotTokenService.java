package uz.devops.intern.service;

import java.util.List;
import java.util.Optional;

import uz.devops.intern.domain.BotToken;
import uz.devops.intern.service.dto.BotTokenDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.BotToken}.
 */
public interface BotTokenService {
    /**
     * Save a botToken.
     *
     * @param botTokenDTO the entity to save.
     * @return the persisted entity.
     */
    BotTokenDTO save(BotTokenDTO botTokenDTO);

    /**
     * Updates a botToken.
     *
     * @param botTokenDTO the entity to update.
     * @return the persisted entity.
     */
    BotTokenDTO update(BotTokenDTO botTokenDTO);

    /**
     * Partially updates a botToken.
     *
     * @param botTokenDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BotTokenDTO> partialUpdate(BotTokenDTO botTokenDTO);

    /**
     * Get all the botTokens.
     *
     * @return the list of entities.
     */
    List<BotTokenDTO> findAll();

    /**
     * Get the "id" botToken.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BotTokenDTO> findOne(Long id);

    /**
     * Delete the "id" botToken.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Optional<BotToken> findByChatId(Long chatId);
}
