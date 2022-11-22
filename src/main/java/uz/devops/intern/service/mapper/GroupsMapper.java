package uz.devops.intern.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.OrganizationDTO;

/**
 * Mapper for the entity {@link Groups} and its DTO {@link GroupsDTO}.
 */
@Mapper(componentModel = "spring")
public interface GroupsMapper extends EntityMapper<GroupsDTO, Groups> {
    @Mapping(target = "customers", source = "customers", qualifiedByName = "customersIdSet")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationId")
    GroupsDTO toDto(Groups s);

    @Mapping(target = "removeCustomers", ignore = true)
    Groups toEntity(GroupsDTO groupsDTO);

    @Named("customersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomersDTO toDtoCustomersId(Customers customers);

    @Named("customersIdSet")
    default Set<CustomersDTO> toDtoCustomersIdSet(Set<Customers> customers) {
        return customers.stream().map(this::toDtoCustomersId).collect(Collectors.toSet());
    }

    @Named("organizationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationDTO toDtoOrganizationId(Organization organization);
}
