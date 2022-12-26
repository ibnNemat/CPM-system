package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.repository.OrganizationRepository;
import uz.devops.intern.service.OrganizationService;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.mapper.OrganizationMapper;
import uz.devops.intern.service.mapper.OrganizationsMapper;
import uz.devops.intern.service.utils.ContextHolderUtil;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Organization}.
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
    private final Logger log = LoggerFactory.getLogger(OrganizationServiceImpl.class);
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private static final String ENTITY_NAME = "organization";

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public OrganizationDTO save(OrganizationDTO organizationDTO) {
        log.debug("Request to save Organization : {}", organizationDTO);
        String orgOwner = ContextHolderUtil.getUsernameFromContextHolder();
        if (orgOwner == null){
            log.error("Error while saving new organization: user principal not found!");
            return null;
        }
        organizationDTO.setOrgOwnerName(orgOwner);
        Organization organization = organizationMapper.toEntity(organizationDTO);
        organization = organizationRepository.save(organization);
        return OrganizationsMapper.toDtoWithGroups(organization);
    }

    @Override
    public OrganizationDTO update(OrganizationDTO organizationDTO) {
        log.debug("Request to update Organization : {}", organizationDTO);
        if (organizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!organizationRepository.existsById(organizationDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Organization organization = organizationMapper.toEntity(organizationDTO);
        organization = organizationRepository.save(organization);
        return OrganizationsMapper.toDtoWithGroups(organization);
    }

    @Override
    public Optional<OrganizationDTO> partialUpdate(OrganizationDTO organizationDTO) {
        log.debug("Request to partially update Organization : {}", organizationDTO);

        return organizationRepository
            .findById(organizationDTO.getId())
            .map(existingOrganization -> {
                organizationMapper.partialUpdate(existingOrganization, organizationDTO);

                return existingOrganization;
            })
            .map(organizationRepository::save)
            .map(OrganizationsMapper::toDtoWithGroups);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationDTO> findAll() {
        log.debug("Request to get all Organizations");
        return organizationRepository.findAll().stream()
            .map(OrganizationsMapper::toDtoWithGroups)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationDTO> findOne(Long id) {
        log.debug("Request to get Organization : {}", id);
        return organizationRepository.findById(id)
            .map(OrganizationsMapper::toDtoWithGroups);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Organization : {}", id);
        organizationRepository.deleteById(id);
    }

    @Override
    public List<OrganizationDTO> getOrganizationsByUserLogin() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user != null && user.getUsername() != null){
            List<Organization> organizations = organizationRepository.findAllByOrgOwnerName(user.getUsername());
            return organizations.stream().map(OrganizationsMapper::toDtoWithoutGroups).collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public ResponseDTO<OrganizationDTO> getOrganizationByName(String name) {
        if(name == null || name.trim().isEmpty()){
            return ResponseDTO.<OrganizationDTO>builder()
                .success(false).message("Parameter \"Name\" is null or empty!").build();
        }

        Optional<Organization> organizationOptional =
            organizationRepository.findByName(name);
        if(organizationOptional.isEmpty()){
            return ResponseDTO.<OrganizationDTO>builder()
                .success(false).message("Data is not found!").build();
        }
        OrganizationDTO dto = organizationOptional.map(OrganizationsMapper::toDtoWithoutGroups).get();
        return ResponseDTO.<OrganizationDTO>builder()
            .success(true).message("OK").responseData(dto).build();
    }

    @Override
    public List<OrganizationDTO> managerOrganizations() {
        return getOrganizationDTOS(log, organizationRepository, organizationMapper);
    }

    @Nullable
    public static List<OrganizationDTO> getOrganizationDTOS(Logger log, OrganizationRepository organizationRepository, OrganizationMapper organizationMapper) {
        String username = ContextHolderUtil.getUsernameFromContextHolder();
        if (username == null){
            log.error("Error while getting organizations: user principal not found!");
            return null;
        }

        List<Organization> organizations = organizationRepository.findAllByOrgOwnerName(username);
        return organizations.stream()
            .map(OrganizationsMapper::toDtoWithGroups)
            .toList();
    }
}
