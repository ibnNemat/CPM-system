package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "ok", "error_code", "description", "result"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseFromTelegram<T>{

    @JsonProperty("ok")
    private Boolean ok;
    @JsonProperty("error_code")
    private Integer errorCode;
    @JsonProperty("description")
    private String description;
    @JsonProperty("result")
    private T result;

    public ResponseFromTelegram(){};

    public ResponseFromTelegram(Boolean ok, Integer errorCode, String description, T result) {
        this.ok = ok;
        this.errorCode = errorCode;
        this.description = description;
        this.result = result;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
