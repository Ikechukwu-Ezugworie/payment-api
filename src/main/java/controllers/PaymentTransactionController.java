package controllers;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentTransactionStatus;
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
import ninja.utils.NinjaProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ItemPojo;
import pojo.Ticket;
import pojo.TransactionRequestPojo;
import services.PaymentTransactionService;
import utils.Constants;
import utils.LocalizationUtils;
import utils.PaymentUtil;
import utils.ResponseUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
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
    @Inject
    private NinjaProperties ninjaProperties;

    @FilterWith(MerchantFilter.class)
    public Result createPaymentTransaction(@ContentExtract String payload,
                                           Context context, @Merch Merchant merchant) {
        logger.info(payload);
        String hash = context.getHeader(Constants.REQUEST_HASH_HEADER);
        if (StringUtils.isBlank(hash)) {
            return ResponseUtil.returnJsonResult(400, "Missing hash header");
        }

        if (!ninjaProperties.isDev()) {
            String ver = PaymentUtil.generateDigest("" + merchant.getCode() + merchant.getApiKey() + payload,
                    Constants.SHA_512_ALGORITHM_NAME);

            logger.info(ver);
            if (!hash.equals(ver)) {
                return ResponseUtil.returnJsonResult(403, "Invalid request");
            }
        }

        TransactionRequestPojo request = PaymentUtil.fromJSON(payload, TransactionRequestPojo.class);

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TransactionRequestPojo>> constraintViolations = validator.validate(request);

        if (constraintViolations.iterator().hasNext()) {
            String message = constraintViolations.iterator().next().getMessage();
            Path field = constraintViolations.iterator().next().getPropertyPath();

            field.iterator().forEachRemaining(node -> logger.info(node.getName()));
            return ResponseUtil.returnJsonResult(400, LocalizationUtils.getLocalizedMessage(message, context, messages, field));
        }

        PaymentTransaction paymentTransaction = null;
        try {
            paymentTransaction = paymentTransactionService.processTransactionCreationRequest(request, merchant);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.returnJsonResult(400, e.getMessage());
        }

        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionRequestPojo.setId(paymentTransaction.getId());
        transactionRequestPojo.setMerchantTransactionReferenceId(paymentTransaction.getMerchantTransactionReferenceId());
        transactionRequestPojo.setAmountInKobo(paymentTransaction.getAmountInKobo());
        transactionRequestPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        transactionRequestPojo.setServiceTypeId(paymentTransaction.getServiceTypeId());
        transactionRequestPojo.setPaymentTransactionStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionRequestPojo.setValidateTransaction(null);
        transactionRequestPojo.setTransactionValidationUrl(null);

        return ResponseUtil.returnJsonResult(201, transactionRequestPojo);
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
        paymentTransaction.setPaymentProvider(null);
        paymentTransaction.setPaymentChannel(null);
        paymentTransaction.setServiceTypeId(null);
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

        if (paymentTransaction == null) {
            return ResponseUtil.returnJsonResult(400, LocalizationUtils.getLocalizedMessage("invalid.transaction.id", context, messages));
        }

        if (!paymentTransaction.getMerchant().getId().equals(merchant.getId())) {
            return ResponseUtil.returnJsonResult(Result.SC_403_FORBIDDEN);
        }

        TransactionRequestPojo transactionRequestPojo = paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction);
        transactionRequestPojo.setNotifyOnStatusChange(null);
        transactionRequestPojo.setNotificationUrl(null);
        transactionRequestPojo.setMerchant(null);
        transactionRequestPojo.setValidateTransaction(null);
        transactionRequestPojo.setTransactionValidationUrl(null);

        for (ItemPojo itemPojo : transactionRequestPojo.getItems()) {
            itemPojo.setStatus(null);
        }

        return ResponseUtil.returnJsonResult(200, transactionRequestPojo);
    }

    @FilterWith(MerchantFilter.class)
    public Result getPaymentTransactionTicket(@PathParam("transactionId") String transactionId, Context context,
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

        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(transactionId);
        if (!paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.PENDING)) {
            return ResponseUtil.returnJsonResult(400, LocalizationUtils.getLocalizedMessage("not.pending.transaction", context, messages));
        }
        Ticket o = paymentTransactionService.getInstantTransaction(paymentTransaction, merchant);
        if (o == null) {
            return ResponseUtil.returnJsonResult(400, "Payment method not supported");
        }
        return ResponseUtil.returnJsonResult(200, o);
    }

    @FilterWith(MerchantFilter.class)
    public Result createTicketForNewTransaction(@ContentExtract String payload,
                                                Context context, @Merch Merchant merchant, @PathParam("type")String type) {
        logger.info(payload);
        String hash = context.getHeader(Constants.REQUEST_HASH_HEADER);
        if (StringUtils.isBlank(hash)) {
            return ResponseUtil.returnJsonResult(400, "Missing hash header");
        }

        if (!ninjaProperties.isDev()) {
            String ver = PaymentUtil.generateDigest("" + merchant.getCode() + merchant.getApiKey() + payload,
                    Constants.SHA_512_ALGORITHM_NAME);

            logger.info(ver);
            if (!hash.equals(ver)) {
                return ResponseUtil.returnJsonResult(403, "Invalid request");
            }
        }

        TransactionRequestPojo request = PaymentUtil.fromJSON(payload, TransactionRequestPojo.class);

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TransactionRequestPojo>> constraintViolations = validator.validate(request);

        if (constraintViolations.iterator().hasNext()) {
            String message = constraintViolations.iterator().next().getMessage();
            Path field = constraintViolations.iterator().next().getPropertyPath();

            field.iterator().forEachRemaining(node -> logger.info(node.getName()));
            return ResponseUtil.returnJsonResult(400, LocalizationUtils.getLocalizedMessage(message, context, messages, field));
        }

        PaymentTransaction paymentTransaction = null;
        try {
            paymentTransaction = paymentTransactionService.processTransactionCreationRequest(request, merchant);
            return ResponseUtil.returnJsonResult(200,paymentTransactionService.getInstantTransaction(paymentTransaction,merchant));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.returnJsonResult(400, e.getMessage());
        }
    }
}
