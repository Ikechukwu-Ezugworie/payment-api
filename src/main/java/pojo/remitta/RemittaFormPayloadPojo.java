package pojo.remitta;

import com.google.gson.Gson;
import pojo.Item;

import java.io.Serializable;
import java.util.List;

/**

    Author: Oluwatobi Adenekan
    email:  tadenekan@byteworks.com.ng
    date:    25/02/2019

**/
public class RemittaFormPayloadPojo implements Serializable {

    private String merchantId;
    private String hash;
    private String rrr;
    private String responseurl;
    private String remittaFormActionUrl;
    private List<Item> items;
    private String amount;
    private String paymentTransactionReference;


    public String getPaymentTransactionReference() {
        return paymentTransactionReference;
    }

    public RemittaFormPayloadPojo setPaymentTransactionReference(String paymentTransactionReference) {
        this.paymentTransactionReference = paymentTransactionReference;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public RemittaFormPayloadPojo setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public RemittaFormPayloadPojo setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public RemittaFormPayloadPojo setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getRrr() {
        return rrr;
    }

    public RemittaFormPayloadPojo setRrr(String rrr) {
        this.rrr = rrr;
        return this;
    }

    public String getResponseurl() {
        return responseurl;
    }

    public RemittaFormPayloadPojo setResponseurl(String responseurl) {
        this.responseurl = responseurl;
        return this;
    }

    public String getRemittaFormActionUrl() {
        return remittaFormActionUrl;
    }

    public RemittaFormPayloadPojo setRemittaFormActionUrl(String remittaFormActionUrl) {
        this.remittaFormActionUrl = remittaFormActionUrl;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public RemittaFormPayloadPojo setItems(List<Item> items) {
        this.items = items;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
