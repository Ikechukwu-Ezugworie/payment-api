
package pojo.payDirect.paymentNotification.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OtherCustomerInfo {

    @JacksonXmlProperty(localName = "EmailAddress")
    private String emailAddress;
    @JacksonXmlProperty(localName = "TaxOfficeID")
    private String taxOfficeId;
    @JacksonXmlProperty(localName = "NationalID")
    private String nationalId;
    @JacksonXmlProperty(localName = "NotificationMethod")
    private String notificationMethod;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getTaxOfficeId() {
        return taxOfficeId;
    }

    public void setTaxOfficeId(String taxOfficeId) {
        this.taxOfficeId = taxOfficeId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(String notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    @Override
    public String toString() {
        return "OtherCustomerInfo{" +
                "emailAddress='" + emailAddress + '\'' +
                ", taxOfficeId='" + taxOfficeId + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", notificationMethod='" + notificationMethod + '\'' +
                '}';
    }
}
