package pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MerchantRequestPojo {
    private Long id;
    private String identifier;
    private String name;
    private Timestamp dateCreated;
    private Timestamp lastModified;
    private List<PaymentProviderDetailsPojo> paymentProviders;

    public Long getId() {
        return id;
    }

    public MerchantRequestPojo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public MerchantRequestPojo setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getName() {
        return name;
    }

    public MerchantRequestPojo setName(String name) {
        this.name = name;
        return this;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public MerchantRequestPojo setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public MerchantRequestPojo setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public List<PaymentProviderDetailsPojo> getPaymentProviders() {
        return paymentProviders;
    }

    public MerchantRequestPojo setPaymentProviders(List<PaymentProviderDetailsPojo> paymentProviders) {
        this.paymentProviders = paymentProviders;
        return this;
    }

    public MerchantRequestPojo addPaymentProvider(PaymentProviderDetailsPojo paymentProvider) {
        if (this.paymentProviders == null) {
            this.paymentProviders = new ArrayList<>();
        }
        this.paymentProviders.add(paymentProvider);
        return this;
    }
}
