package services;

import com.bw.payment.entity.Currency;
import com.bw.payment.entity.FlutterWaveServiceCredentials;
import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.RemitaServiceCredentials;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.inject.Inject;
import dao.BaseDao;
import dao.CurrencyDao;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import ninja.utils.NinjaProperties;
import services.sequence.PayerIdSequence;
import services.sequence.TicketIdSequence;
import services.sequence.TransactionIdSequence;

import java.util.List;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class PaymentService {
    @Inject
    private BaseDao baseDao;
    @Inject
    private MerchantDao merchantDao;
    @Inject
    private CurrencyDao currencyDao;

    @Inject
    protected TransactionIdSequence transactionIdSequence;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private NinjaProperties ninjaProperties;

    @Inject
    protected PayerIdSequence payerIdSequence;
    @Inject
    protected TicketIdSequence ticketIdSequence;

    public <T> T getProviderCredentials(Class<T> tClass, Merchant merchant) {
        if (merchant == null) {
            List<T> allRecords = baseDao.getAllRecords(tClass);
            if (allRecords.size() > 0) {
                return allRecords.get(0);
            }
            return null;
        }
        return baseDao.getUniqueRecordByProperty(tClass, "merchant", merchant);
    }

    public RemitaServiceCredentials getRemitaCredentials(Merchant merchant) {
        if (merchant == null) {
            List<RemitaServiceCredentials> allRecords = baseDao.getAllRecords(RemitaServiceCredentials.class);
            if (allRecords.size() > 0) {
                return allRecords.get(0);
            }
            return null;
        }
        return baseDao.getUniqueRecordByProperty(RemitaServiceCredentials.class, "merchant", merchant);
    }


    public Merchant getMerchant() {
        return this.merchantDao.getFirstMerchant();
    }

    public FlutterWaveServiceCredentials getFlutterWaveServiceCredential(Merchant merchant) {
        return getProviderCredentials(FlutterWaveServiceCredentials.class, merchant);
    }

    public Currency findByCode(String code) {
        return currencyDao.findByCode(code, GenericStatusConstant.ACTIVE);
    }
}
