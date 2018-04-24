package pojo.payDirect.paymentNotification.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class Payments {
    @JacksonXmlProperty(localName = "Payment")
    private List<Payment> payments;

    public List<Payment> getPayment() {
        return payments;
    }

    public void setPayment(List<Payment> payment) {
        this.payments = payment;
    }
}
