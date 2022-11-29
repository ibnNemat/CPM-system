package uz.devops.intern.web.rest.cpm_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.IntegrationTest;
import uz.devops.intern.domain.*;
import uz.devops.intern.domain.enumeration.PeriodType;
import uz.devops.intern.repository.*;
import uz.devops.intern.service.dto.ServicesDTO;
import uz.devops.intern.service.mapper.ServiceMapper;
import uz.devops.intern.web.rest.TestUtil;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@IntegrationTest
@WithMockUser(authorities = {"ROLE_MANAGER"})
public class ServiceRestTest {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String ENTITY_API_URL = "/api/services";
    private static final String DEFAULT_SERVICE_NAME = "FOOD";
    private static final Double DEFAULT_PRICE = 250000D;
    private static final LocalDate DEFAULT_STARTED_PERIOD = LocalDate.of(2022, 12, 12);
    private static final PeriodType DEFAULT_PERIOD_TYPE = PeriodType.MONTH;

    private static final Integer DEFAULT_COUNT_PERIOD = 1;
    private final Set<Groups> groups = new HashSet<>();
    private Services service;
    private Organization organization;
    private Set<Customers> customersSet = new HashSet<>();

    @BeforeEach
    public void initMethod(){
        Authority authorityManager = new Authority();
        authorityManager.setName("ROLE_MANAGER");
        authorityRepository.saveAndFlush(authorityManager);
        organization = createEntityOrganizationAndSave();
        customersSet = createEntityCustomersSetAndSave();
        Groups groupEntity = createEntityGroupAndSave("3-sinf");
        groups.add(groupEntity);
        service = createServiceEntity(groups);
    }

    public Groups createEntityGroupAndSave(String groupName) {
        Groups group = new Groups()
            .name(groupName)
            .groupOwnerName("user")
            .organization(organization)
            .customers(customersSet);

        groupsRepository.saveAndFlush(group);
        return group;
    }

    public Set<Customers> createEntityCustomersSetAndSave(){
        String [] customerNames = {"Murod", "Sanjar"};
        Set<Customers> customers = new HashSet<>();
        for (String customerName : customerNames) {
            Customers customerEntity = new Customers()
                .username(customerName)
                .password("12345")
                .balance(2500000D)
                .phoneNumber("+998950645097");
            customers.add(customerEntity);
        }
        customersRepository.saveAllAndFlush(customers);

        return customers;
    }

    public Organization createEntityOrganizationAndSave(){
        organization = new Organization()
            .name("5 - maktab")
            .orgOwnerName("user");
        organizationRepository.saveAndFlush(organization);
        return organization;
    }

    public Services createServiceEntity(Set<Groups> groups){
        return new Services()
            .name(DEFAULT_SERVICE_NAME)
            .groups(groups)
            .price(DEFAULT_PRICE)
            .periodType(DEFAULT_PERIOD_TYPE)
            .countPeriod(DEFAULT_COUNT_PERIOD)
            .startedPeriod(DEFAULT_STARTED_PERIOD);
    }

    //Negative tests
    @Test
    @Transactional
    void createServicesWithExistingId() throws Exception {
        service.setId(1L);
        ServicesDTO servicesDTO = ServiceMapper.toDtoForSaveServiceMethod(service);
        int databaseSizeBeforeCreate = servicesRepository.findAll().size();

        mockMvc.perform(post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeCreate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "price", "startedPeriod", "periodType", "countPeriod"})
    @Transactional
    void checkRequiredFields(String fieldName) throws Exception {
        int databaseSizeBeforeTest = servicesRepository.findAll().size();
        switch(fieldName){
            case "name" -> service.setName(null);
            case "price" -> service.setPrice(null);
            case "periodType" -> service.setStartedPeriod(null);
            case "startedPeriod" -> service.setPeriodType(null);
            case "countPeriod" -> service.setCountPeriod(null);
        }

        ServicesDTO servicesDTO = ServiceMapper.toDtoForSaveServiceMethod(service);

        mockMvc.perform(post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(servicesDTO)))
            .andExpect(status().isBadRequest());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPermissionGetAllServices() throws Exception {
        servicesRepository.saveAndFlush(service);

        mockMvc.perform(get(ENTITY_API_URL))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void putNonExistingServices() throws Exception {
        System.out.println(servicesRepository.findAll());
        service.setId(15000L);

        ServicesDTO servicesDTO = ServiceMapper.toDtoForSaveServiceMethod(service);
        mockMvc
            .perform(
                put(ENTITY_API_URL + "/{id}", servicesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(servicesDTO))
            )
            .andExpect(status().isBadRequest());
    }

    // Positive
    @Test
    @Transactional
    void createServices() throws Exception {
        int databaseSizeBeforeCreate = servicesRepository.findAll().size();
        ServicesDTO servicesDTO = ServiceMapper.toDtoForSaveServiceMethod(service);

        mockMvc.perform(post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(servicesDTO)))
            .andDo(print())
            .andExpect(status().isOk());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeCreate + 1);
        Services testServices = servicesList.get(servicesList.size() - 1);
        assertThat(testServices.getName()).isEqualTo(DEFAULT_SERVICE_NAME);
        assertThat(testServices.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testServices.getStartedPeriod()).isEqualTo(DEFAULT_STARTED_PERIOD);
        assertThat(testServices.getPeriodType()).isEqualTo(DEFAULT_PERIOD_TYPE);
        assertThat(testServices.getCountPeriod()).isEqualTo(DEFAULT_COUNT_PERIOD);
    }

    @Test
    @Transactional
    void addNewGroupExistingServices() throws Exception {
        servicesRepository.saveAndFlush(service);
        int databaseSizeBeforeUpdate = servicesRepository.findAll().size();

        Services updatedServices = servicesRepository.findById(service.getId()).get();
        Set<Groups> serviceGroupsBefore = updatedServices.getGroups();
        int sizeGroups = serviceGroupsBefore.size();
        entityManager.detach(updatedServices);

        Groups newGroup = createEntityGroupAndSave("4 - sinf");
        groupsRepository.saveAndFlush(newGroup);
        serviceGroupsBefore.add(newGroup);
        updatedServices.setGroups(serviceGroupsBefore);
        ServicesDTO servicesDTO = ServiceMapper.toDtoForSaveServiceMethod(updatedServices);

        mockMvc.perform(
                put(ENTITY_API_URL + "/{id}", servicesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(servicesDTO))
            )
            .andExpect(status().isOk());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeUpdate);

        Services testServices = servicesList.get(servicesList.size() - 1);
        Set<Groups> serviceGroupsAfter = testServices.getGroups();
        Assertions.assertEquals(serviceGroupsAfter.size(), sizeGroups+1);
    }

    @Test
    @Transactional
    void getAllManagerServices() throws Exception {
       service = servicesRepository.saveAndFlush(service);
       mockMvc.perform(get("/api/manager-services"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$..id").value(hasItem(service.getId().intValue())))
            .andExpect(jsonPath("$..name").value(hasItem(DEFAULT_SERVICE_NAME)))
            .andExpect(jsonPath("$..price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$..startedPeriod").value(hasItem(DEFAULT_STARTED_PERIOD.toString())))
            .andExpect(jsonPath("$..periodType").value(hasItem(DEFAULT_PERIOD_TYPE.toString())))
            .andExpect(jsonPath("$..countPeriod").value(hasItem(DEFAULT_COUNT_PERIOD)));
    }

    @Test
    @Transactional
    void deleteService() throws Exception {
        servicesRepository.saveAndFlush(service);

        int databaseSizeBeforeDelete = servicesRepository.findAll().size();

        mockMvc.perform(
            delete(ENTITY_API_URL + "/{id}", service.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        List<Services> servicesList = servicesRepository.findAll();
        assertThat(servicesList).hasSize(databaseSizeBeforeDelete - 1);

        Optional<Services> deletedServiceOptional = servicesRepository.findById(service.getId());
        Assertions.assertTrue(deletedServiceOptional.isEmpty());
    }
}


