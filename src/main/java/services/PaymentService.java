package services;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.WebPayServiceCredentials;
import com.google.inject.Inject;
import dao.BaseDao;
import dao.MerchantDao;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class PaymentService {
    @Inject
    BaseDao baseDao;
    @Inject
    MerchantDao merchantDao;

    public WebPayServiceCredentials getWebPayCredentials(Merchant merchant) {
        if (merchant == null) {
            return baseDao.getAllRecords(WebPayServiceCredentials.class).get(0);
        }
        return baseDao.getUniqueRecordByProperty(WebPayServiceCredentials.class, "merchant", merchant);
    }

    public Merchant getMerchant() {
        return this.merchantDao.getFirstMerchant();
    }
}
