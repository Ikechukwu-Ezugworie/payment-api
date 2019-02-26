package services;

import com.bw.payment.entity.Merchant;
import com.bw.payment.entity.RemitaServiceCredentials;
import com.bw.payment.entity.WebPayServiceCredentials;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.BaseDao;
import dao.MerchantDao;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.MerchantRequestPojo;
import utils.Constants;

import java.sql.Timestamp;
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
    private PaymentService paymentService;
    private TransactionTemplate transactionTemplate;

    @Inject
    public SetupService(BaseDao baseDao, MerchantDao merchantDao, NinjaProperties ninjaProperties, PaymentService paymentService, TransactionTemplate transactionTemplate) {
        this.baseDao = baseDao;
        this.merchantDao = merchantDao;
        this.ninjaProperties = ninjaProperties;
        this.paymentService = paymentService;
        this.transactionTemplate = transactionTemplate;
    }


    public void setUp() {
        if (ninjaProperties.isDev()) {
            logger.info("<=== Displaying all system properties");
            Properties p = System.getProperties();
            Enumeration keys = p.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) p.get(key);
                logger.info(key + " : " + value);
            }
            logger.info("<=== finished Displaying all system properties");
        }
        createInterswitchWhitelist();
        Merchant merchant = createDefaultMerchant();
        createRemittaCredentials(merchant);
    }


    private Merchant createDefaultMerchant() {
        Merchant merchant = merchantDao.getFirstMerchant();
        if (merchant == null) {
            MerchantRequestPojo merchantRequestPojo = new MerchantRequestPojo();
            merchantRequestPojo.setName("- NOT CONFIGURED -");
            merchantRequestPojo.setPaydirectMerchantReference("- NOT CONFIGURED -");
            merchantRequestPojo.setLookupUrl("- NOT CONFIGURED -");
            merchantRequestPojo.setNotificationUrl("- NOT CONFIGURED -");

            merchant = merchantDao.createMerchant(merchantRequestPojo);
        }
        createWebPayCredentials(merchant);
        createRemitaCredentials(merchant);
    }




    private void createRemittaCredentials(Merchant merchant) {
        RemitaServiceCredentials credentials = paymentService.getProviderCredentials(RemitaServiceCredentials.class, merchant);

        if (credentials == null) {
            System.out.println("credentials are null");
            RemitaServiceCredentials data = new RemitaServiceCredentials();
            data.setApiKey(Constants.NOT_CONFIGURED);
            data.setMerchantId(Constants.NOT_CONFIGURED);
            data.setBaseUrl(Constants.NOT_CONFIGURED);
            data.setServiceTypeId(Constants.NOT_CONFIGURED);
            data.setMerchant(merchant);
            baseDao.saveObject(data);
            data.setMerchantId("1234");

        }

    }


    private void createWebPayCredentials(Merchant merchant) {
        WebPayServiceCredentials webPayServiceCredentials = paymentService.getProviderCredentials(WebPayServiceCredentials.class, merchant);

        if (webPayServiceCredentials == null) {
            webPayServiceCredentials = new WebPayServiceCredentials();
            webPayServiceCredentials.setMacKey("E187B1191265B18338B5DEBAF9F38FEC37B170FF582D4666DAB1F098304D5EE7F3BE15540461FE92F1D40332FDBBA34579034EE2AC78B1A1B8D9A321974025C4");
            webPayServiceCredentials.setServiceBaseUrl("- NOT CONFIGURED -");
            webPayServiceCredentials.setDateCreated(new Timestamp(new java.util.Date().getTime()));
            webPayServiceCredentials.setMerchant(merchant);
            webPayServiceCredentials.setMerchantRedirectUrl("_ NOT CONFIGURED  _");
            WebPayServiceCredentials finalWebPayServiceCredentials = webPayServiceCredentials;
            transactionTemplate.execute(entityManager -> {
                baseDao.saveObject(finalWebPayServiceCredentials);
            });

        }
    }

    private void createRemitaCredentials(Merchant merchant) {
        RemitaServiceCredentials remitaServiceCredentials = paymentService.getProviderCredentials(RemitaServiceCredentials.class, merchant);

        if (remitaServiceCredentials == null) {
            remitaServiceCredentials = new RemitaServiceCredentials();
            remitaServiceCredentials.setApiKey("_ NOT CONFIGURED _");
            remitaServiceCredentials.setMerchantId("_ NOT CONFIGURED _");
            remitaServiceCredentials.setBaseUrl("_ NOT CONFIGURED _");
            remitaServiceCredentials.setServiceTypeId("_ NOT CONFIGURED _");
            remitaServiceCredentials.setMerchantRedirectUrl("_ NOT CONFIGURED _");
            remitaServiceCredentials.setMerchant(merchant);

            RemitaServiceCredentials finalRemitaServiceCredentials = remitaServiceCredentials;
            transactionTemplate.execute(entityManager -> {
                entityManager.persist(finalRemitaServiceCredentials);
            });
        }
    }

    @Transactional
    private void createInterswitchWhitelist() {
        String whitelist = "41.223.145.174,154.72.34.174";
        baseDao.saveToSettings(Constants.INTERSWITCH_IPS, whitelist, false);
    }
}
