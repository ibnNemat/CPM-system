package uz.devops.intern.redis;

import org.springframework.data.redis.core.RedisHash;

@RedisHash(timeToLive = 60 * 20)
public class CallbackDataRedis {
    private Long id;
    private String callbackDate;

    public CallbackDataRedis(){}

    public CallbackDataRedis(Long id, String callbackDate) {
        this.id = id;
        this.callbackDate = callbackDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallbackDate() {
        return callbackDate;
    }

    public void setCallbackDate(String callbackDate) {
        this.callbackDate = callbackDate;
    }
}
