
package pojo.payDirect.paymentNotification.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public class Payment {

    @JacksonXmlProperty(localName = "IsRepeated")
    private Boolean isRepeated;
    @JacksonXmlProperty(localName = "ProductGroupCode")
    private String productGroupCode;
    @JacksonXmlProperty(localName = "PaymentLogId")
    private Integer paymentLogId;
    @JacksonXmlProperty(localName = "CustReference")
    private String custReference;
    @JacksonXmlProperty(localName = "AlternateCustReference")
    private String alternateCustReference;
    @JacksonXmlProperty(localName = "Amount")
    private BigDecimal amount;
    @JacksonXmlProperty(localName = "PaymentStatus")
    private Integer paymentStatus;
    @JacksonXmlProperty(localName = "PaymentMethod")
    private String paymentMethod;
    @JacksonXmlProperty(localName = "PaymentReference")
    private String paymentReference;
    @JacksonXmlProperty(localName = "TerminalId")
    private String terminalId;
    @JacksonXmlProperty(localName = "ChannelName")
    private String channelName;
    @JacksonXmlProperty(localName = "Location")
    private String location;
    @JacksonXmlProperty(localName = "IsReversal")
    private Boolean isReversal;
    @JacksonXmlProperty(localName = "PaymentDate")
    private String paymentDate;
    @JacksonXmlProperty(localName = "SettlementDate")
    private String settlementDate;
    @JacksonXmlProperty(localName = "InstitutionId")
    private String institutionId;
    @JacksonXmlProperty(localName = "InstitutionName")
    private String institutionName;
    @JacksonXmlProperty(localName = "BranchName")
    private String branchName;
    @JacksonXmlProperty(localName = "BankName")
    private String bankName;
    @JacksonXmlProperty(localName = "FeeName")
    private String feeName;
    @JacksonXmlProperty(localName = "CustomerName")
    private String customerName;
    @JacksonXmlProperty(localName = "OtherCustomerInfo")
    private String otherCustomerInfo;
    @JacksonXmlProperty(localName = "ReceiptNo")
    private String receiptNo;
    @JacksonXmlProperty(localName = "CollectionsAccount")
    private String collectionsAccount;
    @JacksonXmlProperty(localName = "ThirdPartyCode")
    private String thirdPartyCode;
    @JacksonXmlProperty(localName = "BankCode")
    private String bankCode;
    @JacksonXmlProperty(localName = "CustomerAddress")
    private String customerAddress;
    @JacksonXmlProperty(localName = "CustomerPhoneNumber")
    private String customerPhoneNumber;
    @JacksonXmlProperty(localName = "DepositorName")
    private String depositorName;
    @JacksonXmlProperty(localName = "DepositSlipNumber")
    private String depositSlipNumber;
    @JacksonXmlProperty(localName = "PaymentCurrency")
    private String paymentCurrency;
    @JacksonXmlProperty(localName = "OriginalPaymentLogId")
    private Long originalPaymentLogId;
    @JacksonXmlProperty(localName = "OriginalPaymentReference")
    private String originalPaymentReference;
    @JacksonXmlProperty(localName = "Teller")
    private String teller;
    @JacksonXmlProperty(localName = "Status")
    private Integer status;
    @JacksonXmlProperty(localName = "PaymentItems")
    private PaymentItems paymentItems;

    public Boolean getRepeated() {
        return isRepeated;
    }

    public void setRepeated(Boolean repeated) {
        isRepeated = repeated;
    }

    public String getProductGroupCode() {
        return productGroupCode;
    }

    public void setProductGroupCode(String productGroupCode) {
        this.productGroupCode = productGroupCode;
    }

    public Integer getPaymentLogId() {
        return paymentLogId;
    }

    public void setPaymentLogId(Integer paymentLogId) {
        this.paymentLogId = paymentLogId;
    }

    public String getCustReference() {
        return custReference;
    }

    public void setCustReference(String custReference) {
        this.custReference = custReference;
    }

    public String getAlternateCustReference() {
        return alternateCustReference;
    }

    public void setAlternateCustReference(String alternateCustReference) {
        this.alternateCustReference = alternateCustReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getReversal() {
        return isReversal;
    }

    public void setReversal(Boolean reversal) {
        isReversal = reversal;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOtherCustomerInfo() {
        return otherCustomerInfo;
    }

    public void setOtherCustomerInfo(String otherCustomerInfo) {
        this.otherCustomerInfo = otherCustomerInfo;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getCollectionsAccount() {
        return collectionsAccount;
    }

    public void setCollectionsAccount(String collectionsAccount) {
        this.collectionsAccount = collectionsAccount;
    }

    public String getThirdPartyCode() {
        return thirdPartyCode;
    }

    public void setThirdPartyCode(String thirdPartyCode) {
        this.thirdPartyCode = thirdPartyCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getDepositorName() {
        return depositorName;
    }

    public void setDepositorName(String depositorName) {
        this.depositorName = depositorName;
    }

    public String getDepositSlipNumber() {
        return depositSlipNumber;
    }

    public void setDepositSlipNumber(String depositSlipNumber) {
        this.depositSlipNumber = depositSlipNumber;
    }

    public String getPaymentCurrency() {
        return paymentCurrency;
    }

    public void setPaymentCurrency(String paymentCurrency) {
        this.paymentCurrency = paymentCurrency;
    }

    public Long getOriginalPaymentLogId() {
        return originalPaymentLogId;
    }

    public void setOriginalPaymentLogId(Long originalPaymentLogId) {
        this.originalPaymentLogId = originalPaymentLogId;
    }

    public String getOriginalPaymentReference() {
        return originalPaymentReference;
    }

    public void setOriginalPaymentReference(String originalPaymentReference) {
        this.originalPaymentReference = originalPaymentReference;
    }

    public String getTeller() {
        return teller;
    }

    public void setTeller(String teller) {
        this.teller = teller;
    }

    public PaymentItems getPaymentItems() {
        return paymentItems;
    }

    public void setPaymentItems(PaymentItems paymentItems) {
        this.paymentItems = paymentItems;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PaymentPojo{" +
                "isRepeated=" + isRepeated +
                ", productGroupCode='" + productGroupCode + '\'' +
                ", paymentLogId=" + paymentLogId +
                ", custReference='" + custReference + '\'' +
                ", alternateCustReference='" + alternateCustReference + '\'' +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentReference='" + paymentReference + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", location='" + location + '\'' +
                ", isReversal=" + isReversal +
                ", paymentDate='" + paymentDate + '\'' +
                ", settlementDate='" + settlementDate + '\'' +
                ", institutionId='" + institutionId + '\'' +
                ", institutionName='" + institutionName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", bankName='" + bankName + '\'' +
                ", feeName='" + feeName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", otherCustomerInfo='" + otherCustomerInfo + '\'' +
                ", receiptNo='" + receiptNo + '\'' +
                ", collectionsAccount='" + collectionsAccount + '\'' +
                ", thirdPartyCode='" + thirdPartyCode + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerPhoneNumber='" + customerPhoneNumber + '\'' +
                ", depositorName='" + depositorName + '\'' +
                ", depositSlipNumber='" + depositSlipNumber + '\'' +
                ", paymentCurrency='" + paymentCurrency + '\'' +
                ", originalPaymentLogId=" + originalPaymentLogId +
                ", originalPaymentReference='" + originalPaymentReference + '\'' +
                ", teller='" + teller + '\'' +
                ", status=" + status +
                ", paymentItems=" + paymentItems +
                '}';
    }
}
