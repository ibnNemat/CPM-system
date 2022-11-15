package uz.devops.intern.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.intern.service.dto.GroupsDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.Groups}.
 */
public interface GroupsService {
    /**
     * Save a groups.
     *
     * @param groupsDTO the entity to save.
     * @return the persisted entity.
     */
    GroupsDTO save(GroupsDTO groupsDTO);

    /**
     * Updates a groups.
     *
     * @param groupsDTO the entity to update.
     * @return the persisted entity.
     */
    GroupsDTO update(GroupsDTO groupsDTO);

    /**
     * Partially updates a groups.
     *
     * @param groupsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GroupsDTO> partialUpdate(GroupsDTO groupsDTO);

    /**
     * Get all the groups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GroupsDTO> findAll(Pageable pageable);

    /**
     * Get all the groups with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GroupsDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" groups.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GroupsDTO> findOne(Long id);

    /**
     * Delete the "id" groups.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
