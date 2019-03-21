package services.api;

import pojo.webPay.WebPayPaymentDataDto;
import retrofit2.Call;
import retrofit2.http.*;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public interface WebPayApi {

    @GET("/collections/api/v1/gettransaction.json")
    Call<WebPayPaymentDataDto> getTransactionStatus(@Query("productid") String productId,
                                                    @Query("amount") Long amount,
                                                    @Query("transactionreference") String transactionReference,
                                                    @Header("hash") String hash);
}
