package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.service.dto.OrganizationDTO;

/**
 * Mapper for the entity {@link Organization} and its DTO {@link OrganizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMapper extends EntityMapper<OrganizationDTO, Organization> {}
