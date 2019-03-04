package pojo.ussd;

import java.math.BigDecimal;

public class UssdNotification {
    String msisdn;
    BigDecimal amount;
    String revenueCode;
    String transactionReference;


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
}
