package dao;

import com.bw.payment.entity.PaymentTransaction;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.PaymentUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

@Singleton
public class RemittaDao extends BaseDao {

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public static String REMITTA_MECHANT_ID = "REMITTA_MERCHANT_ID";
    public static String CBS_REMITTA_SERVICE_TYPE_ID = "CBS_REMITTA_SERVICE_TYPE_ID";
    public static String REMITTA_API_KEY = "REMITTA_API_KEY";



    public String getMerchantId(){
        return getSettingsValue(REMITTA_MECHANT_ID, "2547916", Boolean.TRUE);
    }

    private String getRemittaCustomerToken(String orderId, String serviceTypeId, BigInteger totalAmount ){
        if(StringUtils.isBlank(orderId)  || StringUtils.isBlank(serviceTypeId) ){
            throw new IllegalArgumentException("Order Id cannot be null");
        }

        StringBuilder customerToken = new StringBuilder();
        customerToken.append(getMerchantId());
        customerToken.append(serviceTypeId);
        customerToken.append(orderId.trim());
        customerToken.append(totalAmount);
        customerToken.append(getSettingsValue(REMITTA_API_KEY,"1956", Boolean.TRUE));

        logger.info(customerToken.toString());
        return customerToken.toString();
    }


    public String generateAutorisationHeader(String orderId, String serviceTypeId, BigInteger totalAmountInNaira ){
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
        unHashed.append(getSettingsValue(REMITTA_API_KEY,"7bd7d59cfe90e4d32b1d2f20d39c86df-fbaa8670-1008-ac7a-398a-3c11ac797c77", Boolean.TRUE));
        unHashed.append(getSettingsValue(REMITTA_MECHANT_ID,"123456", Boolean.TRUE));


        logger.info("Hashed is " + unHashed.toString());

        return PaymentUtil.getHash(unHashed.toString(), Constants.SHA_512_ALGORITHM_NAME);

    }

    @Transactional
    public PaymentTransaction getPaymentTrnsactionByRRR(String rrr){
        PaymentTransaction paymentTransaction = getUniqueRecordByProperty(PaymentTransaction.class,"providerTransactionReference",rrr);
        return paymentTransaction;
    }

}
