package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.PaymentHistory;

/**
 * Spring Data JPA repository for the PaymentHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {}
