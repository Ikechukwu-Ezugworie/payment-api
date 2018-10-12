package dao;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import org.apache.commons.lang3.StringUtils;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionRequestPojo;
import pojo.payDirect.paymentNotification.request.Payment;
import services.sequence.PayerIdSequence;
import services.sequence.TicketIdSequence;
import services.sequence.TransactionIdSequence;
import utils.PaymentUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class PaymentTransactionDao extends BaseDao {

    @Inject
    protected TransactionIdSequence transactionIdSequence;

    @Inject
    protected PayerIdSequence payerIdSequence;
    @Inject
    protected TicketIdSequence ticketIdSequence;
    @Inject
    protected MerchantDao merchantDao;


    @Transactional
    public PaymentTransaction createTransaction(TransactionRequestPojo request, Merchant merchant, String transactionId) {
        if (StringUtils.isBlank(transactionId)) {
            transactionId = transactionIdSequence.getNext();
        }

        if (merchant == null) {
            merchant = merchantDao.getAllRecords(Merchant.class).get(0);
        }

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setTransactionId(transactionId);
        paymentTransaction.setDateCreated(PaymentUtil.nowToTimeStamp());
        paymentTransaction.setMerchantTransactionReferenceId(request.getMerchantTransactionReferenceId());
        paymentTransaction.setAmountInKobo(request.getAmountInKobo());
        paymentTransaction.setAmountPaidInKobo(0L);
        paymentTransaction.setPaymentProvider(PaymentProviderConstant.fromValue(request.getPaymentProvider()));
        paymentTransaction.setPaymentChannel(PaymentChannelConstant.fromValue(request.getPaymentChannel()));
        paymentTransaction.setServiceTypeId(request.getServiceTypeId());
        paymentTransaction.setMerchant(merchant);
        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);
        paymentTransaction.setCustomerTransactionReference(request.getCustomerTransactionReference());

        System.out.println("<=== cref" + request.getCustomerTransactionReference());

        Payer payer = new Payer();
        payer.setPayerId(payerIdSequence.getNext());
        payer.setFirstName(request.getPayer().getFirstName());
        payer.setLastName(request.getPayer().getLastName());
        payer.setEmail(request.getPayer().getEmail());
        payer.setPhoneNumber(request.getPayer().getPhoneNumber());
//        payer.setAddress(request.getPayer().getAddress());

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

    public PaymentTransaction createTransaction(TransactionRequestPojo request, Merchant merchant) {
        return createTransaction(request, merchant, null);
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

    public boolean isDuplicateNotificationAndAccepted(Payment request) {
        Query q = entityManagerProvider.get().createQuery("select count(x) from PaymentResponseLog x where x.paymentLogId=:pLogId" +
                " and x.amountInKobo=:amount and x.recieptNumber=:rNo and x.paymentReference=:pRef and x.status=:status");
        q.setParameter("pLogId", String.valueOf(request.getPaymentLogId()))
                .setParameter("amount", PaymentUtil.getAmountInKobo(request.getAmount()))
                .setParameter("rNo", request.getReceiptNo())
                .setParameter("status", "ACCEPTED")
                .setParameter("pRef", request.getPaymentReference());

        return (getCount(q)) > 0;
    }

//    public Merchant getMerchantByMerchantId(String merchantReference, PaymentProviderConstant paymentProvider) {
//        Query q = entityManagerProvider.get().createQuery("select m from Merchant m, MerchantProviderDetails mp where mp.paymentProviderDetails.merchantId=:mid" +
//                " and mp.paymentProviderDetails.name=:name and m.id=mp.merchant.id");
//
//        q.setParameter("mid", merchantReference)
//                .setParameter("name", paymentProvider.getValue());
//        return uniqueResultOrNull(q, Merchant.class);
//    }

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

    public PaymentTransaction getQTPaymentTransaction(String tx_ref) {
        Query q = entityManagerProvider.get().createQuery("select x from PaymentTransaction x where x.providerTransactionReference=:ptr" +
                " and x.paymentChannel=:pc");
        q.setParameter("ptr", tx_ref)
                .setParameter("pc", PaymentChannelConstant.QUICKTELLER);

        return uniqueResultOrNull(q, PaymentTransaction.class);
    }

    public String getTicketId() {
        return ticketIdSequence.getNext();
    }

    public PayerPojo getPayerAsPojo(Long id) {
        Payer payer = getRecordById(Payer.class, id);
        return getPayerAsPojo(payer);
    }

    public PayerPojo getPayerAsPojo(Payer payer) {
        if (payer == null) {
            return null;
        }
        PayerPojo payerPjo = new PayerPojo();
        payerPjo.setId(payer.getId());
        payerPjo.setPayerId(payer.getPayerId());
        payerPjo.setFirstName(payer.getFirstName());
        payerPjo.setLastName(payer.getLastName());
        payerPjo.setEmail(payer.getEmail());
        payerPjo.setPhoneNumber(payer.getPhoneNumber());

        return payerPjo;
    }

    public PaymentTransaction getPaymentTransactionForReversal(Payment payment) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PaymentTransaction> clientCriteriaQuery = criteriaBuilder.createQuery(PaymentTransaction.class);
        Root<PaymentTransaction> clientRoot = clientCriteriaQuery.from(PaymentTransaction.class);
        Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.equal(clientRoot.get("providerTransactionReference"), payment.getOriginalPaymentReference()),
                criteriaBuilder.equal(clientRoot.get("paymentTransactionStatus"), PaymentTransactionStatus.SUCCESSFUL.getValue())
        );
        clientCriteriaQuery.where(predicate);

        List<PaymentTransaction> paymentTransactions = resultsList(entityManager.createQuery(clientCriteriaQuery));
        return paymentTransactions.size() == 0 ? null : paymentTransactions.get(0);
    }

    public PaymentTransaction getPaymentResponseLogByLogIdAndStatus(String logId, PaymentResponseStatusConstant status) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PaymentTransaction> clientCriteriaQuery = criteriaBuilder.createQuery(PaymentTransaction.class);
        Root<PaymentResponseLog> clientRoot = clientCriteriaQuery.from(PaymentResponseLog.class);
        Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.equal(clientRoot.get("paymentLogId"), logId),
                criteriaBuilder.equal(clientRoot.get("status"), status.getValue())
        );
        clientCriteriaQuery.select(clientRoot.get("paymentTransaction")).where(predicate);

        return uniqueResultOrNull(entityManager.createQuery(clientCriteriaQuery));

    }

    public PaymentTransaction getPaymentTransactionByPaymentProviderReference(String providerReference) {

        return getUniqueRecordByProperty(PaymentTransaction.class, "providerTransactionReference", providerReference);
    }

    public List<PaymentTransaction> getPendingPaymentTransactions(PaymentProviderConstant paymentProvider, PaymentChannelConstant paymentChannel, Integer batch) {
        EntityManager entityManager = entityManagerProvider.get();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PaymentTransaction> criteriaQuery = criteriaBuilder.createQuery(PaymentTransaction.class);
        Root<PaymentTransaction> root = criteriaQuery.from(PaymentTransaction.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("paymentProvider"), paymentProvider))
                .where(criteriaBuilder.equal(root.get("paymentChannel"), paymentChannel))
                .where(criteriaBuilder.equal(root.get("paymentTransactionStatus"), PaymentTransactionStatus.PENDING));

        return resultsList(entityManager.createQuery(criteriaQuery).setMaxResults(batch));
    }

    public Merchant getMerchant(String merchantReference) {
        Boolean validateMerchRef = Boolean.valueOf(getSettingsValue("VALIDATE_MERCHANT_REF", "true", true));
        if (validateMerchRef) {
            if (StringUtils.isBlank(merchantReference)) {
                System.out.println("<== MERCHANT REF IS EMPTY xx");
                return null;
            }
            System.out.println("<== RETURNING MERCHANT BY MERCHANT REF " + merchantReference + " xx");
            return getUniqueRecordByProperty(Merchant.class, "paydirectMerchantReference", merchantReference);

        }
        List<Merchant> merchants = getAllRecords(Merchant.class);
        if (merchants.size() < 1) {
            System.out.println("<== NO MERCHANT HAS BEEN REGISTERED xx");
            return null;
        }
        System.out.println("<== RETURNING FIRST OF MANY MERCHANTS xx");
        return merchants.get(0);
    }
}
