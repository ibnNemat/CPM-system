package uz.devops.intern.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.TelegramGroup;
import uz.devops.intern.repository.TelegramGroupRepository;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.service.mapper.TelegramGroupMapper;

/**
 * Service Implementation for managing {@link TelegramGroup}.
 */
@Service
@Transactional
public class TelegramGroupServiceImpl implements TelegramGroupService {

    private final Logger log = LoggerFactory.getLogger(TelegramGroupServiceImpl.class);

    private final TelegramGroupRepository telegramGroupRepository;

    private final TelegramGroupMapper telegramGroupMapper;

    public TelegramGroupServiceImpl(TelegramGroupRepository telegramGroupRepository, TelegramGroupMapper telegramGroupMapper) {
        this.telegramGroupRepository = telegramGroupRepository;
        this.telegramGroupMapper = telegramGroupMapper;
    }

    @Override
    public TelegramGroupDTO save(TelegramGroupDTO telegramGroupDTO) {
        log.debug("Request to save TelegramGroup : {}", telegramGroupDTO);
        TelegramGroup telegramGroup = telegramGroupMapper.toEntity(telegramGroupDTO);
        telegramGroup = telegramGroupRepository.save(telegramGroup);
        return telegramGroupMapper.toDto(telegramGroup);
    }

    @Override
    public TelegramGroupDTO update(TelegramGroupDTO telegramGroupDTO) {
        log.debug("Request to update TelegramGroup : {}", telegramGroupDTO);
        TelegramGroup telegramGroup = telegramGroupMapper.toEntity(telegramGroupDTO);
        telegramGroup = telegramGroupRepository.save(telegramGroup);
        return telegramGroupMapper.toDto(telegramGroup);
    }

    @Override
    public Optional<TelegramGroupDTO> partialUpdate(TelegramGroupDTO telegramGroupDTO) {
        log.debug("Request to partially update TelegramGroup : {}", telegramGroupDTO);

        return telegramGroupRepository
            .findById(telegramGroupDTO.getId())
            .map(existingTelegramGroup -> {
                telegramGroupMapper.partialUpdate(existingTelegramGroup, telegramGroupDTO);

                return existingTelegramGroup;
            })
            .map(telegramGroupRepository::save)
            .map(telegramGroupMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelegramGroupDTO> findAll() {
        log.debug("Request to get all TelegramGroups");
        return telegramGroupRepository.findAll().stream().map(telegramGroupMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TelegramGroupDTO> findOne(Long id) {
        log.debug("Request to get TelegramGroup : {}", id);
        return telegramGroupRepository.findById(id).map(telegramGroupMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TelegramGroup : {}", id);
        telegramGroupRepository.deleteById(id);
    }

    @Override
    public List<TelegramGroupDTO> getThreeDTO(Long telegramGroupId) {
        if(telegramGroupId == null){
            return List.of();
        }
        return telegramGroupRepository.getThreeEntityWithUnion(telegramGroupId)
                .stream().map(telegramGroupMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TelegramGroupDTO findOneByChatId(Long chatId) {
        if(chatId != null){
            Optional<TelegramGroup> groupOptional = telegramGroupRepository.findByChatId(chatId);
            return groupOptional.map(telegramGroupMapper::toDto).orElse(null);
        }
        return null;
    }

    @Override
    public List<TelegramGroupDTO> findByOwnerId(Long managerId) {
        if(managerId != null){
            List<TelegramGroup> groups = telegramGroupRepository.findByCustomer(managerId);
            return groups.stream().map(telegramGroupMapper::toDto).collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public TelegramGroup getEntityByChatId(Long chatId) {
        if(chatId == null){
            return null;
        }
        return telegramGroupRepository.findByChatId(chatId).orElse(null);
    }
}
