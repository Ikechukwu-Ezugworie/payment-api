package services;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.BaseDao;
import dao.MerchantDao;
import pojo.MerchantRequestPojo;
import utils.Constants;

/**
 * CREATED BY GIBAH
 */
public class SetupService {
    private BaseDao baseDao;
    private MerchantDao merchantDao;

    @Inject
    public SetupService(BaseDao baseDao, MerchantDao merchantDao) {
        this.baseDao = baseDao;
        this.merchantDao = merchantDao;
    }


    public void setUp() {
        createInterswitchWhitelist();
        createDefaultMerchant();
    }

    private void createDefaultMerchant() {
        long merc = merchantDao.getNumberOfMerchantRecords();
        if (merc < 1) {
            MerchantRequestPojo merchantRequestPojo = new MerchantRequestPojo();
            merchantRequestPojo.setName("DEFAULT MERCHANT");
            merchantRequestPojo.setPaydirectMerchantReference("_CHANGE_");
            merchantRequestPojo.setLookupUrl("_CHANGE_");
            merchantRequestPojo.setNotificationUrl("_CHANGE_");

            merchantDao.createMerchant(merchantRequestPojo);
        }
    }

    @Transactional
    private void createInterswitchWhitelist() {
        String whitelist = "41.223.145.174,154.72.34.174";
        baseDao.saveToSettings(Constants.INTERSWITCH_IPS, whitelist, false);
    }
}
