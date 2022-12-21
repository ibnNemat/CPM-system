package uz.devops.intern.telegram.bot.customer.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.devops.intern.telegram.bot.customer.command.commandImpl.UnknownCommand;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandContainer {
    private final ExecuteCommand unknownCommand;
    private final Map<String, ExecuteCommand> commandsMap = new HashMap<>();
    private final Logger logger;

    public CommandContainer(List<ExecuteCommand> commands) {
        commands.forEach((executeCommand -> commandsMap.put(executeCommand.commandName(), executeCommand)));
        logger = LoggerFactory.getLogger(CommandContainer.class);
        unknownCommand = new UnknownCommand();
    }

    @PostConstruct
    public void init(){
        logger.info("Started working CommandContainer");
    }

    public ExecuteCommand getCommand(String commandName){
        return commandsMap.getOrDefault(commandName, unknownCommand);
    }
}
