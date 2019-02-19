package services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.NotificationQueue;
import com.bw.payment.entity.Payer;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import controllers.RemitaController;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import dao.RemittaDao;
import exceptions.ApiResponseException;
import javassist.NotFoundException;
import ninja.Context;
import ninja.ReverseRouter;
import org.apache.commons.lang3.RandomUtils;
import pojo.PayerPojo;
import pojo.TransactionNotificationPojo;
import pojo.TransactionRequestPojo;
import pojo.remitta.*;
import retrofit2.Call;
import retrofit2.Response;
import services.api.RemittaApi;
import services.sequence.NotificationIdSequence;
import services.sequence.PayerIdSequence;
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

    @Inject
    private RemittaApi remittaApi;

    @Inject
    private PayerIdSequence payerIdSequence;

    @Inject
    private RemittaDao remittaDao;

    @Inject
    private ReverseRouter reverseRouter;

    @Inject
    private NotificationService notificationService;


    @Transactional
    public PaymentTransaction generateRemittaRRR(TransactionRequestPojo request) throws ApiResponseException {


        // Try to Generate an RRR

        String transactionId = transactionIdSequence.getNext();
        String serviceTypeId = remittaDao.getSettingsValue(RemittaDao.CBS_REMITTA_SERVICE_TYPE_ID, "123456", Boolean.TRUE);

        RemittaGenerateRequestRRRPojo requestData = new RemittaGenerateRequestRRRPojo();
        requestData.setServiceTypeId(serviceTypeId);
        requestData.setAmount(BigInteger.valueOf(request.getAmountInKobo()));
        requestData.setOrderId(transactionId);
        requestData.setPayerName(request.getPayer().getFirstName() + " " + request.getPayer().getLastName());
        requestData.setPayerEmail(request.getPayer().getEmail());
        requestData.setPayerPhone(request.getPayer().getPhoneNumber());
        requestData.setDescription(request.getDescription());


        System.out.println("Payload:::: " + requestData.toString());

        Call<RemittaRrrResponse> remittaRrrResponseCall = remittaApi
                .postToGenerateRRR(requestData, remittaDao.generateAutorisationHeader(transactionId, serviceTypeId, BigInteger.valueOf(request.getAmountInKobo())));

        try {
            Response<RemittaRrrResponse> execute = remittaRrrResponseCall.execute();

            if (execute.isSuccessful() && execute.body() != null) {
                // Create Payment Transaction
                RemittaRrrResponse remittaRrrResponse = execute.body();


                if (remittaRrrResponse.getStatuscode() == null) {
                    //createFakerRemitta(request); //TODO fAKER
                    throw new ApiResponseException("REMITA:::" + remittaRrrResponse.getStatus());
                }

                if (!remittaRrrResponse.getStatuscode().equalsIgnoreCase("025")) {
                    //createFakerRemitta(request); //TODO fAKER
                    throw new ApiResponseException(remittaRrrResponse.getStatus());
                }
                request.setProviderTransactionReference(remittaRrrResponse.getRRR());
                request.setCustomerTransactionReference(remittaRrrResponse.getRRR());
            } else {
                throw new ApiResponseException(execute.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            //createFakerRemitta(request); //TODO fAKER
            throw new ApiResponseException(e.getMessage());
        }

        return paymentTransactionDao.createTransaction(request, transactionId);
    }



    @Transactional
    public PaymentTransaction updatePaymentTransaction(List<RemittaNotification> remittaNotifications) throws Exception {
        for (RemittaNotification remittaNotification : remittaNotifications) {
            PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(remittaNotification.getRrr());
            if (paymentTransaction == null) {
                throw new NotFoundException("Payment Transaction with RRR cannot be found");
            }
            paymentTransaction.setPaymentProvider(PaymentProviderConstant.REMITA);
            paymentTransaction.setPaymentChannel(PaymentChannelConstant.BANK); // TODO update after model update
            paymentTransaction.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(remittaNotification.getAmount()));
            paymentTransaction.setMerchantTransactionReferenceId(remittaNotification.getOrderRef());
            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);


            Payer payer = new Payer();
            payer.setPayerId(payerIdSequence.getNext());
            payer.setFirstName(remittaNotification.getPayerName());
            payer.setLastName("");
            payer.setEmail(remittaNotification.getPayerEmail());
            payer.setPhoneNumber(remittaNotification.getPayerPhoneNumber());
            remittaDao.saveObject(payer);
            paymentTransaction.setPayer(payer);


            Call<RemittaTransactionStatusPojo> transactionStatusResponse = remittaApi.getTransactionStatus(remittaDao.getSettingsValue(RemittaDao.REMITTA_MECHANT_ID, "657", true),
                    remittaNotification.getRrr(),
                    remittaDao.generateHash(remittaNotification.getRrr()));

            try {
                Response<RemittaTransactionStatusPojo> execute = transactionStatusResponse.execute();
                RemittaTransactionStatusPojo responseBody = execute.body();
                if (execute.isSuccessful() && responseBody != null) {
                    if (responseBody.getStatus().equalsIgnoreCase("01")) {
                        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                throw new ApiResponseException(e.getMessage());
            }

            paymentTransactionDao.updateObject(paymentTransaction);
            queueNotification(remittaNotification, paymentTransaction);
            notificationService.sendPaymentNotification(10);

        }

        return null;

    }
//
//    @Transactional
//    public PaymentTransaction processPaymentNotification(List<RemittaNotification> remittaNotifications) {
//        for (RemittaNotification remittaNotification : remittaNotifications) {
//            TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
//
//
//            try {
//                PayerPojo payerPojo = new PayerPojo();
//                payerPojo.setFirstName(remittaNotification.getPayerName());
//                payerPojo.setLastName("");
//                payerPojo.setEmail(remittaNotification.getPayerEmail());
//                payerPojo.setAddress("");
//                payerPojo.setPhoneNumber(remittaNotification.getPayerPhoneNumber());
//                transactionRequestPojo.setPayer(payerPojo);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            transactionRequestPojo.setNotifyOnStatusChange(false);
//            transactionRequestPojo.setNotificationUrl("");
//
//            PaymentTransaction paymentTransaction = paymentTransactionDao.createTransaction(transactionRequestPojo, null, null);
//
//            if (paymentTransaction == null) {
//                return null;
//            }
//
//            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);
//            paymentTransaction.setLastUpdated(Timestamp.from(Instant.now()));
//            paymentTransactionDao.updateObject(paymentTransaction);
//            queueNotification(remittaNotification, paymentTransaction);
//            notificationService.sendPaymentNotification(10);
//
//        }
//        return null;
//    }

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


    public void createFakerRemitta(TransactionRequestPojo request) {
        String fakeRRR = "RRR" + System.currentTimeMillis();
        request.setProviderTransactionReference(fakeRRR);
        request.setCustomerTransactionReference(fakeRRR);
    }
}
