package uz.devops.intern.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.BotToken;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.mapper.BotTokenMapper;

/**
 * Service Implementation for managing {@link BotToken}.
 */
@Service
@Transactional
public class BotTokenServiceImpl implements BotTokenService {

    private final Logger log = LoggerFactory.getLogger(BotTokenServiceImpl.class);

    private final BotTokenRepository botTokenRepository;

    private final BotTokenMapper botTokenMapper;

    public BotTokenServiceImpl(BotTokenRepository botTokenRepository, BotTokenMapper botTokenMapper) {
        this.botTokenRepository = botTokenRepository;
        this.botTokenMapper = botTokenMapper;
    }

    @Override
    public BotTokenDTO save(BotTokenDTO botTokenDTO) {
        log.debug("Request to save BotToken : {}", botTokenDTO);
        BotToken botToken = botTokenMapper.toEntity(botTokenDTO);
        botToken = botTokenRepository.save(botToken);
        return botTokenMapper.toDto(botToken);
    }

    @Override
    public BotTokenDTO update(BotTokenDTO botTokenDTO) {
        log.debug("Request to update BotToken : {}", botTokenDTO);
        BotToken botToken = botTokenMapper.toEntity(botTokenDTO);
        botToken = botTokenRepository.save(botToken);
        return botTokenMapper.toDto(botToken);
    }

    @Override
    public Optional<BotTokenDTO> partialUpdate(BotTokenDTO botTokenDTO) {
        log.debug("Request to partially update BotToken : {}", botTokenDTO);

        return botTokenRepository
            .findById(botTokenDTO.getId())
            .map(existingBotToken -> {
                botTokenMapper.partialUpdate(existingBotToken, botTokenDTO);

                return existingBotToken;
            })
            .map(botTokenRepository::save)
            .map(botTokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotTokenDTO> findAll() {
        log.debug("Request to get all BotTokens");
        return botTokenRepository.findAll().stream().map(botTokenMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BotTokenDTO> findOne(Long id) {
        log.debug("Request to get BotToken : {}", id);
        return botTokenRepository.findById(id).map(botTokenMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BotToken : {}", id);
        botTokenRepository.deleteById(id);
    }

    @Override
    public BotTokenDTO findByChatId(Long chatId) {
        if(chatId == null)return null;
        return botTokenRepository.findByTelegramId(chatId).map(botTokenMapper::toDto).orElse(null);
    }

    @Override
    public ResponseDTO<BotTokenDTO> findByChatId(Long chatId, Boolean newMethod) {
        if(chatId == null){
            return ResponseDTO.<BotTokenDTO>builder()
                .success(false).message("Parameter \"Chat id\" is null!").build();
        }

        Optional<BotToken> botTokenOptional = botTokenRepository.findByTelegramId(chatId);
        if(botTokenOptional.isEmpty()){
            return ResponseDTO.<BotTokenDTO>builder()
                .success(false).message("Data is not found!").build();
        }

        BotTokenDTO dto = botTokenOptional.map(botTokenMapper::toDto).get();
        return ResponseDTO.<BotTokenDTO>builder()
            .success(true).message("OK").responseData(dto).build();
    }

    @Override
    public ResponseDTO<BotTokenDTO> findByToken(String token) {
        if(token == null){
            return ResponseDTO.<BotTokenDTO>builder()
                .success(false).message("Parameter \"Token\" is null!").build();
        }

        if(token.trim().isEmpty()){
            return ResponseDTO.<BotTokenDTO>builder()
                .success(false).message("Parameter \"Token\" is empty!").build();
        }

        Optional<BotToken> botTokenOptional = botTokenRepository.findByToken(token);
        if(botTokenOptional.isEmpty()){
            return ResponseDTO.<BotTokenDTO>builder()
                .success(false).message("Data is not found!").build();
        }

        BotTokenDTO dto = botTokenOptional.map(botTokenMapper::toDto).get();
        return ResponseDTO.<BotTokenDTO>builder()
            .success(true).message("OK").responseData(dto).build();
    }

    @Override
    public ResponseDTO<BotTokenDTO> findByManagerId(Long managerTgId) {
        if(managerTgId == null){
            return ResponseDTO.<BotTokenDTO>builder()
                .success(false).message("Parameter \"Manager telegram id\" is null!").build();
        }

        Optional<BotToken> botTokenOptional = botTokenRepository.findByManagerId(managerTgId);
        if(botTokenOptional.isEmpty()){
            return ResponseDTO.<BotTokenDTO>builder()
                .success(false).message("Data is not found!").build();
        }

        BotTokenDTO dto = botTokenOptional.map(botTokenMapper::toDto).get();
        return ResponseDTO.<BotTokenDTO>builder()
            .success(true).message("OK").responseData(dto).build();
    }


}
