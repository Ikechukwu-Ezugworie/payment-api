package exceptions;

public class ApiResponseException extends Exception {
    public ApiResponseException() {
    }

    public ApiResponseException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

