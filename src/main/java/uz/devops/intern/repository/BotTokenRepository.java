package uz.devops.intern.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.BotToken;

/**
 * Spring Data JPA repository for the BotToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BotTokenRepository extends JpaRepository<BotToken, Long> {
    @Query("select botToken from BotToken botToken where botToken.createdBy.login = ?#{principal.username}")
    List<BotToken> findByCreatedByIsCurrentUser();
}
