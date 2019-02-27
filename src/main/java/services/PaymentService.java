package services;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.RemitaServiceCredentials;
import com.bw.payment.entity.WebPayServiceCredentials;
import com.google.inject.Inject;
import dao.BaseDao;
import dao.MerchantDao;

import java.util.List;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class PaymentService {
    @Inject
    private BaseDao baseDao;
    @Inject
    private MerchantDao merchantDao;

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
}
