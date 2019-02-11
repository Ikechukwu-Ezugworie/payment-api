package pojo.webPay;

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

    public Map<String, String> toMap() {
        return null;
    }
}
