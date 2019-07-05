package dao;

import com.bw.payment.entity.*;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.PaymentUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class RemittaDao extends BaseDao {


    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public static String REMITTA_MECHANT_ID = "REMITTA_MERCHANT_ID";
    public static String CBS_REMITTA_SERVICE_TYPE_ID = "CBS_REMITTA_SERVICE_TYPE_ID";
    public static String REMITTA_API_KEY = "REMITTA_API_KEY";



    public String getMerchantId(){
        return getRemittaCredentials().getMerchantId();
    }



    private String getRemittaCustomerToken(String orderId, String serviceTypeId, BigDecimal totalAmount ){
        if(StringUtils.isBlank(orderId)  || StringUtils.isBlank(serviceTypeId) ){
            throw new IllegalArgumentException("Order Id cannot be null");
        }

        StringBuilder customerToken = new StringBuilder();
        customerToken.append(getMerchantId());
        customerToken.append(serviceTypeId);
        customerToken.append(orderId.trim());
        customerToken.append(totalAmount.toPlainString());
        customerToken.append(getRemittaCredentials().getApiKey());

        logger.info(customerToken.toString());
        return customerToken.toString();
    }


    public String generateAutorisationHeader(String orderId, String serviceTypeId, BigDecimal totalAmountInNaira ){
        StringBuilder authHeader= new StringBuilder();
        authHeader.append("remitaConsumerKey").append("=").append(getMerchantId())
        .append(",")
        .append("remitaConsumerToken").append("=").append(PaymentUtil.getHash(getRemittaCustomerToken(orderId,serviceTypeId,totalAmountInNaira),Constants.SHA_512_ALGORITHM_NAME));

        logger.info("Remitta Authorisation is " + authHeader.toString());


        return authHeader.toString();

    }

    public String generateHash(String rrr){
        StringBuilder unHashed = new StringBuilder();


        unHashed.append(rrr);
        unHashed.append(getRemittaCredentials().getApiKey());
        unHashed.append(getRemittaCredentials().getMerchantId());


        logger.info("Hashed is " + unHashed.toString());

        return PaymentUtil.getHash(unHashed.toString(), Constants.SHA_512_ALGORITHM_NAME);

    }

    public String generateCardHash(String rrr){
        StringBuilder unHashed = new StringBuilder();

        unHashed.append(getRemittaCredentials().getMerchantId());
        unHashed.append(rrr);
        unHashed.append(getRemittaCredentials().getApiKey());
        logger.info("Hashed is " + unHashed.toString());

        return PaymentUtil.getHash(unHashed.toString(), Constants.SHA_512_ALGORITHM_NAME);

    }




    @Transactional
    public PaymentTransaction getPaymentTrnsactionByRRR(String rrr){
        PaymentTransaction paymentTransaction = getUniqueRecordByProperty(PaymentTransaction.class,"providerTransactionReference",rrr);
        return paymentTransaction;
    }

    public List<Item> getPaymentItemsByPaymentTransaction(PaymentTransaction paymentTransaction){
        System.out.println("{}{}{}{}" + paymentTransaction.getId());
       List<PaymentTransactionItem> paymentTransactions = getByProperty(PaymentTransactionItem.class, "paymentTransaction",paymentTransaction);
       logger.info(new Gson().toJson(paymentTransactions.stream()
               .map(it -> it.getItem().getId()).collect(Collectors.toSet())));
        return getPyPropertyIn(Item.class, "id", paymentTransactions.stream()
                .map(it -> it.getItem().getId()).collect(Collectors.toList()) );

    }

    public RemitaServiceCredentials getRemittaCredentials(){
        return getAllRecords(RemitaServiceCredentials.class).get(0);
    }

}
