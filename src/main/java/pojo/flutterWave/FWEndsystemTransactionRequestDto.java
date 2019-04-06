package pojo.flutterWave;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class FWEndsystemTransactionRequestDto {
    private String accountCode;
    private String apiKey;
    private String transactionReference;
    private Long amountInKobo;
    private String hash;
    private String redirectUrl;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerFirstname;
    private String customerLastname;

    public String getAccountCode() {
        return accountCode;
    }

    public FWEndsystemTransactionRequestDto setAccountCode(String accountCode) {
        this.accountCode = accountCode;
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    public FWEndsystemTransactionRequestDto setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public FWEndsystemTransactionRequestDto setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public Long getAmountInKobo() {
        return amountInKobo;
    }

    public FWEndsystemTransactionRequestDto setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
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
