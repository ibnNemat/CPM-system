package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.PaymentHistory;

import java.util.List;

/**
 * Spring Data JPA repository for the PaymentHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findAllByCustomer(Customers customer);
}
