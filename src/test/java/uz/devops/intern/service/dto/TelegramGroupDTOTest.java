package uz.devops.intern.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class TelegramGroupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelegramGroupDTO.class);
        TelegramGroupDTO telegramGroupDTO1 = new TelegramGroupDTO();
        telegramGroupDTO1.setId(1L);
        TelegramGroupDTO telegramGroupDTO2 = new TelegramGroupDTO();
        assertThat(telegramGroupDTO1).isNotEqualTo(telegramGroupDTO2);
        telegramGroupDTO2.setId(telegramGroupDTO1.getId());
        assertThat(telegramGroupDTO1).isEqualTo(telegramGroupDTO2);
        telegramGroupDTO2.setId(2L);
        assertThat(telegramGroupDTO1).isNotEqualTo(telegramGroupDTO2);
        telegramGroupDTO1.setId(null);
        assertThat(telegramGroupDTO1).isNotEqualTo(telegramGroupDTO2);
    }
}
