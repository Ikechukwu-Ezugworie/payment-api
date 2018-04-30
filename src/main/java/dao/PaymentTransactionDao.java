package dao;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import pojo.ItemPojo;
import pojo.TransactionRequestPojo;
import pojo.payDirect.paymentNotification.request.Payment;
import services.sequence.PayerIdSequence;
import services.sequence.TransactionIdSequence;
import utils.PaymentUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class PaymentTransactionDao extends BaseDao {

    @Inject
    protected TransactionIdSequence transactionIdSequence;

    @Inject
    protected PayerIdSequence payerIdSequence;

    @Transactional
    public PaymentTransaction createTransaction(TransactionRequestPojo request, Merchant merchant) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setTransactionId(transactionIdSequence.getNext());
        paymentTransaction.setDateCreated(PaymentUtil.nowToTimeStamp());
        paymentTransaction.setMerchantTransactionReferenceId(request.getMerchantTransactionReferenceId());
        paymentTransaction.setAmountInKobo(request.getAmountInKobo());
        paymentTransaction.setNotifyOnStatusChange(request.getNotifyOnStatusChange());
        paymentTransaction.setNotificationUrl(request.getNotificationUrl());
        paymentTransaction.setPaymentProvider(PaymentProviderConstant.fromValue(request.getPaymentProvider()));
        paymentTransaction.setPaymentChannel(PaymentChannelConstant.fromValue(request.getPaymentChannel()));
        paymentTransaction.setServiceTypeId(request.getServiceTypeId());
        paymentTransaction.setMerchant(merchant);
        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);
        paymentTransaction.setValidateTransaction(request.getValidateTransaction());
        paymentTransaction.setTransactionValidationUrl(request.getTransactionValidationUrl());

        Payer payer = new Payer();
        payer.setPayerId(payerIdSequence.getNext());
        payer.setFirstName(request.getPayer().getFirstName());
        payer.setLastName(request.getPayer().getLastName());
        payer.setEmail(request.getPayer().getEmail());
        payer.setPhoneNumber(request.getPayer().getPhoneNumber());

        saveObject(payer);

        paymentTransaction.setPayer(payer);
        saveObject(paymentTransaction);

        if (request.getItems() != null) {
            for (ItemPojo itemPojo : request.getItems()) {
                Item item = new Item();
                item.setName(itemPojo.getName());
                item.setItemId(itemPojo.getItemId());
                item.setQuantity(itemPojo.getQuantity());
                item.setPriceInKobo(itemPojo.getPriceInKobo());
                item.setTaxInKobo(itemPojo.getTaxInKobo());
                item.setSubTotalInKobo(itemPojo.getSubTotalInKobo());
                item.setTotalInKobo(itemPojo.getTotalInKobo());
                item.setDescription(itemPojo.getDescription());
                item.setStatus(GenericStatusConstant.ACTIVE);

                saveObject(item);

                PaymentTransactionItem paymentTransactionItem = new PaymentTransactionItem();
                paymentTransactionItem.setItem(item);
                paymentTransactionItem.setPaymentTransaction(paymentTransaction);
                saveObject(paymentTransactionItem);

            }
        }

        PaymentRequestLog paymentTransactionRequestLog = new PaymentRequestLog();
        paymentTransactionRequestLog.setRequestDump(PaymentUtil.toJSON(request));
        paymentTransactionRequestLog.setDateCreated(PaymentUtil.nowToTimeStamp());
        paymentTransactionRequestLog.setPaymentTransaction(paymentTransaction);

        saveObject(paymentTransactionRequestLog);

        return paymentTransaction;
    }

    public List<Item> getPaymentTransactionItems(Long id, GenericStatusConstant status) {
        Query q = entityManagerProvider.get().createQuery("select x from Item x, PaymentTransactionItem pi where pi.paymentTransaction.id=:id and x.id=pi.item.id" +
                " and x.status=:status");
        q.setParameter("id", id).setParameter("status", status.getValue());

        return (List<Item>) q.getResultList();
    }

    public boolean isDuplicateNotification(Payment request) {
        Query q = entityManagerProvider.get().createQuery("select count(x) from PaymentResponseLog x where x.paymentLogId=:pLogId" +
                " and x.amountInKobo=:amount and x.recieptNumber=:rNo and x.paymentReference=:pRef");
        q.setParameter("pLogId", String.valueOf(request.getPaymentLogId()))
                .setParameter("amount", PaymentUtil.getAmountInKobo(request.getAmount()))
                .setParameter("rNo", request.getReceiptNo())
                .setParameter("pRef", request.getPaymentReference());

        return (getCount(q)) > 0;
    }

    public Merchant getMerchantByMerchantId(String merchantReference, PaymentProviderConstant paymentProvider) {
        Query q = entityManagerProvider.get().createQuery("select m from Merchant m, MerchantProviderDetails mp where mp.paymentProviderDetails.merchantId=:mid" +
                " and mp.paymentProviderDetails.name=:name and m.id=mp.merchant.id");

        q.setParameter("mid", merchantReference)
                .setParameter("name", paymentProvider.getValue());
        return uniqueResultOrNull(q, Merchant.class);
    }

    public List<NotificationQueue> getPendingNotifications(int max) {
        if (max == 0) {
            max = 10;
        }
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NotificationQueue> clientCriteriaQuery = criteriaBuilder.createQuery(NotificationQueue.class);
        Root<NotificationQueue> clientRoot = clientCriteriaQuery.from(NotificationQueue.class);
        clientCriteriaQuery.where(criteriaBuilder.equal(clientRoot.get("notificationSent"), false));

        return resultsList(entityManager.createQuery(clientCriteriaQuery).setMaxResults(max));
    }

    public boolean isProcessed(Payment request) {
        Query q = entityManagerProvider.get().createQuery("select count(x) from PaymentResponseLog x where x.paymentLogId=:pLogId" +
                " and x.amountInKobo=:amount and x.recieptNumber=:rNo and x.processed=:processed and x.paymentReference=:pRef");
        q.setParameter("pLogId", String.valueOf(request.getPaymentLogId()))
                .setParameter("amount", PaymentUtil.getAmountInKobo(request.getAmount()))
                .setParameter("rNo", request.getReceiptNo())
                .setParameter("pRef", request.getPaymentReference())
                .setParameter("processed", true);

        return getCount(q) > 0;
    }
}
