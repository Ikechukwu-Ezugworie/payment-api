package pojo.payDirect.customerValidation;

import pojo.PayerPojo;
import pojo.payDirect.customerValidation.response.PaymentItems;

/**
 * CREATED BY GIBAH
 */
public class EndSystemCustomerValidationResponse {
    private PaymentStatus paymentStatus;
    private PayerPojo payer;
    private Long amountInKobo;
    private PaymentItems paymentItems;

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PayerPojo getPayer() {
        return payer;
    }

    public void setPayer(PayerPojo payer) {
        this.payer = payer;
    }

    public Long getAmountInKobo() {
        return amountInKobo;
    }

    public void setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
    }

    public PaymentItems getPaymentItems() {
        return paymentItems;
    }

    public EndSystemCustomerValidationResponse setPaymentItems(PaymentItems paymentItems) {
        this.paymentItems = paymentItems;
        return this;
    }

    public enum PaymentStatus {
        PAID("PAID"), NOT_PAID("NOT_PAID");
        private String value;

        PaymentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
