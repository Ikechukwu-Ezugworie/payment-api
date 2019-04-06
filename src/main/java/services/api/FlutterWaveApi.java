package services.api;

import pojo.flutterWave.FWPaymentVerificationRequestDto;
import pojo.flutterWave.FWTransactionResponseDto;
import pojo.webPay.WebPayPaymentDataDto;
import retrofit2.Call;
import retrofit2.http.*;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public interface FlutterWaveApi {

    @POST("flwv3-pug/getpaidx/api/v2/verify")
    Call<FWTransactionResponseDto> getTransactionStatus(@Body FWPaymentVerificationRequestDto fwPaymentVerificationRequestDto);
}
