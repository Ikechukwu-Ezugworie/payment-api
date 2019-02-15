package pojo.webPay;

import utils.Constants;
import utils.PaymentUtil;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class WebPayTransactionRequestPojo {
    private Long amount;
    private int currency = 566;
    private String customerId;
    private String hash;
    private String transactionReference;
    private Integer paymentItemId;
    private Integer productId;
    private String siteRedirectUrl;

    private String customerIdDescription;
    private String customerName;
    private String customerEmail; // not required by ISW
    private String customerNameDescription;
    private String siteName;
    private String paymentItemName;

    public Long getAmount() {
        return amount;
    }

    public WebPayTransactionRequestPojo setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public int getCurrency() {
        return currency;
    }

    public WebPayTransactionRequestPojo setCurrency(int currency) {
        this.currency = currency;
        return this;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public WebPayTransactionRequestPojo setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        return this;
    }

    public String getCustomerId() {
        return customerId;
    }

    public WebPayTransactionRequestPojo setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public WebPayTransactionRequestPojo setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public WebPayTransactionRequestPojo setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public Integer getPaymentItemId() {
        return paymentItemId;
    }

    public WebPayTransactionRequestPojo setPaymentItemId(Integer paymentItemId) {
        this.paymentItemId = paymentItemId;
        return this;
    }

    public Integer getProductId() {
        return productId;
    }

    public WebPayTransactionRequestPojo setProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public String getSiteRedirectUrl() {
        return siteRedirectUrl;
    }

    public WebPayTransactionRequestPojo setSiteRedirectUrl(String siteRedirectUrl) {
        this.siteRedirectUrl = siteRedirectUrl;
        return this;
    }

    public String getCustomerIdDescription() {
        return customerIdDescription;
    }

    public WebPayTransactionRequestPojo setCustomerIdDescription(String customerIdDescription) {
        this.customerIdDescription = customerIdDescription;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public WebPayTransactionRequestPojo setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerNameDescription() {
        return customerNameDescription;
    }

    public WebPayTransactionRequestPojo setCustomerNameDescription(String customerNameDescription) {
        this.customerNameDescription = customerNameDescription;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public WebPayTransactionRequestPojo setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public String getPaymentItemName() {
        return paymentItemName;
    }

    public WebPayTransactionRequestPojo setPaymentItemName(String paymentItemName) {
        this.paymentItemName = paymentItemName;
        return this;
    }

    public void computeHash(String macKey) {
        String message = transactionReference + productId + paymentItemId + amount + siteRedirectUrl + macKey;

        setHash(PaymentUtil.getHash(message, Constants.SHA_512_ALGORITHM_NAME));
    }
}
