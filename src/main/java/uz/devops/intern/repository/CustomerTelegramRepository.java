package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.CustomerTelegram;

import java.util.Optional;

/**
 * Spring Data JPA repository for the CustomerTelegram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerTelegramRepository extends JpaRepository<CustomerTelegram, Long> {
    boolean existsByTelegramId(Long telegramId);
    Optional<CustomerTelegram> findByTelegramId(Long telegramId);
}
