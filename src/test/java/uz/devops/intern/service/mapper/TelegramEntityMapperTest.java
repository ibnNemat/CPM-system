package uz.devops.intern.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TelegramEntityMapperTest {

    private TelegramEntityMapper telegramEntityMapper;

    @BeforeEach
    public void setUp() {
        telegramEntityMapper = new TelegramEntityMapperImpl();
    }
}
