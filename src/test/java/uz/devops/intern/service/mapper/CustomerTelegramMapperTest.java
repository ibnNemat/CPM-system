package uz.devops.intern.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerTelegramMapperTest {

    private CustomerTelegramMapper customerTelegramMapper;

    @BeforeEach
    public void setUp() {
        customerTelegramMapper = new CustomerTelegramMapperImpl();
    }
}
