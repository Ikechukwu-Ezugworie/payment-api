
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentsType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="PaymentsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Payment" type="{}PaymentType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentsType", propOrder = {
        "payment"
})
public class PaymentsType {

    @XmlElement(name = "Payment", required = true)
    protected PaymentType payment;

    /**
     * Gets the value of the payment property.
     *
     * @return possible object is
     * {@link PaymentType }
     */
    public PaymentType getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     *
     * @param value allowed object is
     *              {@link PaymentType }
     */
    public void setPayment(PaymentType value) {
        this.payment = value;
    }

}
