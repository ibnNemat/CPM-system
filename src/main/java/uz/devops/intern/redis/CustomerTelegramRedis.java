package devops.intern.cpmtelegrambot.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.telegram.telegrambots.meta.api.objects.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
@RedisHash(timeToLive = 60 * 60 * 24)
public class CustomerTelegramRedis {
    private Long Id;
    private User telegramUser;
}
