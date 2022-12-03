package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseFromTelegram<T>{

    private Boolean ok;
    @JsonProperty("error_code")
    private Integer errorCode;
    private String description;
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
