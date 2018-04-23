package dao;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import pojo.ItemPojo;
import pojo.TransactionRequestPojo;
import utils.PaymentUtil;

import javax.persistence.Query;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class PaymentTransactionDao extends BaseDao {
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
        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENIDNG);

        Payer payer = new Payer();
        payer.setPayerId(payerIdSequence.getNext());
        payer.setFirstName(request.getPayer().getFirstName());
        payer.setLastName(request.getPayer().getLastName());
        payer.setEmail(request.getPayer().getEmail());
        payer.setPhoneNumber(request.getPayer().getPhoneNumber());

        paymentTransaction.setPayer(payer);

        saveObject(payer);
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

                PaymentTransactionItem paymentTransactionItem = new PaymentTransactionItem();
                paymentTransactionItem.setItem(item);
                paymentTransactionItem.setPaymentTransaction(paymentTransaction);

                saveObject(item);
                saveObject(paymentTransactionItem);

            }
        }

        PaymentTransactionRequestLog paymentTransactionRequestLog = new PaymentTransactionRequestLog();
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
}
