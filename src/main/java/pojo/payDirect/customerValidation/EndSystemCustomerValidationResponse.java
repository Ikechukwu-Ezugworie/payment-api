package pojo.payDirect.customerValidation;

import pojo.PayerPojo;

/**
 * CREATED BY GIBAH
 */
public class EndSystemCustomerValidationResponse {
    private PaymentStatus paymentStatus;
    private PayerPojo payer;
    private Long amountInKobo;

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
