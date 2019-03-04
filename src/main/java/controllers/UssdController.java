package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import constraints.PaymentChannel;
import pojo.ItemPojo;
import pojo.PayerPojo;
import com.google.common.collect.Lists;
import pojo.MerchantRequestPojo;


import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import extractors.ContentExtract;
import extractors.IPAddress;
import ninja.Result;
import ninja.Results;
import pojo.TransactionRequestPojo;
import pojo.payDirect.customerValidation.EndSystemCustomerValidationResponse;
import pojo.remitta.RemittaNotification;
import pojo.ussd.UssdNotification;
import services.PaymentTransactionService;
import services.UssdService;
import utils.Constants;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class UssdController {


    @Inject
    PaymentTransactionService paymentTransactionService;

    @Inject
    UssdService ussdService;


    public Result doUssdNotification(@ContentExtract String payload, @IPAddress String ipAddress) {
        System.out.println("About to start Ussd notification");
        if(payload == null){
            Results.badRequest();
        }

        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.NIBBS);  // Todo Update to USSD
        rawDump.setPaymentChannel(PaymentChannelConstant.WEBPAY); //
        rawDump.setRequestIp(ipAddress);
        paymentTransactionService.dump(rawDump);


        UssdNotification ussdNotification = new Gson().fromJson(payload, new TypeToken<UssdNotification>() {
        }.getType());


        PaymentTransaction createdPaymentTrnsaction = ussdService.doUssdNotification(ussdNotification);

        if (createdPaymentTrnsaction == null) {
            return Results.notFound();
        }



        return Results.ok();


    }
}
