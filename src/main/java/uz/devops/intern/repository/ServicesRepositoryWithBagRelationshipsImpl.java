package uz.devops.intern.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.devops.intern.domain.Services;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ServicesRepositoryWithBagRelationshipsImpl implements ServicesRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Services> fetchBagRelationships(List<Services> services) {
        return Optional.of(services).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    Services fetchUsers(Services result) {
        return entityManager
            .createQuery(
                "select services from Services services left join fetch services.customers where services is :services",
                Services.class
            )
            .setParameter("services", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Services> fetchUsers(List<Services> services) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, services.size()).forEach(index -> order.put(services.get(index).getId(), index));
        List<Services> result = entityManager
            .createQuery(
                "select distinct services from Services services left join fetch services.customers where services in :services",
                Services.class
            )
            .setParameter("services", services)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
