package uz.devops.intern.telegram.bot.customer.command.enumeration;

import lombok.Getter;

@Getter
public enum CommandsName {
    START_WITHOUT_CHAT_ID("/start"),
    START_WITH_CHAT_ID(""),
    HELP("/help"),
    UNKNOWN_COMMAND_WITH_SLASH("/unknown command"),
    COMMAND_WITH_MESSAGE("update with message"),
    COMMAND_WITH_CALLBACK_QUERY("update with callback query");

    private final String commandName;

    CommandsName(String commandName) {
        this.commandName = commandName;
    }
}
