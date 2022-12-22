package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.User;
import uz.devops.intern.repository.CustomersRepository;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.mapper.CustomerMapper;
import uz.devops.intern.service.mapper.CustomersMapper;
import uz.devops.intern.service.utils.ContextHolderUtil;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

import static uz.devops.intern.constants.ResponseCodeConstants.OK;
import static uz.devops.intern.constants.ResponseMessageConstants.*;

import java.util.Optional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Service Implementation for managing {@link Customers}.
 */
@Service
@Transactional
public class CustomersServiceImpl implements CustomersService {
    private final Logger log = LoggerFactory.getLogger(CustomersServiceImpl.class);
    private final CustomersRepository customersRepository;
    private final CustomersMapper customersMapper;
    public CustomersServiceImpl(CustomersRepository customersRepository, CustomersMapper customersMapper) {
        this.customersRepository = customersRepository;
        this.customersMapper = customersMapper;
    }

    @Override
    public CustomersDTO findByIdBalanceGreaterThen(Long customerId, Double account) {
        Optional<Customers> optionalCustomers = customersRepository.findByIdAndBalanceGreaterThan(customerId, account);
        if (optionalCustomers.isPresent()){
            return optionalCustomers.map(CustomerMapper::toDtoWithNoUser).get();
        }
        return null;
    }

    @Override
    public CustomersDTO save(CustomersDTO customersDTO) {
        log.debug("Request to save Customers : {}", customersDTO);
        String username = ContextHolderUtil.getUsernameFromContextHolder();
        if (username == null){
            log.error("Error while saving customer: user principal not found!");
            return null;
        }
        customersDTO.setUsername(username);
        Customers customers = customersMapper.toEntity(customersDTO);
        customers = customersRepository.save(customers);
        return customersMapper.toDto(customers);
    }

    @Override
    public ResponseDTO<CustomersDTO> update(CustomersDTO customersDTO) {
        if (customersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (!customersRepository.existsById(customersDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        log.debug("Request to update Customers : {}", customersDTO);
        Customers customers = customersMapper.toEntity(customersDTO);
        customers = customersRepository.save(customers);
        CustomersDTO responseCustomersDTO = customersMapper.toDto(customers);
        return new ResponseDTO<>(OK, SAVED, true, responseCustomersDTO);
    }

    @Override
    public Boolean existsByUser(User user){
        log.info("request to check customer exists by user: {}", user);
        return customersRepository.existsByUser(user);
    }
    @Override
    public Optional<CustomersDTO> partialUpdate(CustomersDTO customersDTO) {
        log.debug("Request to partially update Customers : {}", customersDTO);

        return customersRepository
            .findById(customersDTO.getId())
            .map(existingCustomers -> {
                customersMapper.partialUpdate(existingCustomers, customersDTO);

                return existingCustomers;
            })
            .map(customersRepository::save)
            .map(customersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomersDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        return customersRepository.findAll(pageable).map(customersMapper::toDto);
    }

//    public Page<CustomersDTO> findAllWithEagerRelationships(Pageable pageable) {
//        return customersRepository.findAllWithEagerRelationships(pageable).map(customersMapper::toDto);
//    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomersDTO> findOne(Long id) {
        log.debug("Request to get Customers : {}", id);
        return customersRepository.findById(id).map(CustomerMapper::toDtoWithAll);
    }

    @Override
    public Optional<Customers> findByPhoneNumber(String phoneNumber) {
         return customersRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public void decreaseCustomerBalance(Double paidMoney, Long customerId){
        customersRepository.decreaseCustomerBalance(paidMoney, customerId);
    }

    @Override
    public Optional<Customers> findByUsername(String username) {
        return customersRepository.findByUsername(username);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Customers : {}", id);
        customersRepository.deleteById(id);
    }

    @Override
    public Page<CustomersDTO> findAllWithEagerRelationships(Pageable pageable) {
        return null;
    }

    @Override
    public void replenishCustomerBalance(Double money, Long customerId) {
        customersRepository.replenishCustomerBalance(money, customerId);
    }

    @Override
    public void updateCustomerPhoneNumber(String phoneNumber, Long customerId) {
        customersRepository.updatePhoneNumber(phoneNumber, customerId);
    }
}
