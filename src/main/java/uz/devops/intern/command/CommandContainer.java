package uz.devops.intern.command;

import uz.devops.intern.telegram.bot.service.Command;

import java.util.Map;

public class CommandContainer {
    private final Command unknownCommand;
    private final Map<String, Command> commandsMap;

    public CommandContainer(Command unknownCommand, Map<String, Command> commandsMap) {
        this.unknownCommand = unknownCommand;
        this.commandsMap = commandsMap;
    }
}
