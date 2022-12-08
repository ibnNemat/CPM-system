package uz.devops.intern.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRedisRepository extends CrudRepository<ServicesRedisDTO, Long> {
}
