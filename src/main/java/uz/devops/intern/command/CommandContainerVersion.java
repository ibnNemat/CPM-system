package uz.devops.intern.command;

import org.springframework.stereotype.Service;
import uz.devops.intern.command.commandImpl.*;
import uz.devops.intern.service.CustomerTelegramService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class CommandContainerVersion {
    private final ExecuteCommand unknownCommand;
    private final Map<String, ExecuteCommand> commandsMap = new HashMap<>();

    public CommandContainerVersion(CustomerTelegramService customerTelegramService, List<ExecuteCommand> commands) {

        commands.forEach((executeCommand ->
            commandsMap.put(executeCommand.commandName(), executeCommand))
        );

        unknownCommand = new UnknownCommand(customerTelegramService);
    }

    public ExecuteCommand getCommand(String commandName){
        return commandsMap.getOrDefault(commandName, unknownCommand);
    }
}
