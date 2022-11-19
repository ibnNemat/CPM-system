package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.dto.ServicesDTO;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customersId")
    @Mapping(target = "service", source = "service", qualifiedByName = "servicesId")
    @Mapping(target = "group", source = "group", qualifiedByName = "groupsId")
    PaymentDTO toDto(Payment s);















}
