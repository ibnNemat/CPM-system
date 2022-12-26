package uz.devops.intern.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentRequestParamDTO {
    private Long customerId;
    private Long serviceId;
    private Long groupId;
}
