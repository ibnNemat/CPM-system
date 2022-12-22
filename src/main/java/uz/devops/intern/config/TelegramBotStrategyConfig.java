package uz.devops.intern.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.BotCommandDTO;
import uz.devops.intern.telegram.bot.dto.BotCommandsMenuDTO;
import uz.devops.intern.telegram.bot.service.BotCommand;
import uz.devops.intern.telegram.bot.service.BotCommandAbs;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class TelegramBotStrategyConfig {
    private final Logger log = LoggerFactory.getLogger(TelegramBotStrategyConfig.class);
    private final List<BotStrategyAbs> strategyObjs;
    private final List<BotCommandAbs> commands;
    private final AdminFeign adminFeign;

    @Bean("strategy-objects-map")
    public Map<Integer, BotStrategyAbs> botStrategyMap(){
        Map<Integer, BotStrategyAbs> strategyAbsHashMap = new HashMap<>();
        for(BotStrategyAbs obj: strategyObjs){
            strategyAbsHashMap.put(obj.getStep(), obj);
        }
        return strategyAbsHashMap;
    }

    @Bean("command-objects-map")
    public Map<String, BotCommandAbs> botCommandsInjection(){
        Map<String, BotCommandAbs> commandsMap = new HashMap<>();
        for(BotCommandAbs c: commands){
            commandsMap.put(c.getCommand(), c);
        }
        setBotCommandsWithRest(commands);
        return commandsMap;
    }

    private void setBotCommandsWithRest(List<BotCommandAbs> commands){
        List<String> ignoreCommands = List.of("/start", "/unknown", "/menu");
        List<BotCommandDTO> commandList = new ArrayList<>();
        for(BotCommand command: commands){
            if(ignoreCommands.contains(command.getCommand())){
                continue;
            }
            ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode("ru");
            String commandText = command.getCommand().replace('_', '.');
            String commandDescription = bundle.getString("bot.admin.description.command." + commandText.substring(1));

            commandList.add(
                BotCommandDTO.builder()
                    .command(command.getCommand()).description(commandDescription).build()
            );
        }

        ResponseFromTelegram<Boolean> response = adminFeign.setMyCommands(
            BotCommandsMenuDTO.builder().commands(commandList).build()
        );

        if(response.getResult()){
            log.info("Commands are set successfully, Response: {} | Commands: {}", response, commands);
        }else {
            log.warn("Commands are not set! Response: {} | Commands: {}", response, commands);
        }
    }

}
