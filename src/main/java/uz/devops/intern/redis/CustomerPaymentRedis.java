package uz.devops.intern.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import uz.devops.intern.service.dto.PaymentDTO;

import java.util.List;

@RedisHash(timeToLive = 60 * 60 * 24)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerPaymentRedis {
    private Long id;
    private List<PaymentDTO> paymentDTOList;
}
