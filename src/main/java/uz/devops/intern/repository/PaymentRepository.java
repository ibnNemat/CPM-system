package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(
        Customers customer, Groups group, Services service, LocalDate startedDate
    );

    List<Payment> findAllByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(
        Customers customer, Groups group, Services service, LocalDate startedDate
    );

    @Modifying
    @Query("update Payment p set p.paidMoney = p.paidMoney + ?1 " +
        "where p.customer.id = ?2 and p.group.id = ?3 and p.service.id = ?4 and p.startedPeriod = ?5")
    void paymentForCurrentPeriod(
        Double payment, Long customerId, Long groupId, Long serviceID, LocalDate startedPeriodDate
    );

    @Query("select p from Payment p where p.group.groupOwnerName = ?1")
    List<Payment> findAllByGroupOwnerName(String managerName);

    List<Payment> findAllByCustomerOrderByStartedPeriod(Customers customer);

    List<Payment> findAllByCustomerAndIsPaidFalseOrderByStartedPeriod(Customers customer);
}
