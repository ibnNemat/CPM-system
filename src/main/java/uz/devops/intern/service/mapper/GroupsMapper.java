package uz.devops.intern.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.dto.ServicesDTO;

/**
 * Mapper for the entity {@link Groups} and its DTO {@link GroupsDTO}.
 */
@Mapper(componentModel = "spring")
public interface GroupsMapper extends EntityMapper<GroupsDTO, Groups> {
    @Mapping(target = "services", source = "services", qualifiedByName = "servicesIdSet")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationId")
    GroupsDTO toDto(Groups s);

    @Mapping(target = "removeServices", ignore = true)
    Groups toEntity(GroupsDTO groupsDTO);

    @Named("servicesId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServicesDTO toDtoServicesId(Services services);

    @Named("servicesIdSet")
    default Set<ServicesDTO> toDtoServicesIdSet(Set<Services> services) {
        return services.stream().map(this::toDtoServicesId).collect(Collectors.toSet());
    }

    @Named("organizationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationDTO toDtoOrganizationId(Organization organization);
}
