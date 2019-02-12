package services;

import com.bw.payment.entity.Item;
import com.bw.payment.entity.Payer;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.inject.Inject;
import dao.BaseDao;
import dao.PaymentTransactionDao;
import ninja.utils.NinjaProperties;
import pojo.webPay.WebPayPaymentDataDto;
import pojo.webPay.WebPayTransactionRequestPojo;
import retrofit2.Response;
import services.api.WebPayApi;
import utils.Constants;
import utils.PaymentUtil;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class WebPayService {
    public static final String AMOUNT_FIELD = "amount"; //numeric
    public static final String CURRENCY = "currency";//566
    public static final String CUSTOMER_ID = "cust_id";// string
    public static final String HASH = "hash"; // string
    public static final String TRANSACTION_REF = "txn_ref";
    public static final String PAYMENT_ITEM_ID = "pay_item_id";//numeric
    public static final String PRODUCT_ID = "product_id";//numeric
    public static final String SITE_REDIRECT_URL = "site_redirect_url";
    public static final String CUSTOMER_ID_DESCRIPTION = "cust_id_desc";
    public static final String CUSTOMER_NAME = "cust_name";
    public static final String CUSTOMER_NAME_DESC = "cust_name_desc";
    public static final String LOCAL_DATE_TIME = "local_date_time";
    public static final String SITE_NAME = "site_name";
    public static final String PAYMENT_ITEM_NAME = "pay_item_name";
    private static String WEBPAY_PAYMENT_REQUEST_URL = "WEBPAY_PAYMENT_REQUEST_URL";

    private NinjaProperties ninjaProperties;
    //    private OkHttpClient client;
    private String webpayRequestUrl;
    private PaymentTransactionDao paymentTransactionDao;
    private PaymentTransactionService paymentTransactionService;
    private WebPayApi webPayApi;

    @Inject
    public WebPayService(NinjaProperties ninjaProperties, BaseDao baseDao, PaymentTransactionDao paymentTransactionDao, PaymentTransactionService paymentTransactionService, WebPayApi webPayApi) {
        this.ninjaProperties = ninjaProperties;
//        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
        webpayRequestUrl = baseDao.getSettingsValue(WEBPAY_PAYMENT_REQUEST_URL, "20", true);
        this.paymentTransactionDao = paymentTransactionDao;
        this.paymentTransactionService = paymentTransactionService;
        this.webPayApi = webPayApi;
    }

//    public void initiateTransaction(TransactionRequestPojo data) {
//        WebPayTransactionRequestPojo webPayTransactionRequestPojo=new WebPayTransactionRequestPojo();
//        webPayTransactionRequestPojo.setCustomerIdDescription("");
//        webPayTransactionRequestPojo.setCustomerName("");
//        webPayTransactionRequestPojo.setCustomerNameDescription("");
//        webPayTransactionRequestPojo.setSiteName("");
//        webPayTransactionRequestPojo.setPaymentItemName("");
//        webPayTransactionRequestPojo.setAmount(data.getAmountInKobo());
//        webPayTransactionRequestPojo.setCurrency(566);
//        webPayTransactionRequestPojo.setCustomerId(data.getTransactionId());
//        webPayTransactionRequestPojo.setTransactionReference(data.getTransactionId());
//        webPayTransactionRequestPojo.setPaymentItemId(data.get);
//        webPayTransactionRequestPojo.setProductId("");
//        webPayTransactionRequestPojo.setSiteRedirectUrl("");
//
//        webPayTransactionRequestPojo.setHash("");

//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart(AMOUNT_FIELD, "someValue")
//                .addFormDataPart(CURRENCY, "someValue")
//                .addFormDataPart(CUSTOMER_ID, "someValue")
//                .addFormDataPart(HASH, "someValue")
//                .addFormDataPart(TRANSACTION_REF, "someValue")
//                .addFormDataPart(PAYMENT_ITEM_ID, "someValue")
//                .addFormDataPart(PRODUCT_ID, "someValue")
//                .addFormDataPart(SITE_REDIRECT_URL, "someValue")
//                .addFormDataPart(CUSTOMER_ID_DESCRIPTION, "someValue")
//                .addFormDataPart(CUSTOMER_NAME, "someValue")
//                .addFormDataPart(CUSTOMER_NAME_DESC, "someValue")
//                .addFormDataPart(LOCAL_DATE_TIME, "someValue")
//                .addFormDataPart(SITE_NAME, "someValue")
//                .addFormDataPart(PAYMENT_ITEM_NAME, "someValue")
//                .build();
//
//        Request request = new Request.Builder()
//                .url(webpayRequestUrl)
//                .post(requestBody)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public WebPayTransactionRequestPojo createWebPayRequest(PaymentTransaction paymentTransaction) {
        WebPayTransactionRequestPojo webPayTransactionRequestPojo = new WebPayTransactionRequestPojo();
        webPayTransactionRequestPojo.setAmount(paymentTransaction.getAmountInKobo());
        webPayTransactionRequestPojo.setCustomerId(paymentTransaction.getCustomerTransactionReference());
        webPayTransactionRequestPojo.setTransactionReference(paymentTransaction.getTransactionId());
        for (Item paymentTransactionItem : paymentTransactionDao.getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE)) {
            webPayTransactionRequestPojo.setPaymentItemId(Integer.valueOf(paymentTransactionItem.getItemId()));
        }
        webPayTransactionRequestPojo.setProductId(Integer.valueOf(paymentTransaction.getServiceTypeId()));
        webPayTransactionRequestPojo.setSiteRedirectUrl(paymentTransactionDao.getSettingsValue(Constants.WEB_PAY_REDIRECT_URL_SETTINGS_KEY, "- MODIFY -", true));
//        webPayTransactionRequestPojo.setCustomerIdDescription("");
        if (paymentTransaction.getPayer() != null) {
            Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
            webPayTransactionRequestPojo.setCustomerName(PaymentUtil.getFormattedFullName(payer.getFirstName(), payer.getLastName()));
        }

        String mac = paymentTransactionDao.getSettingsValue(Constants.WEB_PAY_MAC_SETTINGS_KEY, "E187B1191265B18338B5DEBAF9F38FEC37B170FF582D4666DAB1F098304D5EE7F3BE15540461FE92F1D40332FDBBA34579034EE2AC78B1A1B8D9A321974025C4", true);
        webPayTransactionRequestPojo.computeHash(mac);

//        webPayTransactionRequestPojo.setCustomerNameDescription("");
//        webPayTransactionRequestPojo.setSiteName("");
//        webPayTransactionRequestPojo.setPaymentItemName("");

        return webPayTransactionRequestPojo;

    }

    public WebPayPaymentDataDto getPaymentData(PaymentTransaction paymentTransaction) {
        String mac = paymentTransactionDao.getSettingsValue(Constants.WEB_PAY_MAC_SETTINGS_KEY, "E187B1191265B18338B5DEBAF9F38FEC37B170FF582D4666DAB1F098304D5EE7F3BE15540461FE92F1D40332FDBBA34579034EE2AC78B1A1B8D9A321974025C4", true);
        String message = paymentTransaction.getServiceTypeId() + paymentTransaction.getAmountInKobo() + mac;
        retrofit2.Call<WebPayPaymentDataDto> transactionStatus = webPayApi.getTransactionStatus(paymentTransaction.getServiceTypeId(),
                paymentTransaction.getAmountInKobo(), paymentTransaction.getTransactionId(), PaymentUtil.getHash(message, Constants.SHA_512_ALGORITHM_NAME));

        try {
            Response<WebPayPaymentDataDto> response = transactionStatus.execute();
            if (response.code() == 200) {
                return response.body();
            }
            throw new IllegalArgumentException(response.code() + " : " + response.message());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }
}
