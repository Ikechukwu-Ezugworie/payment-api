
package pojo.payDirect.paymentNotification.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public class PaymentItem {

    @JacksonXmlProperty(localName = "ItemName")
    private String itemName;
    @JacksonXmlProperty(localName = "ItemCode")
    private String itemCode;
    @JacksonXmlProperty(localName = "ItemAmount")
    private BigDecimal itemAmount;
    @JacksonXmlProperty(localName = "LeadBankCode")
    private String leadBankCode;
    @JacksonXmlProperty(localName = "LeadBankCbnCode")
    private String leadBankCbnCode;
    @JacksonXmlProperty(localName = "LeadBankName")
    private String leadBankName;
    @JacksonXmlProperty(localName = "CategoryCode")
    private String categoryCode;
    @JacksonXmlProperty(localName = "CategoryName")
    private String categoryName;
    @JacksonXmlProperty(localName = "ItemQuantity")
    private Integer itemQuantity;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getLeadBankCode() {
        return leadBankCode;
    }

    public void setLeadBankCode(String leadBankCode) {
        this.leadBankCode = leadBankCode;
    }

    public String getLeadBankCbnCode() {
        return leadBankCbnCode;
    }

    public void setLeadBankCbnCode(String leadBankCbnCode) {
        this.leadBankCbnCode = leadBankCbnCode;
    }

    public String getLeadBankName() {
        return leadBankName;
    }

    public void setLeadBankName(String leadBankName) {
        this.leadBankName = leadBankName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}
