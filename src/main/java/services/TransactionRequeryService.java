package services;

import com.google.inject.Inject;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class TransactionRequeryService {
    private WebPayService webPayService;
    private PaymentTransactionService paymentTransactionService;

    @Inject
    public TransactionRequeryService(WebPayService webPayService, PaymentTransactionService paymentTransactionService) {
        this.webPayService = webPayService;
        this.paymentTransactionService = paymentTransactionService;
    }

    public void processPendingTransactions(int batch) {
//        paymentTransactionService.getPaymentTransactionByS
    }
}
