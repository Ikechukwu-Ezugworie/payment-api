package pojo.quickTeller;

import pojo.Ticket;

/**
 * CREATED BY GIBAH
 */
public class QuickTellerTicket extends Ticket {
    private String paymentCode;
    private Long amountInKobo;
    private String customerId;
    private String mobileNumber;
    private String email;
    private String redirectUrl;
    private String requestReference;

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public Long getAmountInKobo() {
        return amountInKobo;
    }

    public void setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRequestReference() {
        return requestReference;
    }

    public void setRequestReference(String requestReference) {
        this.requestReference = requestReference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
