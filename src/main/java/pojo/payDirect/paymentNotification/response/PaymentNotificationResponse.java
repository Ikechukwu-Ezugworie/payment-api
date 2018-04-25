
package pojo.payDirect.paymentNotification.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class PaymentNotificationResponse {

    @JacksonXmlProperty(localName = "Payments")
    private Payments payments;

    public Payments getPayments() {
        return payments;
    }

    public void setPayments(Payments payments) {
        this.payments = payments;
    }
}
