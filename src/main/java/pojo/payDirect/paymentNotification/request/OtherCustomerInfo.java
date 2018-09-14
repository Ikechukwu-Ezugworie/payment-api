
package pojo.payDirect.paymentNotification.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sun.istack.internal.Nullable;

public class OtherCustomerInfo {

    @JacksonXmlProperty(localName = "EmailAddress")
    private String emailAddress;
    @JacksonXmlProperty(localName = "TaxOfficeID")
    private String taxOfficeId;
    @JacksonXmlProperty(localName = "NationalID")
    private String nationalId;
    @JacksonXmlProperty(localName = "NotificationMethod")
    private String notificationMethod;
    @JacksonXmlProperty(localName = "PhoneNumber")
    private String phoneNumber;
    private String rawValue;

    @Nullable
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Nullable
    public String getTaxOfficeId() {
        return taxOfficeId;
    }

    public void setTaxOfficeId(String taxOfficeId) {
        this.taxOfficeId = taxOfficeId;
    }

    @Nullable
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    @Nullable
    public String getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(String notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    @Override
    public String toString() {
        return "OtherCustomerInfo{" +
                "emailAddress='" + emailAddress + '\'' +
                ", taxOfficeId='" + taxOfficeId + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", notificationMethod='" + notificationMethod + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rawValue='" + rawValue + '\'' +
                '}';
    }
}
