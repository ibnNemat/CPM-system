package uz.devops.intern.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import uz.devops.intern.domain.Customers;

public interface CustomersRepositoryWithBagRelationships {
    Optional<Customers> fetchBagRelationships(Optional<Customers> customers);

    List<Customers> fetchBagRelationships(List<Customers> customers);

    Page<Customers> fetchBagRelationships(Page<Customers> customers);
}
