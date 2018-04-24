
package pojo.payDirect.paymentNotification.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import pojo.payDirect.pojo.PaymentResponsePojo;

import java.util.ArrayList;
import java.util.List;

public class PaymentNotificationResponse {

    @JacksonXmlProperty(localName = "Payments")
    private List<PaymentResponsePojo> payments;

    public List<PaymentResponsePojo> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentResponsePojo> payments) {
        this.payments = payments;
    }

    public void addPayment(PaymentResponsePojo payment) {
        if (this.payments == null) {
            this.payments = new ArrayList<>();
        }
        this.payments.add(payment);
    }
}
