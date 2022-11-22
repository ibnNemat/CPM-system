package uz.devops.intern.service.mapper;

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
    @Mapping(target = "group", source = "group", qualifiedByName = "groupsId")
    ServicesDTO toDto(Services s);

    @Named("groupsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GroupsDTO toDtoGroupsId(Groups groups);
}
