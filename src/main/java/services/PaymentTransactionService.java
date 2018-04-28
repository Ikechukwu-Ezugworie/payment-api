package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.TransactionNotificationPojo;
import pojo.TransactionRequestPojo;
import utils.Constants;
import utils.PaymentUtil;

import java.io.IOException;
import java.util.List;

/**
 * CREATED BY GIBAH
 */
public class PaymentTransactionService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private MerchantDao merchantDao;

    private final OkHttpClient client = new OkHttpClient();

    public TransactionRequestPojo getFullPaymentTransactionDetailsAsPojo(PaymentTransaction paymentTransaction) {
        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setId(paymentTransaction.getId());
        transactionRequestPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionRequestPojo.setDateCreated(PaymentUtil.format(paymentTransaction.getDateCreated(), Constants.ISO_DATE_TIME_FORMAT));
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

    public PaymentTransaction createTransaction(TransactionRequestPojo request, Merchant merchant) {

        if (request.getValidateTransaction()) {
            if (StringUtils.isBlank(request.getTransactionValidationUrl())) {
                throw new IllegalArgumentException("Transaction validation url not set");
            }
        }
        PaymentProviderConstant providerConstant = PaymentProviderConstant.fromValue(request.getPaymentProvider());
        switch (providerConstant) {
            case INTERSWITCH:
                validateInterswitchTransactionRequest(request, merchant);
                break;
            case REMITA:
                break;
            case NIBBS:
                break;
        }
        return paymentTransactionDao.createTransaction(request, merchant);
    }

    private void validateInterswitchTransactionRequest(TransactionRequestPojo request, Merchant merchant) throws IllegalArgumentException {
        PaymentProviderDetails paymentProviderDetails = merchantDao.getMerchantPaymentProviderDetails(merchant.getId(), PaymentProviderConstant.INTERSWITCH);
        if (paymentProviderDetails == null) {
            throw new IllegalArgumentException("Please add interswitch payment provider details");
        }

        if (StringUtils.isBlank(paymentProviderDetails.getMerchantId())) {
            throw new IllegalArgumentException("No payment provider merchant id provided");
        }
    }

    @Transactional
    public void processPendingNotifications() {
        List<NotificationQueue> notificationQueues = paymentTransactionDao.getPendingNotifications(10);
        System.out.println("<==== processing notifications : " + notificationQueues.size());
        for (NotificationQueue notificationQueue : notificationQueues) {
            try {
                RequestBody body = RequestBody.create(JSON, notificationQueue.getMessageInJson());
                System.out.println("<==== processing notification : " + notificationQueue.getMessageInJson());
                Request request = new Request.Builder().url(notificationQueue.getNotificationUrl()).post(body).build();
                TransactionNotificationPojo transactionNotificationPojo = new Gson().fromJson(notificationQueue.getMessageInJson(), TransactionNotificationPojo.class);
                System.out.println(transactionNotificationPojo.toString());
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    notificationQueue.setNotificationSent(true);
                    notificationSent(notificationQueue);
                }

                System.out.println("<== noitification response code " + request.url() + " : " + response.code());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notificationSent(NotificationQueue notificationQueue) {
        paymentTransactionDao.updateObject(notificationQueue);
    }
}
