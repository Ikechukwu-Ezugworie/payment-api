package services;

/**
 * Author: Oluwatobi Adenekan
 * email:  tadenekan@byteworks.com.ng
 * date:    04/03/2019
 **/

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.PaymentTransactionDao;
import pojo.TransactionRequestPojo;
import services.sequence.TransactionIdSequence;

@Singleton
public class UssdService {

    @Inject
    PaymentTransactionDao paymentTransactionDao;

    @Inject
    protected TransactionIdSequence transactionIdSequence;


    public void doUssdNotification(TransactionRequestPojo request) {
        String transactionId = transactionIdSequence.getNext();
        paymentTransactionDao.createTransaction(request, transactionId);
    }
}
