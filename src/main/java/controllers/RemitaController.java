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
import extractors.ContentExtract;
import extractors.IPAddress;
import javassist.NotFoundException;
import ninja.Context;
import ninja.Result;
import ninja.Results;
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


    public Result doCreateTransaction(@JSR303Validation TransactionRequestPojo data, Validation validation) {

        ApiResponse<RemittaCreateTransactionResponse> apiResponse = new ApiResponse<>();
        if (validation.hasViolations()) {
            apiResponse.setMessage(validation.getViolations().get(0).getDefaultMessage());
            apiResponse.setCode(HttpStatus.SC_BAD_REQUEST);
            return Results.ok().json().render(apiResponse);
        }
        //transactionRequestPojo.setTotalAmount(data.getAmount());
        data.setNotifyOnStatusChange(true);
        data.setPaymentProvider(PaymentProviderConstant.REMITA.value());
        data.setPaymentChannel(PaymentChannelConstant.BANK.getValue()); // TODO Update after model update
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
            remittaService.updatePaymentTransaction(remittaNotifications);
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
            apiResponse.setMessage(String.format("Payment transaction with  %s cannot be found", paymentReference));
            return Results.notFound().json().render(apiResponse);
        }

        RemittaFormPayloadPojo data = new RemittaFormPayloadPojo();
        data.setMerchantId(remittaDao.getMerchantId());
        data.setHash(remittaDao.generateCardHash(paymentTransaction.getProviderTransactionReference()));
        data.setRrr(paymentTransaction.getProviderTransactionReference());
        String responseUrl = "http://localhost:8880/api/v1/payments/remitta/card/make-payment";
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


    public Result cardNotificationUrl(@Param("RRR") String paymentReference) {


        UriBuilder uriBuilder = UriBuilder.fromPath(remittaDao.getRemittaCredentials().getMerchantRedirectUrl());
        RemittaTransactionStatusPojo response = null;


        PaymentTransaction paymentTransaction = remittaDao.getPaymentTrnsactionByRRR(paymentReference);
        try {
            response = remittaService.updatePaymentTransactionOnCardPay(paymentTransaction);
        } catch (NotFoundException e) {
            e.printStackTrace();
            uriBuilder.queryParam("status", "404");
            uriBuilder.queryParam("message", "RRR cannot found");

            URI uri = uriBuilder.build();
            String url = uri.toString();
            System.out.println(url);
            return Results.redirect(url);


        } catch (ApiResponseException e) {
            e.printStackTrace();
            uriBuilder.queryParam("status", HttpStatus.SC_BAD_GATEWAY);
            uriBuilder.queryParam("message", "Cannot verify payment at this time ");
            URI uri = uriBuilder.build();
            String url = uri.toString();
            System.out.println(url);
            return Results.redirect(url);

        }

        System.out.println("Response after confirming from Remitta {}{}{}" + response.toString());


        uriBuilder.queryParam("rrr", paymentTransaction.getProviderTransactionReference());
        uriBuilder.queryParam("orderId", response.getOrderId());
        uriBuilder.queryParam("invoiceRef", paymentTransaction.getMerchantTransactionReferenceId());
        if (response.getStatusmessage() != null) {
            uriBuilder.queryParam("message", response.getStatusmessage());
        }
        uriBuilder.queryParam("status", response.getStatus());
        URI uri = uriBuilder.build();
        String url = uri.toString();
        System.out.println(url);
        return Results.redirect(url);

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
