package uz.devops.intern.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devops.intern.repository.ServicesRepository;
import uz.devops.intern.service.ServicesService;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.dto.ServicesDTO;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.intern.domain.Services}.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
public class ServicesResource {
    private final Logger log = LoggerFactory.getLogger(ServicesResource.class);
    private static final String ENTITY_NAME = "services";
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServicesService servicesService;

    public ServicesResource(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    /**
     * {@code POST  /services} : Create a new services.
     *
     * @param servicesDTO the servicesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new servicesDTO, or with status {@code 400 (Bad Request)} if the services has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/services")
    public ResponseEntity<ResponseDTO<ServicesDTO>> createServices(@Valid @RequestBody ServicesDTO servicesDTO) throws URISyntaxException {
        log.debug("REST request to save Services : {}", servicesDTO);
        if (servicesDTO.getId() != null) {
            throw new BadRequestAlertException("A new services cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResponseDTO<ServicesDTO> result = servicesService.save(servicesDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code PUT  /services/:id} : Updates an existing services.
     *
     * @param id the id of the servicesDTO to save.
     * @param servicesDTO the servicesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated servicesDTO,
     * or with status {@code 400 (Bad Request)} if the servicesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the servicesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/services/{id}")
    public ResponseEntity<ServicesDTO> updateServices(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServicesDTO servicesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Services : {}, {}", id, servicesDTO);
        if (!Objects.equals(id, servicesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        ServicesDTO result = servicesService.update(servicesDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, servicesDTO.getId().toString()))
            .body(result);
    }


    /**
     * {@code GET  /services} : get all the services.
     *
     * @return the ServicesDTOLIst with status {@code 200 (OK)} and the list of services in body.
     */
    @GetMapping("/services")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public List<ServicesDTO> getAllServices() {
        log.debug("REST request to get all Services");
        return servicesService.findAll();
    }

    /**
     * {@code GET  /services} : get all the services.
     *
     * @return the {@link ResponseDTO} with status {@code 200 (OK)} and the list of services in body.
     */
    @GetMapping("/manager-services")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
    public ResponseDTO<List<ServicesDTO>> getAllManagerServices() {
        log.debug("REST request to get all Services");
        return servicesService.getAllManagerServices();
    }

    /**
     * {@code GET  /services/:id} : get the "id" services.
     *
     * @param id the id of the servicesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the servicesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/services/{id}")
    public ResponseEntity<ServicesDTO> getServices(@PathVariable Long id) {
        log.debug("REST request to get Services : {}", id);
        Optional<ServicesDTO> servicesDTO = servicesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(servicesDTO);
    }

    /**
     * {@code DELETE  /services/:id} : delete the "id" services.
     *
     * @param id the id of the servicesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteServices(@PathVariable Long id) {
        log.debug("REST request to delete Services : {}", id);
        servicesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
