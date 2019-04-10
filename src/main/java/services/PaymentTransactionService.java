package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.CurrencyDao;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import ninja.ReverseRouter;
import ninja.utils.NinjaProperties;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import pojo.ItemPojo;
import pojo.PayerPojo;
import pojo.Ticket;
import pojo.TransactionRequestPojo;
import pojo.flutterWave.SplitDto;
import services.sequence.PayerIdSequence;
import services.sequence.TransactionIdSequence;
import utils.Constants;
import utils.PaymentUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CREATED BY GIBAH
 */
public class PaymentTransactionService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private PaymentTransactionDao paymentTransactionDao;
    private MerchantDao merchantDao;
    private NinjaProperties ninjaProperties;

    private OkHttpClient client;
    @Inject
    private TransactionIdSequence transactionIdSequence;
    @Inject
    private PayerIdSequence payerIdSequence;
    @Inject
    private ReverseRouter reverseRouter;
    @Inject
    private QuickTellerService quickTellerService;
    @Inject
    CurrencyDao currencyDao;

    @Inject
    public PaymentTransactionService(PaymentTransactionDao paymentTransactionDao, MerchantDao merchantDao, NinjaProperties ninjaProperties) {
        this.paymentTransactionDao = paymentTransactionDao;
        this.merchantDao = merchantDao;
        this.ninjaProperties = ninjaProperties;
        this.client = PaymentUtil.getOkHttpClient(ninjaProperties);
    }

    public TransactionRequestPojo getFullPaymentTransactionDetailsAsPojo(PaymentTransaction paymentTransaction) {
        TransactionRequestPojo transactionRequestPojo = new TransactionRequestPojo();
        transactionRequestPojo.setId(paymentTransaction.getId());
        transactionRequestPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionRequestPojo.setDateCreated(PaymentUtil.format(paymentTransaction.getDateCreated(), Constants.ISO_DATE_TIME_FORMAT));
        transactionRequestPojo.setMerchantTransactionReferenceId(paymentTransaction.getMerchantTransactionReferenceId());
        transactionRequestPojo.setAmountInKobo(paymentTransaction.getAmountInKobo());
        transactionRequestPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        transactionRequestPojo.setPaymentChannel(paymentTransaction.getPaymentChannel().getValue());
        transactionRequestPojo.setServiceTypeId(paymentTransaction.getServiceTypeId());
        transactionRequestPojo.setCustomerTransactionReference(paymentTransaction.getCustomerTransactionReference());

        Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
        transactionRequestPojo.setPayer(generatePayerPojo(payer));

        List<Item> items = paymentTransactionDao.getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE);
        for (Item item : items) {
            transactionRequestPojo.addItem(generateItemPojo(item));
        }
        transactionRequestPojo.setPaymentTransactionStatus(paymentTransaction.getPaymentTransactionStatus().getValue());

        return transactionRequestPojo;
    }

    public ItemPojo generateItemPojo(Item item) {
        if (item == null) {
            return null;
        }

        ItemPojo itemPojo = new ItemPojo();
        itemPojo.setName(item.getName());
        itemPojo.setItemId(item.getItemId());
        itemPojo.setQuantity(item.getQuantity());
        itemPojo.setPriceInKobo(item.getPriceInKobo());
        itemPojo.setTaxInKobo(item.getTaxInKobo());
        itemPojo.setSubTotalInKobo(item.getSubTotalInKobo());
        itemPojo.setTotalInKobo(item.getTotalInKobo());
        itemPojo.setDescription(item.getDescription());
        itemPojo.setStatus(item.getStatus().getValue());
        itemPojo.setId(item.getId());

        return itemPojo;
    }

    public PayerPojo generatePayerPojo(Payer payer) {
        return paymentTransactionDao.getPayerAsPojo(payer);
    }

    public PaymentTransaction processTransactionCreationRequest(TransactionRequestPojo request, Merchant merchant) {

//        if (request.getValidateTransaction()) {
//            if (StringUtils.isBlank(request.getTransactionValidationUrl())) {
//                throw new IllegalArgumentException("Transaction validation url not set");
//            }
//        }
//        PaymentProviderConstant providerConstant = PaymentProviderConstant.fromValue(request.getPaymentProvider());
//        switch (providerConstant) {
//            case INTERSWITCH:
//                validateInterswitchTransactionRequest(request, merchant);
//                break;
//            case REMITA:
//                break;
//            case NIBBS:
//                break;
//        }
        return createTransaction(request, merchant);
    }

//    private void validateInterswitchTransactionRequest(TransactionRequestPojo request, Merchant merchant) throws IllegalArgumentException {
//        PaymentProviderDetails paymentProviderDetails = merchantDao.getMerchantPaymentProviderDetails(merchant.getId(), PaymentProviderConstant.INTERSWITCH);
//        if (paymentProviderDetails == null) {
//            throw new IllegalArgumentException("Please add interswitch payment provider details");
//        }
//
//        if (StringUtils.isBlank(paymentProviderDetails.getMerchantId())) {
//            throw new IllegalArgumentException("No payment provider merchant id provided");
//        }
//    }

    public PaymentTransaction getPaymentTransactionByTransactionId(String transactionId) {
        return paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId", transactionId);
    }

    @Transactional
    public Ticket getInstantTransaction(PaymentTransaction paymentTransaction, Merchant merchant) {
        PaymentChannelConstant paymentChannel = paymentTransaction.getPaymentChannel();
        switch (paymentChannel) {
            case PAYDIRECT:
                break;
            case QUICKTELLER:
                return quickTellerService.generateTicket(paymentTransaction, merchant);
            case MASTERCARD:
                break;
        }
        return null;
    }

    @Transactional
    public void dump(RawDump rawDump) {
        if (rawDump == null) {
            return;
        }
        if (rawDump.getId() != null) {
            paymentTransactionDao.updateObject(rawDump);
            return;
        }
        paymentTransactionDao.saveObject(rawDump);
    }

    @Transactional
    public void updateDump(RawDump rawDump) {
        paymentTransactionDao.updateObject(rawDump);
    }


    @Transactional
    public PaymentTransaction createTransaction(TransactionRequestPojo request, Merchant merchant, String transactionId) {
        if (StringUtils.isBlank(transactionId)) {
            transactionId = transactionIdSequence.getNext();
        }

        if (merchant == null) {
            merchant = merchantDao.getAllRecords(Merchant.class).get(0);
        }

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        if (ninjaProperties.isDev()) {
            paymentTransaction.setTransactionId("DEV" + transactionId);
        } else if (ninjaProperties.isTest()) {
            paymentTransaction.setTransactionId("TEST" + transactionId);
        } else {
            paymentTransaction.setTransactionId(transactionId);

        }
        paymentTransaction.setDateCreated(PaymentUtil.nowToTimeStamp());
        paymentTransaction.setMerchantTransactionReferenceId(request.getMerchantTransactionReferenceId());
        paymentTransaction.setAmountInKobo(request.getAmountInKobo());
        paymentTransaction.setAmountPaidInKobo(0L);
        paymentTransaction.setPaymentProvider(PaymentProviderConstant.fromValue(request.getPaymentProvider()));
        if (StringUtils.isNotBlank(request.getPaymentChannel())) {
            paymentTransaction.setPaymentChannel(PaymentChannelConstant.fromValue(request.getPaymentChannel()));
        }

        paymentTransaction.setServiceTypeId(request.getServiceTypeId());
        paymentTransaction.setMerchant(merchant);
        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENDING);
        paymentTransaction.setCustomerTransactionReference(request.getCustomerTransactionReference());

        Payer payer = new Payer();
        payer.setPayerId(payerIdSequence.getNext());
        payer.setFirstName(request.getPayer().getFirstName());
        payer.setLastName(request.getPayer().getLastName());
        payer.setEmail(request.getPayer().getEmail());
        payer.setPhoneNumber(request.getPayer().getPhoneNumber());
//        payer.setAddress(request.getPayer().getAddress());

        paymentTransactionDao.saveObject(payer);

        paymentTransaction.setPayer(payer);
        Currency currency = currencyDao.findByCode(request.getCurrencyCode(), GenericStatusConstant.ACTIVE);
        paymentTransaction.setCurrency(currency);

        paymentTransactionDao.saveObject(paymentTransaction);
        if (request.getItems() != null) {
            for (ItemPojo itemPojo : request.getItems()) {
                Item item = new Item();
                item.setName(itemPojo.getName());
                item.setItemId(itemPojo.getItemId());
                item.setQuantity(itemPojo.getQuantity());
                item.setPriceInKobo(itemPojo.getPriceInKobo());
                item.setTaxInKobo(itemPojo.getTaxInKobo());
                item.setSubTotalInKobo(itemPojo.getSubTotalInKobo());
                item.setTotalInKobo(itemPojo.getTotalInKobo());
                item.setDescription(itemPojo.getDescription());
                item.setStatus(GenericStatusConstant.ACTIVE);

                paymentTransactionDao.saveObject(item);

                PaymentTransactionItem paymentTransactionItem = new PaymentTransactionItem();
                paymentTransactionItem.setItem(item);
                paymentTransactionItem.setPaymentTransaction(paymentTransaction);
                paymentTransactionDao.saveObject(paymentTransactionItem);

            }
        }

        PaymentRequestLog paymentTransactionRequestLog = new PaymentRequestLog();
        paymentTransactionRequestLog.setRequestDump(PaymentUtil.toJSON(request));
        paymentTransactionRequestLog.setDateCreated(PaymentUtil.nowToTimeStamp());
        paymentTransactionRequestLog.setPaymentTransaction(paymentTransaction);

        paymentTransactionDao.saveObject(paymentTransactionRequestLog);

        if (request.getSplit() != null) {
            for (SplitDto splitDto : request.getSplit()) {
                TransactionSplit transactionSplit = new TransactionSplit();
                transactionSplit.setCode(splitDto.getCode());
                transactionSplit.setRatio(splitDto.getRatio());
                transactionSplit.setMerchantIdentifier(splitDto.getIdentifier());
                transactionSplit.setPaymentTransaction(paymentTransaction);

                paymentTransactionDao.saveObject(transactionSplit);
            }
        }

        return paymentTransaction;
    }

    public PaymentTransaction createTransaction(TransactionRequestPojo request, Merchant merchant) {
        return createTransaction(request, merchant, null);
    }
}
