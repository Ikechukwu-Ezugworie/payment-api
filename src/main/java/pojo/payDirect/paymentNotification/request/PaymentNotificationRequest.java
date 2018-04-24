
package pojo.payDirect.paymentNotification.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PaymentNotificationRequest {

    @JacksonXmlProperty(localName = "ServiceUrl")
    private String serviceUrl;
    @JacksonXmlProperty(localName = "ServiceUsername")
    private String serviceUsername;
    @JacksonXmlProperty(localName = "ServicePassword")
    private String servicePassword;
    @JacksonXmlProperty(localName = "FtpUrl")
    private String ftpUrl;
    @JacksonXmlProperty(localName = "FtpUsername")
    private String ftpUsername;
    @JacksonXmlProperty(localName = "FtpPassword")
    private String ftpPassword;
    @JacksonXmlProperty(localName = "Payments")
    private Payments payments;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServiceUsername() {
        return serviceUsername;
    }

    public void setServiceUsername(String serviceUsername) {
        this.serviceUsername = serviceUsername;
    }

    public String getServicePassword() {
        return servicePassword;
    }

    public void setServicePassword(String servicePassword) {
        this.servicePassword = servicePassword;
    }

    public String getFtpUrl() {
        return ftpUrl;
    }

    public void setFtpUrl(String ftpUrl) {
        this.ftpUrl = ftpUrl;
    }

    public String getFtpUsername() {
        return ftpUsername;
    }

    public void setFtpUsername(String ftpUsername) {
        this.ftpUsername = ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public Payments getPayments() {
        return payments;
    }

    public void setPayments(Payments payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "PaymentNotificationRequest{" +
                "serviceUrl='" + serviceUrl + '\'' +
                ", serviceUsername='" + serviceUsername + '\'' +
                ", servicePassword='" + servicePassword + '\'' +
                ", ftpUrl='" + ftpUrl + '\'' +
                ", ftpUsername='" + ftpUsername + '\'' +
                ", ftpPassword='" + ftpPassword + '\'' +
                ", payments=" + payments +
                '}';
    }
}
