package utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import ninja.utils.NinjaProperties;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import okhttp3.logging.HttpLoggingInterceptor;

public class PaymentUtil {

    public static final String NAIRA_FORMAT = "^\\d+\\.?\\d{0,2}$";
    public static final int NAIRA_TO_KOBO = 100;
    private static final Gson gson = new Gson();
    //    private static String sep = System.lineSeparator();

    public static Long getAmountInKobo(BigDecimal amountInNaira) {
        Long amountInKobo = (amountInNaira.multiply(new BigDecimal(Constants.NAIRA_TO_KOBO))).longValue();
        return amountInKobo;
    }

    public static int getIntegerValue(String intValue) {
        int value = 0;
        try {
            value = Integer.parseInt(intValue);
        } catch (NumberFormatException ex) {
        }
        return value;
    }

    public static long getLongValue(String longValue) {
        long value = 0l;
        try {
            value = Long.parseLong(longValue);
        } catch (NumberFormatException ex) {
        }
        return value;
    }

    public static long getLongValueThrowException(String longValue) throws Exception {
        long value = 0l;
        try {
            value = Long.parseLong(longValue);
        } catch (NumberFormatException ex) {
            throw new Exception("Could not convert to long");
        }
        return value;
    }

    public static String formatName(String name) {

        WordUtils.capitalizeFully(name);
        return name;
    }

    public static Timestamp getMonthStartTimestampByMonthValue(int monthValue) {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.set(currentYear, monthValue, 1, 0, 0, 0);
        Timestamp timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp;
    }

    public static int getYearFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currentYear = calendar.get(Calendar.YEAR);
        return currentYear;
    }

    public static int getMonthFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currentMonth = calendar.get(Calendar.MONTH);
        return currentMonth;
    }

    public static Timestamp getTimestampFromMonthValueAndYearValue(int monthValue, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthValue, 1, 0, 0, 0);
        Timestamp timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp;
    }

    public static Timestamp getTimestampFromMonthValueAndYearValue2(int monthValue, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthValue, 31, 0, 0, 0);
        Timestamp timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp;
    }

    public static Timestamp getMonthEndTimestampByMonthAndYear(int monthValue, int year) {
        Calendar cal = Calendar.getInstance();
        // int currentYear = cal.get(Calendar.YEAR);
        int days = getNumberOfDaysInMonth(monthValue);
        cal.set(year, monthValue, days, 23, 59, 59);
        Timestamp timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp;
    }

    public static Timestamp getMonthStartTimestampByMonthAndYear(int monthValue, int year) {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.set(currentYear, monthValue, 1, 0, 0, 0);
        Timestamp timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp;
    }

    public static Long computeVendorCommission(Long benchmarkAmount, Long totalCommission, int percentageCommission) {
        long tempCollection = totalCommission - benchmarkAmount;
        if (tempCollection < 0) {
            return 0l;
        }
        long commission = (tempCollection * percentageCommission) / 100;
        return commission;
    }

    public static Timestamp getMonthEndTimestampByMonthValue(int monthValue) {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int days = getNumberOfDaysInMonth(monthValue);
        cal.set(currentYear, monthValue, days, 23, 59, 59);
        Timestamp timestamp = new Timestamp(cal.getTime().getTime());
        return timestamp;
    }

    private static int getNumberOfDaysInMonth(int monthValue) {
        int numberOfDays = 0;
        switch (monthValue) {
            case 3:
            case 5:
            case 8:
            case 10:
                numberOfDays = 30;
                break;
            case 1:
                numberOfDays = 28;
                break;
            default:
                numberOfDays = 31;
        }
        return numberOfDays;
    }

    public static Timestamp getDateStartTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 000);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Date getDateFromString(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

        try {

            Date date = formatter.parse(dateInString);
            System.out.println(date);
            System.out.println(formatter.format(date));

            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Date getDateFromString(String dateInString, String dateStringPattern) {
        if (StringUtils.isBlank(dateInString))
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(dateStringPattern);

        try {
            Date date = formatter.parse(dateInString);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static Timestamp getDateEndTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static String getFormattedDateMonth(Timestamp timestamp) {
        String formattedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        formattedDate = formatter.format(timestamp);
        return formattedDate;
    }

    public static String formatNameString(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        String firstChar = name.substring(0, 1).toUpperCase();
        name = name.substring(1).toLowerCase();
        name = firstChar + name;
        return name;
    }

    public static Timestamp getTimestamp(String dateString, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public static Timestamp getTimestamp(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT);
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return new Timestamp(date.getTime());
    }

    /**
     * Date format: dd/MM/yyyy HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getDateAsString(Date date) {
        String formattedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = formatter.format(date);
        return formattedDate;
    }

    public static Timestamp getTimestampOfRegDate(String datestring) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        try {
            date = formatter.parse(datestring.trim());
        } catch (ParseException e) {
            System.out.println("Exception in date");
            date = new Date();
        }
        return new Timestamp(date.getTime());
    }

    public static Timestamp getTimestampOfLoginAnalysisDate(String datestring) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        try {
            date = formatter.parse(datestring.trim());
        } catch (ParseException e) {
            System.out.println("Exception in date");
            date = new Date();
        }
        return new Timestamp(date.getTime());
    }

    public static Timestamp getFormattedDateForTimestamp(String datestring) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date = new Date();
        try {
            date = formatter.parse(datestring);
        } catch (ParseException e) {
            System.out.println("Exception in date");
        }
        return new Timestamp(date.getTime());
    }

    public static Timestamp getTimestampOfDate(String dateString) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            try {
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                date = formatter2.parse(dateString);
            } catch (Exception localException) {
                try {
                    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = formatter2.parse(dateString);
                } catch (Exception localException2) {
                }
            }
        }
        return new Timestamp(date.getTime());
    }

    public static Timestamp getTimestampOfDate(String dateString, String fomart) {

        SimpleDateFormat formatter = new SimpleDateFormat(fomart);
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTruncatedMessage(String message, int size) {
        if (message == null) {
            return "";
        }
        if (message.length() > size) {
            message = message.substring(0, size);
            message = message + "...";
        }
        return message;
    }

    public static String showErrorMessage(String message) {
        String errorMessage = "";
        if (message != null && !message.isEmpty()) {
            errorMessage = "<div class='alert alert-danger' role='alert'>"
                    + "<button type='button' class='close' data-dismiss='alert'><i class='ace-icon fa fa-times'></i></button>"
                    + "<strong>Error: </strong>" + message + "</div>";
        }
        return errorMessage;
    }

    public static String showSuccessMessage(String message) {
        String errorMessage = "";
        if (message != null && !message.isEmpty()) {
            errorMessage = "<div class='alert alert-success' role='alert'>"
                    + "<button type='button' class='close' data-dismiss='alert'><i class='ace-icon fa fa-times'></i></button>"
                    + "<strong>Success: </strong>" + message + "</div>";
        }
        return errorMessage;
    }

    public static String showInformationMessage(String message) {
        String infoMessage = "";
        if (message != null && !message.isEmpty()) {
            infoMessage = "<div class='alert alert-info' role='alert'>"
                    + "<button type='button' class='close' data-dismiss='alert'><i class='ace-icon fa fa-times'></i></button>"
                    + "<strong>Info: </strong> " + message + "</div>";
        }
        return infoMessage;
    }

    private static String priceWithDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }

    private static String priceWithoutDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static String getFormattedMoneyDisplay(long value) {
        Double price = (double) value;
        price = price / 100.00;
        String toShow = priceWithoutDecimal(price);
        /*
         * if (toShow.indexOf(".") > 0) { return priceWithDecimal(price); } else
         * { return priceWithoutDecimal(price); }
         */
        String formattedPrice = priceWithDecimal(price);
        if (formattedPrice.equalsIgnoreCase(".00")) {
            formattedPrice = "0.00";
        }
        return formattedPrice;
    }

    public static String getFormattedMoneyDisplay(long value, String currencySymbol) {
        Double price = (double) value;
        price = price / 100.00;
        String toShow = priceWithoutDecimal(price);
        /*
         * if (toShow.indexOf(".") > 0) { return priceWithDecimal(price); } else
         * { return priceWithoutDecimal(price); }
         */
        String formattedPrice = priceWithDecimal(price);
        if (formattedPrice.equalsIgnoreCase(".00")) {
            formattedPrice = "0.00";
        }
        return String.format(currencySymbol + " %s", formattedPrice);
    }

    public static String getFormattedMoneyDisplay(String value) {
        long newvalue = Long.parseLong(value);
        Double price = (double) newvalue;
        price = price / 100;
        String toShow = priceWithoutDecimal(price);
        /*
         * if (toShow.indexOf(".") > 0) { return priceWithDecimal(price); } else
         * { return priceWithoutDecimal(price); }
         */
        String formattedPrice = priceWithDecimal(price);
        if (formattedPrice.equalsIgnoreCase(".00")) {
            formattedPrice = "0.00";
        }
        return formattedPrice;
    }

    public static int getYearFromTimestamp(Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        return cal.get(Calendar.YEAR);
    }

    public static String getFormattedMoneyWithoutDecimal(long value) {
        Double price = (double) value;
        price = price / 100;
        String toShow = priceWithoutDecimal(price);
        if (toShow.indexOf(".") > 0) {
            return priceWithDecimal(price);
        } else {
            return priceWithoutDecimal(price);
        }
    }

    public static String getFormattedFullName(String firstName, String lastName) {
        return formatNameString(firstName) + " " + formatNameString(lastName);
    }

    /*
     * public static void sendGSONObjects(Object[] response, ResourceResponse
     * resp) { Gson gson = new Gson(); String jsonString =
     * gson.toJson(response); resp.setContentType("text/json"); PrintWriter pw =
     * null; try { pw = resp.getWriter(); } catch (IOException e) {
     * e.printStackTrace(); } if (pw != null) { pw.write(jsonString);
     * pw.flush(); pw.close(); } }
     */

    /*
     * public static String getCurrentCaptchaText(ActionRequest request) {
     * PortletSession session = request.getPortletSession(); Enumeration<String>
     * attributeNames = session.getAttributeNames(); while
     * (attributeNames.hasMoreElements()) { String name =
     * attributeNames.nextElement(); if (name.contains("CAPTCHA_TEXT")) { String
     * cText = (String) session.getAttribute(name);
     *
     * return cText; } } return ""; }
     */

    public static int getHTTPResponseStatusCode(String u) throws IOException {
        URL url = new URL(u);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        return http.getResponseCode();
    }

    public static String formatPhoneNumber(String phoneNumber) {
        try {
            PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = numberUtil.parse(phoneNumber, "NG");
            return numberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (Exception ignore) {
            //e.printStackTrace();
        }
        return phoneNumber;
    }

    public static Boolean isValidString(String str) {
        Boolean valid = Boolean.FALSE;
        if (str != null) {
            valid = str.trim().isEmpty() ? Boolean.FALSE : Boolean.TRUE;
        }

        return valid;
    }

    public static String getDefaultDueDate() {
        String formattedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = formatter.format(DateUtils.addDays(new Date(), 14));
        return formattedDate;
    }

    public static String getHash(String message, String hashType) {
        String msg = message;
        MessageDigest m = null;
        String hashText = null;
        try {
            m = MessageDigest.getInstance(hashType);
            try {
                m.update(msg.getBytes("UTF-8"), 0, msg.length());

            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

            hashText = new BigInteger(1, m.digest()).toString(16);
            if (hashText.length() < 64) { // must be 64 in length
                int numberOfZeroes = 64 - hashText.length();
                String zeroes = "";

                for (int i = 0; i < numberOfZeroes; i++) {
                    zeroes = zeroes + "0";
                }
                hashText = zeroes + hashText;
            }

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        hashText = hashText.toLowerCase();
        return hashText;
    }

    public static <T> T fromJSON(String s, Class<T> tClass) {
        return gson.fromJson(s, tClass);
    }

    public static String toJSON(Object data) {
        return gson.toJson(data);
    }

    public static String toJSONWithAdaptor(Object data) {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        return b.create().toJson(data);
    }

    public static <T> T fromJSON(String data, Type tClass) {
        return gson.fromJson(data, tClass);
    }

    public static String generateAPIKey(int lent) {
        SecretKeySpec aesKey = null;

        SecureRandom random = new SecureRandom();

        byte[] keyBytes = new byte[lent];
        random.nextBytes(keyBytes);
        aesKey = new SecretKeySpec(keyBytes, "AES");
        return new String(Base64.getEncoder().encode(aesKey.getEncoded()));
    }

    public static Timestamp getEndOfYear(int currentyear) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, currentyear);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static String format(Date date, String format) {
        if (date == null)
            return "";
        return new SimpleDateFormat(format).format(date);
    }

    public static String getFormattedDate4(Timestamp timestamp) {
        String formattedDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        formattedDate = formatter.format(timestamp);
        return formattedDate;
    }

    public static String getFormattedDate5(Timestamp timestamp) {
        String formattedDate = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy mm:ss");
            formattedDate = formatter.format(timestamp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static List<String> getAllMonths() {
        List<String> monthsList = new ArrayList<String>();
        String[] months = new DateFormatSymbols().getMonths();
        //System.out.println("month = " + months.length);
        for (int i = 0; i < months.length - 1; i++) {
            String month = months[i];
            //System.out.println("month = " + month);
            monthsList.add(months[i]);
        }
        return monthsList;
    }


    public static String koboToNaira(Long amountInKobo) throws NumberFormatException {
        String amount;
        if (amountInKobo == null) {
            return null;
        }
        try {
            BigDecimal bigAmount = new BigDecimal(amountInKobo);
            bigAmount = bigAmount.divide(new BigDecimal(String.valueOf(NAIRA_TO_KOBO)));
            amount = bigAmount.toPlainString();
        } catch (Exception e) {
            throw new NumberFormatException();
        }
        return amount;
    }

    public static BigDecimal koboToNaira(BigInteger amountInKobo) throws NumberFormatException {
        if (amountInKobo == null) {
            return null;
        }
        try {
            BigDecimal bigAmount = new BigDecimal(amountInKobo);
            bigAmount = bigAmount.divide(new BigDecimal(NAIRA_TO_KOBO)).setScale(2, RoundingMode.HALF_EVEN);
            return bigAmount;
        } catch (Exception e) {
            throw new NumberFormatException();
        }

    }


    public static Timestamp getEndOfDayTimeStamp(String dateString) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            try {
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                date = formatter2.parse(dateString);
            } catch (Exception localException) {
                System.err.println(localException.getMessage());
            }
        }

        return getDateEndTime(date);
    }

    public static String generateDigest(String data, String algorithm) {
        String encodedDigest = "";
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] output = md.digest(data.getBytes("UTF-8"));
            md.update(output);
            encodedDigest = convertByteArrayToHexString(output);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        } catch (UnsupportedEncodingException ignored) {
        }
        return encodedDigest;
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public static Timestamp nowToTimeStamp() {
        return Timestamp.from(Instant.now());
    }

    public static Timestamp addDaysToTimestamp(Timestamp timestamp, int numOfDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        cal.add(Calendar.DAY_OF_WEEK, numOfDays);
        return new Timestamp(cal.getTime().getTime());
    }

    public static String timeStampToDate(Timestamp timestamp, String dateFormat) {
        if (timestamp == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(timestamp.getTime());
        return sdf.format(date);
    }

    public static String getDate(Timestamp timestamp) {
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
            String valueFromDB = timestamp.toString();
            Date d1 = null;
            d1 = sdf1.parse(valueFromDB);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateWithoutTime = sdf.format(d1);
            System.out.println("sdf.format(d) " + dateWithoutTime);
            return dateWithoutTime;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Integer getHours(Timestamp timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timestamp);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            return hours;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static BigDecimal getAmountInNaira(Long amountInKobo) {
        if (amountInKobo == null) {
            return new BigDecimal(0);
        }
        BigDecimal koboAmount = new BigDecimal(amountInKobo);
        BigDecimal divisor = new BigDecimal(Constants.NAIRA_TO_KOBO);
        return koboAmount.divide(divisor, Constants.NAIRA_TO_KOBO_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private static String generate() {
        UUID uuid = UUID.randomUUID();
        return String.valueOf(Math.abs(uuid.getMostSignificantBits()));
    }

    public static String generateApiKey() {

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            byte[] encoded = secretKey.getEncoded();
            return DatatypeConverter.printHexBinary(encoded).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generate();
    }

    public static OkHttpClient getOkHttpClient(NinjaProperties ninjaProperties) {
        return getOkHttpClient(ninjaProperties, 15);
    }

    public static OkHttpClient getOkHttpClient(NinjaProperties ninjaProperties, int timeout) {
        if (ninjaProperties.isProd()) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .build();
            return okHttpClient;
        }
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = builder
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
//                    .addInterceptor(interceptor)
                    .build();
            return okHttpClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    public static void main(String[] args) {
//        String payload = "{ \"merchantTransactionReferenceId\": \"000000012\", \"amountInKobo\": 5000000, \"paymentProvider\": \"INTERSWITCH\", \"paymentChannel\": \"PAYDIRECT\", \"payer\": { \"firstName\": \"JOhn\", \"lastName\": \"Doe\", \"email\": \"jdoe@gmail.com\", \"phoneNumber\": \"01212023023\" } }";
//        System.out.println(generateDigest("M0000003" + "de769088d33a77b20a874b3fbace7e12" + payload, Constants.SHA_512_ALGORITHM_NAME));

        System.out.println(getHash("3", Constants.SHA_512_ALGORITHM_NAME)); ;
    }

    public static String getFirstNameFromFullName(String customerName) {
        if (StringUtils.isNotBlank(customerName)) {
            if (customerName.trim().contains(" ")) {
                String names[] = customerName.trim().split(" ");
                return names[0];
            }
        }
        return customerName;
    }

    public static String getLastNameFromFullName(String customerName) {
        if (StringUtils.isNotBlank(customerName)) {
            if (customerName.trim().contains(" ")) {
                String names[] = customerName.trim().split(" ");
                return names[1];
            }
        }
        return "";
    }


    public Long multiply(Long a, Long b){

    }
}