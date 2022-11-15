package uz.devops.intern.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.PaymentHistory;
import uz.devops.intern.repository.PaymentHistoryRepository;
import uz.devops.intern.service.PaymentHistoryService;
import uz.devops.intern.service.dto.PaymentHistoryDTO;
import uz.devops.intern.service.mapper.PaymentHistoryMapper;

/**
 * Service Implementation for managing {@link PaymentHistory}.
 */
@Service
@Transactional
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

    private final Logger log = LoggerFactory.getLogger(PaymentHistoryServiceImpl.class);

    private final PaymentHistoryRepository paymentHistoryRepository;

    private final PaymentHistoryMapper paymentHistoryMapper;

    public PaymentHistoryServiceImpl(PaymentHistoryRepository paymentHistoryRepository, PaymentHistoryMapper paymentHistoryMapper) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentHistoryMapper = paymentHistoryMapper;
    }

    @Override
    public PaymentHistoryDTO save(PaymentHistoryDTO paymentHistoryDTO) {
        log.debug("Request to save PaymentHistory : {}", paymentHistoryDTO);
        PaymentHistory paymentHistory = paymentHistoryMapper.toEntity(paymentHistoryDTO);
        paymentHistory = paymentHistoryRepository.save(paymentHistory);
        return paymentHistoryMapper.toDto(paymentHistory);
    }

    @Override
    public PaymentHistoryDTO update(PaymentHistoryDTO paymentHistoryDTO) {
        log.debug("Request to update PaymentHistory : {}", paymentHistoryDTO);
        PaymentHistory paymentHistory = paymentHistoryMapper.toEntity(paymentHistoryDTO);
        paymentHistory = paymentHistoryRepository.save(paymentHistory);
        return paymentHistoryMapper.toDto(paymentHistory);
    }

    @Override
    public Optional<PaymentHistoryDTO> partialUpdate(PaymentHistoryDTO paymentHistoryDTO) {
        log.debug("Request to partially update PaymentHistory : {}", paymentHistoryDTO);

        return paymentHistoryRepository
            .findById(paymentHistoryDTO.getId())
            .map(existingPaymentHistory -> {
                paymentHistoryMapper.partialUpdate(existingPaymentHistory, paymentHistoryDTO);

                return existingPaymentHistory;
            })
            .map(paymentHistoryRepository::save)
            .map(paymentHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryDTO> findAll() {
        log.debug("Request to get all PaymentHistories");
        return paymentHistoryRepository
            .findAll()
            .stream()
            .map(paymentHistoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentHistoryDTO> findOne(Long id) {
        log.debug("Request to get PaymentHistory : {}", id);
        return paymentHistoryRepository.findById(id).map(paymentHistoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PaymentHistory : {}", id);
        paymentHistoryRepository.deleteById(id);
    }
}
