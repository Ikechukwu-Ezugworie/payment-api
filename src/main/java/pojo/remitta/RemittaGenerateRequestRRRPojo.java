package pojo.remitta;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.BigInteger;

/**

    Author: Oluwatobi Adenekan
    email:  tadenekan@byteworks.com.ng
    date:    16/02/2019

**/
public class RemittaGenerateRequestRRRPojo {

    private String serviceTypeId;
    private BigDecimal amount;
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


    public BigDecimal getAmount() {
        return amount;
    }

    public RemittaGenerateRequestRRRPojo setAmount(BigDecimal amount) {
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
        return new Gson().toJson(this);
    }
}
