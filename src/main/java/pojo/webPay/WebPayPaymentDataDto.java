package pojo.webPay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class WebPayPaymentDataDto {

    @SerializedName("Amount")
    @Expose
    private Long amount;
    @SerializedName("CardNumber")
    @Expose
    private String cardNumber;
    @SerializedName("MerchantReference")
    @Expose
    private String merchantReference;
    @SerializedName("PaymentReference")
    @Expose
    private String paymentReference;
    @SerializedName("RetrievalReferenceNumber")
    @Expose
    private String retrievalReferenceNumber;
    @SerializedName("LeadBankCbnCode")
    @Expose
    private String leadBankCbnCode;
    @SerializedName("LeadBankName")
    @Expose
    private String leadBankName;
    //    @SerializedName("SplitAccounts")
//    @Expose
//    private List<Object> splitAccounts = null;
    @SerializedName("TransactionDate")
    @Expose
    private String transactionDate;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseDescription")
    @Expose
    private String responseDescription;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getRetrievalReferenceNumber() {
        return retrievalReferenceNumber;
    }

    public void setRetrievalReferenceNumber(String retrievalReferenceNumber) {
        this.retrievalReferenceNumber = retrievalReferenceNumber;
    }

    public Object getLeadBankCbnCode() {
        return leadBankCbnCode;
    }

    public void setLeadBankCbnCode(String leadBankCbnCode) {
        this.leadBankCbnCode = leadBankCbnCode;
    }

    public Object getLeadBankName() {
        return leadBankName;
    }

    public void setLeadBankName(String leadBankName) {
        this.leadBankName = leadBankName;
    }

//    public List<Object> getSplitAccounts() {
//        return splitAccounts;
//    }
//
//    public void setSplitAccounts(List<Object> splitAccounts) {
//        this.splitAccounts = splitAccounts;
//    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("amount", amount)
                .append("cardNumber", cardNumber)
                .append("merchantReference", merchantReference)
                .append("paymentReference", paymentReference)
                .append("retrievalReferenceNumber", retrievalReferenceNumber)
                .append("leadBankCbnCode", leadBankCbnCode)
                .append("leadBankName", leadBankName)
//                .append("splitAccounts", splitAccounts)
                .append("transactionDate", transactionDate)
                .append("responseCode", responseCode)
                .append("responseDescription", responseDescription)
                .toString();
    }
}