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
import uz.devops.intern.domain.Customers;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CustomersRepositoryWithBagRelationshipsImpl implements CustomersRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Customers> fetchBagRelationships(Optional<Customers> customers) {
        return customers.map(this::fetchGroups).map(this::fetchServices);
    }

    @Override
    public Page<Customers> fetchBagRelationships(Page<Customers> customers) {
        return new PageImpl<>(fetchBagRelationships(customers.getContent()), customers.getPageable(), customers.getTotalElements());
    }

    @Override
    public List<Customers> fetchBagRelationships(List<Customers> customers) {
        return Optional.of(customers).map(this::fetchGroups).map(this::fetchServices).orElse(Collections.emptyList());
    }

    Customers fetchGroups(Customers result) {
        return entityManager
            .createQuery(
                "select customers from Customers customers left join fetch customers.groups where customers is :customers",
                Customers.class
            )
            .setParameter("customers", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Customers> fetchGroups(List<Customers> customers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, customers.size()).forEach(index -> order.put(customers.get(index).getId(), index));
        List<Customers> result = entityManager
            .createQuery(
                "select distinct customers from Customers customers left join fetch customers.groups where customers in :customers",
                Customers.class
            )
            .setParameter("customers", customers)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Customers fetchServices(Customers result) {
        return entityManager
            .createQuery(
                "select customers from Customers customers left join fetch customers.services where customers is :customers",
                Customers.class
            )
            .setParameter("customers", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Customers> fetchServices(List<Customers> customers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, customers.size()).forEach(index -> order.put(customers.get(index).getId(), index));
        List<Customers> result = entityManager
            .createQuery(
                "select distinct customers from Customers customers left join fetch customers.services where customers in :customers",
                Customers.class
            )
            .setParameter("customers", customers)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
