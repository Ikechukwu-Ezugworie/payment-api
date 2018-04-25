package controllers;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.PaymentTransaction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import extractors.Merch;
import filters.MerchantFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.TransactionRequestPojo;
import services.PaymentTransactionService;
import utils.LocalizationUtils;
import utils.ResponseUtil;
import utils.ValidationUtils;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class PaymentTransactionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private Messages messages;
    @Inject
    private MerchantDao merchantDao;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private PaymentTransactionService paymentTransactionService;

    @FilterWith(MerchantFilter.class)
    public Result createPaymentTransaction(@JSR303Validation TransactionRequestPojo request, Validation validation,
                                           Context context, @Merch Merchant merchant) {
        if (validation.hasViolations()) {
            return ResponseUtil.returnJsonResult(Result.SC_400_BAD_REQUEST,
                    ValidationUtils.getFirstViolationMessage(context, messages, validation));
        }

        PaymentTransaction paymentTransaction = null;
        try {
            paymentTransaction = paymentTransactionService.createTransaction(request, merchant);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.returnJsonResult(400, e.getMessage());
        }
        paymentTransaction.setId(null);
        paymentTransaction.setMerchant(null);
        paymentTransaction.setPayer(null);
        paymentTransaction.setDateCreated(null);
        paymentTransaction.setLastUpdated(null);
        paymentTransaction.setPayer(null);

        return ResponseUtil.returnJsonResult(201, paymentTransaction);
    }

    @FilterWith(MerchantFilter.class)
    public Result getPaymentTransactionStatus(@PathParam("transactionId") String transactionId, @Merch Merchant merchant, Context context) {
        if (StringUtils.isBlank(transactionId)) {
            return ResponseUtil.returnJsonResult(400, LocalizationUtils.getLocalizedMessage("invalid.transaction.id", context, messages));
        }

        PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId", transactionId);

        if (paymentTransaction == null) {
            return ResponseUtil.returnJsonResult(404, LocalizationUtils.getLocalizedMessage("payment.transaction.not.found", context, messages));
        }

        paymentTransaction.setDateCreated(null);
        paymentTransaction.setLastUpdated(null);
        paymentTransaction.setAmountInKobo(null);
        paymentTransaction.setNotifyOnStatusChange(null);
        paymentTransaction.setNotificationUrl(null);
        paymentTransaction.setPaymentProvider(null);
        paymentTransaction.setPaymentChannel(null);
        paymentTransaction.setServiceTypeId(null);
        paymentTransaction.setId(null);
        paymentTransaction.setMerchant(null);
        paymentTransaction.setPayer(null);

        return ResponseUtil.returnJsonResult(200, null, paymentTransaction);
    }

    @FilterWith(MerchantFilter.class)
    public Result getPaymentTransactionDetails(@Param("transactionId") String transactionId, Context context,
                                               @Merch Merchant merchant) {
        if (StringUtils.isBlank(transactionId)) {
            return ResponseUtil.returnJsonResult(400, LocalizationUtils.getLocalizedMessage("invalid.transaction.id", context, messages));
        }

        PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId", transactionId);

        if (!paymentTransaction.getMerchant().getId().equals(merchant.getId())) {
            return ResponseUtil.returnJsonResult(Result.SC_403_FORBIDDEN);
        }

        TransactionRequestPojo transactionRequestPojo = paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction);

        return ResponseUtil.returnJsonResult(200, transactionRequestPojo);
    }
}
