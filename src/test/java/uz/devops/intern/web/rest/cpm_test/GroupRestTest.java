package uz.devops.intern.web.rest.cpm_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uz.devops.intern.IntegrationTest;
import uz.devops.intern.domain.Authority;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Organization;
import uz.devops.intern.repository.AuthorityRepository;
import uz.devops.intern.repository.CustomersRepository;
import uz.devops.intern.repository.GroupsRepository;
import uz.devops.intern.repository.OrganizationRepository;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.mapper.GroupMapper;
import uz.devops.intern.web.rest.TestUtil;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasItem;

@AutoConfigureMockMvc
@IntegrationTest
@WithMockUser(authorities = {"ROLE_MANAGER", "ROLE_CUSTOMER"})
public class GroupRestTest {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private MockMvc mockMvc;
    private static final String ENTITY_API_URL = "/api/groups";
    private static final String DEFAULT_NAME = "Group A";
    private static final String DEFAULT_GROUP_OWNER_NAME = "user";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Groups groupEntity;
    private Organization organizationEntity;
    private final Set<Customers> customers = new HashSet<>();
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void initMethod(){
        Authority authorityManager = new Authority();
        authorityManager.setName("ROLE_MANAGER");
        authorityRepository.saveAndFlush(authorityManager);

        organizationEntity = createEntityOrganizationAndSave();

        String [] customerNames = {"Murod", "Sanjar"};

        for (String customerName : customerNames) {
            Customers customerEntity = createEntityCustomers(customerName);
            customers.add(customerEntity);
        }
        customersRepository.saveAllAndFlush(customers);
        groupEntity = createEntityGroup(DEFAULT_NAME, organizationEntity, customers);
    }

    public Groups createEntityGroup(String groupName, Organization organization, Set<Customers> customersSet) {
        return new Groups()
            .name(groupName)
            .groupOwnerName(DEFAULT_GROUP_OWNER_NAME)
            .organization(organization)
            .customers(customersSet);
    }
    public Customers createEntityCustomers(String customerName){
         return new Customers()
                .username(customerName)
                .password("12345")
                .balance(2500000D)
                .phoneNumber("+998950645097");
    }

    public Organization createEntityOrganizationAndSave(){
        Organization newOrganization = new Organization()
            .name("3-MTM")
            .orgOwnerName(DEFAULT_GROUP_OWNER_NAME);
        organizationEntity = organizationRepository.saveAndFlush(newOrganization);
        return organizationEntity;
    }
    // Negative tests
    @Test
    @Transactional
    @Order(1)
    void createGroupsWithExistingId() throws Exception {
        groupEntity.setId(1L);
        GroupsDTO groupsDTO = GroupMapper.toDto(groupEntity);

        int databaseSizeBeforeCreate = groupsRepository.findAll().size();
        mockMvc.perform(post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(groupsDTO)))
            .andExpect(status().isBadRequest());

        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @Order(2)
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupsRepository.findAll().size();
        groupEntity.setName(null);

        GroupsDTO groupsDTO = GroupMapper.toDto(groupEntity);

        mockMvc.perform(post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(groupsDTO)))
            .andExpect(status().isBadRequest());

        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @Order(3)
    void checkPermissionWhileGettingGroups() throws Exception {
        Set<Groups> groups = new HashSet<>();
        String [] groupNames = {"Group A", "Group B", "Group C"};
        for (String groupName : groupNames) {
            Groups newGroup = createEntityGroup(groupName, organizationEntity, customers);
            groups.add(newGroup);
        }

        groupsRepository.saveAllAndFlush(groups);

        mockMvc.perform(get(ENTITY_API_URL))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @Order(4)
    void putNonExistingGroups() throws Exception {
        customersRepository.saveAllAndFlush(customers);
        groupsRepository.saveAndFlush(groupEntity);

        int databaseSizeBeforeUpdate = groupsRepository.findAll().size();
        entityManager.detach(groupEntity);

        groupEntity.setId(15000L);
        GroupsDTO groupsDTO = GroupMapper.toDto(groupEntity);

        mockMvc.perform(
                put(ENTITY_API_URL + "/{id}", groupsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(groupsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeUpdate);
    }

    // Positive tests
    @Test
    @Transactional
    @Order(5)
    void createGroups() throws Exception {
        int databaseSizeBeforeCreate = groupsRepository.findAll().size();
        GroupsDTO groupsDTO = GroupMapper.toDto(groupEntity);

        mockMvc.perform(post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(groupsDTO)))
            .andDo(print())
            .andExpect(status().isCreated());

        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeCreate + 1);
        Groups testGroups = groupsList.get(groupsList.size() - 1);
        assertThat(testGroups.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testGroups.getGroupOwnerName()).isEqualTo(DEFAULT_GROUP_OWNER_NAME);
    }

    @Test
    @Transactional
    @Order(6)
    void addCustomerToGroups() throws Exception {
        customersRepository.saveAllAndFlush(customers);
        groupsRepository.saveAndFlush(groupEntity);
        int databaseSizeBeforeUpdate = groupsRepository.findAll().size();
        Optional<Groups> updatedGroupOptional = groupsRepository.findById(groupEntity.getId());

        Assertions.assertTrue(updatedGroupOptional.isPresent());
        Groups updatedGroup = updatedGroupOptional.get();
        Set<Customers> updatedGroupCustomers = updatedGroup.getCustomers();
        int sizeCustomersBefore = updatedGroupCustomers.size();

        Customers newCustomer = createEntityCustomers("Dovud");
        customersRepository.saveAndFlush(newCustomer);
        updatedGroupCustomers.add(newCustomer);
        updatedGroup.setName("anotherGroup");
        updatedGroup.setCustomers(updatedGroupCustomers);

        GroupsDTO groupsDTO = GroupMapper.toDto(updatedGroup);
        mockMvc
            .perform(
                put(ENTITY_API_URL + "/{id}", groupsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(groupsDTO))
            )
            .andExpect(status().isOk());

        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeUpdate);
        Groups testGroups = groupsList.get(groupsList.size() - 1);
        int sizeCustomersAfter = testGroups.getCustomers().size();
        assertThat(testGroups.getName()).isEqualTo("anotherGroup");
        Assertions.assertEquals(sizeCustomersAfter, sizeCustomersBefore+1);
    }

    @Test
    @Transactional
    @Order(7)
    void getAllManagerGroups() throws Exception {
        Set<Groups> groups = new HashSet<>();
        String [] groupNames = {"Group A", "Group B", "Group C"};
        for (String groupName : groupNames) {
            Groups newGroup = createEntityGroup(groupName, organizationEntity, customers);
            groups.add(newGroup);
        }

        List<Groups> groupsList = groupsRepository.saveAllAndFlush(groups);

        String body = mockMvc.perform(get("/api/manager-groups"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupsList.get(0).getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].groupOwnerName").value(hasItem(DEFAULT_GROUP_OWNER_NAME)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        List<Groups> groupsListResponse = objectMapper.readValue(body, new TypeReference<List<Groups>>(){});
        Assertions.assertEquals(3, groupsListResponse.size());
    }

    @Test
    @Transactional
    @Order(8)
    void deleteGroup() throws Exception {
        groupsRepository.saveAndFlush(groupEntity);

        int databaseSizeBeforeDelete = groupsRepository.findAll().size();
        mockMvc.perform(delete(ENTITY_API_URL + "/{id}", groupEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNoContent());

        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeDelete - 1);

        Optional<Groups> groupsOptional = groupsRepository.findById(groupEntity.getId());
        Assertions.assertTrue(groupsOptional.isEmpty());
    }
}
