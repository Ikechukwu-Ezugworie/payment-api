package controllers;

import com.bw.payment.enumeration.GenericStatusConstant;
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
        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.NIBBS);  // Todo Update to USSD
        rawDump.setPaymentChannel(PaymentChannelConstant.WEBPAY); //
        rawDump.setRequestIp(ipAddress);
        paymentTransactionService.dump(rawDump);


        UssdNotification ussdNotification = new Gson().fromJson(payload, new TypeToken<UssdNotification>() {
        }.getType());


        TransactionRequestPojo transaction = new TransactionRequestPojo();
        transaction.setMerchantTransactionReferenceId(String.format("%s-%s", ussdNotification.getMsisdn(), ussdNotification.getTransactionReference()));
        transaction.setAmountInKobo(PaymentUtil.getAmountInKobo(ussdNotification.getAmount()));
        transaction.setPaymentProvider(PaymentProviderConstant.NIBBS.getValue()); // Todo:: Please Update
        transaction.setPaymentChannel(PaymentChannelConstant.BANK.getValue()); // Todo:: Please Update
        PayerPojo payerPojo = new PayerPojo();
        payerPojo.setFirstName(ussdNotification.getMsisdn());
        payerPojo.setLastName("");
        payerPojo.setEmail(Constants.NOT_PROVIDED);
        payerPojo.setPhoneNumber(ussdNotification.getMsisdn());

        transaction.setPayer(payerPojo);
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
        transaction.setItems(items);
        transaction.setPaymentTransactionStatus(EndSystemCustomerValidationResponse.PaymentStatus.PAID.getValue());

        transaction.setProviderTransactionReference(ussdNotification.getTransactionReference());
        transaction.setMerchantTransactionReferenceId(String.format("%s-%s", ussdNotification.getMsisdn(), ussdNotification.getTransactionReference()));



        ussdService.doUssdNotification();


    }
}
