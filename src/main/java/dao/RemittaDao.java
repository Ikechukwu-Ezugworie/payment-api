package dao;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.PaymentUtil;

import java.math.BigInteger;

@Singleton
public class RemittaDao extends BaseDao {

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static String REMITTA_MECHANT_ID = "REMITTA_MERCHANT_ID";
    private static String REMITTA_SERVICE_TYPE_ID = "SERVICE_TYPE_ID";
    private static String REMITTA_API_KEY = "REMITTA_API_KEY";



    public String getMerchantId(){
        return getSettingsValue(REMITTA_MECHANT_ID, "123456", Boolean.TRUE);
    }

    private String getRemittaCustomerToken(String orderId, BigInteger totalAmount ){
        if(orderId == null){
            throw new IllegalArgumentException("Order Id cannot be null");
        }

        StringBuilder customerToken = new StringBuilder();
        customerToken.append(getSettingsValue(REMITTA_MECHANT_ID,"123456", Boolean.TRUE));
        customerToken.append(getSettingsValue(REMITTA_SERVICE_TYPE_ID,"1234", Boolean.TRUE));
        customerToken.append(orderId.trim());
        customerToken.append(totalAmount.toString());
        customerToken.append(getSettingsValue(REMITTA_API_KEY,"7bd7d59cfe90e4d32b1d2f20d39c86df-fbaa8670-1008-ac7a-398a-3c11ac797c77", Boolean.TRUE));

        return customerToken.toString();
    }


    public String generateAutorisationHeader(String orderId, BigInteger totalAmount ){
        StringBuilder authHeader= new StringBuilder();
        authHeader.append("remitaConsumerKey").append("={").append(getMerchantId()).append("=}")
        .append(",")
        .append("remitaConsumerToken").append("={").append(PaymentUtil.getHash(getRemittaCustomerToken(orderId,totalAmount),Constants.SHA_512_ALGORITHM_NAME));

        logger.info("Remitta Authorisation is " + authHeader.toString());


        return authHeader.toString();

    }

    public String generateHash(String rrr){
        StringBuilder unHashed = new StringBuilder();

        unHashed.append(getSettingsValue(REMITTA_MECHANT_ID,"123456", Boolean.TRUE));
        unHashed.append(rrr);
        unHashed.append(getSettingsValue(REMITTA_API_KEY,"7bd7d59cfe90e4d32b1d2f20d39c86df-fbaa8670-1008-ac7a-398a-3c11ac797c77", Boolean.TRUE));

        logger.info("Hashed is " + unHashed.toString());

        return PaymentUtil.getHash(unHashed.toString(), Constants.SHA_512_ALGORITHM_NAME);

    }

}
