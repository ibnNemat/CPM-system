package uz.devops.intern.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class BotTokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BotToken.class);
        BotToken botToken1 = new BotToken();
        botToken1.setId(1L);
        BotToken botToken2 = new BotToken();
        botToken2.setId(botToken1.getId());
        assertThat(botToken1).isEqualTo(botToken2);
        botToken2.setId(2L);
        assertThat(botToken1).isNotEqualTo(botToken2);
        botToken1.setId(null);
        assertThat(botToken1).isNotEqualTo(botToken2);
    }
}
