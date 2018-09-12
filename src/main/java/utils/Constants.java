package utils;

/**
 * Created by Gibah on 2/3/2018.
 */
public class Constants {
    public static final String APPLICATION_SUFFIX = "EPLUC";
    public static final int MAX_AUTH_KEY_CHARACTERS = 16;
    public static final String AUTH_HEADER_KEY = "Authorization";
    public static final int MAX_AUTO_GEN_PASSWORD_CHARACTERS = 6;

    public static final String AUDIT_TRAIL_UPDATE = "UPDATE";

    public static final String ACCOUNT_CREATION_NOTIFICATION_EMAIL = "ACCOUNT_CREATION_NOTIFICATION_EMAIL";
    public static final String AUDIT_TRAIL_LOGIN = "LOGIN";
    public static final String TEMPLATE_NAME_KEY = "tmplName";
    public static final String NOTIFICATION_EMAIL_PASSWORD = "NOTIFICATION_EMAIL_PASSWORD";
    public static final String NOTIFICATION_EMAIL_ADDRESS = "NOTIFICATION_EMAIL_ADDRESS";

    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String BW_PORTAL_ACCOUNT_ID = "BW_PORTAL_ACCOUNT_ID";
    public static final String WORKSMART_PORTAL_ACCOUNT_ID = "WORKSMART_PORTAL_ACCOUNT_ID";

    public static final String BASE_ACCOUNTS_URL = "baseAccountsUrl";
    public static final String BASE_URL = "BASE_URL";
    public static final String BW_ACCOUNTS_CLIENT_CODE = "BW_ACCOUNTS_CLIENT_CODE";
    public static final String BW_ADMIN_USERNAME = "bwadmin";
    public static final String MERCHANT_CODE_HEADER = "X-MERCH-CODE";
    public static final String MERCHANT_CONTEXT_KEY = "_merch_key_";
    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.Z";
    public static final int NAIRA_TO_KOBO = 100;
    public static final int NAIRA_TO_KOBO_SCALE = 2;
    public static final String INTERSWITCH_IPS = "INTERSWITCH_IPS";
    public static final String REQUEST_HASH_HEADER = "X-HASH";
    public static final String SHA_512_ALGORITHM_NAME = "SHA-512";
    public static final String INTERSWITCH_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss";
    public static final String QUICK_TELLER_MERCHANT_PAYMENT_CODE = "QUICK_TELLER_MERCHANT_PAYMENT_CODE";
    public static final String QUICK_TELLER_INTERSWITCH_PREFIX = "QUICK_TELLER_INTERSWITCH_PREFIX";
    public static final String QUICK_TELLER_SECRET_KEY = "QUICK_TELLER_SECRET_KEY";
    public static final String SETTING_QUICKTELLER_CLIENTID = "SETTING_QUICKTELLER_CLIENTID";
    public static final String QUICKTELLER_GET_TRANSACTION_BASEURL = "QUICKTELLER_GET_TRANSACTION_BASEURL";
    public static final String PAYDIRECT_MERCHANT_REFERENCE = "PAYDIRECT_MERCHANT_REFERENCE";
    public static final String PAYMENT_TRANSACTION_IN_DUPLICATE_CHECK = "PAYMENT_TRANSACTION_IN_DUPLICATE_CHECK";
}
