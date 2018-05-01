package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.quickTeller.QTPaymentNotificationPojo;
import services.QuickTellerService;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class QuickTellerController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private QuickTellerService quickTellerService;

    public Result doQuickTellerNotification(QTPaymentNotificationPojo pojo) {
        logger.info(pojo.toString());
        quickTellerService.updateTransactionStatus(pojo.getShort_trans_ref());
        return Results.json();
    }
}
