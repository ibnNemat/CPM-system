package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.telegram.bot.service.BotCommand;
import uz.devops.intern.telegram.bot.service.BotStrategy;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;


import javax.annotation.PostConstruct;

import java.util.*;

@Service
public class AdminTgServiceImpl implements AdminTgService {

    private final Logger log = LoggerFactory.getLogger(AdminTgServiceImpl.class);
    private final List<BotStrategyAbs> commands;
    private final List<BotCommand> botCommands;
    private final CustomerTelegramService customerTelegramService;
    private final AdminFeign adminFeign;
    private HashMap<Integer, BotStrategy> map;
    private HashMap<String, BotCommand> commandsMap;

    public AdminTgServiceImpl(List<BotStrategyAbs> commands, List<BotCommand> botCommands, CustomerTelegramService customerTelegramService, AdminFeign adminFeign) {
        this.commands = commands;
        this.botCommands = botCommands;
        this.customerTelegramService = customerTelegramService;
        this.adminFeign = adminFeign;
    }

    @PostConstruct
    public void inject(){
        map = new HashMap<>();
        for (BotStrategy command: commands){
            map.put(command.getStep(), command);
        }

        commandsMap = new HashMap<>();
        for (BotCommand botCommand: botCommands){
            commandsMap.put(botCommand.getCommand(), botCommand);
        }
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
            String languageCode = response.getResponseData() != null?
                response.getResponseData().getLanguageCode():
                update.getMessage().getFrom().getLanguageCode();

            ResourceBundle resourceBundle = TelegramsUtil.getResourceBundleByUserLanguageCode(languageCode);

            String messageText = update.getMessage().hasText()?
                update.getMessage().getText():
                update.getMessage().hasContact()?
                    update.getMessage().getContact().getPhoneNumber(): null;

            if(messageText == null){
                String newMessage = resourceBundle.getString("bot.admin.send.only.message.or.contact");
                SendMessage sendMessage = TelegramsUtil.sendMessage(userId, newMessage);
                adminFeign.sendMessage(sendMessage);
                log.warn("User didn't send Message or Contact! User id: {} | Update: {}", userId, update);
                return false;
            }

            else if(messageText.contains("/")){
                log.info("\"/\" contains in Message!");
                BotCommand command = commandsMap.getOrDefault(messageText, commandsMap.get("/unknown"));
                return command.executeCommand(update, userId);
            }

            if(!response.getSuccess()){
                String newMessage = resourceBundle.getString("bot.admin.user.is.not.registered");
                SendMessage sendMessage = TelegramsUtil.sendMessage(userId, newMessage);
                adminFeign.sendMessage(sendMessage);
                log.warn("User is not found! User id: {} | Response: {}", userId, response);
                return false;
            }
            return logic(update, response);

        }
        else if(update.hasCallbackQuery() && response.getSuccess()){
            return logic(update, response);
        }

        return false;
    }

    private boolean logic(Update update, ResponseDTO<CustomerTelegramDTO> response){
        log.info("Into main business logic, Update: {} | Manager: {}", update, response.getMessage());
        CustomerTelegramDTO customerTgDTO = response.getResponseData();
        BotStrategy obj = map.get(customerTgDTO.getStep());
        boolean isSuccess = obj.execute(update, customerTgDTO);
        if(isSuccess){
            ResponseDTO<CustomerTelegramDTO> updatedCustomerTgDTO = customerTelegramService.update(customerTgDTO);
            log.info("User is successfully updated! Response: {}", updatedCustomerTgDTO);
        }
        return isSuccess;
    }
}
