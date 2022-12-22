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
    "chat_id", "message_id", "disable_notification"
})
public class PinMessageDTO {

    @JsonProperty("chat_id")
    private String chatId;
    @JsonProperty("message_id")
    private Integer messageId;
    @JsonProperty(value = "disable_notification", defaultValue = "false")
    private Boolean disableNotification;
}
