package services;

import java.io.IOException;
import java.math.BigInteger;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.NotificationQueue;
import com.bw.payment.entity.Payer;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import dao.RemittaDao;
import exceptions.ApiResponseException;
import javassist.NotFoundException;
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

/**
 * Author: Oluwatobi Adenekan
 * email:  tadenekan@byteworks.com.ng
 * date:    21/02/2019
 **/


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
    private NotificationService notificationService;


    @Transactional
    public PaymentTransaction generateRemittaRRR(TransactionRequestPojo request) throws ApiResponseException {


        // Try to Generate an RRR

        String transactionId = transactionIdSequence.getNext();
        String serviceTypeId = remittaDao.getSettingsValue(RemittaDao.CBS_REMITTA_SERVICE_TYPE_ID, "4430731", Boolean.TRUE);

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
                    throw new ApiResponseException("REMITA:::" + remittaRrrResponse.getStatus());
                }

                if (!remittaRrrResponse.getStatuscode().equalsIgnoreCase("025")) {
                    throw new ApiResponseException(remittaRrrResponse.getStatus());
                }
                request.setProviderTransactionReference(remittaRrrResponse.getRRR());
                request.setCustomerTransactionReference(remittaRrrResponse.getRRR());
            } else {
                throw new ApiResponseException(execute.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiResponseException(e.getMessage());
        }

        return paymentTransactionDao.createTransaction(request, transactionId);
    }


    @Transactional
    public PaymentTransaction updatePaymentTransaction(List<RemittaNotification> remittaNotifications) throws NotFoundException, ApiResponseException {
        for (RemittaNotification remittaNotification : remittaNotifications) {

            PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(remittaNotification.getRrr());

            if (paymentTransaction == null) {
                throw new NotFoundException("Payment Transaction with RRR cannot be found");
            }
            paymentTransaction.setPaymentProvider(PaymentProviderConstant.REMITA);
            paymentTransaction.setPaymentChannel(PaymentChannelConstant.BANK); // TODO update after model update
            paymentTransaction.setAmountPaidInKobo(remittaNotification.getAmount().longValue());
            paymentTransaction.setLastUpdated(Timestamp.from(Instant.now()));
            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);
            Payer payer = new Payer();
            payer.setPayerId(payerIdSequence.getNext());
            payer.setFirstName(remittaNotification.getPayerName());
            payer.setLastName("");
            payer.setEmail(remittaNotification.getPayerEmail());
            payer.setPhoneNumber(remittaNotification.getPayerPhoneNumber());
            remittaDao.saveObject(payer);
            paymentTransaction.setPayer(payer);


            Boolean shouldNotify = requestForPaymentTransactionStatus(paymentTransaction);


            // Todo:: Please Update the True to shouldNotify - This is only Useful for Testing
            if (true) {

                queueNotification(remittaNotification, paymentTransaction);

                notificationService.sendPaymentNotification(10);
            }


            paymentTransactionDao.updateObject(paymentTransaction);


        }

        return null;

    }


    /**
     * @param paymentTransaction
     * @return Boolean Value to make a notify decision
     * @throws ApiResponseException
     */
    private Boolean requestForPaymentTransactionStatus(PaymentTransaction paymentTransaction) throws ApiResponseException {

        Boolean shouldDoNotifaction = false;

        Call<RemittaTransactionStatusPojo> transactionStatusResponse = remittaApi.getTransactionStatus(remittaDao.getSettingsValue(RemittaDao.REMITTA_MECHANT_ID, "657", true),
                paymentTransaction.getProviderTransactionReference(),
                remittaDao.generateHash(paymentTransaction.getProviderTransactionReference()));

        try {

            Response<RemittaTransactionStatusPojo> execute = transactionStatusResponse.execute();

            RemittaTransactionStatusPojo responseBody = execute.body();

            if (execute.isSuccessful() && responseBody != null) {

                if (responseBody.getStatus().equalsIgnoreCase("01") || responseBody.getStatus().equalsIgnoreCase("00")) {

                    if (paymentTransaction.getAmountPaidInKobo() < paymentTransaction.getAmountInKobo()) {
                        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PARTIAL);
                    } else {
                        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
                    }

                    shouldDoNotifaction = true;
                }

                System.out.println("()()()() Remitta Result " + responseBody);
                paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.FAILED);

            }

        } catch (IOException e) {

            e.printStackTrace();
            throw new ApiResponseException(e.getMessage());
        }

        return shouldDoNotifaction;
    }


    private void queueNotification(RemittaNotification paymentPojo, PaymentTransaction paymentTransaction) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        TransactionNotificationPojo<RemittaNotification> transactionNotificationPojo = new TransactionNotificationPojo<>();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
        if (paymentTransaction.getPaymentProvider().equals(PaymentProviderConstant.REMITA)) {  //TODO This is to alter the system
            transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        } else {
            transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue() + "_" + paymentTransaction.getPaymentChannel().getValue());

        }
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
