package pojo.flutterWave;

import javax.validation.constraints.NotNull;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class FWEndsystemTransactionRequestDto {
    private String accountCode;
    private String hash;
    private String transactionReference;
    private String customerReference;
    private String merchantTransactionReferenceId;
    //  NGN USD EUR GBP
    private String currencyCode;
    private Double amount;
    private String redirectUrl;
    private String customerName;
    @NotNull
    private String customerEmail;
    @NotNull
    private String customerPhone;
    private String customerFirstname;
    private String customerLastname;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public FWEndsystemTransactionRequestDto setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public FWEndsystemTransactionRequestDto setAccountCode(String accountCode) {
        this.accountCode = accountCode;
        return this;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public FWEndsystemTransactionRequestDto setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public FWEndsystemTransactionRequestDto setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public FWEndsystemTransactionRequestDto setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public String getMerchantTransactionReferenceId() {
        return merchantTransactionReferenceId;
    }

    public FWEndsystemTransactionRequestDto setMerchantTransactionReferenceId(String merchantTransactionReferenceId) {
        this.merchantTransactionReferenceId = merchantTransactionReferenceId;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public FWEndsystemTransactionRequestDto setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public FWEndsystemTransactionRequestDto setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public FWEndsystemTransactionRequestDto setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public FWEndsystemTransactionRequestDto setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public FWEndsystemTransactionRequestDto setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getCustomerFirstname() {
        return customerFirstname;
    }

    public FWEndsystemTransactionRequestDto setCustomerFirstname(String customerFirstname) {
        this.customerFirstname = customerFirstname;
        return this;
    }

    public String getCustomerLastname() {
        return customerLastname;
    }

    public FWEndsystemTransactionRequestDto setCustomerLastname(String customerLastname) {
        this.customerLastname = customerLastname;
        return this;
    }
}
