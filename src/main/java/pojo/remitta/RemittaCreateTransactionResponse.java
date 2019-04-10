package pojo.remitta;

public class RemittaCreateTransactionResponse {
    private String transactionId;
    private String rrr;

    public String getTransactionId() {
        return transactionId;
    }

    public String getRrr() {
        return rrr;
    }

    public RemittaCreateTransactionResponse setRrr(String rrr) {
        this.rrr = rrr;
        return this;
    }

    public RemittaCreateTransactionResponse setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
}
