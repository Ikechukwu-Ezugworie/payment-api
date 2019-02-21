package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import exceptions.ApiResponseException;
import extractors.ContentExtract;
import extractors.IPAddress;
import ninja.Result;
import ninja.Results;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;
import pojo.TransactionRequestPojo;
import pojo.remitta.RemittaCreateTransactionResponse;
import pojo.remitta.RemittaNotification;
import services.PaymentTransactionService;
import services.RemittaService;
import utils.Constants;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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


    public Result doCreateTransaction(@JSR303Validation TransactionRequestPojo data, Validation validation) {

        ApiResponse<RemittaCreateTransactionResponse> apiResponse = new ApiResponse<>();
        if (validation.hasViolations()) {
            apiResponse.setMessage(validation.getViolations().get(0).getDefaultMessage());
            apiResponse.setCode(HttpStatus.SC_BAD_REQUEST);
            return Results.ok().json().render(apiResponse);
        }
        //transactionRequestPojo.setAmountInKobo(data.getAmount());
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
}
