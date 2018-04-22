package pojo.payDirect;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public class Customer {
    @JacksonXmlProperty(localName = "FirstName")
    private String firstName;
    @JacksonXmlProperty(localName = "LastName")
    private String lastName;
    @JacksonXmlProperty(localName = "Email")
    private String email;
    @JacksonXmlProperty(localName = "Phone")
    private String phone;
    @JacksonXmlProperty(localName = "ThirdPartyCode")
    private String thirdPartyCode;
    @JacksonXmlProperty(localName = "Amount")
    private BigDecimal amount;
    @JacksonXmlProperty(localName = "CustReference")
    private long custReference;
    @JacksonXmlProperty(localName = "Status")
    private int status;
    @JacksonXmlProperty(localName = "CustomerReferenceAlternate")
    private String customerReferenceAlternate;

    public String getFirstName() {
        return firstName;
    }

    public Customer setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Customer setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Customer setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getThirdPartyCode() {
        return thirdPartyCode;
    }

    public Customer setThirdPartyCode(String thirdPartyCode) {
        this.thirdPartyCode = thirdPartyCode;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Customer setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public long getCustReference() {
        return custReference;
    }

    public Customer setCustReference(long custReference) {
        this.custReference = custReference;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Customer setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getCustomerReferenceAlternate() {
        return customerReferenceAlternate;
    }

    public Customer setCustomerReferenceAlternate(String customerReferenceAlternate) {
        this.customerReferenceAlternate = customerReferenceAlternate;
        return this;
    }
}
