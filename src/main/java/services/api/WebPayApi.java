package services.api;

import pojo.webPay.WebPayPaymentDataDto;
import retrofit2.Call;
import retrofit2.http.*;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public interface WebPayApi {

    @GET("/webpay/api/v1/gettransaction.json")
    Call<WebPayPaymentDataDto> getTransactionStatus(@Field("productid") String productId,
                                                    @Field("amount") Long amount,
                                                    @Field("transactionreference") String transactionReference,
                                                    @Header("hash") String hash);
}
