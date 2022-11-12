package uz.devops.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.devops.intern.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
