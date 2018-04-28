package pojo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * CREATED BY GIBAH
 */
public class ItemPojo {
    @NotBlank(message = "validation.not.blank")
    private String name;
    @NotBlank(message = "validation.not.blank")
    private String itemId;
    @NotNull(message = "validation.not.blank")
    private Integer quantity;
    @NotNull(message = "validation.not.blank")
    private Long priceInKobo;
    private Long taxInKobo;
    @NotNull(message = "validation.not.blank")
    private Long subTotalInKobo;
    @NotNull(message = "validation.not.blank")
    private Long totalInKobo;
    @NotBlank(message = "validation.not.blank")
    private String description;
    private String status;
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getPriceInKobo() {
        return priceInKobo;
    }

    public void setPriceInKobo(Long priceInKobo) {
        this.priceInKobo = priceInKobo;
    }

    public Long getTaxInKobo() {
        return taxInKobo;
    }

    public void setTaxInKobo(Long taxInKobo) {
        this.taxInKobo = taxInKobo;
    }

    public Long getSubTotalInKobo() {
        return subTotalInKobo;
    }

    public void setSubTotalInKobo(Long subTotalInKobo) {
        this.subTotalInKobo = subTotalInKobo;
    }

    public Long getTotalInKobo() {
        return totalInKobo;
    }

    public void setTotalInKobo(Long totalInKobo) {
        this.totalInKobo = totalInKobo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
