package pojo.quickTeller;

/**
 * CREATED BY GIBAH
 */
public class QTPaymentNotificationPojo {
    private String resp_code;
    private String resp_desc;
    private String tx_ref;
    private String recharge_pin;
    private String short_trans_ref;
    private String client_id;
    private Long amount;
    private String signature;

    public String getResp_code() {
        return resp_code;
    }

    public void setResp_code(String resp_code) {
        this.resp_code = resp_code;
    }

    public String getResp_desc() {
        return resp_desc;
    }

    public void setResp_desc(String resp_desc) {
        this.resp_desc = resp_desc;
    }

    public String getTx_ref() {
        return tx_ref;
    }

    public void setTx_ref(String tx_ref) {
        this.tx_ref = tx_ref;
    }

    public String getRecharge_pin() {
        return recharge_pin;
    }

    public void setRecharge_pin(String recharge_pin) {
        this.recharge_pin = recharge_pin;
    }

    public String getShort_trans_ref() {
        return short_trans_ref;
    }

    public void setShort_trans_ref(String short_trans_ref) {
        this.short_trans_ref = short_trans_ref;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "QTPaymentNotificationPojo{" +
                "resp_code='" + resp_code + '\'' +
                ", resp_desc='" + resp_desc + '\'' +
                ", tx_ref='" + tx_ref + '\'' +
                ", recharge_pin='" + recharge_pin + '\'' +
                ", short_trans_ref='" + short_trans_ref + '\'' +
                ", client_id='" + client_id + '\'' +
                ", amount=" + amount +
                ", signature='" + signature + '\'' +
                '}';
    }
}
