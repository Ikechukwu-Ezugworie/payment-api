
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="PaymentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentLogId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentType", propOrder = {
        "paymentLogId",
        "status"
})
public class PaymentType {

    @XmlElement(name = "PaymentLogId", required = true)
    protected String paymentLogId;
    @XmlElement(name = "Status", required = true)
    protected String status;

    /**
     * Gets the value of the paymentLogId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPaymentLogId() {
        return paymentLogId;
    }

    /**
     * Sets the value of the paymentLogId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPaymentLogId(String value) {
        this.paymentLogId = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatus(String value) {
        this.status = value;
    }

}
