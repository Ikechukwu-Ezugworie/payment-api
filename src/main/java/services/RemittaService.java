package services;

import java.math.BigDecimal;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.math.BigInteger;

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
import ninja.utils.NinjaProperties;
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
import java.util.stream.Collectors;

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
    private NinjaProperties ninjaProperties;

    @Inject
    private NotificationService notificationService;


    @Transactional
    public PaymentTransaction generateRemittaRRR(TransactionRequestPojo request) throws ApiResponseException {


        // Try to Generate an RRR

        String transactionId = transactionIdSequence.getNext();
        if (ninjaProperties.isDev()) {
            transactionId += "DV";
        } else if (ninjaProperties.isTest()) {
            transactionId += "TS";
        }
        String serviceTypeId = remittaDao.getSettingsValue(RemittaDao.CBS_REMITTA_SERVICE_TYPE_ID, "4430731", Boolean.TRUE);

        RemittaGenerateRequestRRRPojo requestData = new RemittaGenerateRequestRRRPojo();
        requestData.setServiceTypeId(serviceTypeId);
        requestData.setAmount(PaymentUtil.koboToNaira(BigInteger.valueOf(request.getAmountInKobo())));
        requestData.setOrderId(transactionId);
        requestData.setPayerName(request.getPayer().getFirstName() + " " + request.getPayer().getLastName());
        requestData.setPayerEmail(request.getPayer().getEmail());
        requestData.setPayerPhone(request.getPayer().getPhoneNumber());
        requestData.setDescription(request.getDescription());


        System.out.println("Payload:::: " + requestData.toString());

        Call<RemittaRrrResponse> remittaRrrResponseCall = remittaApi
                .postToGenerateRRR(requestData, remittaDao.generateAutorisationHeader(transactionId, serviceTypeId, requestData.getAmount()));

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


            RemittaTransactionStatusPojo response = requestForPaymentTransactionStatus(paymentTransaction);
            PaymentTransactionStatus status = paymentTransaction.getPaymentTransactionStatus();


            if(response != null && (status.equals(PaymentTransactionStatus.PENDING)  || status.equals(PaymentTransactionStatus.SUCCESSFUL))){
                paymentTransaction.setPaymentChannel(PaymentChannelConstant.BANK);
                paymentTransaction.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(remittaNotification.getAmount()));

                queueNotification(remittaNotification, paymentTransaction);
                notificationService.sendPaymentNotification(10);

            }

            paymentTransactionDao.updateObject(paymentTransaction);

        }

        return null;



    }


    /**
     * Method calls Remitta to request for a payment Status, This is called after banking notification or card Payment!!
     * @param paymentTransaction
     * @return Boolean Value to make a notify decision
     * @throws ApiResponseException
     */
    private RemittaTransactionStatusPojo requestForPaymentTransactionStatus(PaymentTransaction paymentTransaction) throws ApiResponseException {


        Boolean isTesting = ninjaProperties.isDev() || ninjaProperties.isTest();

        Call<RemittaTransactionStatusPojo> transactionStatusResponse = remittaApi.getTransactionStatus(remittaDao.getSettingsValue(RemittaDao.REMITTA_MECHANT_ID, "657", true),
                paymentTransaction.getProviderTransactionReference(),
                remittaDao.generateHash(paymentTransaction.getProviderTransactionReference()));

        try {

            Response<RemittaTransactionStatusPojo> execute = transactionStatusResponse.execute();

            RemittaTransactionStatusPojo responseBody = execute.body();

            if (execute.isSuccessful() && responseBody != null) {

                if (isTesting || responseBody.getStatus().equalsIgnoreCase("01") || responseBody.getStatus().equalsIgnoreCase("00")) {

                    if (paymentTransaction.getAmountPaidInKobo() < paymentTransaction.getAmountInKobo()) {
                        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PARTIAL);
                    } else {
                        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
                    }


                }else {
                    paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);
                }


                paymentTransaction.setPaymentProvider(PaymentProviderConstant.REMITA);
                paymentTransaction.setLastUpdated(Timestamp.from(Instant.now()));
                return responseBody;

            }

        } catch (IOException e) {

            e.printStackTrace();
            throw new ApiResponseException(e.getMessage());
        }

        return null;
    }


    @Transactional
    public RemittaTransactionStatusPojo updatePaymentTransactionOnCardPay(PaymentTransaction paymentTransaction) throws NotFoundException, ApiResponseException {
        if (paymentTransaction == null) {
            throw new NotFoundException("Payment Transaction with RRR cannot be found");
        }

        RemittaTransactionStatusPojo response = null;

        if (!paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
            paymentTransaction.setPaymentProvider(PaymentProviderConstant.REMITA);
            paymentTransaction.setPaymentChannel(PaymentChannelConstant.MASTERCARD); // TODO update after model update
            paymentTransaction.setAmountPaidInKobo(paymentTransaction.getAmountInKobo());
            paymentTransaction.setLastUpdated(Timestamp.from(Instant.now()));
            paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);



            response = requestForPaymentTransactionStatus(paymentTransaction);

            if (response != null && (response.getStatus().equalsIgnoreCase("01") || response.getStatus().equalsIgnoreCase("00"))) {

                Payer payer = remittaDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
                RemittaNotification notification = new RemittaNotification();
                notification.setRrr(paymentTransaction.getProviderTransactionReference());
                notification.setChannel(PaymentChannelConstant.MASTERCARD.getValue());
                notification.setAmount(PaymentUtil.getAmountInNaira(paymentTransaction.getAmountPaidInKobo()));
                notification.setTransactiondate(PaymentUtil.getDate(paymentTransaction.getLastUpdated()));
                notification.setDebitdate("");
                notification.setBank("CARD");
                notification.setBranch("CARD");
                notification.setServiceTypeId(remittaDao.getSettingsValue(RemittaDao.CBS_REMITTA_SERVICE_TYPE_ID, "1234"));
                notification.setPayerName(String.format("%s-%s", payer.getFirstName(), payer.getLastName()));
                notification.setPayerPhoneNumber(payer.getPhoneNumber());
                notification.setPayerEmail(payer.getEmail());
                queueNotification(notification, paymentTransaction);

                notificationService.sendPaymentNotification(10);

            }

            paymentTransactionDao.updateObject(paymentTransaction);
        }


        return response;


    }


    private void queueNotification(RemittaNotification paymentPojo, PaymentTransaction paymentTransaction) {
        Merchant merchant = paymentTransactionDao.getRecordById(Merchant.class, paymentTransaction.getMerchant().getId());
        TransactionNotificationPojo<RemittaNotification> transactionNotificationPojo = new TransactionNotificationPojo<>();
        List<String> itemCodes = paymentTransactionDao
                .getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE).stream().map(it -> it.getItemId()).collect(Collectors.toList());

        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), Constants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
        if (paymentTransaction.getPaymentProvider().equals(PaymentProviderConstant.REMITA)) {  //TODO This is to avoid  alter the system
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
