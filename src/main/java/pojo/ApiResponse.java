package pojo;

public class ApiResponse<T> {
    private String status;
    private String message;
    private int code;
    private T data;

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

}
