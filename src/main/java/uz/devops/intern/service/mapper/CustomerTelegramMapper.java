package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.CustomersDTO;

/**
 * Mapper for the entity {@link CustomerTelegram} and its DTO {@link CustomerTelegramDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerTelegramMapper extends EntityMapper<CustomerTelegramDTO, CustomerTelegram> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customersId")
    CustomerTelegramDTO toDto(CustomerTelegram s);

    @Named("customersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomersDTO toDtoCustomersId(Customers customers);
}
