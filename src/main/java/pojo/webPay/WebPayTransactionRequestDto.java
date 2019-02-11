package pojo.webPay;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class WebPayTransactionRequestDto {
    private Long amount;
    private int currency;
    private int cust_id;
    private String hash;
    private String txn_ref;
    private Integer pay_item_id;
    private Integer product_id;
    private String site_redirect_url;

    //    optional
    private String cust_id_desc;
    private String cust_name;
    private String cust_name_desc;
    private String site_name;
    private String pay_item_name;

    public Long getAmount() {
        return amount;
    }

    public WebPayTransactionRequestDto setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public int getCurrency() {
        return currency;
    }

    public WebPayTransactionRequestDto setCurrency(int currency) {
        this.currency = currency;
        return this;
    }

    public int getCust_id() {
        return cust_id;
    }

    public WebPayTransactionRequestDto setCust_id(int cust_id) {
        this.cust_id = cust_id;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public WebPayTransactionRequestDto setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getTxn_ref() {
        return txn_ref;
    }

    public WebPayTransactionRequestDto setTxn_ref(String txn_ref) {
        this.txn_ref = txn_ref;
        return this;
    }

    public Integer getPay_item_id() {
        return pay_item_id;
    }

    public WebPayTransactionRequestDto setPay_item_id(Integer pay_item_id) {
        this.pay_item_id = pay_item_id;
        return this;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public WebPayTransactionRequestDto setProduct_id(Integer product_id) {
        this.product_id = product_id;
        return this;
    }

    public String getSite_redirect_url() {
        return site_redirect_url;
    }

    public WebPayTransactionRequestDto setSite_redirect_url(String site_redirect_url) {
        this.site_redirect_url = site_redirect_url;
        return this;
    }

    public String getCust_id_desc() {
        return cust_id_desc;
    }

    public WebPayTransactionRequestDto setCust_id_desc(String cust_id_desc) {
        this.cust_id_desc = cust_id_desc;
        return this;
    }

    public String getCust_name() {
        return cust_name;
    }

    public WebPayTransactionRequestDto setCust_name(String cust_name) {
        this.cust_name = cust_name;
        return this;
    }

    public String getCust_name_desc() {
        return cust_name_desc;
    }

    public WebPayTransactionRequestDto setCust_name_desc(String cust_name_desc) {
        this.cust_name_desc = cust_name_desc;
        return this;
    }

    public String getSite_name() {
        return site_name;
    }

    public WebPayTransactionRequestDto setSite_name(String site_name) {
        this.site_name = site_name;
        return this;
    }

    public String getPay_item_name() {
        return pay_item_name;
    }

    public WebPayTransactionRequestDto setPay_item_name(String pay_item_name) {
        this.pay_item_name = pay_item_name;
        return this;
    }
}
