package services;

import java.io.IOException;
import java.math.BigInteger;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.NotificationQueue;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import controllers.RemitaController;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import dao.RemittaDao;
import exceptions.ApiResponseException;
import ninja.Context;
import ninja.ReverseRouter;
import pojo.PayerPojo;
import pojo.TransactionNotificationPojo;
import pojo.TransactionRequestPojo;
import pojo.remitta.RemittaGenerateRequestRRRPojo;
import pojo.remitta.RemittaNotification;
import pojo.remitta.RemittaPaymentRequestPojo;
import pojo.remitta.RemittaRrrResponse;
import retrofit2.Call;
import retrofit2.Response;
import services.api.RemittaApi;
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

    @Inject
    private RemittaApi remittaApi;

    @Inject
    private RemittaDao remittaDao;

    @Inject
    private ReverseRouter reverseRouter;


    @Transactional
    public PaymentTransaction generateRemittaRRR(TransactionRequestPojo request) throws ApiResponseException {


        // Try to Generate an RRR

        RemittaGenerateRequestRRRPojo requestData = new RemittaGenerateRequestRRRPojo();
        requestData.setServiceTypeId("");
        requestData.setAmount(BigInteger.valueOf(request.getAmountInKobo()));
        requestData.setOrderId(request.getTransactionId());
        requestData.setPayerName(request.getPayer().getFirstName() + " " + request.getPayer().getLastName());
        requestData.setPayerEmail(request.getPayer().getPhoneNumber());
        requestData.setPayerPhone(request.getPayer().getPhoneNumber());
        requestData.setDescription(request.getDescription());


        Call<RemittaRrrResponse> remittaRrrResponseCall = remittaApi
                .postToGenerateRRR(requestData, remittaDao.generateAutorisationHeader(request.getTransactionId(), BigInteger.valueOf(request.getAmountInKobo())));

        try {
            Response<RemittaRrrResponse> execute = remittaRrrResponseCall.execute();
            if (execute.isSuccessful() && execute.body() != null) {
                // Create Payment Transaction
                RemittaRrrResponse remittaRrrResponse = execute.body();

                if(!remittaRrrResponse.getStatuscode().equals("025")){
                    throw new ApiResponseException(remittaRrrResponse.getStatus());
                }

                request.setProviderTransactionReference(remittaRrrResponse.getRRR());
            } else {
                throw new ApiResponseException(execute.message());
            }
        } catch (IOException e) {
            throw new ApiResponseException(e.getMessage());
        }


        return paymentTransactionDao.createTransaction(request, null);
    }


    public RemittaPaymentRequestPojo createRemittaPaymentRequestPojo(PaymentTransaction paymentTransaction, Context context){
        RemittaPaymentRequestPojo requestData = new RemittaPaymentRequestPojo();
        requestData.setHash(remittaDao.generateHash(paymentTransaction.getCustomerTransactionReference()));
        requestData.setMerchantId(remittaDao.getMerchantId());
        requestData.setResponseurl(reverseRouter.with(RemitaController::paymentCompleted).absolute(context).build());
        requestData.setRrr(paymentTransaction.getProviderTransactionReference());
        return requestData;

    }

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
