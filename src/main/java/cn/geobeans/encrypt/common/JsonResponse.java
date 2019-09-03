package cn.geobeans.encrypt.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * common 返回结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonResponse {

    private boolean success;
    private String message;
    private Object data;

    public JsonResponse(Object data) {
        this.success = true;
        this.data = data;
    }

    public JsonResponse(String message) {
        this.success = false;
        this.message = message;
    }

    public JsonResponse(boolean success) {
        this.success = success;
    }

    public JsonResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public JsonResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

}
