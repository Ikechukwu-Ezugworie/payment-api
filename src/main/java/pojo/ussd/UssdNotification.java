package pojo.ussd;

import com.google.gson.internal.LinkedTreeMap;

import java.math.BigDecimal;

public class UssdNotification {
    private String msisdn;
    private BigDecimal amount;
    private String revenueCode;
    private String transactionReference;
    private LinkedTreeMap<String, Object> customFieldData;

    private String paymentDate;


    public String getMsisdn() {
        return msisdn;
    }

    public UssdNotification setMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public UssdNotification setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getRevenueCode() {
        return revenueCode;
    }

    public UssdNotification setRevenueCode(String revenueCode) {
        this.revenueCode = revenueCode;
        return this;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public UssdNotification setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public UssdNotification setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
        return this;
    }

    public LinkedTreeMap<String, Object> getCustomFieldData() {
        return customFieldData;
    }

    public UssdNotification setCustomFieldData(LinkedTreeMap<String, Object> customFieldData) {
        this.customFieldData = customFieldData;
        return this;
    }
}
