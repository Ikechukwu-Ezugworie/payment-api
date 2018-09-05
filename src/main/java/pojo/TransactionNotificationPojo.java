package pojo;

/**
 * CREATED BY GIBAH
 */
public class TransactionNotificationPojo<T> {
    private String status;
    private String transactionId;
    private String datePaymentReceived;
    private String receiptNumber;
    private Long amountPaidInKobo;
    private String paymentProvider;
    private String paymentProviderTransactionId;
    private boolean isReversal = false;
    private String paymentDate;
    private String settlementDate;
    private String paymentChannelName;
    private String paymentProviderPaymentReference;
    private String paymentMethod;
    private String notificationId;
    private String description;
    private String merchantTransactionReference;
    private String customerTransactionReference;
    private PayerPojo payer;

    private T actualNotification;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDatePaymentReceived() {
        return datePaymentReceived;
    }

    public void setDatePaymentReceived(String datePaymentReceived) {
        this.datePaymentReceived = datePaymentReceived;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Long getAmountPaidInKobo() {
        return amountPaidInKobo;
    }

    public void setAmountPaidInKobo(Long amountPaidInKobo) {
        this.amountPaidInKobo = amountPaidInKobo;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentProviderTransactionId() {
        return paymentProviderTransactionId;
    }

    public void setPaymentProviderTransactionId(String paymentProviderTransactionId) {
        this.paymentProviderTransactionId = paymentProviderTransactionId;
    }

    public boolean isReversal() {
        return isReversal;
    }

    public void setReversal(boolean reversal) {
        isReversal = reversal;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getPaymentChannelName() {
        return paymentChannelName;
    }

    public void setPaymentChannelName(String paymentChannelName) {
        this.paymentChannelName = paymentChannelName;
    }

    public String getPaymentProviderPaymentReference() {
        return paymentProviderPaymentReference;
    }

    public void setPaymentProviderPaymentReference(String paymentProviderPaymentReference) {
        this.paymentProviderPaymentReference = paymentProviderPaymentReference;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public PayerPojo getPayer() {
        return payer;
    }

    public void setPayer(PayerPojo payer) {
        this.payer = payer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchantTransactionReference() {
        return merchantTransactionReference;
    }

    public void setMerchantTransactionReference(String merchantTransactionReference) {
        this.merchantTransactionReference = merchantTransactionReference;
    }

    public String getCustomerTransactionReference() {
        return customerTransactionReference;
    }

    public void setCustomerTransactionReference(String customerTransactionReference) {
        this.customerTransactionReference = customerTransactionReference;
    }

    public T getActualNotification() {
        return actualNotification;
    }

    public void setActualNotification(T actualNotification) {
        this.actualNotification = actualNotification;
    }

    @Override
    public String toString() {
        return "TransactionNotificationPojo{" +
                "status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", datePaymentReceived='" + datePaymentReceived + '\'' +
                ", receiptNumber='" + receiptNumber + '\'' +
                ", amountPaidInKobo=" + amountPaidInKobo +
                ", paymentProvider='" + paymentProvider + '\'' +
                ", paymentProviderTransactionId='" + paymentProviderTransactionId + '\'' +
                ", isReversal=" + isReversal +
                ", paymentDate='" + paymentDate + '\'' +
                ", settlementDate='" + settlementDate + '\'' +
                ", paymentChannelName='" + paymentChannelName + '\'' +
                ", paymentProviderPaymentReference='" + paymentProviderPaymentReference + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", notificationId='" + notificationId + '\'' +
                ", description='" + description + '\'' +
                ", merchantTransactionReference='" + merchantTransactionReference + '\'' +
                ", customerTransactionReference='" + customerTransactionReference + '\'' +
                ", payer=" + payer +
                ", actualNotification=" + actualNotification +
                '}';
    }
}
