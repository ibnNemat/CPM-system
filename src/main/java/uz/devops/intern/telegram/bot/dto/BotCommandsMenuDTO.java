package uz.devops.intern.telegram.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "commands", "language_code"
})
public class BotCommandsMenuDTO {

    @JsonProperty("commands")
    private List<BotCommandDTO> commands;

//    @JsonProperty("scope")
//    private BotCommandScope scope;

//    @JsonProperty("language_code")
//    private String languageCode = "en_US";
}
