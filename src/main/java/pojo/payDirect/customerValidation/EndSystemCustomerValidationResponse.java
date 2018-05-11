package pojo.payDirect.customerValidation;

import pojo.PayerPojo;

/**
 * CREATED BY GIBAH
 */
public class EndSystemCustomerValidationResponse {
    private String paymentStatus;
    private PayerPojo payer;
    private Long amountInKobo;

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
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
}
