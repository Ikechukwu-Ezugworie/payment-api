package services;

import com.bw.payment.entity.Item;
import com.bw.payment.entity.Payer;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.inject.Inject;
import dao.PaymentTransactionDao;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionRequestPojo;
import utils.GeneralConstants;
import utils.PaymentUtil;

import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class PaymentTransactionService {
    @Inject
    private PaymentTransactionDao paymentTransactionDao;

    public TransactionRequestPojo getFullPaymentTransactionDetailsAsPojo(PaymentTransaction paymentTransaction) {
        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setId(paymentTransaction.getId());
        transactionRequestPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionRequestPojo.setDateCreated(PaymentUtil.format(paymentTransaction.getDateCreated(), GeneralConstants.ISO_DATE_TIME_FORMAT));
        transactionRequestPojo.setMerchantTransactionReferenceId(paymentTransaction.getMerchantTransactionReferenceId());
        transactionRequestPojo.setAmountInKobo(paymentTransaction.getAmountInKobo());
        transactionRequestPojo.setNotifyOnStatusChange(paymentTransaction.getNotifyOnStatusChange());
        transactionRequestPojo.setNotificationUrl(paymentTransaction.getNotificationUrl());
        transactionRequestPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        transactionRequestPojo.setPaymentChannel(paymentTransaction.getPaymentChannel().getValue());
        transactionRequestPojo.setServiceTypeId(paymentTransaction.getServiceTypeId());

        Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
        transactionRequestPojo.setPayer(generatePayerPojo(payer));

        List<Item> items = paymentTransactionDao.getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE);
        for (Item item : items) {
            transactionRequestPojo.addItem(generateItemPojo(item));
        }
        transactionRequestPojo.setPaymentTransactionStatus(paymentTransaction.getPaymentTransactionStatus().getValue());

        return transactionRequestPojo;
    }

    public ItemPojo generateItemPojo(Item item) {
        if (item == null) {
            return null;
        }

        ItemPojo itemPojo = new ItemPojo();
        itemPojo.setName(item.getName());
        itemPojo.setItemId(item.getItemId());
        itemPojo.setQuantity(item.getQuantity());
        itemPojo.setPriceInKobo(item.getPriceInKobo());
        itemPojo.setTaxInKobo(item.getTaxInKobo());
        itemPojo.setSubTotalInKobo(item.getSubTotalInKobo());
        itemPojo.setTotalInKobo(item.getTotalInKobo());
        itemPojo.setDescription(item.getDescription());
        itemPojo.setStatus(item.getStatus().getValue());
        itemPojo.setId(item.getId());

        return itemPojo;
    }

    public PayerPojo generatePayerPojo(Payer payer) {
        if (payer == null) {
            return null;
        }
        PayerPojo payerPojo = new PayerPojo();
        payerPojo.setId(payer.getId());
        payerPojo.setPayerId(payer.getPayerId());
        payerPojo.setFirstName(payer.getFirstName());
        payerPojo.setLastName(payer.getLastName());
        payerPojo.setEmail(payer.getEmail());
        payerPojo.setPhoneNumber(payer.getPhoneNumber());

        return payerPojo;
    }
}
