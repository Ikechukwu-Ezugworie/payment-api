
package pojo.payDirect.paymentNotification.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PaymentResponsePojo {
    @JacksonXmlProperty(localName = "PaymentLogId")
    private Integer paymentLogId;
    @JacksonXmlProperty(localName = "Status")
    private Integer status;
    @JacksonXmlProperty(localName = "StatusMessage")
    private String statusMessage;

    public Integer getPaymentLogId() {
        return paymentLogId;
    }

    public void setPaymentLogId(Integer paymentLogId) {
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
