package uz.devops.intern.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupsMapperTest {

    private GroupsMapper groupsMapper;

    @BeforeEach
    public void setUp() {
        groupsMapper = new GroupsMapperImpl();
    }
}
