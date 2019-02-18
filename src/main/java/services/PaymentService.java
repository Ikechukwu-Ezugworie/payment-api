package services;

import com.bw.payment.entity.Merchant;
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

    public Merchant getMerchant() {
        return this.merchantDao.getFirstMerchant();
    }
}
