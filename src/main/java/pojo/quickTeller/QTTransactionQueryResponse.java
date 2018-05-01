
package pojo.quickTeller;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class QTTransactionQueryResponse {

    private Long Amount;
    private String PaymentReference;
    private String ResponseCode;
    private String ResponseDescription;
    private String CustomerId;
    private String TransactionDate;

    public Long getAmount() {
        return Amount;
    }

    public void setAmount(Long amount) {
        Amount = amount;
    }

    public String getPaymentReference() {
        return PaymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        PaymentReference = paymentReference;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
    }
}
