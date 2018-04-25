package controllers;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.PaymentTransaction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import extractors.ContentExtract;
import extractors.Merch;
import filters.MerchantFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.TransactionRequestPojo;
import services.PaymentTransactionService;
import utils.Constants;
import utils.LocalizationUtils;
import utils.PaymentUtil;
import utils.ResponseUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

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
    public Result createPaymentTransaction(@ContentExtract String payload,
                                           Context context, @Merch Merchant merchant) {
        String hash = context.getHeader(Constants.REQUEST_HASH_HEADER);
        if (StringUtils.isBlank(hash)) {
            return ResponseUtil.returnJsonResult(400, "Missing hash header");
        }

        String ver = PaymentUtil.generateDigest("" + merchant.getCode() + merchant.getApiKey() + payload,
                Constants.SHA_512_ALGORITHM_NAME);

        logger.info(ver);
        if (!hash.equals(ver)) {
            return ResponseUtil.returnJsonResult(403, "Invalid request");
        }

        TransactionRequestPojo request = PaymentUtil.fromJSON(payload, TransactionRequestPojo.class);

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TransactionRequestPojo>> constraintViolations = validator.validate(request);

        if (constraintViolations.iterator().hasNext()) {
            String message = constraintViolations.iterator().next().getMessage();
            Object field = constraintViolations.iterator().next().getInvalidValue();

            return ResponseUtil.returnJsonResult(400, LocalizationUtils.getLocalizedMessage(message, context, messages, field));
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

        String hash = context.getHeader(Constants.REQUEST_HASH_HEADER);
        if (StringUtils.isBlank(hash)) {
            return ResponseUtil.returnJsonResult(400, "Missing hash header");
        }

        String ver = PaymentUtil.generateDigest("" + merchant.getCode() + merchant.getApiKey() + transactionId,
                Constants.SHA_512_ALGORITHM_NAME);

        logger.info(ver);
        if (!hash.equals(ver)) {
            return ResponseUtil.returnJsonResult(403, "Invalid request");
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

        String hash = context.getHeader(Constants.REQUEST_HASH_HEADER);
        if (StringUtils.isBlank(hash)) {
            return ResponseUtil.returnJsonResult(400, "Missing hash header");
        }

        String ver = PaymentUtil.generateDigest("" + merchant.getCode() + merchant.getApiKey() + transactionId,
                Constants.SHA_512_ALGORITHM_NAME);

        logger.info(ver);
        if (!hash.equals(ver)) {
            return ResponseUtil.returnJsonResult(403, "Invalid request");
        }

        PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId", transactionId);

        if (!paymentTransaction.getMerchant().getId().equals(merchant.getId())) {
            return ResponseUtil.returnJsonResult(Result.SC_403_FORBIDDEN);
        }

        TransactionRequestPojo transactionRequestPojo = paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction);

        return ResponseUtil.returnJsonResult(200, transactionRequestPojo);
    }
}
