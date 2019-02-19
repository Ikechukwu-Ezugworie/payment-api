package pojo.remitta;

import java.math.BigDecimal;
import java.math.BigInteger;

/**

    Author: Oluwatobi Adenekan
    email:  tadenekan@byteworks.com.ng
    date:    16/02/2019

**/
public class RemittaGenerateRequestRRRPojo {

    private String serviceTypeId;
    private BigInteger amount;
    private String orderId;
    private String payerName;
    private String payerEmail;
    private String payerPhone;
    private String description;


    public String getServiceTypeId() {
        return serviceTypeId;
    }




    public RemittaGenerateRequestRRRPojo setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
        return this;
    }


    public BigInteger getAmount() {
        return amount;
    }

    public RemittaGenerateRequestRRRPojo setAmount(BigInteger amount) {
        this.amount = amount;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public RemittaGenerateRequestRRRPojo setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getPayerName() {
        return payerName;
    }

    public RemittaGenerateRequestRRRPojo setPayerName(String payerName) {
        this.payerName = payerName;
        return this;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public RemittaGenerateRequestRRRPojo setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
        return this;
    }

    public String getPayerPhone() {
        return payerPhone;
    }

    public RemittaGenerateRequestRRRPojo setPayerPhone(String payerPhone) {
        this.payerPhone = payerPhone;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RemittaGenerateRequestRRRPojo setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RemittaGenerateRequestRRRPojo{");
        sb.append("serviceTypeId='").append(serviceTypeId).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", orderId='").append(orderId).append('\'');
        sb.append(", payerName='").append(payerName).append('\'');
        sb.append(", payerEmail='").append(payerEmail).append('\'');
        sb.append(", payerPhone='").append(payerPhone).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
