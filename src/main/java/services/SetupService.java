package services;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.BaseDao;
import dao.MerchantDao;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.MerchantRequestPojo;
import utils.Constants;

import java.util.Enumeration;
import java.util.Properties;

/**
 * CREATED BY GIBAH
 */
public class SetupService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private BaseDao baseDao;
    private MerchantDao merchantDao;
    private NinjaProperties ninjaProperties;

    @Inject
    public SetupService(BaseDao baseDao, MerchantDao merchantDao, NinjaProperties ninjaProperties) {
        this.baseDao = baseDao;
        this.merchantDao = merchantDao;
        this.ninjaProperties = ninjaProperties;
    }


    public void setUp() {
        if (ninjaProperties.isDev()) {
            logger.info("<=== Displaying all system properties");
            Properties p = System.getProperties();
            Enumeration keys = p.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) p.get(key);
                logger.info(key + ": " + value);
            }
            logger.info("<=== finished Displaying all system properties");
        }
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
