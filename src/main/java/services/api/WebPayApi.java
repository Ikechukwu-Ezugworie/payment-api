package services.api;

import pojo.webPay.WebPayPaymentDataDto;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public interface WebPayApi {
    @FormUrlEncoded
    @GET("https://sandbox.interswitchng.com/webpay/api/v1/gettransaction.json")
    Call<WebPayPaymentDataDto> getTransactionStatus(@Field("productid") String productId,
                                                    @Field("amount") Long amount, @Field("transactionreference") String transactionReference,
                                                    @Header("hash") String hash);
}
