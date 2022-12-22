package uz.devops.intern.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.repository.CustomersRepository;
import uz.devops.intern.security.SecurityUtils;

import java.util.Optional;

@Component
public class AuthenticatedUserUtil {
    @Autowired
    private CustomersRepository customersRepository;
    public Customers getAuthenticatedUser(){
        Optional<String> loginOptional = SecurityUtils.getCurrentUserLogin();
        if (loginOptional.isEmpty()) return null;
        String userName = loginOptional.get();
        Optional<Customers> customerOptional = customersRepository.findByUsername(userName);
        if (customerOptional.isEmpty()) return null;
        return customerOptional.get();
    }
}
