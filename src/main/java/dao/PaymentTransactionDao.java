package dao;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import org.hibernate.Session;
import pojo.ItemPojo;
import pojo.TransactionRequestPojo;
import utils.PaymentUtil;
import utils.SequenceService;

/**
 * CREATED BY GIBAH
 */
public class PaymentTransactionDao extends BaseDao {
    public PaymentTransaction createTransaction(TransactionRequestPojo request, Merchant merchant) {
        return transactionManager.doForResult(session -> {
            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setTransactionId(generatePaymentTransactionId(session));
            paymentTransaction.setDateCreated(PaymentUtil.nowToTimeStamp());
            paymentTransaction.setMerchantTransactionReferenceId(request.getMerchantTransactionReferenceId());
            paymentTransaction.setAmountInKobo(request.getAmountInKobo());
            paymentTransaction.setNotifyOnStatusChange(request.getNotifyOnStatusChange());
            paymentTransaction.setNotificationUrl(request.getNotificationUrl());
            paymentTransaction.setPaymentProvider(PaymentProviderConstant.fromValue(request.getPaymentProvider()));
            paymentTransaction.setPaymentChannel(PaymentChannelConstant.fromValue(request.getPaymentChannel()));
            paymentTransaction.setServiceTypeId(request.getServiceTypeId());
            paymentTransaction.setMerchant(merchant);
            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENIDNG);

            Payer payer = new Payer();
            payer.setPayerId(generatePayerId(session));
            payer.setFirstName(request.getPayer().getFirstName());
            payer.setLastName(request.getPayer().getLastName());
            payer.setEmail(request.getPayer().getEmail());
            payer.setPhoneNumber(request.getPayer().getPhoneNumber());

            paymentTransaction.setPayer(payer);

            session.save(payer);
            session.save(paymentTransaction);

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

                    PaymentTransactionItem paymentTransactionItem = new PaymentTransactionItem();
                    paymentTransactionItem.setItem(item);
                    paymentTransactionItem.setPaymentTransaction(paymentTransaction);

                    session.save(item);
                    session.save(paymentTransactionItem);

                }
            }

            PaymentTransactionRequestLog paymentTransactionRequestLog = new PaymentTransactionRequestLog();
            paymentTransactionRequestLog.setRequestDump(PaymentUtil.toJSON(request));
            paymentTransactionRequestLog.setDateCreated(PaymentUtil.nowToTimeStamp());
            paymentTransactionRequestLog.setPaymentTransaction(paymentTransaction);

            session.save(paymentTransactionRequestLog);

            return paymentTransaction;
        });
    }

    private String generatePaymentTransactionId(Session session) {
        SequenceService sequenceService = new SequenceService(session, "payment_transaction_id");
        return sequenceService.getNextId("%09d");
    }

    private String generatePayerId(Session session) {
        SequenceService sequenceService = new SequenceService(session, "payer_id");
        return sequenceService.getNextId("%09d");
    }
}
