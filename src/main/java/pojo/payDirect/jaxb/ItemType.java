
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="ItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProductName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Quantity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Subtotal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Tax" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Total" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemType", propOrder = {
        "productName",
        "productCode",
        "quantity",
        "price",
        "subtotal",
        "tax",
        "total"
})
public class ItemType {

    @XmlElement(name = "ProductName", required = true)
    protected String productName;
    @XmlElement(name = "ProductCode", required = true)
    protected String productCode;
    @XmlElement(name = "Quantity", required = true)
    protected String quantity;
    @XmlElement(name = "Price", required = true)
    protected String price;
    @XmlElement(name = "Subtotal", required = true)
    protected String subtotal;
    @XmlElement(name = "Tax", required = true)
    protected String tax;
    @XmlElement(name = "Total", required = true)
    protected String total;

    /**
     * Gets the value of the productName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the value of the productName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setProductName(String value) {
        this.productName = value;
    }

    /**
     * Gets the value of the productCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * Sets the value of the productCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setProductCode(String value) {
        this.productCode = value;
    }

    /**
     * Gets the value of the quantity property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setQuantity(String value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the price property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPrice(String value) {
        this.price = value;
    }

    /**
     * Gets the value of the subtotal property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the value of the subtotal property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSubtotal(String value) {
        this.subtotal = value;
    }

    /**
     * Gets the value of the tax property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTax() {
        return tax;
    }

    /**
     * Sets the value of the tax property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTax(String value) {
        this.tax = value;
    }

    /**
     * Gets the value of the total property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTotal(String value) {
        this.total = value;
    }

}
