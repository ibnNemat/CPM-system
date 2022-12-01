package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.CustomerTelegram;

/**
 * Spring Data JPA repository for the CustomerTelegram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerTelegramRepository extends JpaRepository<CustomerTelegram, Long> {}
