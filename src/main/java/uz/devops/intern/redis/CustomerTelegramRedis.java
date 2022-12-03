package uz.devops.intern.redis;

import org.springframework.data.redis.core.RedisHash;
import org.telegram.telegrambots.meta.api.objects.User;

//@NoArgsConstructor
//@AllArgsConstructor
@RedisHash(timeToLive = 60 * 60 * 24 * 365)
public class CustomerTelegramRedis {
    private Long id;
    private User telegramUser;
    public CustomerTelegramRedis(){}

    public CustomerTelegramRedis(Long id, User telegramUser) {
        this.id = id;
        this.telegramUser = telegramUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTelegramUser() {
        return telegramUser;
    }

    public void setTelegramUser(User telegramUser) {
        this.telegramUser = telegramUser;
    }
}
