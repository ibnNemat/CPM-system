package uz.devops.intern.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Services;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ServicesDTO;
import uz.devops.intern.service.dto.UserDTO;

/**
 * Mapper for the entity {@link Customers} and its DTO {@link CustomersDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomersMapper extends EntityMapper<CustomersDTO, Customers> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "groups", source = "groups", qualifiedByName = "groupsIdSet")
    @Mapping(target = "services", source = "services", qualifiedByName = "servicesIdSet")
    CustomersDTO toDto(Customers s);

    @Mapping(target = "removeGroups", ignore = true)
    @Mapping(target = "removeServices", ignore = true)
    Customers toEntity(CustomersDTO customersDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("groupsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GroupsDTO toDtoGroupsId(Groups groups);

    @Named("groupsIdSet")
    default Set<GroupsDTO> toDtoGroupsIdSet(Set<Groups> groups) {
        return groups.stream().map(this::toDtoGroupsId).collect(Collectors.toSet());
    }

    @Named("servicesId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServicesDTO toDtoServicesId(Services services);

    @Named("servicesIdSet")
    default Set<ServicesDTO> toDtoServicesIdSet(Set<Services> services) {
        return services.stream().map(this::toDtoServicesId).collect(Collectors.toSet());
    }
}
