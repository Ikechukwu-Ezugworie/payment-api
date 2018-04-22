package controllers;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.PaymentTransaction;
import com.google.inject.Inject;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import extractors.Merch;
import filters.MerchantFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.TransactionRequestPojo;
import utils.ResponseUtil;
import utils.ValidationUtils;

/**
 * CREATED BY GIBAH
 */
public class PaymentTransactionController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private Messages messages;
    @Inject
    private MerchantDao merchantDao;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;

    @FilterWith(MerchantFilter.class)
    public Result createPaymentTransaction(@JSR303Validation TransactionRequestPojo request, Validation validation,
                                           Context context, @Merch Merchant merchant) {
        if (validation.hasViolations()) {
            return ResponseUtil.returnJsonResult(Result.SC_400_BAD_REQUEST,
                    ValidationUtils.getFirstViolationMessage(context, messages, validation));
        }

        PaymentTransaction paymentTransaction = null;
        try {
            paymentTransaction = paymentTransactionDao.createTransaction(request, merchant);
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
}
