package uz.devops.intern.telegram.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "command", "description"
})
public class BotCommandDTO {

    @JsonProperty("command")
    private String command;

    @JsonProperty("description")
    private String description;
}
