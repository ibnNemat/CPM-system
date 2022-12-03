package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.TelegramGroup;

/**
 * Spring Data JPA repository for the TelegramGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelegramGroupRepository extends JpaRepository<TelegramGroup, Long> {}
