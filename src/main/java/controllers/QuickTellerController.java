package controllers;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;

import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentResponseStatusConstant;

import java.sql.Timestamp;
import java.time.Instant;

import com.bw.payment.entity.PaymentTransaction;

import com.bw.payment.entity.PaymentResponseLog;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.quickTeller.QTPaymentNotificationPojo;
import services.PaymentTransactionService;
import services.QuickTellerService;
import utils.PaymentUtil;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class QuickTellerController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private QuickTellerService quickTellerService;
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;

    public Result doQuickTellerNotification(QTPaymentNotificationPojo pojo, @Param("successurl") String s, @Param("failurl") String f, Context context) {
        logger.info(pojo.toString());
        try {
            RawDump rawDump=new RawDump();
            rawDump.setRequest(PaymentUtil.toJSON(pojo));
            rawDump.setDateCreated(Timestamp.from(Instant.now()));
            rawDump.setPaymentProvider(PaymentProviderConstant.INTERSWITCH);
            rawDump.setPaymentChannel(PaymentChannelConstant.QUICKTELLER);

            paymentTransactionService.dump(rawDump);

            if (pojo.getResp_code().equalsIgnoreCase(QuickTellerService.TRANSACTION_APPROVED)) {
                quickTellerService.updateTransactionStatus(pojo.getShort_trans_ref());
                logger.info(s);
                return Results.html().render("success", true).render("successUrl", s).render("amount", pojo.getAmount());
            }
        } catch (Exception e) {
            e.printStackTrace();
//            PaymentResponseLog paymentResponseLog = new PaymentResponseLog();
//            paymentResponseLog.setRecieptNumber(paymentTransactionService.get);
//            paymentResponseLog.setPaymentReference("");
//            paymentResponseLog.setAmountInKobo(pojo.getAmount());
//            paymentResponseLog.setPaymentLogId("");
//            paymentResponseLog.setResponseDump(PaymentUtil.toJSON(pojo));
//            paymentResponseLog.setValidated(false);
//            paymentResponseLog.setProcessed(false);
//            paymentResponseLog.setStatus(new PaymentResponseStatusConstant());
//            paymentResponseLog.setReason(pojo.getResp_desc());
//            paymentResponseLog.setDateCreated(Timestamp.from(Instant.now()));
//            paymentResponseLog.setPaymentTransaction();
        }
        return Results.html().render("fail", true).render("failUrl", f).render("why", pojo.getResp_desc()).render("amount", pojo.getAmount());
    }

//    public Result updatePayment(QTPaymentNotificationPojo pojo, @Param("successurl") String s, @Param("failurl") String f, Context context) {
//        logger.info(pojo.toString());
//        if (pojo.getResp_code().equalsIgnoreCase(QuickTellerService.TRANSACTION_APPROVED)) {
//            quickTellerService.updateTransactionStatus(pojo.getShort_trans_ref());
//            logger.info(s);
//            return Results.html().render("success", true).render("successUrl", s).render("amount", pojo.getAmount());
//        }
//        logger.info(f);
//        return Results.html().render("fail", true).render("failUrl", f).render("why", pojo.getResp_desc()).render("amount", pojo.getAmount());
//    }
}
