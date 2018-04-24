
package pojo.payDirect.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the pojo.payDirect package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CustomerInformationResponse_QNAME = new QName("", "CustomerInformationResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pojo.payDirect
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CustomerInformationResponseType }
     */
    public CustomerInformationResponseType createCustomerInformationResponseType() {
        return new CustomerInformationResponseType();
    }

    /**
     * Create an instance of {@link PaymentItemsType }
     */
    public PaymentItemsType createPaymentItemsType() {
        return new PaymentItemsType();
    }

    /**
     * Create an instance of {@link ItemType }
     */
    public ItemType createItemType() {
        return new ItemType();
    }

    /**
     * Create an instance of {@link CustomersType }
     */
    public CustomersType createCustomersType() {
        return new CustomersType();
    }

    /**
     * Create an instance of {@link CustomerType }
     */
    public CustomerType createCustomerType() {
        return new CustomerType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CustomerInformationResponseType }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "CustomerInformationResponse")
    public JAXBElement<CustomerInformationResponseType> createCustomerInformationResponse(CustomerInformationResponseType value) {
        return new JAXBElement<CustomerInformationResponseType>(_CustomerInformationResponse_QNAME, CustomerInformationResponseType.class, null, value);
    }

}
