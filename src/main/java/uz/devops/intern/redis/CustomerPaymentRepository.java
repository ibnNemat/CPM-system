package uz.devops.intern.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerPaymentRepository extends CrudRepository<CustomerPaymentRedis, Integer> {
}
