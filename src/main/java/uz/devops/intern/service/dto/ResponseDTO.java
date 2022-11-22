package uz.devops.intern.service.dto;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class ResponseDTO<T> {
    private Integer code;
    private String message;
    private Boolean success;
    private T responseData;

    public ResponseDTO(){}

    public ResponseDTO(Integer code, String message, Boolean success, T responseData) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.responseData = responseData;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getResponseData() {
        return responseData;
    }

    public void setResponseData(T responseData) {
        this.responseData = responseData;
    }
}
