package uz.devops.intern.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.intern.service.dto.ServicesDTO;

/**
 * Service Interface for managing {@link uz.devops.intern.domain.Services}.
 */
public interface ServicesService {
    /**
     * Save a services.
     *
     * @param servicesDTO the entity to save.
     * @return the persisted entity.
     */
    ServicesDTO save(ServicesDTO servicesDTO);

    /**
     * Updates a services.
     *
     * @param servicesDTO the entity to update.
     * @return the persisted entity.
     */
    ServicesDTO update(ServicesDTO servicesDTO);

    /**
     * Partially updates a services.
     *
     * @param servicesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ServicesDTO> partialUpdate(ServicesDTO servicesDTO);

    /**
     * Get all the services.
     *
     * @return the list of entities.
     */
    List<ServicesDTO> findAll();

    /**
     * Get all the services with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServicesDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" services.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServicesDTO> findOne(Long id);

    /**
     * Delete the "id" services.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
