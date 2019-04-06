package pojo.flutterWave;

import java.util.List;
import java.util.Map;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class FWPaymentRequestDto {
    private String pubKey;
    private String integrityHash;
    private String transactionReference;
    private String paymentOptions = "Card";
    private String paymentPlan;//	false	This is the payment plan ID used for Recurring billing ].
    private String subAccounts;// false	This is an array of objects containing the subaccount IDs to split the payment into.
    private Long amount;//	true	Amount to charge.
    private String currency = "USD";//	false	currency to charge in. Defaults to NGN
    private String country = "NG";//	false	route country. Defaults to NG
    private String customerEmail;//	true	Email of the customer.
    private String customerPhone;//	true	phone number of the customer.
    private String customerFirstname;//	false	firstname of the customer.
    private String customerLastname;//	false	lastname of the customer.
    private String payButtonText;//	false	Text to be displayed on the Rave Checkout Button.
    private String customTitle;//	false	Text to be displayed as the title of the payment modal.
    private String customDescription;//	false	Text to be displayed as a short modal description.
    private String redirectUrl;//	false	URL to redirect to when a transaction is completed. This is useful for 3DSecure payments so we can redirect your customer back to a custom page you want to show them.
    private String customLogo;//	false	Link to the Logo image.
    private List<SplitDto> split;//
    private Map<String, Object> meta;// meta:[{metaname:‘flightid’,metavalue:‘93849-MK5000’}]

    public String getPubKey() {
        return pubKey;
    }

    public List<SplitDto> getSplit() {
        return split;
    }

    public FWPaymentRequestDto setSplit(List<SplitDto> split) {
        this.split = split;
        return this;
    }

    public FWPaymentRequestDto setPubKey(String pubKey) {
        this.pubKey = pubKey;
        return this;
    }

    public String getIntegrityHash() {
        return integrityHash;
    }

    public FWPaymentRequestDto setIntegrityHash(String integrityHash) {
        this.integrityHash = integrityHash;
        return this;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public FWPaymentRequestDto setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public String getPaymentOptions() {
        return paymentOptions;
    }

    public FWPaymentRequestDto setPaymentOptions(String paymentOptions) {
        this.paymentOptions = paymentOptions;
        return this;
    }

    public String getPaymentPlan() {
        return paymentPlan;
    }

    public FWPaymentRequestDto setPaymentPlan(String paymentPlan) {
        this.paymentPlan = paymentPlan;
        return this;
    }

    public String getSubAccounts() {
        return subAccounts;
    }

    public FWPaymentRequestDto setSubAccounts(String subAccounts) {
        this.subAccounts = subAccounts;
        return this;
    }

    public Long getAmount() {
        return amount;
    }

    public FWPaymentRequestDto setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public FWPaymentRequestDto setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public FWPaymentRequestDto setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public FWPaymentRequestDto setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public FWPaymentRequestDto setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getCustomerFirstname() {
        return customerFirstname;
    }

    public FWPaymentRequestDto setCustomerFirstname(String customerFirstname) {
        this.customerFirstname = customerFirstname;
        return this;
    }

    public String getCustomerLastname() {
        return customerLastname;
    }

    public FWPaymentRequestDto setCustomerLastname(String customerLastname) {
        this.customerLastname = customerLastname;
        return this;
    }

    public String getPayButtonText() {
        return payButtonText;
    }

    public FWPaymentRequestDto setPayButtonText(String payButtonText) {
        this.payButtonText = payButtonText;
        return this;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public FWPaymentRequestDto setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
        return this;
    }

    public String getCustomDescription() {
        return customDescription;
    }

    public FWPaymentRequestDto setCustomDescription(String customDescription) {
        this.customDescription = customDescription;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public FWPaymentRequestDto setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public String getCustomLogo() {
        return customLogo;
    }

    public FWPaymentRequestDto setCustomLogo(String customLogo) {
        this.customLogo = customLogo;
        return this;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public FWPaymentRequestDto setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }
}
