package pojo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class MerchantRequestPojo {
    private Long id;
    private String code;
    @NotBlank(message = "validation.not.blank")
    private String name;
    private String dateCreated;
    private String lastModified;
    @NotNull
    @Valid
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
}
