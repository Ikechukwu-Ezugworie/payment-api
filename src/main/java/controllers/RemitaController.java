package controllers;

import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import extractors.ContentExtract;
import extractors.IPAddress;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.remitta.RemittaNotification;
import services.PayDirectService;
import services.PaymentTransactionService;
import services.RemittaService;

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
    private Messages messages;
    @Inject
    private MerchantDao merchantDao;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private PayDirectService payDirectService;
    @Inject
    private RemittaService remittaService;


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
        remittaService.processPaymentNotification(remittaNotifications);

        return Results.json();

    }
}
