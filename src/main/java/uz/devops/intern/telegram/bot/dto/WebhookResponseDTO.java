package uz.devops.intern.telegram.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponseDTO {

    private Boolean ok;
    @JsonProperty("error_code")
    private Integer errorCode;
    private Boolean result;
    private String description;
}
