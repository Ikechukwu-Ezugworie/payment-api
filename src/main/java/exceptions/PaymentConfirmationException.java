package exceptions;

public class PaymentConfirmationException extends Exception {

    public PaymentConfirmationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
