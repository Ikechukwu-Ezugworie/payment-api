package pojo;

import java.util.ArrayList;
import java.util.List;

public class MerchantRequestPojo {
    private Long id;
    private String identifier;
    private String name;
    private String dateCreated;
    private String lastModified;
    private List<PaymentProviderDetailsPojo> paymentProviders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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
}
