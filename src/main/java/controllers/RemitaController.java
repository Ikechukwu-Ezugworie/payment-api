package controllers;

import com.google.common.collect.Lists;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.RemittaDao;
import exceptions.ApiResponseException;
import exceptions.RemitaPaymentConfirmationException;
import extractors.ContentExtract;
import extractors.IPAddress;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.ReverseRouter;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;
import pojo.Item;
import pojo.TransactionRequestPojo;
import pojo.remitta.*;
import services.PaymentTransactionService;
import services.RemittaService;
import utils.Constants;
import utils.PaymentUtil;

import javax.validation.Valid;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class RemitaController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private RemittaService remittaService;
    @Inject
    PaymentTransactionService paymentTransactionService;

    @Inject
    RemittaDao remittaDao;

    @Inject
    ReverseRouter reverseRouter;


    public Result doCreateTransaction(@JSR303Validation @Valid TransactionRequestPojo data, Validation validation) {

        ApiResponse<RemittaCreateTransactionResponse> apiResponse = new ApiResponse<>();
        if (validation.hasViolations()) {
            apiResponse.setMessage(validation.getViolations().get(0).getDefaultMessage());
            apiResponse.setCode(HttpStatus.SC_BAD_REQUEST);
            return Results.ok().json().render(apiResponse);
        }
        //transactionRequestPojo.setTotalAmount(data.getAmount());
        data.setNotifyOnStatusChange(true);
        data.setPaymentProvider(PaymentProviderConstant.REMITA.value());
        data.setPaymentChannel(PaymentChannelConstant.BANK.getValue());
        try {
            PaymentTransaction paymentTransaction = remittaService.generateRemittaRRR(data);
            apiResponse.setMessage("Created a payment Transaction Successfully");
            apiResponse.setData(new RemittaCreateTransactionResponse()
                    .setTransactionId(paymentTransaction.getTransactionId())
                    .setRrr(paymentTransaction.getProviderTransactionReference()));
            apiResponse.setCode(HttpStatus.SC_OK);
            return Results.ok().json().render(apiResponse);


        } catch (Exception ex) {

            ex.printStackTrace();
            if (ex instanceof ApiResponseException) {
                apiResponse.setMessage(ex.getMessage());
                apiResponse.setCode(HttpStatus.SC_FORBIDDEN);
                return Results.ok().json().render(apiResponse);
            }
            apiResponse.setMessage(ex.getMessage());

        }

        apiResponse.setMessage("There was an internal error");
        apiResponse.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return Results.ok().json().render(apiResponse);
    }


    public Result doRemittaNotification(@ContentExtract String payload, @IPAddress String ipAddress) {
        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.REMITA);
        rawDump.setPaymentChannel(PaymentChannelConstant.BANK);
        rawDump.setRequestIp(ipAddress);
        paymentTransactionService.dump(rawDump);


        List<RemittaNotification> remittaNotifications = new Gson().fromJson(payload, new TypeToken<List<RemittaNotification>>() {
        }.getType());


        try {
            remittaService.updatePaymentTransactionForBank(remittaNotifications);
            return Results.json().render(Constants.OK_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            return Results.json().render(Constants.NOT_OK_MESSAGE);
        }

    }


    public Result makePaymentWithCard(@PathParam("rrr") String paymentReference, Context context) {
        ApiResponse apiResponse = new ApiResponse();
        PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(paymentReference);

        if (paymentTransaction == null) {
            apiResponse.setCode(HttpStatus.SC_NOT_FOUND);
            apiResponse.setMessage(String.format("Payment transaction with reference with  %s cannot be found", paymentReference));
            return Results.notFound().json().render(apiResponse);
        }

        RemittaFormPayloadPojo data = new RemittaFormPayloadPojo();
        data.setMerchantId(remittaDao.getMerchantId());
        data.setHash(remittaDao.generateCardHash(paymentTransaction.getProviderTransactionReference()));
        data.setRrr(paymentTransaction.getProviderTransactionReference());


        String responseUrl = reverseRouter.with(RemitaController::notificationOnCardPay).absolute(context).build();

        System.out.println(responseUrl);

        data.setResponseurl(responseUrl);
        data.setRemittaFormActionUrl(remittaDao.getSettingsValue("REMITTA_CARD_URL", "https://remitademo.net/remita/ecomm/finalize.reg"));
        data.setAmount(PaymentUtil.koboToNaira(paymentTransaction.getAmountInKobo()));

        data.setPaymentTransactionReference(paymentTransaction.getTransactionId());
        data.setItems(remittaDao.getPaymentItemsByPaymentTransaction(paymentTransaction).stream().map(it -> {
            Item item = new Item();
            item.setTotalAmount(PaymentUtil.koboToNaira(it.getTotalInKobo()));
            item.setItemId(it.getItemId());
            item.setName(it.getName());
            item.setPricePerItem(PaymentUtil.koboToNaira(it.getPriceInKobo()));
            item.setQuantity(it.getQuantity().toString());
            return item;

        }).collect(Collectors.toList()));

        System.out.println("Data + " + data);

        return Results.html().render("data", data);


    }


    public Result getTransactionStatus(@PathParam("rrr") String rrr) {
        PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(rrr);

        ApiResponse<RemittaTransactionStatusPojo> response = new ApiResponse<>();

        try {
            if (paymentTransaction.getPaymentChannel().equals(PaymentChannelConstant.MASTERCARD)) {
                response.setData(remittaService.updatePaymentTransactionOnCardPay(paymentTransaction));
            } else {
                response.setData(remittaService.requestForPaymentTransactionStatus(paymentTransaction));
            }

        } catch (ApiResponseException e) {
            response.setData(null);
            response.setCode(HttpStatus.SC_EXPECTATION_FAILED);
            response.setMessage("Cannot call Remita to confirm transaction");
            return Results.ok().json().render(response);
        } catch (RemitaPaymentConfirmationException e) {
            response.setData(e.getResponseObject());
            response.setCode(HttpStatus.SC_FORBIDDEN);
            response.setMessage("Cannot confirm payment at this time");
            return Results.ok().json().render(response);
        }
        return Results.internalServerError();
    }


    public Result confirmStatusForCard(@PathParam("rrr") String rrr) {

        PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(rrr);
        ApiResponse<RemittaTransactionStatusPojo> response = new ApiResponse<>();

        if (paymentTransaction == null) {
            response.setData(null);
            response.setCode(HttpStatus.SC_NOT_FOUND);
            response.setMessage(String.format("Payment transaction with RRR %s cannot be found", rrr));
        }

        try {
            response.setData(remittaService.updatePaymentTransactionOnCardPay(paymentTransaction));
            response.setCode(HttpStatus.SC_OK);
            response.setMessage("Successful ");
            return Results.ok().json().render(response);
        } catch (ApiResponseException ex) {

            response.setData(null);
            response.setCode(HttpStatus.SC_FORBIDDEN);
            response.setMessage("Cannot confirm payment at this time");
            return Results.ok().json().render(response);

        } catch (RemitaPaymentConfirmationException ex) {
            response.setData(ex.getResponseObject());
            response.setCode(HttpStatus.SC_FORBIDDEN);
            response.setMessage("Payment cannot be confirmed");
            return Results.ok().json().render(response);
        }

    }

    public Result notificationOnCardPay(@Param("RRR") String paymentReference) {

        String redirectUrl = remittaDao.getRemittaCredentials().getMerchantRedirectUrl() + "/" + paymentReference.trim();
        PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(paymentReference);
        RemittaTransactionStatusPojo response = null;


        if (paymentTransaction == null) {
            return Results.redirect(redirectUrl);

        }



        try {
            remittaService.updatePaymentTransactionOnCardPay(paymentTransaction);
        } catch (RemitaPaymentConfirmationException | ApiResponseException ex) {
            ex.printStackTrace();
        }


        return Results.redirect(redirectUrl);

    }


    public Result showRemittaBankTestSandBoxNotificationView() {

        return Results.html().render("result", "12345");

    }


    public Result performTestNotification(@ContentExtract String requestData) {

        System.out.println("{}{}{}{}Data " + requestData);

        RemittaDummyNotificationPojo request = new Gson().fromJson(requestData, new TypeToken<RemittaDummyNotificationPojo>() {
        }.getType());

        PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(request.getRrr());

        if (paymentTransaction == null) {
            return Results.json().render(HttpStatus.SC_NOT_FOUND);
        }

        if (PaymentUtil.getAmountInKobo(request.getAmount()) < paymentTransaction.getAmountInKobo()) {
            return Results.json().render(HttpStatus.SC_NOT_ACCEPTABLE);
        }

        RemittaNotification notification = new RemittaNotification();
        notification.setRrr(request.getRrr());
        notification.setChannel("BRANCH");
        notification.setAmount(request.getAmount());
        notification.setTransactiondate(PaymentUtil.format(new Date(), "dd/MM/yyy"));
        notification.setDebitdate(PaymentUtil.format(new Date(), "dd/MM/yyy"));
        notification.setBank("030");
        notification.setBranch("02");
        notification.setServiceTypeId(remittaDao.getSettingsValue(RemittaDao.CBS_REMITTA_SERVICE_TYPE_ID, "45635464"));
        notification.setOrderRef(request.getRrr());
        notification.setOrderId(String.valueOf(System.currentTimeMillis()));
        notification.setPayerName(request.getName());
        notification.setPayerPhoneNumber(request.getPhoneNumber());
        notification.setPayerEmail(request.getEmail());
        notification.setCustomFieldData(Lists.newArrayList());
        notification.setDateRequested(PaymentUtil.format(new Date(), "dd/MM/yyy"));
        List<RemittaNotification> data = new ArrayList<>();

        data.add(notification);

        String payload = new Gson().toJson(data);

        return doRemittaNotification(payload, "127.0.0.1");


    }
}
