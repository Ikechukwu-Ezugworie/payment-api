package dao;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.PaymentProviderDetails;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import pojo.MerchantRequestPojo;
import services.sequence.MerchantIdentifierSequence;
import utils.PaymentUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    public long getNumberOfMerchantRecords() {
        EntityManager entityManager = entityManagerProvider.get();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Merchant> root = cq.from(Merchant.class);

        cq.select(cb.count(root));

        return getCount(entityManager.createQuery(cq));
    }

}
