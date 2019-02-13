package pojo.webPay;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class WebPayTransactionResponsePojo {
    private Long apprAmnt;
    private Long cardNum;
    private String payRef;
    private String retRef;
    private String txnref;
    private String url;
    private String amt;

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public Long getApprAmnt() {
        return apprAmnt;
    }

    public WebPayTransactionResponsePojo setApprAmnt(Long apprAmnt) {
        this.apprAmnt = apprAmnt;
        return this;
    }

    public Long getCardNum() {
        return cardNum;
    }

    public WebPayTransactionResponsePojo setCardNum(Long cardNum) {
        this.cardNum = cardNum;
        return this;
    }

    public String getPayRef() {
        return payRef;
    }

    public WebPayTransactionResponsePojo setPayRef(String payRef) {
        this.payRef = payRef;
        return this;
    }

    public String getRetRef() {
        return retRef;
    }

    public WebPayTransactionResponsePojo setRetRef(String retRef) {
        this.retRef = retRef;
        return this;
    }

    public String getTxnref() {
        return txnref;
    }

    public WebPayTransactionResponsePojo setTxnref(String txnref) {
        this.txnref = txnref;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public WebPayTransactionResponsePojo setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("apprAmnt", apprAmnt)
                .append("cardNum", cardNum)
                .append("payRef", payRef)
                .append("retRef", retRef)
                .append("txnref", txnref)
                .append("url", url)
                .toString();
    }
}
