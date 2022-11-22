package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Customers;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.UserDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class CustomerMapper {
    public static CustomersDTO toDtoWithNoUser(Customers customers){
        CustomersDTO customersDTO = new CustomersDTO();
        if (customers != null) {
            customersDTO.setId(customersDTO.getId());
            customersDTO.setUsername(customers.getUsername());
            customersDTO.setPassword(customers.getPassword());
            customersDTO.setId(customers.getId());
            customersDTO.setAccount(customers.getAccount());
            customersDTO.setPhoneNumber(customers.getPhoneNumber());
        }
        return customersDTO;
    }

    public static CustomersDTO toDtoWithAll(Customers customers){
        CustomersDTO customersDTO = new CustomersDTO();
        if (customers != null) {
            customersDTO.setId(customersDTO.getId());
            customersDTO.setUsername(customers.getUsername());
            customersDTO.setPassword(customers.getPassword());
            customersDTO.setId(customers.getId());
            customersDTO.setAccount(customers.getAccount());
            customersDTO.setPhoneNumber(customers.getPhoneNumber());
            if (customers.getUser() != null){
                UserDTO userDTO = new UserDTO();
                userDTO.setId(customers.getUser().getId());
                customersDTO.setUser(userDTO);
            }
            customersDTO.setGroups(GroupMapper.groupsDTOSet(customers.getGroups()));
            customersDTO.setServices(ServiceMapper.servicesDTOSet(customers.getServices()));
        }
        return customersDTO;
    }

    public static Customers toEntityWithNoUser(CustomersDTO c){
        Customers customers = new Customers();
        customers.setUsername(c.getUsername());
        customers.setPassword(c.getPassword());
        customers.setId(c.getId());
        customers.setAccount(c.getAccount());
        customers.setPhoneNumber(c.getPhoneNumber());
        return customers;
    }

    public static Set<CustomersDTO> toSetCustomerDto(Set<Customers> customersSet){
        return customersSet.stream()
            .map(CustomerMapper::toDtoWithNoUser)
            .collect(Collectors.toSet());
    }

    public static Set<CustomersDTO> toSetCustomerDtoForSavingService(Set<Customers> customersSet){
        return customersSet.stream()
            .map(CustomerMapper::toDtoOnlyCustomerId)
            .collect(Collectors.toSet());
    }

    private static CustomersDTO toDtoOnlyCustomerId(Customers customers) {
        CustomersDTO c = new CustomersDTO();
        c.setId(customers.getId());
        return c;
    }

    public static Set<Customers> toSetCustomerEntityForSavingService(Set<CustomersDTO> c){
        return c.stream()
            .map(CustomerMapper::toEntityOnlyCustomerId)
            .collect(Collectors.toSet());
    }

    private static Customers toEntityOnlyCustomerId(CustomersDTO customersDTO) {
        Customers customers = new Customers();
        customers.setId(customersDTO.getId());
        return customers;
    }
}
