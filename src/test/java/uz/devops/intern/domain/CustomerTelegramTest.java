package uz.devops.intern.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class CustomerTelegramTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerTelegram.class);
        CustomerTelegram customerTelegram1 = new CustomerTelegram();
        customerTelegram1.setId(1L);
        CustomerTelegram customerTelegram2 = new CustomerTelegram();
        customerTelegram2.setId(customerTelegram1.getId());
        assertThat(customerTelegram1).isEqualTo(customerTelegram2);
        customerTelegram2.setId(2L);
        assertThat(customerTelegram1).isNotEqualTo(customerTelegram2);
        customerTelegram1.setId(null);
        assertThat(customerTelegram1).isNotEqualTo(customerTelegram2);
    }
}
