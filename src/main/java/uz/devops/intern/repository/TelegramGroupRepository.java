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

    @Query(value = "SELECT * FROM ((SELECT * FROM telegram_group WHERE chat_id < :groupId ORDER BY chat_id DESC LIMIT 1)\n" +
        "        UNION\n" +
        "        (SELECT * FROM telegram_group WHERE chat_id = :groupId)\n" +
        "        UNION\n" +
        "        (SELECT * FROM telegram_group WHERE chat_id > :groupId ORDER BY chat_id LIMIT 1)\n" +
        "        ) r ORDER BY r.chat_id",
    nativeQuery = true)
    List<TelegramGroup> getThreeEntityWithUnion(@Param("groupId") Long groupId);

    Optional<TelegramGroup> findByChatId(Long chatId);

    @Query("SELECT tg FROM TelegramGroup tg WHERE tg.id IN (SELECT ct.telegramGroups FROM CustomerTelegram ct WHERE ct.chatId = :managerId)")
    List<TelegramGroup> findByCustomer(@Param("managerId") Long managerId);


}
