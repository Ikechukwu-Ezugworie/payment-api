package pojo;

import constraints.PaymentChannel;
import constraints.PaymentProvider;
import org.hibernate.validator.constraints.NotBlank;
import pojo.flutterWave.SplitDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class TransactionRequestPojo {
    private Long id;
    private String transactionId;
    private String dateCreated;
    private String lastUpdated;
    private String providerTransactionReference;
    private String merchantTransactionReferenceId;
    @NotNull(message = "validation.not.null")
    private Long amountInKobo;
    private Boolean notifyOnStatusChange = false;
    private String notificationUrl;
    @NotBlank(message = "validation.not.null")
    @PaymentProvider
    private String paymentProvider;
    @NotBlank(message = "validation.not.null")
    @PaymentChannel
    private String paymentChannel;
    private String serviceTypeId;
    private MerchantRequestPojo merchant;
    private String paymentTransactionStatus;
    @NotNull(message = "validation.not.null")
    @Valid
    private PayerPojo payer;
    @Valid
    private List<ItemPojo> items;
    private Boolean validateTransaction;
    private String transactionValidationUrl;
    private boolean instantTransaction = false;
    private String customerTransactionReference;
    private List<SplitDto> split;
    private String currencyCode = "NGN";

    public String getCurrencyCode() {
        return currencyCode;
    }

    public TransactionRequestPojo setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getMerchantTransactionReferenceId() {
        return merchantTransactionReferenceId;
    }

    public void setMerchantTransactionReferenceId(String merchantTransactionReferenceId) {
        this.merchantTransactionReferenceId = merchantTransactionReferenceId;
    }

    public Long getAmountInKobo() {
        return amountInKobo;
    }

    public void setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
    }

    public Boolean getNotifyOnStatusChange() {
        return notifyOnStatusChange;
    }

    public void setNotifyOnStatusChange(Boolean notifyOnStatusChange) {
        this.notifyOnStatusChange = notifyOnStatusChange;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public MerchantRequestPojo getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantRequestPojo merchant) {
        this.merchant = merchant;
    }

    public PayerPojo getPayer() {
        return payer;
    }

    public void setPayer(PayerPojo payer) {
        this.payer = payer;
    }

    public List<ItemPojo> getItems() {
        return items;
    }

    public void setItems(List<ItemPojo> items) {
        this.items = items;
    }

    public void addItem(ItemPojo item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public String getPaymentTransactionStatus() {
        return paymentTransactionStatus;
    }

    public void setPaymentTransactionStatus(String paymentTransactionStatus) {
        this.paymentTransactionStatus = paymentTransactionStatus;
    }

    public Boolean getValidateTransaction() {
        return validateTransaction;
    }

    public void setValidateTransaction(Boolean validateTransaction) {
        this.validateTransaction = validateTransaction;
    }

    public String getTransactionValidationUrl() {
        return transactionValidationUrl;
    }

    public void setTransactionValidationUrl(String transactionValidationUrl) {
        this.transactionValidationUrl = transactionValidationUrl;
    }

    public boolean isInstantTransaction() {
        return instantTransaction;
    }

    public void setInstantTransaction(boolean instantTransaction) {
        this.instantTransaction = instantTransaction;
    }

    public String getProviderTransactionReference() {
        return providerTransactionReference;
    }

    public void setProviderTransactionReference(String providerTransactionReference) {
        this.providerTransactionReference = providerTransactionReference;
    }

    public String getCustomerTransactionReference() {
        return customerTransactionReference;
    }

    public void setCustomerTransactionReference(String customerTransactionReference) {
        this.customerTransactionReference = customerTransactionReference;
    }

    public List<SplitDto> getSplit() {
        return split;
    }

    public TransactionRequestPojo setSplit(List<SplitDto> split) {
        this.split = split;
        return this;
    }

    public TransactionRequestPojo addSplit(SplitDto split) {
        if (this.split == null) {
            this.split = new ArrayList<>();
        }
        this.split.add(split);
        return this;
    }
}
