package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.BaseDao;
import dao.CurrencyDao;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import ninja.utils.NinjaProperties;
import org.apache.commons.lang3.StringUtils;
import pojo.ItemPojo;
import pojo.TransactionRequestPojo;
import pojo.flutterWave.SplitDto;
import services.sequence.PayerIdSequence;
import services.sequence.TicketIdSequence;
import services.sequence.TransactionIdSequence;
import utils.PaymentUtil;

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

    public WebPayServiceCredentials getWebPayCredentials(Merchant merchant) {
        if (merchant == null) {
            List<WebPayServiceCredentials> allRecords = baseDao.getAllRecords(WebPayServiceCredentials.class);
            if (allRecords.size() > 0) {
                return allRecords.get(0);
            }
            return null;
        }
        return baseDao.getUniqueRecordByProperty(WebPayServiceCredentials.class, "merchant", merchant);
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

    public Merchant getMerchant() {
        return this.merchantDao.getFirstMerchant();
    }

    public FlutterWaveServiceCredentials getFlutterWaveServiceCredential(Merchant merchant) {
//        JPAQuery flutterWaveServiceCredentialsJPAQuery = baseDao.startJPAQuery(QFlutterWaveServiceCredentials.flutterWaveServiceCredentials);
//        if (merchant != null) {
//            flutterWaveServiceCredentialsJPAQuery.where(QFlutterWaveServiceCredentials.flutterWaveServiceCredentials.merchant.eq(merchant));
//        }
//        return flutterWaveServiceCredentialsJPAQuery.fetchFirst();
        return null;
    }

    public Currency findByCode(String code) {
        return currencyDao.findByCode(code, GenericStatusConstant.ACTIVE);
    }
}
