package dao;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.MerchantProviderDetails;
import com.bw.payment.entity.PaymentProviderDetails;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import pojo.MerchantRequestPojo;
import pojo.PaymentProviderDetailsPojo;
import services.PasswordService;
import services.sequence.MerchantIdentifierSequence;
import utils.PaymentUtil;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * CREATED BY GIBAH
 */
public class MerchantDao extends BaseDao {

    @Inject
    protected MerchantIdentifierSequence merchantIdentifierSequence;

    @Transactional
    public Merchant createMerchant(MerchantRequestPojo request) {
        Merchant merchant = getUniqueRecordByProperty(Merchant.class, "name", request.getName());
        if (merchant != null) {
            throw new IllegalArgumentException("Merchant with same name already exists");
        }
        merchant = new Merchant();
        merchant.setCode(merchantIdentifierSequence.getNext());
        merchant.setApiKey(PaymentUtil.generateApiKey());
        merchant.setName(request.getName());
        merchant.setDateCreated(Timestamp.from(Instant.now()));
        merchant.setPaydirectMerchantReference(request.getPaydirectMerchantReference());
        merchant.setLookupUrl(request.getLookupUrl());
        merchant.setNotificationUrl(request.getNotificationUrl());

        entityManagerProvider.get().persist(merchant);

//        for (PaymentProviderDetailsPojo paymentProviderDetailsPojo : request.getPaymentProviders()) {
//            PaymentProviderDetails paymentProviderDetails = getMerchantPaymentProviderDetails(merchant.getId(),
//                    PaymentProviderConstant.fromValue(paymentProviderDetailsPojo.getName()));
//
//            if (paymentProviderDetails != null) {
//                throw new IllegalArgumentException("Payment provider details already exist for " + paymentProviderDetails.getName().getValue());
//            }
//
//            paymentProviderDetails = new PaymentProviderDetails();
//            paymentProviderDetails.setName(PaymentProviderConstant.fromValue(paymentProviderDetailsPojo.getName()));
//            paymentProviderDetails.setMerchantId(paymentProviderDetailsPojo.getMerchantId());
//            paymentProviderDetails.setApiKey(paymentProviderDetailsPojo.getApiKey());
//            paymentProviderDetails.setProviderUrl(paymentProviderDetailsPojo.getProviderUrl());
//            paymentProviderDetails.setServiceUsername(paymentProviderDetailsPojo.getServiceUsername());
//            paymentProviderDetails.setServicePassword(PasswordService.hashPassword(paymentProviderDetailsPojo.getServicePassword()));
//
//            MerchantProviderDetails merchantProviderDetails = new MerchantProviderDetails();
//            merchantProviderDetails.setDateCreated(PaymentUtil.nowToTimeStamp());
//            merchantProviderDetails.setStatus(GenericStatusConstant.ACTIVE);
//            merchantProviderDetails.setMerchant(merchant);
//            merchantProviderDetails.setPaymentProviderDetails(paymentProviderDetails);
//
//            entityManagerProvider.get().persist(paymentProviderDetails);
//            entityManagerProvider.get().persist(merchantProviderDetails);
//        }

        return merchant;
    }

    public Merchant getMerchantByCode(String merchantIdentifier) {
        return getUniqueRecordByProperty(Merchant.class, "code", merchantIdentifier);
    }

    public PaymentProviderDetails getMerchantPaymentProviderDetails(Long merchantId, PaymentProviderConstant provider) {
        Query q = entityManagerProvider.get().createQuery("select ppd from PaymentProviderDetails ppd, MerchantProviderDetails mpd where" +
                " mpd.merchant.id=:mid and mpd.paymentProviderDetails.name=:pp and mpd.paymentProviderDetails.id=ppd.id");

        q.setParameter("mid", merchantId).setParameter("pp", provider.getValue());

        return uniqueResultOrNull(q, PaymentProviderDetails.class);
    }

}
