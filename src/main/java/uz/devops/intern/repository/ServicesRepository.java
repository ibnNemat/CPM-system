package uz.devops.intern.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.intern.domain.Services;

/**
 * Spring Data JPA repository for the Services entity.
 *
 * When extending this class, extend ServicesRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ServicesRepository extends ServicesRepositoryWithBagRelationships, JpaRepository<Services, Long> {
    default Optional<Services> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Services> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Services> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

//    @Query("select s from Services s where s")
    List<Services> findAllByGroupsGroupOwnerName(String ownerName);
}
