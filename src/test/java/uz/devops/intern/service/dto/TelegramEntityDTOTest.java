package uz.devops.intern.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class TelegramEntityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelegramEntityDTO.class);
        TelegramEntityDTO telegramEntityDTO1 = new TelegramEntityDTO();
        telegramEntityDTO1.setId(1L);
        TelegramEntityDTO telegramEntityDTO2 = new TelegramEntityDTO();
        assertThat(telegramEntityDTO1).isNotEqualTo(telegramEntityDTO2);
        telegramEntityDTO2.setId(telegramEntityDTO1.getId());
        assertThat(telegramEntityDTO1).isEqualTo(telegramEntityDTO2);
        telegramEntityDTO2.setId(2L);
        assertThat(telegramEntityDTO1).isNotEqualTo(telegramEntityDTO2);
        telegramEntityDTO1.setId(null);
        assertThat(telegramEntityDTO1).isNotEqualTo(telegramEntityDTO2);
    }
}
