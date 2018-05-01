package pojo.quickTeller;

import pojo.Ticket;

/**
 * CREATED BY GIBAH
 */
public class QuickTellerTicket extends Ticket {
    private String paymentCode;
    private String customerId;
    private String requestReference;

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRequestReference() {
        return requestReference;
    }

    public void setRequestReference(String requestReference) {
        this.requestReference = requestReference;
    }
}
