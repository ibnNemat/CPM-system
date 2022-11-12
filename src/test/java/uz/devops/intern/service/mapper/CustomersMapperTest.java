package uz.devops.intern.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomersMapperTest {

    private CustomersMapper customersMapper;

    @BeforeEach
    public void setUp() {
        customersMapper = new CustomersMapperImpl();
    }
}
