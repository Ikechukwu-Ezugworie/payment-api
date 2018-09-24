package pojo.payDirect.paymentNotification.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import pojo.payDirect.paymentNotification.request.Payment;

import java.util.ArrayList;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class Payments {
    @JacksonXmlProperty(localName = "Payment")
    private List<PaymentResponsePojo> payment;

    public List<PaymentResponsePojo> getPayment() {
        return payment;
    }

    public void setPayment(List<PaymentResponsePojo> payment) {
        this.payment = payment;
    }

    public void addPayment(PaymentResponsePojo payment) {
        if (this.payment == null) {
            this.payment = new ArrayList<>();
        }
        this.payment.add(payment);
    }
}
