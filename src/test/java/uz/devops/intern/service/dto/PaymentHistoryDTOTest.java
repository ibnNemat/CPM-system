package uz.devops.intern.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class PaymentHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentHistoryDTO.class);
        PaymentHistoryDTO paymentHistoryDTO1 = new PaymentHistoryDTO();
        paymentHistoryDTO1.setId(1L);
        PaymentHistoryDTO paymentHistoryDTO2 = new PaymentHistoryDTO();
        assertThat(paymentHistoryDTO1).isNotEqualTo(paymentHistoryDTO2);
        paymentHistoryDTO2.setId(paymentHistoryDTO1.getId());
        assertThat(paymentHistoryDTO1).isEqualTo(paymentHistoryDTO2);
        paymentHistoryDTO2.setId(2L);
        assertThat(paymentHistoryDTO1).isNotEqualTo(paymentHistoryDTO2);
        paymentHistoryDTO1.setId(null);
        assertThat(paymentHistoryDTO1).isNotEqualTo(paymentHistoryDTO2);
    }
}
