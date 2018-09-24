package pojo;

import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class MerchantRequestPojo {
    private Long id;
    private String code;
    @NotBlank(message = "validation.not.blank")
    private String name;
    private String dateCreated;
    private String lastModified;
    private String apiKey;
    private String paydirectMerchantReference;
    private String lookupUrl;
    private String notificationUrl;
    //    @NotNull
//    @Valid
    private List<PaymentProviderDetailsPojo> paymentProviders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public List<PaymentProviderDetailsPojo> getPaymentProviders() {
        return paymentProviders;
    }

    public void setPaymentProviders(List<PaymentProviderDetailsPojo> paymentProviders) {
        this.paymentProviders = paymentProviders;
    }

    public void addPaymentProvider(PaymentProviderDetailsPojo paymentProvider) {
        if (this.paymentProviders == null) {
            this.paymentProviders = new ArrayList<>();
        }
        this.paymentProviders.add(paymentProvider);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPaydirectMerchantReference() {
        return paydirectMerchantReference;
    }

    public void setPaydirectMerchantReference(String paydirectMerchantReference) {
        this.paydirectMerchantReference = paydirectMerchantReference;
    }

    public String getLookupUrl() {
        return lookupUrl;
    }

    public void setLookupUrl(String lookupUrl) {
        this.lookupUrl = lookupUrl;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }
}
