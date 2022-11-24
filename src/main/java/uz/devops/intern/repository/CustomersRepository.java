package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.service.dto.CustomersDTO;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Customers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomersRepository extends JpaRepository<Customers, Long>, GroupsRepositoryWithBagRelationships {
    Optional<Customers> findByIdAndBalanceGreaterThan(Long customerId, Double account);

    @Modifying
    @Query("update Customers c set c.balance = c.balance - ?1 where c.id = ?2")
    void decreaseCustomerBalance(Double paidMoney, Long customerId);

    Optional<Customers> findByUsername(String username);
}
