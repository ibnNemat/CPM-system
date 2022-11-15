package uz.devops.intern.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class PaymentHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentHistory.class);
        PaymentHistory paymentHistory1 = new PaymentHistory();
        paymentHistory1.setId(1L);
        PaymentHistory paymentHistory2 = new PaymentHistory();
        paymentHistory2.setId(paymentHistory1.getId());
        assertThat(paymentHistory1).isEqualTo(paymentHistory2);
        paymentHistory2.setId(2L);
        assertThat(paymentHistory1).isNotEqualTo(paymentHistory2);
        paymentHistory1.setId(null);
        assertThat(paymentHistory1).isNotEqualTo(paymentHistory2);
    }
}
