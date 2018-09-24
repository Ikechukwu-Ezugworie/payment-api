
package pojo.payDirect.customerValidation.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CustomerInformationResponse {

    @JacksonXmlProperty(localName = "MerchantReference")
    protected String merchantReference;
    @JacksonXmlProperty(localName = "Customers")
    protected Customers customers;

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }
}
