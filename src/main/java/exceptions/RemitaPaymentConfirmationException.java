package exceptions;

import pojo.remitta.RemittaTransactionStatusPojo;

public class RemitaPaymentConfirmationException extends Exception {
    RemittaTransactionStatusPojo response;


    public RemitaPaymentConfirmationException(String message) {
        super(message);
    }

    public RemitaPaymentConfirmationException(RemittaTransactionStatusPojo response){
        this.response = response;

    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public RemittaTransactionStatusPojo getResponseObject(){
        return this.response;
    }


}
