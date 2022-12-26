package uz.devops.intern.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.repository.GroupsRepository;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.mapper.GroupMapper;
import uz.devops.intern.service.mapper.GroupsMapper;
import uz.devops.intern.service.utils.ContextHolderUtil;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static uz.devops.intern.constants.ResponseCodeConstants.NOT_FOUND;
import static uz.devops.intern.constants.ResponseCodeConstants.OK;

/**
 * Service Implementation for managing {@link Groups}.
 */
@Service
@RequiredArgsConstructor
public class GroupsServiceImpl implements GroupsService {
    private final Logger log = LoggerFactory.getLogger(GroupsServiceImpl.class);
    private final GroupsRepository groupsRepository;
    private final GroupsMapper groupsMapper;
    private final CustomersService customersService;
    private static final String ENTITY_NAME = "groups";
    @Override
    public List<GroupsDTO> findOnlyManagerGroups() {
        String ownerName = ContextHolderUtil.getUsernameFromContextHolder();
        if (ownerName == null){
            log.error("Error while getting groupManagerList: user principal not found!");
            return null;
        }
        List<Groups> groupsList = groupsRepository.findAllByGroupOwnerName(ownerName);
        return groupsList.stream()
            .map(GroupMapper::toDto)
            .toList();
    }

    @Override
    public List<Groups> getGroupsIncludeToPayment(List<Long> ids){
        String groupOwner = ContextHolderUtil.getUsernameFromContextHolder();
        return groupsRepository.findAllByIdIn(ids);
    }

    @Override
    public GroupsDTO findOneByTelegramId(Long telegramId) {
        if(telegramId == null)return null;
        Optional<Groups> groupsOptional = groupsRepository.findById(telegramId);

        if(groupsOptional.isEmpty())return null;

        return groupsOptional.map(GroupMapper::toDto).get();
    }

    @Override
    public ResponseDTO<GroupsDTO> findByName(String name) {
        if(name == null || name.trim().isEmpty()){
            return ResponseDTO.<GroupsDTO>builder()
                .success(false).message("Parameter \"Name\" is null or empty!").build();
        }

        Optional<Groups> groupsOptional = groupsRepository.findByName(name);
        return getGroupsDTOResponseDTO(groupsOptional);
    }

    @Override
    public ResponseDTO<GroupsDTO> findByCustomerId(Long customerId) {
        if(customerId == null){
            return ResponseDTO.<GroupsDTO>builder()
                .success(false).message("Parameter \"Customer id\" is null or empty!").build();
        }

        Optional<Groups> groupsOptional = groupsRepository.findByCustomerId(customerId);
        return getGroupsDTOResponseDTO(groupsOptional);
    }

    private ResponseDTO<GroupsDTO> getGroupsDTOResponseDTO(Optional<Groups> groupsOptional) {
        if(groupsOptional.isEmpty()){
            return ResponseDTO.<GroupsDTO>builder()
                .success(false).message("Data is not found!").build();
        }
        GroupsDTO group = groupsOptional.map(GroupMapper::toDto).get();
        return ResponseDTO.<GroupsDTO>builder()
            .success(true).message("OK").responseData(group).build();
    }

    @Override
    public int countAllByGroupsId(Set<Long> groupsId) {
        return groupsRepository.countAllByIdIn(groupsId);
    }

    @Override
    public ResponseDTO<GroupsDTO> addNewCustomerToGroup(String phoneNumberCustomer, Long groupId) {
        log.debug("Request to add new customer to existing group. Customer phone number: {} | GroupId: {}", phoneNumberCustomer, groupId);

        Optional<Groups> optionalGroups = groupsRepository.findById(groupId);
        if (optionalGroups.isEmpty()) return ResponseDTO.<GroupsDTO>builder().code(NOT_FOUND).success(false).message("group not found").build();
        Optional<Customers> optionalCustomers = customersService.findByPhoneNumber(phoneNumberCustomer);
        if (optionalCustomers.isEmpty()) return ResponseDTO.<GroupsDTO>builder().code(NOT_FOUND).success(false).message("customer not found").build();
        Groups group = optionalGroups.get();
        Customers customer = optionalCustomers.get();
        Set<Customers> customersSet = group.getCustomers();
        customersSet.add(customer);
        group.setCustomers(customersSet);
        return ResponseDTO.<GroupsDTO>builder().code(OK).success(true).message("successfully saved").build();
    }

    @Override
    public GroupsDTO save(GroupsDTO groupsDTO) {
        log.debug("Request to save Groups : {}", groupsDTO);
        String groupOwner = ContextHolderUtil.getUsernameFromContextHolder();

        if (groupOwner == null){
            log.error("Error while saving new group: user principal not found!");
            return null;
        }

        groupsDTO.setGroupOwnerName(groupOwner);
        Groups groups = groupsMapper.toEntity(groupsDTO);
        groups = groupsRepository.save(groups);
        return GroupMapper.toDto(groups);
    }

    @Override
    public GroupsDTO update(GroupsDTO groupsDTO) {
        log.debug("Request to update Groups : {}", groupsDTO);
        if (groupsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (!groupsRepository.existsById(groupsDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Groups groups = groupsMapper.toEntity(groupsDTO);
        groups = groupsRepository.save(groups);
        return GroupMapper.toDto(groups);
    }

    @Override
    public Optional<GroupsDTO> partialUpdate(GroupsDTO groupsDTO) {
        log.debug("Request to partially update Groups : {}", groupsDTO);
        if (groupsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (!groupsRepository.existsById(groupsDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        return groupsRepository
            .findById(groupsDTO.getId())
            .map(existingGroups -> {
                groupsMapper.partialUpdate(existingGroups, groupsDTO);

                return existingGroups;
            })
            .map(groupsRepository::save)
            .map(GroupMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Groups");
        return groupsRepository.findAll(pageable).map(GroupMapper::toDto);
    }

    @Override
    public Page<GroupsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return groupsRepository.findAllWithEagerRelationships(pageable).map(GroupMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GroupsDTO> findOne(Long id) {
        log.debug("Request to get Groups : {}", id);
        return groupsRepository.findById(id).map(GroupMapper::toDto);
    }

    @Override
    public Optional<Groups> findById(Long id) {
        log.debug("Request to get Groups : {}", id);
        return groupsRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Groups : {}", id);
        groupsRepository.deleteById(id);
    }
}
