
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentItemType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="PaymentItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ItemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ItemCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ItemAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LeadBankCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LeadBankCbnCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LeadBankName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CategoryCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CategoryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ItemQuantity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentItemType", propOrder = {
        "itemName",
        "itemCode",
        "itemAmount",
        "leadBankCode",
        "leadBankCbnCode",
        "leadBankName",
        "categoryCode",
        "categoryName",
        "itemQuantity"
})
public class PaymentItemType {

    @XmlElement(name = "ItemName", required = true)
    protected String itemName;
    @XmlElement(name = "ItemCode", required = true)
    protected String itemCode;
    @XmlElement(name = "ItemAmount", required = true)
    protected String itemAmount;
    @XmlElement(name = "LeadBankCode", required = true)
    protected String leadBankCode;
    @XmlElement(name = "LeadBankCbnCode", required = true)
    protected String leadBankCbnCode;
    @XmlElement(name = "LeadBankName", required = true)
    protected String leadBankName;
    @XmlElement(name = "CategoryCode", required = true)
    protected String categoryCode;
    @XmlElement(name = "CategoryName", required = true)
    protected String categoryName;
    @XmlElement(name = "ItemQuantity", required = true)
    protected String itemQuantity;

    /**
     * Gets the value of the itemName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the value of the itemName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setItemName(String value) {
        this.itemName = value;
    }

    /**
     * Gets the value of the itemCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * Sets the value of the itemCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setItemCode(String value) {
        this.itemCode = value;
    }

    /**
     * Gets the value of the itemAmount property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getItemAmount() {
        return itemAmount;
    }

    /**
     * Sets the value of the itemAmount property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setItemAmount(String value) {
        this.itemAmount = value;
    }

    /**
     * Gets the value of the leadBankCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLeadBankCode() {
        return leadBankCode;
    }

    /**
     * Sets the value of the leadBankCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLeadBankCode(String value) {
        this.leadBankCode = value;
    }

    /**
     * Gets the value of the leadBankCbnCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLeadBankCbnCode() {
        return leadBankCbnCode;
    }

    /**
     * Sets the value of the leadBankCbnCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLeadBankCbnCode(String value) {
        this.leadBankCbnCode = value;
    }

    /**
     * Gets the value of the leadBankName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLeadBankName() {
        return leadBankName;
    }

    /**
     * Sets the value of the leadBankName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLeadBankName(String value) {
        this.leadBankName = value;
    }

    /**
     * Gets the value of the categoryCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Sets the value of the categoryCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCategoryCode(String value) {
        this.categoryCode = value;
    }

    /**
     * Gets the value of the categoryName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the value of the categoryName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCategoryName(String value) {
        this.categoryName = value;
    }

    /**
     * Gets the value of the itemQuantity property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getItemQuantity() {
        return itemQuantity;
    }

    /**
     * Sets the value of the itemQuantity property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setItemQuantity(String value) {
        this.itemQuantity = value;
    }

}
