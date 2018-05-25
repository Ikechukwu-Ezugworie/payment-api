package pojo.payDirect.customerValidation.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public class CustomerInformationRequest {
    @JacksonXmlProperty(localName = "ServiceUsername")
    private String serviceUsername;
    @JacksonXmlProperty(localName = "ServicePassword")
    private String servicePassword;
    @JacksonXmlProperty(localName = "MerchantReference")
    private String merchantReference;
    @JacksonXmlProperty(localName = "CustReference")
    private String custReference;
    @JacksonXmlProperty(localName = "PaymentItemCode")
    private String paymentItemCode;
    @JacksonXmlProperty(localName = "ThirdPartyCode")
    private String thirdPartyCode;
    @JacksonXmlProperty(localName = "Amount")
    private BigDecimal amount;

    public String getServiceUsername() {
        return serviceUsername;
    }

    public CustomerInformationRequest setServiceUsername(String serviceUsername) {
        this.serviceUsername = serviceUsername;
        return this;
    }

    public String getServicePassword() {
        return servicePassword;
    }

    public CustomerInformationRequest setServicePassword(String servicePassword) {
        this.servicePassword = servicePassword;
        return this;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public CustomerInformationRequest setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
        return this;
    }

    public String getCustReference() {
        return custReference;
    }

    public CustomerInformationRequest setCustReference(String custReference) {
        this.custReference = custReference;
        return this;
    }

    public String getPaymentItemCode() {
        return paymentItemCode;
    }

    public CustomerInformationRequest setPaymentItemCode(String paymentItemCode) {
        this.paymentItemCode = paymentItemCode;
        return this;
    }

    public String getThirdPartyCode() {
        return thirdPartyCode;
    }

    public CustomerInformationRequest setThirdPartyCode(String thirdPartyCode) {
        this.thirdPartyCode = thirdPartyCode;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CustomerInformationRequest setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String toString() {
        return "CustomerInformationRequest{" +
                "serviceUsername='" + serviceUsername + '\'' +
                ", servicePassword='" + servicePassword + '\'' +
                ", merchantReference='" + merchantReference + '\'' +
                ", custReference='" + custReference + '\'' +
                ", paymentItemCode='" + paymentItemCode + '\'' +
                ", thirdPartyCode='" + thirdPartyCode + '\'' +
                ", amount=" + amount +
                '}';
    }
}
