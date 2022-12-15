package uz.devops.intern.telegram.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Service
public class BotStrategyImpl {
    @Autowired
    private List<BotStrategyAbs> commands;
    @Autowired
    private List<BotCommand> botCommands;
    private HashMap<Integer, BotStrategy> map;
    private final CustomerTelegramService customerTelegramService;

    public BotStrategyImpl(CustomerTelegramService customerTelegramService) {
        this.customerTelegramService = customerTelegramService;
    }

    @PostConstruct
    public void inject(){
        map = new HashMap<>();
        for (BotStrategy command: commands){
            map.put(command.getStep(), command);
        }
    }

    public boolean execute(Update update){
        Long userId = update.hasMessage()?
            update.getMessage().getFrom().getId(): update.hasCallbackQuery()?
            update.getCallbackQuery().getFrom().getId(): null;

        if(userId == null)return false;

        ResponseDTO<CustomerTelegramDTO> response =
            customerTelegramService.getCustomerByTelegramId(userId);

        if(!response.getSuccess()){
            return false;
        }

        CustomerTelegramDTO customerTgDTO = response.getResponseData();
        BotStrategy obj = map.get(customerTgDTO.getStep());
        boolean isSuccess = obj.execute(update, customerTgDTO);
        if(isSuccess){
            ResponseDTO<CustomerTelegramDTO> updatedCustomerTgDTO = customerTelegramService.update(customerTgDTO);
            System.out.println(updatedCustomerTgDTO);
        }
        return isSuccess;
    }
}
