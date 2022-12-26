package uz.devops.intern.service.impl;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.constants.ResponseMessageConstants;
import uz.devops.intern.domain.*;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.manualMappper.CustomerTelegramsMapper;
import uz.devops.intern.service.mapper.*;
import static uz.devops.intern.constants.ResponseCodeConstants.NOT_FOUND;
import static uz.devops.intern.constants.ResponseCodeConstants.OK;
/**
 * Service Implementation for managing {@link CustomerTelegram}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerTelegramServiceImpl implements CustomerTelegramService {
    private final Logger log = LoggerFactory.getLogger(CustomerTelegramServiceImpl.class);
    private final CustomerTelegramRepository customerTelegramRepository;
    private final CustomerTelegramMapper customerTelegramMapper;

    @Autowired
    private CustomerTelegramsMapper customerTelegramsMapper;
    @Override
    public ResponseDTO<List<CustomerTelegramDTO>> getCustomerTgByChatId(Long chatId) {
        log.info("request to get CustomerTelegramDTOList by chatId: {}", chatId);
        if(chatId == null){
            return ResponseDTO.<List<CustomerTelegramDTO>>builder()
                .success(false).message("Parameter \"Chat id\" is null!").build();
        }

        List<CustomerTelegramDTO> customerTelegrams =
            customerTelegramRepository.getCustomersByChatId(chatId)
                .stream().map(customerTelegramMapper::toDto).toList();

        return ResponseDTO.<List<CustomerTelegramDTO>>builder()
            .success(true).message("OK").responseData(customerTelegrams).build();
    }

    @Override
    public Optional<CustomerTelegram> findByCustomer(Customers customer) {
        log.info("request to get CustomerTelegram by Customer: {}", customer);
        return customerTelegramRepository.findByCustomer(customer);
    }

    @Override
    public ResponseDTO<CustomerTelegramDTO> findByTelegramId(Long telegramId) {
        log.info("request to get responseDTO with CustomerTelegramDTO by telegramId: {}", telegramId);
        if(telegramId == null){
            return ResponseDTO.<CustomerTelegramDTO>builder()
                .success(false).message("Parameter \"Telegram id\" is null!").build();
        }
        Optional<CustomerTelegram> customerTelegramOptional =
            customerTelegramRepository.findByTelegramId(telegramId);

        if(customerTelegramOptional.isEmpty()){
            return ResponseDTO.<CustomerTelegramDTO>builder()
                .success(false).message("Data is not found!").build();
        }
        CustomerTelegramDTO  dto = customerTelegramOptional.map(customerTelegramMapper::toDto).orElse(null);
        return ResponseDTO.<CustomerTelegramDTO>builder()
            .success(true).message("OK").responseData(dto).build();
    }

    @Override
    public Optional<CustomerTelegram> findCustomerByTelegramId(Long telegramId){
        log.info("request to get CustomerTelegram by telegramId: {}", telegramId);
        return customerTelegramRepository.findByTelegramId(telegramId);
    }

    @Override
    public List<CustomerTelegramDTO> findByTelegramGroupTelegramId(Long telegramId) {
        log.info("request to get list of CustomerTelegram by telegramId: {}", telegramId);
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.getCustomersByChatId(telegramId);
        if (customerTelegramList == null)
            return null;

        return customerTelegramList.stream()
            .map(customerTelegramMapper::toDto)
            .toList();
    }

    @Override
    public List<CustomerTelegram> findByTelegramGroupChatId(Long chatId) {
        log.info("request to get list of CustomerTelegram by telegramId: {}", chatId);
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAllByTelegramGroupsChatId(chatId);
        if (customerTelegramList.size() == 0)
            return null;
        return customerTelegramList;
    }

    @Override
    public List<CustomerTelegramDTO> findAllByIsActiveTrue() {
        log.info("request to get all active telegram customers ");
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAllByIsActiveTrue();
        if (customerTelegramList == null) {
            return null;
        }
        return customerTelegramList.stream()
            .map(customerTelegramMapper::toDto)
            .toList();
    }

    @Override
    public List<CustomerTelegramDTO> findAll() {
        log.info("request to get all telegram customers");
        List<CustomerTelegram> customerTelegramList = customerTelegramRepository.findAll();
        if (customerTelegramList.size() == 0) {
            return null;
        }
        return customerTelegramList.stream()
            .map(customerTelegramMapper::toDto)
            .toList();
    }

    @Override
    public void setFalseToTelegramCustomerProfile(List<Long> ids) {
        log.info("request to set false to telegram customers profile");
        customerTelegramRepository.setFalseTelegramCustomersProfile(ids);
    }

    @Override
    public void deleteAllTelegramCustomersIsActiveFalse(List<Long> ids) {
        log.info("request to delete all telegram customers if isActive is false");
        customerTelegramRepository.deleteAllByIdInAndIsActiveFalse(ids);
    }

    @Override
    public ResponseDTO<CustomerTelegramDTO> getCustomerByTelegramId(Long telegramId) {
        log.info("request to get customerTelegram by telegramId: {}", telegramId);
        Optional<CustomerTelegram> customerTelegramOptional = customerTelegramRepository.findByTelegramId(telegramId);
        if (customerTelegramOptional.isEmpty()) return ResponseDTO.<CustomerTelegramDTO>builder().code(NOT_FOUND).success(false).message(ResponseMessageConstants.NOT_FOUND).build();
        return ResponseDTO.<CustomerTelegramDTO>builder()
            .code(OK)
            .message(ResponseMessageConstants.OK)
            .success(true)
            .responseData(customerTelegramMapper.toDto(customerTelegramOptional.get()))
            .build();
    }

    @Override
    public ResponseDTO<CustomerTelegramDTO> update(CustomerTelegramDTO dto) {
        log.info("request to change customerTelegram with param CustomerTelegramDTO: {}", dto);
        CustomerTelegram customerTelegram = customerTelegramMapper.toEntity(dto);
        customerTelegram = customerTelegramRepository.save(customerTelegram);
        return ResponseDTO.<CustomerTelegramDTO>builder()
            .code(OK)
            .message(ResponseMessageConstants.OK)
            .success(true)
            .responseData(customerTelegramsMapper.toDto(customerTelegram))
            .build();
    }
    @Override
    public ResponseDTO<CustomerTelegramDTO> findByBotTgId(Long botId) {
        log.info("request to get customerTelegram by telegram botId: {}", botId);
        Optional<CustomerTelegram>customerTelegramOptional = customerTelegramRepository.findByBot(botId);
        if (customerTelegramOptional.isEmpty()) return ResponseDTO.<CustomerTelegramDTO>builder().code(NOT_FOUND).success(false).message(ResponseMessageConstants.NOT_FOUND).build();
        return ResponseDTO.<CustomerTelegramDTO>builder()
            .code(OK)
            .message(ResponseMessageConstants.OK)
            .success(true)
            .responseData(customerTelegramsMapper.toDto(customerTelegramOptional.get()))
            .build();
    }
}
