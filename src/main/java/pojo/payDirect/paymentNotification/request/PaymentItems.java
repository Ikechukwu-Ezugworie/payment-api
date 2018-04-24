
package pojo.payDirect.paymentNotification.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class PaymentItems {

    @JacksonXmlProperty(localName = "PaymentItem")
    protected List<PaymentItem> paymentItems;

    public List<PaymentItem> getPaymentItems() {
        return paymentItems;
    }

    public void setPaymentItems(List<PaymentItem> paymentItems) {
        this.paymentItems = paymentItems;
    }

    public void addItem(PaymentItem paymentItem) {
        if (this.paymentItems == null) {
            this.paymentItems = new ArrayList<>();
        }
        this.paymentItems.add(paymentItem);
    }
}
