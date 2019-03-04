package services;

/**
 * Author: Oluwatobi Adenekan
 * email:  tadenekan@byteworks.com.ng
 * date:    04/03/2019
 **/

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.NotificationQueue;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.PaymentTransactionDao;
import ninja.Results;
import ninja.utils.NinjaProperties;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionNotificationPojo;
import pojo.TransactionRequestPojo;
import pojo.payDirect.customerValidation.EndSystemCustomerValidationResponse;
import pojo.remitta.RemittaNotification;
import pojo.ussd.UssdNotification;
import services.sequence.NotificationIdSequence;
import services.sequence.TransactionIdSequence;
import utils.Constants;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class UssdService {

    @Inject
    PaymentTransactionDao paymentTransactionDao;

    @Inject
    NinjaProperties ninjaProperties;

    @Inject
    protected TransactionIdSequence transactionIdSequence;

    @Inject
    NotificationIdSequence notificationIdSequence;

    @Inject
    NotificationService notificationService;


    @Transactional
    public PaymentTransaction doUssdNotification(UssdNotification ussdNotification) {

        PaymentTransaction existingPaymentTransaction = paymentTransactionDao
                .getUniqueRecordByProperty(PaymentTransaction.class, "providerTransactionReference", ussdNotification.getTransactionReference());

        if (existingPaymentTransaction != null) {
            return existingPaymentTransaction;
        }

        TransactionRequestPojo paymentTransaction = new TransactionRequestPojo();
        paymentTransaction.setMerchantTransactionReferenceId(String.format("%s-%s", ussdNotification.getMsisdn(), ussdNotification.getTransactionReference()));
        paymentTransaction.setAmountInKobo(PaymentUtil.getAmountInKobo(ussdNotification.getAmount()));
        paymentTransaction.setPaymentProvider(PaymentProviderConstant.INTERSWITCH.getValue()); // Todo:: Please Update
        paymentTransaction.setPaymentChannel(PaymentChannelConstant.BANK.getValue()); // Todo:: Please Update
        PayerPojo payerPojo = new PayerPojo();
        payerPojo.setFirstName(ussdNotification.getMsisdn());
        payerPojo.setLastName("");
        payerPojo.setEmail(Constants.NOT_PROVIDED);
        payerPojo.setPhoneNumber(ussdNotification.getMsisdn());

        paymentTransaction.setPayer(payerPojo);
        List<ItemPojo> items = new ArrayList<>();
        ItemPojo itemPojo = new ItemPojo();
        itemPojo.setName(Constants.NOT_PROVIDED);
        itemPojo.setItemId(ussdNotification.getRevenueCode());
        itemPojo.setQuantity(1);
        itemPojo.setPriceInKobo(PaymentUtil.getAmountInKobo(ussdNotification.getAmount()));
        itemPojo.setTaxInKobo(0L);
        itemPojo.setSubTotalInKobo(PaymentUtil.getAmountInKobo(ussdNotification.getAmount()));
        itemPojo.setTotalInKobo(PaymentUtil.getAmountInKobo(ussdNotification.getAmount()));
        itemPojo.setDescription(String.format("Payment made for revenue item with code %s via ussd", ussdNotification.getRevenueCode()));
        items.add(itemPojo);
        paymentTransaction.setItems(items);
        paymentTransaction.setPaymentTransactionStatus(EndSystemCustomerValidationResponse.PaymentStatus.PAID.getValue());

        paymentTransaction.setAmountPaid(ussdNotification.getAmount());
        paymentTransaction.setProviderTransactionReference(ussdNotification.getTransactionReference());
        paymentTransaction.setMerchantTransactionReferenceId(String.format("%s-%s", ussdNotification.getMsisdn(), ussdNotification.getTransactionReference()));


        String transactionId = transactionIdSequence.getNext();
        if (ninjaProperties.isDev()) {
            transactionId += "DV";
        } else if (ninjaProperties.isTest()) {
            transactionId += "TS";
        }


        PaymentTransaction createdPaymentTransaction = paymentTransactionDao.createTransaction(paymentTransaction, transactionId);
        createdPaymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
        createdPaymentTransaction.setLastUpdated(Timestamp.from(Instant.now()));
        paymentTransactionDao.updateObject(createdPaymentTransaction);
        queueNotification(ussdNotification, createdPaymentTransaction);

        notificationService.sendPaymentNotification(10);

        return createdPaymentTransaction;

    }


    private void queueNotification(UssdNotification paymentPojo, PaymentTransaction paymentTransaction) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        TransactionNotificationPojo<UssdNotification> transactionNotificationPojo = new TransactionNotificationPojo<>();

        List<String> itemCodes = paymentTransactionDao
                .getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE).stream().map(it -> it.getItemId()).collect(Collectors.toList());


        System.out.println("Lenth of the item codes is " + itemCodes.size());

        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));

        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());

        transactionNotificationPojo.setPaymentProviderTransactionId(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setPaymentDate(paymentPojo.getPaymentDate());
        transactionNotificationPojo.setPaymentChannelName(paymentTransaction.getPaymentChannel().getValue()); // Todo:: Please do Update
        transactionNotificationPojo.setPaymentProviderPaymentReference(paymentTransaction.getProviderTransactionReference());
        transactionNotificationPojo.setNotificationId(notificationIdSequence.getNext());
        transactionNotificationPojo.setCustomerTransactionReference(paymentTransaction.getCustomerTransactionReference());
        transactionNotificationPojo.setItemCodes(itemCodes);

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

