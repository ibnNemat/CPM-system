package uz.devops.intern.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class CustomerTelegramDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerTelegramDTO.class);
        CustomerTelegramDTO customerTelegramDTO1 = new CustomerTelegramDTO();
        customerTelegramDTO1.setId(1L);
        CustomerTelegramDTO customerTelegramDTO2 = new CustomerTelegramDTO();
        assertThat(customerTelegramDTO1).isNotEqualTo(customerTelegramDTO2);
        customerTelegramDTO2.setId(customerTelegramDTO1.getId());
        assertThat(customerTelegramDTO1).isEqualTo(customerTelegramDTO2);
        customerTelegramDTO2.setId(2L);
        assertThat(customerTelegramDTO1).isNotEqualTo(customerTelegramDTO2);
        customerTelegramDTO1.setId(null);
        assertThat(customerTelegramDTO1).isNotEqualTo(customerTelegramDTO2);
    }
}
