package uz.devops.intern.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface CustomerTelegramRedisRepository extends CrudRepository<CustomerTelegramRedis, Long> {

}
