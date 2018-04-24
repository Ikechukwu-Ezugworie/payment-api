package pojo.payDirect.customerValidation.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class Customers {
    @JacksonXmlProperty(localName = "Customer")
    private List<Customer> customers;

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void addCustomer(Customer customer) {
        if (this.customers == null) {
            this.customers = new ArrayList<>();
        }
        this.customers.add(customer);
    }
}
