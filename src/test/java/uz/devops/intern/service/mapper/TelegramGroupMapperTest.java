package uz.devops.intern.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TelegramGroupMapperTest {

    private TelegramGroupMapper telegramGroupMapper;

    @BeforeEach
    public void setUp() {
        telegramGroupMapper = new TelegramGroupMapperImpl();
    }
}
