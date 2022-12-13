package uz.devops.intern.redis;

import org.springframework.data.redis.core.RedisHash;
import uz.devops.intern.service.dto.PaymentDTO;

@RedisHash(timeToLive = 60 * 60 * 24)
public class CustomerPaymentRedis {
    private Integer id;
    private PaymentDTO paymentDTO;

    public CustomerPaymentRedis(){}
    public CustomerPaymentRedis(Integer id, PaymentDTO paymentDTO) {
        this.id = id;
        this.paymentDTO = paymentDTO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PaymentDTO getPaymentDTO() {
        return paymentDTO;
    }

    public void setPaymentDTO(PaymentDTO paymentDTO) {
        this.paymentDTO = paymentDTO;
    }
}
