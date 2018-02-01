package models.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by XIAODA on 2016/1/28.
 */
public class ResponseDto {

    private JsonNode result;
    private boolean success;
    private String message;

    public ResponseDto(){}

    public ResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public JsonNode getResult() {
        return result;
    }

    public void setResult(JsonNode result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
