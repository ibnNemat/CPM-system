package uz.devops.intern.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.intern.web.rest.TestUtil;

class BotTokenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BotTokenDTO.class);
        BotTokenDTO botTokenDTO1 = new BotTokenDTO();
        botTokenDTO1.setId(1L);
        BotTokenDTO botTokenDTO2 = new BotTokenDTO();
        assertThat(botTokenDTO1).isNotEqualTo(botTokenDTO2);
        botTokenDTO2.setId(botTokenDTO1.getId());
        assertThat(botTokenDTO1).isEqualTo(botTokenDTO2);
        botTokenDTO2.setId(2L);
        assertThat(botTokenDTO1).isNotEqualTo(botTokenDTO2);
        botTokenDTO1.setId(null);
        assertThat(botTokenDTO1).isNotEqualTo(botTokenDTO2);
    }
}
