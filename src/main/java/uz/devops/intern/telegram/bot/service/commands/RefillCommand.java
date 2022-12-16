package uz.devops.intern.telegram.bot.service.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.feign.CustomerFeignClient;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;

//@Service
public class RefillCommand extends BotCommandAbs {

    private final String COMMAND = "/refill";

    private final StartCommand startCommand;
    private final CustomerTelegramService customerTelegramService;
    private final BotTokenService botTokenService;
    private final CustomerFeignClient customerFeign;

    protected RefillCommand(AdminFeign adminFeign, StartCommand startCommand, CustomerTelegramService customerTelegramService, BotTokenService botTokenService, CustomerFeignClient customerFeign) {
        super(adminFeign);
        this.startCommand = startCommand;
        this.customerTelegramService = customerTelegramService;
        this.botTokenService = botTokenService;
        this.customerFeign = customerFeign;
    }

    @Override
    public boolean executeCommand(Update update, Long userId) {
        ResponseDTO<CustomerTelegramDTO> response = customerTelegramService.findByTelegramId(userId);
        if(!response.getSuccess() && response.getResponseData() == null){
            wrongValue(update.getMessage().getFrom().getId(), "Siz ro'yxatdan o'tmagansiz!");
            log.warn("Not registered user send /refill command! User id: {} | Update: {}", userId, update);
            return false;
        }

        startCommand.startProcess(update.getMessage(), response.getResponseData().getId());
        return true;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
