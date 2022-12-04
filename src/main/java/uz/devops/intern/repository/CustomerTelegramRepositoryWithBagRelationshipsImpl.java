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
import uz.devops.intern.domain.CustomerTelegram;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CustomerTelegramRepositoryWithBagRelationshipsImpl implements CustomerTelegramRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CustomerTelegram> fetchBagRelationships(Optional<CustomerTelegram> customerTelegram) {
        return customerTelegram.map(this::fetchTelegramGroups);
    }

    @Override
    public Page<CustomerTelegram> fetchBagRelationships(Page<CustomerTelegram> customerTelegrams) {
        return new PageImpl<>(
            fetchBagRelationships(customerTelegrams.getContent()),
            customerTelegrams.getPageable(),
            customerTelegrams.getTotalElements()
        );
    }

    @Override
    public List<CustomerTelegram> fetchBagRelationships(List<CustomerTelegram> customerTelegrams) {
        return Optional.of(customerTelegrams).map(this::fetchTelegramGroups).orElse(Collections.emptyList());
    }

    CustomerTelegram fetchTelegramGroups(CustomerTelegram result) {
        return entityManager
            .createQuery(
                "select customerTelegram from CustomerTelegram customerTelegram left join fetch customerTelegram.telegramGroups where customerTelegram is :customerTelegram",
                CustomerTelegram.class
            )
            .setParameter("customerTelegram", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<CustomerTelegram> fetchTelegramGroups(List<CustomerTelegram> customerTelegrams) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, customerTelegrams.size()).forEach(index -> order.put(customerTelegrams.get(index).getId(), index));
        List<CustomerTelegram> result = entityManager
            .createQuery(
                "select distinct customerTelegram from CustomerTelegram customerTelegram left join fetch customerTelegram.telegramGroups where customerTelegram in :customerTelegrams",
                CustomerTelegram.class
            )
            .setParameter("customerTelegrams", customerTelegrams)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
