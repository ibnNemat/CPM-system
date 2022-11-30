package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.TelegramEntity;

/**
 * Spring Data JPA repository for the TelegramEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelegramEntityRepository extends JpaRepository<TelegramEntity, Long> {}
