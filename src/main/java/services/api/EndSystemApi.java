package services.api;

import pojo.TransactionRequestPojo;
import pojo.flutterWave.FWEndsystemTransactionRequestDto;
import pojo.flutterWave.FlutterWaveValidationResponseDto;
import pojo.webPay.WebPayPaymentDataDto;
import retrofit2.Call;
import retrofit2.http.*;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public interface EndSystemApi {

    @POST
    Call<TransactionRequestPojo> validateFlutterWavePayment(@Url String url, @Body FWEndsystemTransactionRequestDto transactionRequestDto);
}