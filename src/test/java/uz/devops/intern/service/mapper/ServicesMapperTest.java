package uz.devops.intern.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicesMapperTest {

    private ServicesMapper servicesMapper;

    @BeforeEach
    public void setUp() {
        servicesMapper = new ServicesMapperImpl();
    }
}
