package controllers;

import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.quickTeller.QTPaymentNotificationPojo;
import pojo.quickTeller.QTTransactionQueryResponse;
import services.PaymentTransactionService;
import services.QuickTellerService;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.time.Instant;

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
        RawDump rawDump = new RawDump();
        rawDump.setRequest(PaymentUtil.toJSON(pojo));
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.INTERSWITCH);
        rawDump.setPaymentChannel(PaymentChannelConstant.QUICKTELLER);
        paymentTransactionService.dump(rawDump);
        try {
            if (pojo.getResp_code().equalsIgnoreCase(QuickTellerService.TRANSACTION_APPROVED)) {
                QTTransactionQueryResponse res = quickTellerService.updateTransactionStatus(pojo.getShort_trans_ref());
                rawDump.setResponse(PaymentUtil.toJSON(res));
                paymentTransactionService.updateDump(rawDump);
                if (res != null) {
                    pojo.setAmount(res.getAmount());
                }
                return Results.html().render("success", true).render("successUrl", s).render("amount", pojo.getAmount());
            } else {
                paymentTransactionService.updateDump(rawDump);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.html().render("fail", true).render("failUrl", f).render("why", pojo.getResp_desc()).render("amount", pojo.getAmount());
    }

    public Result updatePendingPayment(@Param("batch") Integer batch) {
        if (batch == null) {
            batch = 20;
        }
        quickTellerService.updatePaymentTransactions(batch);
        return Results.json();
    }
}
