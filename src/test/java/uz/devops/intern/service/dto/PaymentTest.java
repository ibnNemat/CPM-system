package uz.devops.intern.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class PaymentTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentDTO.class);
        PaymentDTO paymentDTODTO1 = new PaymentDTO();
        paymentDTODTO1.setId(1L);
        PaymentDTO paymentDTODTO2 = new PaymentDTO();
        assertThat(paymentDTODTO1).isNotEqualTo(paymentDTODTO2);
        paymentDTODTO2.setId(paymentDTODTO1.getId());
        assertThat(paymentDTODTO1).isEqualTo(paymentDTODTO2);
        paymentDTODTO2.setId(2L);
        assertThat(paymentDTODTO1).isNotEqualTo(paymentDTODTO2);
        paymentDTODTO1.setId(null);
        assertThat(paymentDTODTO1).isNotEqualTo(paymentDTODTO2);
    }
}
