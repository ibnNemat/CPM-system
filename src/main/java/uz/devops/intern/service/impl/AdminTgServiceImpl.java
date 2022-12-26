package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.annotations.AnnotationTestArea;
import uz.devops.intern.telegram.bot.dto.BotCommandDTO;
import uz.devops.intern.telegram.bot.dto.BotCommandsMenuDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotCommand;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;
import uz.devops.intern.telegram.bot.service.BotStrategy;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.*;

@Service
public class AdminTgServiceImpl implements AdminTgService {

    private final Logger log = LoggerFactory.getLogger(AdminTgServiceImpl.class);
    private final CustomerTelegramService customerTelegramService;
    private final AdminFeign adminFeign;
    @Autowired
    @Qualifier("strategy-objects-map")
    private Map<Integer, BotStrategyAbs> map;
    @Autowired
    @Qualifier("command-objects-map")
    private Map<String, BotCommandAbs> commandsMap;
    @Autowired
    private AnnotationTestArea annotationTestArea;

    public AdminTgServiceImpl(CustomerTelegramService customerTelegramService, AdminFeign adminFeign) {
        this.customerTelegramService = customerTelegramService;
        this.adminFeign = adminFeign;
    }

    @PostConstruct
    public void test() throws NoSuchMethodException {
        annotationTestArea.checkAnnotationInteger(1);
        annotationTestArea.checkAnnotationInteger("12");
//        annotationTestArea.checkAnnotationWithString("Something");
    }

    @PreDestroy
    public void destroy(){
        ResponseFromTelegram<Boolean> response = adminFeign.deleteMyCommands();
        log.debug("Commands are deleted! Response: {} ", response);
    }

    @Override
    public boolean main(Update update) {
        Long userId = update.hasMessage()?
            update.getMessage().getFrom().getId(): update.hasCallbackQuery()?
            update.getCallbackQuery().getFrom().getId(): null;

        if(userId == null){
            log.warn("There isn't object Message or CallbackQuery! Update: {}", update);
            return false;
        }

        ResponseDTO<CustomerTelegramDTO> response =
            customerTelegramService.getCustomerByTelegramId(userId);

        if(update.hasMessage()){
            if(!update.getMessage().getChatId().equals(update.getMessage().getFrom().getId())){
                return false;
            }

            String languageCode =
            update.getMessage().getFrom().getLanguageCode();

            ResourceBundle resourceBundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(languageCode);

            String messageText = update.getMessage().hasText()?
                update.getMessage().getText():
                update.getMessage().hasContact()?
                    update.getMessage().getContact().getPhoneNumber(): null;


            if(messageText.contains("/")){
                log.info("\"/\" contains in Message!");
                BotCommand command = commandsMap.getOrDefault(messageText, commandsMap.get("/unknown"));
                return command.executeCommand(update, userId);
            }

            if(response == null || !response.getSuccess()){
                String newMessage = resourceBundle.getString("bot.admin.user.is.not.registered");
                SendMessage sendMessage = TelegramsUtil.sendMessage(userId, newMessage);
                adminFeign.sendMessage(sendMessage);
                log.warn("User is not found! User id: {} | Response: {}", userId, response);
                return false;
            }
            return logic(update, response);
        }
        else if(update.hasCallbackQuery() && response.getSuccess()){
            if(!update.getCallbackQuery().getMessage().getChatId().equals(update.getCallbackQuery().getFrom().getId())){
                return false;
            }
            return logic(update, response);
        }

        return false;
    }

    private boolean logic(Update update, ResponseDTO<CustomerTelegramDTO> response){
        CustomerTelegramDTO customerTgDTO = response.getResponseData();
        BotStrategyAbs obj = map.get(customerTgDTO.getStep());
        boolean isSupported = checkSupportedType(update, obj);

        if(!isSupported){
            ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(customerTgDTO.getLanguageCode());
            obj.wrongValue(customerTgDTO.getTelegramId(),obj.getErrorMessage(bundle));
            log.warn("Manager send unsupported thing! Manager id: {} | Update: {} ",
                customerTgDTO.getTelegramId(), update);
            return false;
        }


        boolean isSuccess = obj.execute(update, customerTgDTO);
        if(isSuccess){
            ResponseDTO<CustomerTelegramDTO> updatedCustomerTgDTO = customerTelegramService.update(customerTgDTO);
            log.info("User is successfully updated! Response: {}", updatedCustomerTgDTO);
        }
        return isSuccess;
    }

    private boolean checkSupportedType(Update update, BotStrategy o){
        if(update.hasMessage() && o.messageOrCallback().equals(UpdateType.MESSAGE.name()) &&
            (update.getMessage().hasText() || update.getMessage().hasContact())){
            return true;
        }
        if(update.hasCallbackQuery() && o.messageOrCallback().equals(UpdateType.CALLBACK_QUERY.name())){
            return true;
        }
        if(o.messageOrCallback().equals(UpdateType.BOTH_OF_THEM.name())){
            return true;
        }
        return false;
    }
}
