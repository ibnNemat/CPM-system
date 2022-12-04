package uz.devops.intern.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class TelegramGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelegramGroup.class);
        TelegramGroup telegramGroup1 = new TelegramGroup();
        telegramGroup1.setId(1L);
        TelegramGroup telegramGroup2 = new TelegramGroup();
        telegramGroup2.setId(telegramGroup1.getId());
        assertThat(telegramGroup1).isEqualTo(telegramGroup2);
        telegramGroup2.setId(2L);
        assertThat(telegramGroup1).isNotEqualTo(telegramGroup2);
        telegramGroup1.setId(null);
        assertThat(telegramGroup1).isNotEqualTo(telegramGroup2);
    }
}
