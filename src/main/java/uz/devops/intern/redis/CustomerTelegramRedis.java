package uz.devops.intern.redis;

import org.springframework.data.redis.core.RedisHash;
import org.telegram.telegrambots.meta.api.objects.User;

//@NoArgsConstructor
//@AllArgsConstructor
@RedisHash(timeToLive = 60 * 60 * 24)
public class CustomerTelegramRedis {
    private Long Id;
    private User telegramUser;

    public CustomerTelegramRedis(Long id, User telegramUser) {
        Id = id;
        this.telegramUser = telegramUser;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public User getTelegramUser() {
        return telegramUser;
    }

    public void setTelegramUser(User telegramUser) {
        this.telegramUser = telegramUser;
    }
}
