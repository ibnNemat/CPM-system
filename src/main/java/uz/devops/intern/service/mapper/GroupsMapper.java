package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.OrganizationDTO;

/**
 * Mapper for the entity {@link Groups} and its DTO {@link GroupsDTO}.
 */
@Mapper(componentModel = "spring")
public interface GroupsMapper extends EntityMapper<GroupsDTO, Groups> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationId")
    GroupsDTO toDto(Groups s);

    @Named("organizationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationDTO toDtoOrganizationId(Organization organization);
}
