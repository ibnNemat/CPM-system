package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the CustomerTelegram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerTelegramRepository extends JpaRepository<CustomerTelegram, Long> {

    @Query("SELECT ct FROM CustomerTelegram ct WHERE ct.telegramId = :telegramId")
    Optional<CustomerTelegram> findByTelegramId(@Param("telegramId") Long telegramId);

    @Query(value = "SELECT ct.*\n" +
        "FROM customer_telegram ct JOIN\n" +
        "(SELECT * FROM jhi_user WHERE id = \n" +
        "    (SELECT created_by_id FROM bot_token WHERE telegram_id = :botId))\n" +
        "    r ON ct.phone_number = r.created_by", nativeQuery = true)
    Optional<CustomerTelegram> findByBot(@Param("botId") Long botId);

    @Query(value = "SELECT * FROM customer_telegram WHERE id IN (SELECT customer_telegram_id FROM rel_customer_telegram__telegram_group WHERE telegram_group_id = :chatId)", nativeQuery = true)
    List<CustomerTelegram> getCustomersByChatId(@Param("chatId") Long chatId);

    List<CustomerTelegram> findAllByTelegramGroupsChatId(Long chatId);
    List<CustomerTelegram> findAllByIsActiveTrue();
    boolean existsByTelegramId(Long telegramId);
    @Modifying
    @Query("update CustomerTelegram ct set ct.isActive = false where ct.id in ?1")
    void setFalseTelegramCustomersProfile(List<Long> ids);

    @Modifying
    void deleteAllByIdInAndIsActiveFalse(List<Long> ids);

    Optional<CustomerTelegram> findByCustomer(Customers customer);
}
