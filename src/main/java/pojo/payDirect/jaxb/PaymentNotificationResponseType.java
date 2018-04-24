
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentNotificationResponseType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="PaymentNotificationResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Payments" type="{}PaymentsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentNotificationResponseType", propOrder = {
        "payments"
})
public class PaymentNotificationResponseType {

    @XmlElement(name = "Payments", required = true)
    protected PaymentsType payments;

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
