package dao;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.MerchantProviderDetails;
import com.bw.payment.entity.PaymentProviderDetails;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import pojo.MerchantRequestPojo;
import pojo.PaymentProviderDetailsPojo;
import utils.PaymentUtil;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * CREATED BY GIBAH
 */
public class MerchantDao extends BaseDao {

    @Transactional
    public Merchant createMerchant(MerchantRequestPojo request) {
            Merchant merchant = new Merchant();
        merchant.setIdentifier(merchantIdentifierSequence.getNext());
            merchant.setName(request.getName());
            merchant.setDateCreated(Timestamp.from(Instant.now()));

        saveObject(merchant);

            for (PaymentProviderDetailsPojo paymentProviderDetailsPojo : request.getPaymentProviders()) {
                PaymentProviderDetails paymentProviderDetails = new PaymentProviderDetails();
                paymentProviderDetails.setName(PaymentProviderConstant.fromValue(paymentProviderDetailsPojo.getName()));
                paymentProviderDetails.setMerchantId(paymentProviderDetailsPojo.getMerchantId());
                paymentProviderDetails.setApiKey(paymentProviderDetailsPojo.getApiKey());
                paymentProviderDetails.setProviderUrl(paymentProviderDetailsPojo.getProviderUrl());
                paymentProviderDetails.setServiceUsername(paymentProviderDetailsPojo.getServiceUsername());
                paymentProviderDetails.setServicePassword(paymentProviderDetailsPojo.getServicePassword());

                MerchantProviderDetails merchantProviderDetails = new MerchantProviderDetails();
                merchantProviderDetails.setDateCreated(PaymentUtil.nowToTimeStamp());
                merchantProviderDetails.setStatus(GenericStatusConstant.ACTIVE);
                merchantProviderDetails.setMerchant(merchant);
                merchantProviderDetails.setPaymentProviderDetails(paymentProviderDetails);

                saveObject(paymentProviderDetails);
                saveObject(merchantProviderDetails);
            }

            return merchant;
    }
}
