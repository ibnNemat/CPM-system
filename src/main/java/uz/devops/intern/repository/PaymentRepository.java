package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.Services;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Spring Data JPA repository for the PaymentDTO entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByCustomerAndGroupAndServiceAndStartedPeriodAndIsPayedFalse(
        Customers customer, Groups group, Services service, LocalDate startedDate
    );

    @Modifying
    @Query("update Payment p set p.payedMoney = p.payedMoney + ?1 " +
        "where p.customer.id = ?2 and p.group.id = ?3 and p.service.id = ?4 and p.startedPeriod = ?5")
    void paymentForCurrentPeriod(
        Double payment, Long customerId, Long groupId, Long serviceID, LocalDate startedPeriodDate
    );

    @Modifying
    @Query("update Payment p set p.payedMoney = p.payedMoney + ?1, p.isPayed = true " +
        "where p.customer.id = ?2 and p.group.id = ?3 and p.service.id = ?4 and p.startedPeriod = ?5")
    void paymentForCurrentPeriodAndSetPayedTrue(
        Double payment, Long customerId, Long groupId, Long serviceID, LocalDate startedPeriodDate
    );

//    @Modifying
//    @Query("update Payment p set p.isPayed = true " +
//        "where p.customer.id = ?1 and p.group.id = ?2 and p.service.id = ?3 and p.startedPeriod = ?4 " +
//        "and p.payedMoney = p.paymentForPeriod")
//    void setPayedTrueIfCompletelyPaid(Long customerId, Long groupId, Long serviceID, LocalDate startedPeriodDate);
}
