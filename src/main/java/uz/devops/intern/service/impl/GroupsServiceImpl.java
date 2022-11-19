package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.repository.GroupsRepository;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.mapper.GroupMapper;
import uz.devops.intern.service.mapper.GroupsMapper;
import uz.devops.intern.service.utils.ContextHolderUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Groups}.
 */
@Service
public class GroupsServiceImpl implements GroupsService {
    private final EntityManager entityManager;
    private final Logger log = LoggerFactory.getLogger(GroupsServiceImpl.class);
    private final GroupsRepository groupsRepository;
    private final GroupsMapper groupsMapper;
    public GroupsServiceImpl(EntityManager entityManager, GroupsRepository groupsRepository, GroupsMapper groupsMapper) {
        this.entityManager = entityManager;
        this.groupsRepository = groupsRepository;
        this.groupsMapper = groupsMapper;
    }

    @Override
    public List<GroupsDTO> findOnlyManagerGroups() {
        String ownerName = ContextHolderUtil.getUsernameFromContextHolder();
        if (ownerName == null){
            log.error("Error while getting groupManagerList: user principal not found!");
            return null;
        }
        List<Groups> groupsList = groupsRepository.findAllByGroupOwnerName(ownerName);
        return groupsList.stream()
            .map(groupsMapper::toDto)
            .toList();
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
        System.out.println(groups);
        return GroupMapper.ForSavingGroup(groups);
    }

    @Override
    public GroupsDTO update(GroupsDTO groupsDTO) {
        log.debug("Request to update Groups : {}", groupsDTO);
        Groups groups = groupsMapper.toEntity(groupsDTO);
        groups = groupsRepository.save(groups);
        return groupsMapper.toDto(groups);
    }

    @Override
    public Optional<GroupsDTO> partialUpdate(GroupsDTO groupsDTO) {
        log.debug("Request to partially update Groups : {}", groupsDTO);

        return groupsRepository
            .findById(groupsDTO.getId())
            .map(existingGroups -> {
                groupsMapper.partialUpdate(existingGroups, groupsDTO);

                return existingGroups;
            })
            .map(groupsRepository::save)
            .map(groupsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Groups");
        return groupsRepository.findAll(pageable).map(groupsMapper::toDto);
    }

    @Override
    public Page<GroupsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return groupsRepository.findAllWithEagerRelationships(pageable).map(groupsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GroupsDTO> findOne(Long id) {
        log.debug("Request to get Groups : {}", id);
        return groupsRepository.findOneWithEagerRelationships(id).map(groupsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Groups : {}", id);
        groupsRepository.deleteById(id);
    }
}
