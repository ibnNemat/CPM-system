package uz.devops.intern.telegram.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WebhookResponseDTO {

    private Boolean ok;
    @JsonProperty("error_code")
    private Integer errorCode;
    private Boolean result;
    private String description;

    public WebhookResponseDTO(Boolean ok, Integer errorCode, Boolean result, String description) {
        this.ok = ok;
        this.errorCode = errorCode;
        this.result = result;
        this.description = description;
    }

    public WebhookResponseDTO(){}
    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

