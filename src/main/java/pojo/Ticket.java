package pojo;

/**
 * CREATED BY GIBAH
 */
public class Ticket {
    private String providerMerchantId;
    private String transactionId;
    private Long amountInKobo;
    private String responseUrl;
    private String successRedirect;
    private String failureRedirect;
    private String payerName;
    private String payerPhone;
    private String payerEmail;
    private String hash;

    public Long getAmountInKobo() {
        return amountInKobo;
    }

    public void setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerPhone() {
        return payerPhone;
    }

    public void setPayerPhone(String payerPhone) {
        this.payerPhone = payerPhone;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getProviderMerchantId() {
        return providerMerchantId;
    }

    public void setProviderMerchantId(String providerMerchantId) {
        this.providerMerchantId = providerMerchantId;
    }

    public String getSuccessRedirect() {
        return successRedirect;
    }

    public void setSuccessRedirect(String successRedirect) {
        this.successRedirect = successRedirect;
    }

    public String getFailureRedirect() {
        return failureRedirect;
    }

    public void setFailureRedirect(String failureRedirect) {
        this.failureRedirect = failureRedirect;
    }
}
