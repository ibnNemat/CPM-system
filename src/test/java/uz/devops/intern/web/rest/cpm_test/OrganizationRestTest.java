package uz.devops.intern.web.rest.cpm_test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uz.devops.intern.IntegrationTest;
import uz.devops.intern.domain.Authority;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.repository.AuthorityRepository;
import uz.devops.intern.repository.OrganizationRepository;
import uz.devops.intern.service.dto.OrganizationDTO;
import uz.devops.intern.service.mapper.OrganizationsMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@IntegrationTest
@WithMockUser(authorities = {"ROLE_MANAGER"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrganizationRestTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    private static final String ENTITY_API_URL = "/api/organizations";
    private static final String DEFAULT_NAME = "org-name1";
    private static final String DEFAULT_ORG_OWNER_NAME = "user";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Organization organization;

    @BeforeEach
    public void initMethod(){
        Authority authorityManager = new Authority();
        authorityManager.setName("ROLE_MANAGER");
        authorityRepository.saveAndFlush(authorityManager);

        organization = createEntity(DEFAULT_NAME);
    }

    public Organization createEntity(String orgName){
        return new Organization()
            .name(orgName)
            .orgOwnerName(DEFAULT_ORG_OWNER_NAME);
    }

    // Negative tests
    @Test
    @Transactional
    @Order(1)
    void checkNameIsRequiredWhileSavingEntity() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().size();
        organization.setName(null);

        OrganizationDTO organizationDTO = OrganizationsMapper.toDtoWithGroups(organization);

        mockMvc.perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(organizationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @Order(2)
    void createOrganizationWithExistingId() throws Exception {
        organization.setId(1L);
        OrganizationDTO organizationDTO = OrganizationsMapper.toDtoWithGroups(organization);

        int databaseSizeBeforeCreate = organizationRepository.findAll().size();
        byte[] body = objectMapper.writeValueAsBytes(organizationDTO);

        mockMvc.perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @Order(3)
    void checkPermissionWhileGettingOrganizations() throws Exception {
        String [] orgNames = {"org-name1", "org-name2", "org-name3"};
        Set<Organization> organizations = new HashSet<>();
        for (String orgName : orgNames) {
            Organization newOrganization = createEntity(orgName);
            organizations.add(newOrganization);
        }
        organizationRepository.saveAllAndFlush(organizations);

        mockMvc.perform(get("/api/organizations"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    // Positive tests
    @Test
    @Transactional
    @Order(4)
    void createOrganizationTest() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().size();

        OrganizationDTO organizationDTO = OrganizationsMapper.toDtoWithGroups(organization);
        mockMvc.perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(organizationDTO))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList)
            .isNotNull()
            .hasSize(databaseSizeBeforeCreate + 1);

        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        Assertions.assertEquals(testOrganization.getName(), DEFAULT_NAME);
        Assertions.assertEquals(testOrganization.getOrgOwnerName(), "user");
    }

    @Test
    @Transactional
    @Order(5)
    void getAllOrganizations() throws Exception {
        String [] orgNames = {"org-name1", "org-name2", "org-name3"};
        Set<Organization> organizations = new HashSet<>();
        for (String orgName : orgNames) {
            Organization newOrganization = createEntity(orgName);
            organizations.add(newOrganization);
        }
        List<Organization> organizationList = organizationRepository.saveAllAndFlush(organizations);

        mockMvc.perform(get("/api/manager-organizations"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organizationList.get(0).getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].orgOwnerName").value(hasItem("user")));
    }

    @Test
    @Transactional
    @Order(6)
    void deleteOrganization() throws Exception {
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeDelete = organizationRepository.findAll().size();
        mockMvc.perform(delete("/api/organizations/{id}", organization.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNoContent());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeDelete - 1);

        Optional<Organization> optionalOrganization = organizationRepository.findById(organization.getId());
        Assertions.assertTrue(optionalOrganization.isEmpty());
    }
}
