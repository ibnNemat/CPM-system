package uz.devops.intern.redis;

import org.springframework.data.redis.core.RedisHash;
import uz.devops.intern.service.dto.ServicesDTO;


@RedisHash(timeToLive = 60 * 60 * 24 * 365)
public class ServicesRedisDTO {

    private Long id;
    private ServicesDTO servicesDTO;

    public ServicesRedisDTO(Long id, ServicesDTO servicesDTO) {
        this.id = id;
        this.servicesDTO = servicesDTO;
    }

    public Long getTelegramId() {
        return id;
    }

    public void setTelegramId(Long id) {
        this.id = id;
    }

    public ServicesDTO getServicesDTO() {
        return servicesDTO;
    }

    public void setServicesDTO(ServicesDTO servicesDTO) {
        this.servicesDTO = servicesDTO;
    }
}
