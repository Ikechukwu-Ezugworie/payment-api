
package pojo.payDirect.jaxb;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="CustomerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CustReference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CustomerReferenceAlternate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ThirdPartyCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PaymentItems" type="{}PaymentItemsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerType", propOrder = {
        "status",
        "custReference",
        "customerReferenceAlternate",
        "firstName",
        "lastName",
        "email",
        "phone",
        "thirdPartyCode",
        "amount",
        "paymentItems"
})
public class CustomerType {

    @XmlElement(name = "Status", required = true)
    protected String status;
    @XmlElement(name = "CustReference", required = true)
    protected String custReference;
    @XmlElement(name = "CustomerReferenceAlternate", required = true)
    protected String customerReferenceAlternate;
    @XmlElement(name = "FirstName", required = true)
    protected String firstName;
    @XmlElement(name = "LastName", required = true)
    protected String lastName;
    @XmlElement(name = "Email", required = true)
    protected String email;
    @XmlElement(name = "Phone", required = true)
    protected String phone;
    @XmlElement(name = "ThirdPartyCode", required = true)
    protected String thirdPartyCode;
    @XmlElement(name = "Amount", required = true)
    protected String amount;
    @XmlElement(name = "PaymentItems", required = true)
    protected PaymentItemsType paymentItems;

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

    /**
     * Gets the value of the custReference property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCustReference() {
        return custReference;
    }

    /**
     * Sets the value of the custReference property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCustReference(String value) {
        this.custReference = value;
    }

    /**
     * Gets the value of the customerReferenceAlternate property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCustomerReferenceAlternate() {
        return customerReferenceAlternate;
    }

    /**
     * Sets the value of the customerReferenceAlternate property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCustomerReferenceAlternate(String value) {
        this.customerReferenceAlternate = value;
    }

    /**
     * Gets the value of the firstName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the email property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the phone property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the thirdPartyCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getThirdPartyCode() {
        return thirdPartyCode;
    }

    /**
     * Sets the value of the thirdPartyCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setThirdPartyCode(String value) {
        this.thirdPartyCode = value;
    }

    /**
     * Gets the value of the amount property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAmount(String value) {
        this.amount = value;
    }

    /**
     * Gets the value of the paymentItems property.
     *
     * @return possible object is
     * {@link PaymentItemsType }
     */
    public PaymentItemsType getPaymentItems() {
        return paymentItems;
    }

    /**
     * Sets the value of the paymentItems property.
     *
     * @param value allowed object is
     *              {@link PaymentItemsType }
     */
    public void setPaymentItems(PaymentItemsType value) {
        this.paymentItems = value;
    }

}
