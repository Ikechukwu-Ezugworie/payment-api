package pojo;

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
    private String merchantTransactionReferenceId;
    private Long amountInKobo;
    private Boolean notifyOnStatusChange = false;
    private String notificationUrl;
    private String paymentProvider;
    private String paymentChannel;
    private String serviceTypeId;
    private MerchantRequestPojo merchant;
    private String paymentTransactionStatus;
    private PayerPojo payer;
    private List<ItemPojo> items;

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
}
