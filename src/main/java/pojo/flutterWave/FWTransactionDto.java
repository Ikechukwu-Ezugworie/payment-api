
package pojo.flutterWave;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FWTransactionDto {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("txRef")
    @Expose
    private String txRef;
    @SerializedName("orderRef")
    @Expose
    private String orderRef;
    @SerializedName("flwRef")
    @Expose
    private String flwRef;
    @SerializedName("redirectUrl")
    @Expose
    private String redirectUrl;
    @SerializedName("device_fingerprint")
    @Expose
    private String deviceFingerprint;
    @SerializedName("settlement_token")
    @Expose
    private Object settlementToken;
    @SerializedName("cycle")
    @Expose
    private String cycle;
    @SerializedName("amount")
    @Expose
    private Long amount;
    @SerializedName("charged_amount")
    @Expose
    private Long chargedAmount;
    @SerializedName("appfee")
    @Expose
    private Long appfee;
    @SerializedName("merchantfee")
    @Expose
    private Long merchantfee;
    @SerializedName("merchantbearsfee")
    @Expose
    private Long merchantbearsfee;
    @SerializedName("chargeResponseCode")
    @Expose
    private String chargeResponseCode;
    @SerializedName("chargeResponseMessage")
    @Expose
    private String chargeResponseMessage;
    @SerializedName("authModelUsed")
    @Expose
    private String authModelUsed;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("IP")
    @Expose
    private String iP;
    @SerializedName("narration")
    @Expose
    private String narration;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("vbvrespmessage")
    @Expose
    private String vbvrespmessage;
    @SerializedName("authurl")
    @Expose
    private String authurl;
    @SerializedName("vbvrespcode")
    @Expose
    private String vbvrespcode;
    @SerializedName("acctvalrespmsg")
    @Expose
    private Object acctvalrespmsg;
    @SerializedName("acctvalrespcode")
    @Expose
    private Object acctvalrespcode;
    @SerializedName("paymentType")
    @Expose
    private String paymentType;
    @SerializedName("paymentId")
    @Expose
    private String paymentId;
    @SerializedName("fraud_status")
    @Expose
    private String fraudStatus;
    @SerializedName("charge_type")
    @Expose
    private String chargeType;
    @SerializedName("is_live")
    @Expose
    private Long isLive;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("deletedAt")
    @Expose
    private Object deletedAt;
    @SerializedName("customerId")
    @Expose
    private Long customerId;
    @SerializedName("AccountId")
    @Expose
    private Long accountId;
    @SerializedName("customer")
    @Expose
    private FWCustomerDto customer;
    @SerializedName("chargeToken")
    @Expose
    private FWChargeTokenDto chargeToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxRef() {
        return txRef;
    }

    public void setTxRef(String txRef) {
        this.txRef = txRef;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getFlwRef() {
        return flwRef;
    }

    public void setFlwRef(String flwRef) {
        this.flwRef = flwRef;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public Object getSettlementToken() {
        return settlementToken;
    }

    public void setSettlementToken(Object settlementToken) {
        this.settlementToken = settlementToken;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getChargedAmount() {
        return chargedAmount;
    }

    public void setChargedAmount(Long chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    public Long getAppfee() {
        return appfee;
    }

    public void setAppfee(Long appfee) {
        this.appfee = appfee;
    }

    public Long getMerchantfee() {
        return merchantfee;
    }

    public void setMerchantfee(Long merchantfee) {
        this.merchantfee = merchantfee;
    }

    public Long getMerchantbearsfee() {
        return merchantbearsfee;
    }

    public void setMerchantbearsfee(Long merchantbearsfee) {
        this.merchantbearsfee = merchantbearsfee;
    }

    public String getChargeResponseCode() {
        return chargeResponseCode;
    }

    public void setChargeResponseCode(String chargeResponseCode) {
        this.chargeResponseCode = chargeResponseCode;
    }

    public String getChargeResponseMessage() {
        return chargeResponseMessage;
    }

    public void setChargeResponseMessage(String chargeResponseMessage) {
        this.chargeResponseMessage = chargeResponseMessage;
    }

    public String getAuthModelUsed() {
        return authModelUsed;
    }

    public void setAuthModelUsed(String authModelUsed) {
        this.authModelUsed = authModelUsed;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIP() {
        return iP;
    }

    public void setIP(String iP) {
        this.iP = iP;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVbvrespmessage() {
        return vbvrespmessage;
    }

    public void setVbvrespmessage(String vbvrespmessage) {
        this.vbvrespmessage = vbvrespmessage;
    }

    public String getAuthurl() {
        return authurl;
    }

    public void setAuthurl(String authurl) {
        this.authurl = authurl;
    }

    public String getVbvrespcode() {
        return vbvrespcode;
    }

    public void setVbvrespcode(String vbvrespcode) {
        this.vbvrespcode = vbvrespcode;
    }

    public Object getAcctvalrespmsg() {
        return acctvalrespmsg;
    }

    public void setAcctvalrespmsg(Object acctvalrespmsg) {
        this.acctvalrespmsg = acctvalrespmsg;
    }

    public Object getAcctvalrespcode() {
        return acctvalrespcode;
    }

    public void setAcctvalrespcode(Object acctvalrespcode) {
        this.acctvalrespcode = acctvalrespcode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getFraudStatus() {
        return fraudStatus;
    }

    public void setFraudStatus(String fraudStatus) {
        this.fraudStatus = fraudStatus;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public Long getIsLive() {
        return isLive;
    }

    public void setIsLive(Long isLive) {
        this.isLive = isLive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public FWCustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(FWCustomerDto customer) {
        this.customer = customer;
    }

    public FWChargeTokenDto getChargeToken() {
        return chargeToken;
    }

    public void setChargeToken(FWChargeTokenDto chargeToken) {
        this.chargeToken = chargeToken;
    }

}
