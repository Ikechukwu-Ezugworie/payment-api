
package pojo.payDirect.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentItemsType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="PaymentItemsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Item" type="{}ItemType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentItemsType", propOrder = {
        "item"
})
public class PaymentItemsType {

    @XmlElement(name = "Item", required = true)
    protected ItemType item;

    /**
     * Gets the value of the item property.
     *
     * @return possible object is
     * {@link ItemType }
     */
    public ItemType getItem() {
        return item;
    }

    /**
     * Sets the value of the item property.
     *
     * @param value allowed object is
     *              {@link ItemType }
     */
    public void setItem(ItemType value) {
        this.item = value;
    }

}
