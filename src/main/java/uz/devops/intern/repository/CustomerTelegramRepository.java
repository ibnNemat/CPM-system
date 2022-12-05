package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.CustomerTelegram;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the CustomerTelegram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerTelegramRepository extends JpaRepository<CustomerTelegram, Long> {
    boolean existsByTelegramId(Long telegramId);

    @Query("SELECT ct FROM CustomerTelegram ct WHERE ct.telegramId = :telegramId")
    Optional<CustomerTelegram> findByTelegramId(@Param("telegramId") Long telegramId);

    @Query(value = "SELECT ct.*\n" +
        "FROM customer_telegram ct JOIN\n" +
        "(SELECT * FROM jhi_user WHERE id = \n" +
        "    (SELECT created_by_id FROM bot_token WHERE telegram_id = :botId))\n" +
        "    r ON ct.phone_number = r.created_by", nativeQuery = true)
    Optional<CustomerTelegram> findByBot(@Param("botId") Long botId);

    @Query("SELECT ct FROM CustomerTelegram ct WHERE ct.chatId = :chatId")
    List<CustomerTelegram> getCountCustomersByChatId(@Param("chatId") Long chatId);
}
