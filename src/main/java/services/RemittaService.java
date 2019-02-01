package services;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.NotificationQueue;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import pojo.PayerPojo;
import pojo.TransactionNotificationPojo;
import pojo.TransactionRequestPojo;
import pojo.remitta.RemittaNotification;
import services.sequence.NotificationIdSequence;
import services.sequence.TransactionIdSequence;
import utils.Constants;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/*
 * Created by Gibah Joseph on Jan, 2019
 */
@Singleton
public class RemittaService {
    @Inject
    private MerchantDao merchantDao;
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    protected TransactionIdSequence transactionIdSequence;
    @Inject
    private NotificationIdSequence notificationIdSequence;

    @Transactional
    public PaymentTransaction processPaymentNotification(List<RemittaNotification> remittaNotifications) {
        for (RemittaNotification remittaNotification : remittaNotifications) {
            TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
            transactionRequestPojo.setPaymentProvider(PaymentProviderConstant.REMITA.getValue());
            transactionRequestPojo.setPaymentChannel(PaymentChannelConstant.BANK.getValue());
            transactionRequestPojo.setAmountInKobo(PaymentUtil.getAmountInKobo(remittaNotification.getAmount()));
            transactionRequestPojo.setMerchantTransactionReferenceId(remittaNotification.getOrderRef());
            transactionRequestPojo.setCustomerTransactionReference(remittaNotification.getRrr());

            try {
                PayerPojo payerPojo = new PayerPojo();
                payerPojo.setFirstName(remittaNotification.getPayerName());
                payerPojo.setLastName("");
                payerPojo.setEmail(remittaNotification.getPayerEmail());
                payerPojo.setAddress("");
                payerPojo.setPhoneNumber(remittaNotification.getPayerPhoneNumber());
                transactionRequestPojo.setPayer(payerPojo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            transactionRequestPojo.setNotifyOnStatusChange(false);
            transactionRequestPojo.setNotificationUrl("");

            PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null, null);

            if (paymentTransaction == null) {
                return null;
            }

            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);

            paymentTransactionDao.updateObject(paymentTransaction);

            queueNotification(remittaNotification, paymentTransaction);
        }
        return null;
    }

    private void queueNotification(RemittaNotification paymentPojo, PaymentTransaction paymentTransaction) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        TransactionNotificationPojo<RemittaNotification> transactionNotificationPojo = new TransactionNotificationPojo<>();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue() + "_" + paymentTransaction.getPaymentChannel().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentDate(paymentPojo.getTransactiondate());
        transactionNotificationPojo.setPaymentChannelName(paymentPojo.getChannel());
        transactionNotificationPojo.setPaymentProviderPaymentReference(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setNotificationId(notificationIdSequence.getNext());
        transactionNotificationPojo.setCustomerTransactionReference(paymentTransaction.getCustomerTransactionReference());
        transactionNotificationPojo.setMerchantTransactionReference(paymentTransaction.getMerchantTransactionReferenceId());
        transactionNotificationPojo.setActualNotification(paymentPojo);

        PayerPojo payerPojo = paymentTransactionDao.getPayerAsPojo(paymentTransaction.getPayer().getId());
        transactionNotificationPojo.setPayer(payerPojo);

        NotificationQueue notificationQueue = new NotificationQueue();
        notificationQueue.setMessageInJson(PaymentUtil.toJSON(transactionNotificationPojo));
        notificationQueue.setNotificationUrl(merchant.getNotificationUrl());
        notificationQueue.setNotificationSent(false);
        notificationQueue.setDateCreated(Timestamp.from(Instant.now()));
        notificationQueue.setPaymentTransaction(paymentTransaction);

        paymentTransactionDao.saveObject(notificationQueue);
    }
}
