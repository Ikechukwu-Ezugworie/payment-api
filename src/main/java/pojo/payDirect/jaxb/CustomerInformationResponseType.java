
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerInformationResponseType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="CustomerInformationResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MerchantReference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Customers" type="{}CustomersType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerInformationResponseType", propOrder = {
        "merchantReference",
        "customers"
})
public class CustomerInformationResponseType {

    @XmlElement(name = "MerchantReference", required = true)
    protected String merchantReference;
    @XmlElement(name = "Customers", required = true)
    protected CustomersType customers;

    /**
     * Gets the value of the merchantReference property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMerchantReference() {
        return merchantReference;
    }

    /**
     * Sets the value of the merchantReference property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMerchantReference(String value) {
        this.merchantReference = value;
    }

    /**
     * Gets the value of the customers property.
     *
     * @return possible object is
     * {@link CustomersType }
     */
    public CustomersType getCustomers() {
        return customers;
    }

    /**
     * Sets the value of the customers property.
     *
     * @param value allowed object is
     *              {@link CustomersType }
     */
    public void setCustomers(CustomersType value) {
        this.customers = value;
    }

}
