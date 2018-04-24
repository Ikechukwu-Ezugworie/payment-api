
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentNotificationRequestType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="PaymentNotificationRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceUrl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServiceUsername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServicePassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FtpUrl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FtpUsername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FtpPassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Payments" type="{}PaymentsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentNotificationRequestType", propOrder = {
        "serviceUrl",
        "serviceUsername",
        "servicePassword",
        "ftpUrl",
        "ftpUsername",
        "ftpPassword",
        "payments"
})
public class PaymentNotificationRequestType {

    @XmlElement(name = "ServiceUrl", required = true)
    protected String serviceUrl;
    @XmlElement(name = "ServiceUsername", required = true)
    protected String serviceUsername;
    @XmlElement(name = "ServicePassword", required = true)
    protected String servicePassword;
    @XmlElement(name = "FtpUrl", required = true)
    protected String ftpUrl;
    @XmlElement(name = "FtpUsername", required = true)
    protected String ftpUsername;
    @XmlElement(name = "FtpPassword", required = true)
    protected String ftpPassword;
    @XmlElement(name = "Payments", required = true)
    protected PaymentsType payments;

    /**
     * Gets the value of the serviceUrl property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Sets the value of the serviceUrl property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setServiceUrl(String value) {
        this.serviceUrl = value;
    }

    /**
     * Gets the value of the serviceUsername property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getServiceUsername() {
        return serviceUsername;
    }

    /**
     * Sets the value of the serviceUsername property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setServiceUsername(String value) {
        this.serviceUsername = value;
    }

    /**
     * Gets the value of the servicePassword property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getServicePassword() {
        return servicePassword;
    }

    /**
     * Sets the value of the servicePassword property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setServicePassword(String value) {
        this.servicePassword = value;
    }

    /**
     * Gets the value of the ftpUrl property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFtpUrl() {
        return ftpUrl;
    }

    /**
     * Sets the value of the ftpUrl property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFtpUrl(String value) {
        this.ftpUrl = value;
    }

    /**
     * Gets the value of the ftpUsername property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFtpUsername() {
        return ftpUsername;
    }

    /**
     * Sets the value of the ftpUsername property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFtpUsername(String value) {
        this.ftpUsername = value;
    }

    /**
     * Gets the value of the ftpPassword property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFtpPassword() {
        return ftpPassword;
    }

    /**
     * Sets the value of the ftpPassword property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFtpPassword(String value) {
        this.ftpPassword = value;
    }

    /**
     * Gets the value of the payments property.
     *
     * @return possible object is
     * {@link PaymentsType }
     */
    public PaymentsType getPayments() {
        return payments;
    }

    /**
     * Sets the value of the payments property.
     *
     * @param value allowed object is
     *              {@link PaymentsType }
     */
    public void setPayments(PaymentsType value) {
        this.payments = value;
    }

}
