package pojo;

import constraints.PaymentProvider;
import org.hibernate.validator.constraints.NotBlank;

public class PaymentProviderDetailsPojo {
    private Long id;
    @NotBlank(message = "validation.not.blank")
    @PaymentProvider
    private String name;
    @NotBlank(message = "validation.not.blank")
    private String merchantId;
    private String apiKey;
    @NotBlank(message = "validation.not.blank")
    private String providerUrl;
    private String serviceUsername;
    private String servicePassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public String getServiceUsername() {
        return serviceUsername;
    }

    public void setServiceUsername(String serviceUsername) {
        this.serviceUsername = serviceUsername;
    }

    public String getServicePassword() {
        return servicePassword;
    }

    public void setServicePassword(String servicePassword) {
        this.servicePassword = servicePassword;
    }
}
