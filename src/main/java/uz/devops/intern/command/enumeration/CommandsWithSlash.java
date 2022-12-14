package uz.devops.intern.command.enumeration;

import lombok.Getter;

@Getter
public enum CommandsWithSlash {
    START("/start"),
    START_WITH_CHAT_ID("/start "),
    HELP("/help"),
    COMMANDS_WITHOUT_SLASH("command without slash");

    private final String commandName;

    CommandsWithSlash(String commandName) {
        this.commandName = commandName;
    }
}
