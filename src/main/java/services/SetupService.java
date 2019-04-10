package services;

import com.bw.payment.entity.*;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.querydsl.jpa.impl.JPAQuery;
import dao.BaseDao;
import dao.CurrencyDao;
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
    private CurrencyDao currencyDao;
    private MerchantDao merchantDao;
    private NinjaProperties ninjaProperties;
    private PaymentService paymentService;
    private TransactionTemplate transactionTemplate;

    @Inject
    public SetupService(BaseDao baseDao, CurrencyDao currencyDao, MerchantDao merchantDao, NinjaProperties ninjaProperties, PaymentService paymentService, TransactionTemplate transactionTemplate) {
        this.baseDao = baseDao;
        this.currencyDao = currencyDao;
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
        createDefaultMerchant();
        createDefaultCurrency();
    }

    private void createDefaultCurrency() {
//        JPAQuery<Currency> currencyJPAQuery = baseDao.startJPAQuery(QCurrency.currency);
//        currencyJPAQuery//.where(QCurrency.currency.code.eq("NGN"))
//                .where(QCurrency.currency.status.eq(GenericStatusConstant.ACTIVE));
//        System.out.println(currencyJPAQuery.toString());
//        Currency currency = currencyJPAQuery.fetchFirst();


        Currency currency = currencyDao.findByCode("NGN", GenericStatusConstant.ACTIVE);
        if (currency == null) {
            currency = new Currency();
            currency.setName("Nigerian Naira");
            currency.setCode("NGN");
            currency.setStatus(GenericStatusConstant.ACTIVE);
            baseDao.saveObject(currency);
        }
    }

    private void createDefaultMerchant() {
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
        createFWCredentials(merchant);
    }

    private void createFWCredentials(Merchant merchant) {
        FlutterWaveServiceCredentials flutterWaveServiceCredentials = paymentService.getProviderCredentials(FlutterWaveServiceCredentials.class, merchant);

        if (flutterWaveServiceCredentials == null) {
            flutterWaveServiceCredentials = new FlutterWaveServiceCredentials();
            flutterWaveServiceCredentials.setMerchantRedirectUrl("_ NOT CONFIGURED _");
            flutterWaveServiceCredentials.setApiKey("_ NOT CONFIGURED _");
            flutterWaveServiceCredentials.setSecretKey("_ NOT CONFIGURED _");
            flutterWaveServiceCredentials.setBaseUrl("_ NOT CONFIGURED _");
            flutterWaveServiceCredentials.setMerchant(merchant);

            FlutterWaveServiceCredentials finalFlutterWaveServiceCredentials = flutterWaveServiceCredentials;
            transactionTemplate.execute(entityManager -> {
                entityManager.persist(finalFlutterWaveServiceCredentials);
            });
        }
    }

    private void createWebPayCredentials(Merchant merchant) {
        WebPayServiceCredentials webPayServiceCredentials = paymentService.getWebPayCredentials(merchant);

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
