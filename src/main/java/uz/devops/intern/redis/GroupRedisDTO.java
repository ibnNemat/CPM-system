package uz.devops.intern.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import uz.devops.intern.service.dto.GroupsDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(timeToLive = 60 * 60 * 24)
public class GroupRedisDTO {

    private Long id;

    private GroupsDTO groupsDTO;
}
