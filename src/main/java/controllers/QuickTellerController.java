package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.quickTeller.QTPaymentNotificationPojo;
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

    public Result doQuickTellerNotification(QTPaymentNotificationPojo pojo, @Param("successurl") String s, @Param("failurl") String f) {
        logger.info(pojo.toString());
        if (pojo.getResp_code().equalsIgnoreCase(QuickTellerService.TRANSACTION_APPROVED)) {
            quickTellerService.updateTransactionStatus(pojo.getShort_trans_ref());
            logger.info(s);
            return Results.html().render("success", true).render("successUrl", s).render("amount", PaymentUtil.koboToNaira(pojo.getAmount()));
        }
        logger.info(f);
        return Results.html().render("fail", true).render("failUrl", f).render("why", pojo.getResp_desc()).render("amount", PaymentUtil.koboToNaira(pojo.getAmount()));
    }
}
