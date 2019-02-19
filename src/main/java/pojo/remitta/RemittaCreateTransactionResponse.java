package pojo.remitta;

public class RemittaCreateTransactionResponse {
    private String transactionId;
    private String paymentProviderReference;

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentProviderReference() {
        return paymentProviderReference;
    }

    public RemittaCreateTransactionResponse setPaymentProviderReference(String paymentProviderReference) {
        this.paymentProviderReference = paymentProviderReference;
        return this;
    }

    public RemittaCreateTransactionResponse setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
}
