package pojo.payDirect.customerValidation.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public class Item {
    @JacksonXmlProperty(localName = "ProductName")
    private String productName;
    @JacksonXmlProperty(localName = "ProductCode")
    private String productCode;
    @JacksonXmlProperty(localName = "Quantity")
    private Long quantity;
    @JacksonXmlProperty(localName = "Price")
    private BigDecimal price;
    @JacksonXmlProperty(localName = "subtotal")
    private BigDecimal subtotal;
    @JacksonXmlProperty(localName = "tax")
    private BigDecimal tax;
    @JacksonXmlProperty(localName = "total")
    private BigDecimal total;


    public String getProductName() {
        return productName;
    }

    public Item setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductCode() {
        return productCode;
    }

    public Item setProductCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Item setQuantity(Long quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Item setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Item setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
        return this;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public Item setTax(BigDecimal tax) {
        this.tax = tax;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Item setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }
}
