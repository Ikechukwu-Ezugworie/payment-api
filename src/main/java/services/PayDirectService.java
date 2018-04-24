package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import dao.PaymentTransactionDao;
import ninja.Context;
import pojo.TransactionNotificationPojo;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.Customer;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.customerValidation.response.Customers;
import pojo.payDirect.customerValidation.response.PaymentItems;
import pojo.payDirect.paymentNotification.request.Payment;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import pojo.payDirect.pojo.PaymentResponsePojo;
import utils.GeneralConstants;
import utils.PaymentUtil;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static utils.PaymentUtil.getAmountInKobo;

/**
 * CREATED BY GIBAH
 */
public class PayDirectService {
    @Inject
    PaymentTransactionDao paymentTransactionDao;

    @Transactional
    public PaymentNotificationResponse processPaymentNotification(PaymentNotificationRequest request, Context context) {
        PaymentNotificationResponse paymentNotificationResponsePojo = new PaymentNotificationResponse();

        PaymentResponsePojo responsePojo = new PaymentResponsePojo();
        for (Payment paymentPojo : request.getPayments().getPayment()) {
            if (paymentTransactionDao.isDuplicateNotification(paymentPojo)) {
                responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                responsePojo.setStatus(0);

                paymentNotificationResponsePojo.addPayment(responsePojo);
                break;
            }

            PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId",
                    paymentPojo.getCustReference());

            if (paymentPojo.getReversal()) {
                if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount().abs()))) {
                    saveCurrentPaymentTransactionState(paymentTransaction);

                    paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENIDNG);

                    paymentTransactionDao.updateObject(paymentTransaction);

                    if (paymentTransaction.getNotifyOnStatusChange()) {
                        queueNotification(paymentPojo, paymentTransaction);
                    }

                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                    responsePojo.setStatus(0);

                    paymentNotificationResponsePojo.addPayment(responsePojo);
                } else {

                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                    responsePojo.setStatus(1);
                    responsePojo.setStatusMessage(String.format("Invalid amount. Expected N%s", PaymentUtil.koboToNaira(paymentTransaction.getAmountInKobo())));

                    paymentNotificationResponsePojo.addPayment(responsePojo);
                }

                break;
            }

            if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount()))) {
                if (paymentPojo.getPaymentStatus() == 0) {
                    saveCurrentPaymentTransactionState(paymentTransaction);

                    paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
                    paymentTransactionDao.updateObject(paymentTransaction);

                    if (paymentTransaction.getNotifyOnStatusChange()) {
                        queueNotification(paymentPojo, paymentTransaction);
                    }

                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                    responsePojo.setStatus(0);

                    paymentNotificationResponsePojo.addPayment(responsePojo);
                } else {
                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
                    responsePojo.setStatus(1);
                    responsePojo.setStatusMessage("Invalid status");

                    paymentNotificationResponsePojo.addPayment(responsePojo);
                }

                break;
            } else {
                responsePojo.setStatusMessage(String.format("Invalid amount. Expected N%s", PaymentUtil.koboToNaira(paymentTransaction.getAmountInKobo())));
            }

            responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
            responsePojo.setStatus(0);

            paymentNotificationResponsePojo.addPayment(responsePojo);
        }
        return paymentNotificationResponsePojo;
    }


    public CustomerInformationResponse processCustomerValidationRequest(CustomerInformationRequest request, Context context) {
        CustomerInformationResponse customerInformationResponse = new CustomerInformationResponse();
        customerInformationResponse.setMerchantReference(request.getMerchantReference());

        PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId",
                String.valueOf(request.getCustReference()));
        if (paymentTransaction == null) {
            Customer customer = new Customer();
            customer.setFirstName("");
            customer.setCustReference(request.getCustReference());
            customer.setStatus(1);
            customer.setStatusMessage("Invalid transaction");
            customer.setCustomerReferenceAlternate("");

            Customers customers = new Customers();
            customers.addCustomer(customer);

            customerInformationResponse.setCustomers(customers);
            return customerInformationResponse;
        }

        Merchant merchant = paymentTransactionDao.getMerchantByMerchantId(request.getMerchantReference(), PaymentProviderConstant.INTERSWITCH);
        if (merchant == null) {
            Customer customer = new Customer();
            customer.setFirstName("");
            customer.setCustReference(request.getCustReference());
            customer.setStatus(1);
            customer.setStatusMessage("Merchant not found");
            customer.setCustomerReferenceAlternate("");

            Customers customers = new Customers();
            customers.addCustomer(customer);

            customerInformationResponse.setCustomers(customers);
            return customerInformationResponse;
        }

        if (!paymentTransaction.getMerchant().getId().equals(merchant.getId())) {
            Customer customer = new Customer();
            customer.setFirstName("");
            customer.setCustReference(request.getCustReference());
            customer.setStatus(1);
            customer.setCustomerReferenceAlternate("");

            Customers customers = new Customers();
            customers.addCustomer(customer);

            customerInformationResponse.setCustomers(customers);
            return customerInformationResponse;
        }

        Payer payer = paymentTransactionDao.getRecordById(Payer.class, paymentTransaction.getPayer().getId());
        List<Item> items = paymentTransactionDao.getPaymentTransactionItems(paymentTransaction.getId(), GenericStatusConstant.ACTIVE);

        Customer customer = new Customer();
        customer.setFirstName(payer.getFirstName());
        customer.setLastName(payer.getLastName());
        customer.setEmail(payer.getEmail());
        customer.setPhone(payer.getPhoneNumber());
        customer.setAmount(PaymentUtil.getAmountInNaira(paymentTransaction.getAmountInKobo()));
        customer.setCustReference(paymentTransaction.getTransactionId());
        customer.setStatus(0);

        PaymentItems customerPaymentItems = new PaymentItems();
        for (Item item : items) {
            pojo.payDirect.customerValidation.response.Item paymentItem = new pojo.payDirect.customerValidation.response.Item();
            paymentItem.setProductName(item.getName());
            paymentItem.setProductCode(item.getItemId());
            paymentItem.setQuantity(item.getQuantity().longValue());
            paymentItem.setPrice(PaymentUtil.getAmountInNaira(item.getPriceInKobo()));
            paymentItem.setSubtotal(PaymentUtil.getAmountInNaira(item.getSubTotalInKobo()));
            paymentItem.setTax(PaymentUtil.getAmountInNaira(item.getTaxInKobo()));
            paymentItem.setTotal(PaymentUtil.getAmountInNaira(item.getTotalInKobo()));

            customerPaymentItems.addItem(paymentItem);
        }

        if (customerPaymentItems.getItems() != null) {
            customer.setPaymentItems(customerPaymentItems);
        }

        Customers customers = new Customers();
        customers.addCustomer(customer);

        customerInformationResponse.setCustomers(customers);
        return customerInformationResponse;
    }

    public void savePaymentNotificationRequest(PaymentNotificationRequest request) {
        for (Payment paymentPojo : request.getPayments().getPayment()) {
            PaymentResponseLog paymentResponseLog = new PaymentResponseLog();
            paymentResponseLog.setRecieptNumber(paymentPojo.getReceiptNo());
            paymentResponseLog.setPaymentReference(paymentPojo.getPaymentReference());
            paymentResponseLog.setAmountInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
            paymentResponseLog.setPaymentLogId(String.valueOf(paymentPojo.getPaymentLogId()));
            paymentResponseLog.setResponseDump(PaymentUtil.toJSON(request));
            paymentResponseLog.setDateCreated(Timestamp.from(Instant.now()));
            paymentResponseLog.setPaymentTransaction(paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId",
                    paymentPojo.getCustReference()));

            paymentTransactionDao.saveObject(paymentResponseLog);
        }

    }

    private void queueNotification(Payment paymentPojo, PaymentTransaction paymentTransaction) {
        TransactionNotificationPojo transactionNotificationPojo = new TransactionNotificationPojo();
        transactionNotificationPojo.setStatus(paymentTransaction.getPaymentTransactionStatus().getValue());
        transactionNotificationPojo.setTransactionId(paymentTransaction.getTransactionId());
        transactionNotificationPojo.setDatePaymentReceived(PaymentUtil.format(Timestamp.from(Instant.now()), GeneralConstants.ISO_DATE_TIME_FORMAT));
        transactionNotificationPojo.setReceiptNumber(paymentPojo.getReceiptNo());
        transactionNotificationPojo.setAmountPaidInKobo(PaymentUtil.getAmountInKobo(paymentPojo.getAmount()));
        transactionNotificationPojo.setPaymentProvider(paymentTransaction.getPaymentProvider().getValue());
        transactionNotificationPojo.setPaymentProviderTransactionId(String.valueOf(paymentPojo.getPaymentLogId()));
        transactionNotificationPojo.setPaymentDate(paymentPojo.getPaymentDate());
        transactionNotificationPojo.setSettlementDate(paymentPojo.getSettlementDate());

        NotificationQueue notificationQueue = new NotificationQueue();
        notificationQueue.setMessageInJson(PaymentUtil.toJSON(transactionNotificationPojo));
        notificationQueue.setNotificationUrl(paymentTransaction.getNotificationUrl());
        notificationQueue.setNotificationSent(false);
        notificationQueue.setDateCreated(Timestamp.from(Instant.now()));
        notificationQueue.setPaymentTransaction(paymentTransaction);

        paymentTransactionDao.saveObject(notificationQueue);
    }

    private void saveCurrentPaymentTransactionState(PaymentTransaction paymentTransaction) {
        PaymentTransactionStateLog paymentTransactionStateLog = new PaymentTransactionStateLog();
        paymentTransactionStateLog.setStateDump(PaymentUtil.toJSON(paymentTransaction));
        paymentTransactionStateLog.setDateCreated(Timestamp.from(Instant.now()));
        paymentTransactionStateLog.setPaymentTransaction(paymentTransaction);

        paymentTransactionDao.saveObject(paymentTransactionStateLog);
    }

//    @Transactional
//    public PaymentNotificationResponse processPaymentNotification(PaymentNotificationRequest request, Context context) {
//        PaymentNotificationResponse paymentNotificationResponsePojo = new PaymentNotificationResponse();
//        for (PaymentPojo paymentPojo : request.getPayments()) {
//            if (!paymentTransactionDao.isDuplicateNotification(paymentPojo)) {
//                PaymentTransaction paymentTransaction = paymentTransactionDao.getUniqueRecordByProperty(PaymentTransaction.class, "transactionId",
//                        paymentPojo.getCustReference());
//
//                if (paymentPojo.getReversal()) {
//                    if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount().abs()))) {
//                        saveCurrentPaymentTransactionState(paymentTransaction);
//
//                        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.PENIDNG);
//
//                        paymentTransactionDao.updateObject(paymentTransaction);
//
//                        if (paymentTransaction.getNotifyOnStatusChange()) {
//                            queueNotification(paymentPojo, paymentTransaction);
//                        }
//
//                        PaymentResponsePojo responsePojo = new PaymentResponsePojo();
//                        responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
//                        responsePojo.setStatus(0);
//
//                        paymentNotificationResponsePojo.addPayment(responsePojo);
//                    } else {
//                        PaymentResponsePojo responsePojo = new PaymentResponsePojo();
//                        responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
//                        responsePojo.setStatus(1);
//                        responsePojo.setStatusMessage(String.format("Invalid amount paid. Expected N%s", PaymentUtil.koboToNaira(paymentTransaction.getAmountInKobo())));
//
//                        paymentNotificationResponsePojo.addPayment(responsePojo);
//                    }
//                } else if (paymentTransaction.getAmountInKobo().equals(getAmountInKobo(paymentPojo.getAmount()))) {
//                    if (paymentPojo.getPaymentStatus() == 0) {
//                        saveCurrentPaymentTransactionState(paymentTransaction);
//
//                        paymentTransaction.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESSFUL);
//                        paymentTransactionDao.updateObject(paymentTransaction);
//
//                        if (paymentTransaction.getNotifyOnStatusChange()) {
//                            queueNotification(paymentPojo, paymentTransaction);
//                        }
//
//                        PaymentResponsePojo responsePojo = new PaymentResponsePojo();
//                        responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
//                        responsePojo.setStatus(0);
//
//                        paymentNotificationResponsePojo.addPayment(responsePojo);
//                    } else {
//                        PaymentResponsePojo responsePojo = new PaymentResponsePojo();
//                        responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
//                        responsePojo.setStatus(1);
//                        responsePojo.setStatusMessage("Invalid status");
//
//                        paymentNotificationResponsePojo.addPayment(responsePojo);
//                    }
//                } else {
//                    PaymentResponsePojo responsePojo = new PaymentResponsePojo();
//                    responsePojo.setPaymentLogId(paymentPojo.getPaymentLogId());
//                    responsePojo.setStatus(1);
//                    responsePojo.setStatusMessage(String.format("Invalid amount paid. Expected N%s", PaymentUtil.koboToNaira(paymentTransaction.getAmountInKobo())));
//
//                    paymentNotificationResponsePojo.addPayment(responsePojo);
//                }
//            }
//        }
//        return paymentNotificationResponsePojo;
//    }
}
