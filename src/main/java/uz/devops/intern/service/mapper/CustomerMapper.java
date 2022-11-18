package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Customers;
import uz.devops.intern.service.dto.CustomersDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class CustomerMapper {
    public static CustomersDTO toDtoWithNoUser(Customers customers){
        CustomersDTO customersDTO = new CustomersDTO();
        customersDTO.setUsername(customers.getUsername());
        customersDTO.setPassword(customers.getPassword());
        customersDTO.setId(customers.getId());
        customersDTO.setAccount(customers.getAccount());
        customersDTO.setPhoneNumber(customers.getPhoneNumber());
        return customersDTO;
    }

    public static Set<CustomersDTO> toEntity(Set<Customers> customersSet){
        return customersSet.stream()
            .map(CustomerMapper::toDtoWithNoUser)
            .collect(Collectors.toSet());
    }
}
