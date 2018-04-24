
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomersType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="CustomersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Customer" type="{}CustomerType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomersType", propOrder = {
        "customer"
})
public class CustomersType {

    @XmlElement(name = "Customer", required = true)
    protected CustomerType customer;

    /**
     * Gets the value of the customer property.
     *
     * @return possible object is
     * {@link CustomerType }
     */
    public CustomerType getCustomer() {
        return customer;
    }

    /**
     * Sets the value of the customer property.
     *
     * @param value allowed object is
     *              {@link CustomerType }
     */
    public void setCustomer(CustomerType value) {
        this.customer = value;
    }

}
