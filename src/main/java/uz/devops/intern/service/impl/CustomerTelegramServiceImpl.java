package uz.devops.intern.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.mapper.CustomerTelegramMapper;

/**
 * Service Implementation for managing {@link CustomerTelegram}.
 */
@Service
@Transactional
public class CustomerTelegramServiceImpl implements CustomerTelegramService {

    private final Logger log = LoggerFactory.getLogger(CustomerTelegramServiceImpl.class);

    private final CustomerTelegramRepository customerTelegramRepository;

    private final CustomerTelegramMapper customerTelegramMapper;

    public CustomerTelegramServiceImpl(
        CustomerTelegramRepository customerTelegramRepository,
        CustomerTelegramMapper customerTelegramMapper
    ) {
        this.customerTelegramRepository = customerTelegramRepository;
        this.customerTelegramMapper = customerTelegramMapper;
    }

    @Override
    public CustomerTelegramDTO save(CustomerTelegramDTO customerTelegramDTO) {
        log.debug("Request to save CustomerTelegram : {}", customerTelegramDTO);
        CustomerTelegram customerTelegram = customerTelegramMapper.toEntity(customerTelegramDTO);
        customerTelegram = customerTelegramRepository.save(customerTelegram);
        return customerTelegramMapper.toDto(customerTelegram);
    }

    @Override
    public CustomerTelegramDTO update(CustomerTelegramDTO customerTelegramDTO) {
        log.debug("Request to update CustomerTelegram : {}", customerTelegramDTO);
        CustomerTelegram customerTelegram = customerTelegramMapper.toEntity(customerTelegramDTO);
        customerTelegram = customerTelegramRepository.save(customerTelegram);
        return customerTelegramMapper.toDto(customerTelegram);
    }

    @Override
    public Optional<CustomerTelegramDTO> partialUpdate(CustomerTelegramDTO customerTelegramDTO) {
        log.debug("Request to partially update CustomerTelegram : {}", customerTelegramDTO);

        return customerTelegramRepository
            .findById(customerTelegramDTO.getId())
            .map(existingCustomerTelegram -> {
                customerTelegramMapper.partialUpdate(existingCustomerTelegram, customerTelegramDTO);

                return existingCustomerTelegram;
            })
            .map(customerTelegramRepository::save)
            .map(customerTelegramMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerTelegramDTO> findAll() {
        log.debug("Request to get all CustomerTelegrams");
        return customerTelegramRepository
            .findAll()
            .stream()
            .map(customerTelegramMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerTelegramDTO> findOne(Long id) {
        log.debug("Request to get CustomerTelegram : {}", id);
        return customerTelegramRepository.findById(id).map(customerTelegramMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CustomerTelegram : {}", id);
        customerTelegramRepository.deleteById(id);
    }
}
