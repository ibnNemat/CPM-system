package uz.devops.intern.command.enumeration;

import lombok.Getter;

@Getter
public enum CommandsWithSlash {
    START("/start"),
    START_WITH_CHAT_ID("/start "),
    HELP("/help");

    private final String commandName;

    CommandsWithSlash(String commandName) {
        this.commandName = commandName;
    }

}
