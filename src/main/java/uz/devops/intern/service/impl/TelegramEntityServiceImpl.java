package uz.devops.intern.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.TelegramEntity;
import uz.devops.intern.repository.TelegramEntityRepository;
import uz.devops.intern.service.TelegramEntityService;
import uz.devops.intern.service.dto.TelegramEntityDTO;
import uz.devops.intern.service.mapper.TelegramEntityMapper;

/**
 * Service Implementation for managing {@link TelegramEntity}.
 */
@Service
@Transactional
public class TelegramEntityServiceImpl implements TelegramEntityService {

    private final Logger log = LoggerFactory.getLogger(TelegramEntityServiceImpl.class);

    private final TelegramEntityRepository telegramEntityRepository;

    private final TelegramEntityMapper telegramEntityMapper;

    public TelegramEntityServiceImpl(TelegramEntityRepository telegramEntityRepository, TelegramEntityMapper telegramEntityMapper) {
        this.telegramEntityRepository = telegramEntityRepository;
        this.telegramEntityMapper = telegramEntityMapper;
    }

    @Override
    public TelegramEntityDTO save(TelegramEntityDTO telegramEntityDTO) {
        log.debug("Request to save TelegramEntity : {}", telegramEntityDTO);
        TelegramEntity telegramEntity = telegramEntityMapper.toEntity(telegramEntityDTO);
        telegramEntity = telegramEntityRepository.save(telegramEntity);
        return telegramEntityMapper.toDto(telegramEntity);
    }

    @Override
    public TelegramEntityDTO update(TelegramEntityDTO telegramEntityDTO) {
        log.debug("Request to update TelegramEntity : {}", telegramEntityDTO);
        TelegramEntity telegramEntity = telegramEntityMapper.toEntity(telegramEntityDTO);
        telegramEntity = telegramEntityRepository.save(telegramEntity);
        return telegramEntityMapper.toDto(telegramEntity);
    }

    @Override
    public Optional<TelegramEntityDTO> partialUpdate(TelegramEntityDTO telegramEntityDTO) {
        log.debug("Request to partially update TelegramEntity : {}", telegramEntityDTO);

        return telegramEntityRepository
            .findById(telegramEntityDTO.getId())
            .map(existingTelegramEntity -> {
                telegramEntityMapper.partialUpdate(existingTelegramEntity, telegramEntityDTO);

                return existingTelegramEntity;
            })
            .map(telegramEntityRepository::save)
            .map(telegramEntityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelegramEntityDTO> findAll() {
        log.debug("Request to get all TelegramEntities");
        return telegramEntityRepository
            .findAll()
            .stream()
            .map(telegramEntityMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TelegramEntityDTO> findOne(Long id) {
        log.debug("Request to get TelegramEntity : {}", id);
        return telegramEntityRepository.findById(id).map(telegramEntityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TelegramEntity : {}", id);
        telegramEntityRepository.deleteById(id);
    }
}
