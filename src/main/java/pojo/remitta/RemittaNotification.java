package pojo.remitta;

import java.math.BigDecimal;
import java.util.List;

/*
 * Created by Gibah Joseph on Jan, 2019
 */
public class RemittaNotification {
    private String rrr;
    private String channel;
    private BigDecimal amount;
    private String transactiondate;
    private String debitdate;
    private String bank;
    private String branch;
    private String serviceTypeId;
    private String orderRef;
    private String orderId;
    private String payerName;
    private String payerPhoneNumber;
    private String payerEmail;
    private String type;
    private String dateRequested;
    private List<RemittaCustomFieldData> customFieldData;

    public String getRrr() {
        return rrr;
    }

    public void setRrr(String rrr) {
        this.rrr = rrr;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public RemittaNotification setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getTransactiondate() {
        return transactiondate;
    }

    public void setTransactiondate(String transactiondate) {
        this.transactiondate = transactiondate;
    }

    public String getDebitdate() {
        return debitdate;
    }

    public void setDebitdate(String debitdate) {
        this.debitdate = debitdate;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerPhoneNumber() {
        return payerPhoneNumber;
    }

    public void setPayerPhoneNumber(String payerPhoneNumber) {
        this.payerPhoneNumber = payerPhoneNumber;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<RemittaCustomFieldData> getCustomFieldData() {
        return customFieldData;
    }

    public RemittaNotification setCustomFieldData(List<RemittaCustomFieldData> customFieldData) {
        this.customFieldData = customFieldData;
        return this;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public RemittaNotification setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
        return this;
    }
}
