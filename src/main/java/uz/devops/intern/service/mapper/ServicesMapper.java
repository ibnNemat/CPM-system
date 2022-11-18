package uz.devops.intern.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ServicesDTO;

/**
 * Mapper for the entity {@link Services} and its DTO {@link ServicesDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServicesMapper extends EntityMapper<ServicesDTO, Services> {
    @Mapping(target = "group", source = "group", qualifiedByName = "groupsId")
    @Mapping(target = "users", source = "users", qualifiedByName = "customersIdSet")
    ServicesDTO toDto(Services s);

    @Mapping(target = "removeUsers", ignore = true)
    Services toEntity(ServicesDTO servicesDTO);

    @Named("groupsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GroupsDTO toDtoGroupsId(Groups groups);

    @Named("customersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomersDTO toDtoCustomersId(Customers customers);

    @Named("customersIdSet")
    default Set<CustomersDTO> toDtoCustomersIdSet(Set<Customers> customers) {
        return customers.stream().map(this::toDtoCustomersId).collect(Collectors.toSet());
    }
}
