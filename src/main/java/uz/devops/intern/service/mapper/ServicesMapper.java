package uz.devops.intern.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ServicesDTO;

/**
 * Mapper for the entity {@link Services} and its DTO {@link ServicesDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServicesMapper extends EntityMapper<ServicesDTO, Services> {
    @Mapping(target = "groups", source = "groups", qualifiedByName = "groupsIdSet")
    ServicesDTO toDto(Services s);

    @Mapping(target = "removeGroups", ignore = true)
    Services toEntity(ServicesDTO servicesDTO);

    @Named("groupsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GroupsDTO toDtoGroupsId(Groups groups);

    @Named("groupsIdSet")
    default Set<GroupsDTO> toDtoGroupsIdSet(Set<Groups> groups) {
        return groups.stream().map(this::toDtoGroupsId).collect(Collectors.toSet());
    }
}
