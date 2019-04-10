package pojo.remitta;

import com.google.gson.Gson;

public class RemittaTransactionStatusPojo {

    private String statusmessage;
    private String merchantId;
    private String status;
    private String RRR;
    private String transactiontime;
    private String orderId;

    public String getStatusmessage() {
        return statusmessage;
    }

    public RemittaTransactionStatusPojo setStatusmessage(String statusmessage) {
        this.statusmessage = statusmessage;
        return this;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public RemittaTransactionStatusPojo setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public RemittaTransactionStatusPojo setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getRRR() {
        return RRR;
    }

    public RemittaTransactionStatusPojo setRRR(String RRR) {
        this.RRR = RRR;
        return this;
    }

    public String getTransactiontime() {
        return transactiontime;
    }

    public RemittaTransactionStatusPojo setTransactiontime(String transactiontime) {
        this.transactiontime = transactiontime;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public RemittaTransactionStatusPojo setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
