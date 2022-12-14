package uz.devops.intern.pattern.command.enumeration;

import lombok.Getter;

@Getter
public enum CommandsWithSlash {
    START("/start"),
    START_WITH_CHAT_ID("/start "),
    HELP("/help"),
    COMMAND_WITH_MESSAGE("update with message"),
    COMMAND_WITH_CALLBACK_QUERY("update with callback query");

    private final String commandName;

    CommandsWithSlash(String commandName) {
        this.commandName = commandName;
    }
}
