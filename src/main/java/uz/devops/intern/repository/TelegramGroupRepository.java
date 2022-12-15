package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.TelegramGroup;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data JPA repository for the TelegramGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelegramGroupRepository extends JpaRepository<TelegramGroup, Long> {

    Boolean existsByChatId(Long chatId);

    @Query(value = "SELECT EXISTS (SELECT * FROM rel_customer_telegram__telegram_group WHERE telegram_group_id = " +
        "(SELECT id FROM telegram_group WHERE chat_id = :chatId))", nativeQuery = true)
    Boolean existsInRelatedTableByChatId(@Param("chatId") Long chatId);

    @Query(value = "SELECT * FROM ((SELECT * FROM telegram_group WHERE chat_id < :groupId ORDER BY chat_id DESC LIMIT 1)\n" +
        "        UNION\n" +
        "        (SELECT * FROM telegram_group WHERE chat_id = :groupId)\n" +
        "        UNION\n" +
        "        (SELECT * FROM telegram_group WHERE chat_id > :groupId ORDER BY chat_id LIMIT 1)\n" +
        "        ) r ORDER BY r.chat_id",
    nativeQuery = true)
    List<TelegramGroup> getThreeEntityWithUnion(@Param("groupId") Long groupId);

    Optional<TelegramGroup> findByChatId(Long chatId);

    @Query(value = "SELECT * FROM telegram_group WHERE id IN (SELECT telegram_group_id FROM rel_customer_telegram__telegram_group WHERE customer_telegram_id = :id)", nativeQuery = true)
    List<TelegramGroup> findByCustomer(@Param("id") Long id);

    @Query(value = "SELECT t2.* FROM \n" +
        "(SELECT telegram_group_id FROM rel_customer_telegram__telegram_group \n" +
        "WHERE customer_telegram_id = :id) t1\n" +
        "JOIN \n" +
        "(SELECT tg.* FROM telegram_group tg WHERE tg.name NOT IN (SELECT name FROM groups)) t2\n" +
        "ON t1.telegram_group_id = t2.id",
        nativeQuery = true)
    List<TelegramGroup> findBYCustomerWhichIsNotInGroups(@Param("id") Long id);

    @Query(value = "SELECT telegram_group_id FROM rel_customer_telegram__telegram_group WHERE customer_telegram_id = " +
        "(SELECT id FROM customer_telegram WHERE telegram_id = :managerId)",
        nativeQuery = true)
    List<TelegramGroup> getNotRegisteredTgGroups(@Param("managerId") Long managerId);
}
