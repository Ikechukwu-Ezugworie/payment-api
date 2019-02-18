package pojo.remitta;

public class RemittaPaymentRequestPojo {
    private String merchantId;
    private String hash;
    private String rrr;
    private String responseurl;


    public String getMerchantId() {
        return merchantId;
    }

    public RemittaPaymentRequestPojo setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public RemittaPaymentRequestPojo setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getRrr() {
        return rrr;
    }

    public RemittaPaymentRequestPojo setRrr(String rrr) {
        this.rrr = rrr;
        return this;
    }

    public String getResponseurl() {
        return responseurl;
    }

    public RemittaPaymentRequestPojo setResponseurl(String responseurl) {
        this.responseurl = responseurl;
        return this;
    }
}
