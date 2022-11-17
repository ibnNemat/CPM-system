package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.PaymentHistory;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.PaymentHistoryDTO;

/**
 * Mapper for the entity {@link PaymentHistory} and its DTO {@link PaymentHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentHistoryMapper extends EntityMapper<PaymentHistoryDTO, PaymentHistory> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customersId")
    PaymentHistoryDTO toDto(PaymentHistory s);

    @Named("customersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomersDTO toDtoCustomersId(Customers customers);
}
