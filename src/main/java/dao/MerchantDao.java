package dao;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.MerchantProviderDetails;
import com.bw.payment.entity.PaymentProviderDetails;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import org.hibernate.Session;
import pojo.MerchantRequestPojo;
import pojo.PaymentProviderDetailsPojo;
import utils.PaymentUtil;
import utils.SequenceService;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * CREATED BY GIBAH
 */
public class MerchantDao extends BaseDao {


    public Merchant createMerchant(MerchantRequestPojo request) {
        return transactionManager.doForResult(session -> {
            Merchant merchant = new Merchant();
            merchant.setIdentifier(generateMerchantId(session));
            merchant.setName(request.getName());
            merchant.setDateCreated(Timestamp.from(Instant.now()));

            session.save(merchant);

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

                session.save(paymentProviderDetails);
                session.save(merchantProviderDetails);
            }

            return merchant;
        });
    }


    public String generateMerchantId() {
        return transactionManager.doForResult(session -> {
            SequenceService sequenceService = new SequenceService(session, "merchant_identifier");
            return sequenceService.getNextId("%09d");
        });
    }

    public String generateMerchantId(Session session) {
        SequenceService sequenceService = new SequenceService(session, "merchant_identifier");
        return sequenceService.getNextId("%09d");
    }
}
