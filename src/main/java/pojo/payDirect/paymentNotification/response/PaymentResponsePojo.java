
package pojo.payDirect.paymentNotification.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PaymentResponsePojo {
    @JacksonXmlProperty(localName = "PaymentLogId")
    private String paymentLogId;
    @JacksonXmlProperty(localName = "Status")
    private Integer status;
    @JacksonXmlProperty(localName = "StatusMessage")
    private String statusMessage;

    public String getPaymentLogId() {
        return paymentLogId;
    }

    public void setPaymentLogId(String paymentLogId) {
        this.paymentLogId = paymentLogId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
