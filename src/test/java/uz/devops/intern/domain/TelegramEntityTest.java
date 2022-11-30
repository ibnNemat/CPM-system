package uz.devops.intern.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class TelegramEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelegramEntity.class);
        TelegramEntity telegramEntity1 = new TelegramEntity();
        telegramEntity1.setId(1L);
        TelegramEntity telegramEntity2 = new TelegramEntity();
        telegramEntity2.setId(telegramEntity1.getId());
        assertThat(telegramEntity1).isEqualTo(telegramEntity2);
        telegramEntity2.setId(2L);
        assertThat(telegramEntity1).isNotEqualTo(telegramEntity2);
        telegramEntity1.setId(null);
        assertThat(telegramEntity1).isNotEqualTo(telegramEntity2);
    }
}
