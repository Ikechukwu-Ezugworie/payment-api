package pojo;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse<T> {
    private String status;
    private String message;
    private int code;
    private T data;
    private HashMap<String, Object> meta;

    public ApiResponse() {
    }

    public ApiResponse(String status, String message, int code, T data) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public ApiResponse<T> setMeta(HashMap<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public ApiResponse<T> addMeta(String key, Object value) {
        if (this.meta == null) {
            this.meta = new HashMap<>();
        }
        this.meta.put(key, value);
        return this;
    }
}
