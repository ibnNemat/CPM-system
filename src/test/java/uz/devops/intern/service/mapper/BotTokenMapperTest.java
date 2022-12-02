package uz.devops.intern.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BotTokenMapperTest {

    private BotTokenMapper botTokenMapper;

    @BeforeEach
    public void setUp() {
        botTokenMapper = new BotTokenMapperImpl();
    }
}
