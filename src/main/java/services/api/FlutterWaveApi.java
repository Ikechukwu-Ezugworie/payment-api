package services.api;

import pojo.flutterWave.FWPaymentVerificationRequestDto;
import pojo.flutterWave.FWApiResponseDto;
import pojo.flutterWave.FWTransactionResponseDataDto;
import pojo.flutterWave.api.request.FWSubAccountRequestDto;
import pojo.flutterWave.api.request.FWSubAccountUpdateRequestDto;
import pojo.flutterWave.api.response.FWSubAccountResponseDto;
import retrofit2.Call;
import retrofit2.http.*;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public interface FlutterWaveApi {

    @POST("flwv3-pug/getpaidx/api/v2/verify")
    Call<FWApiResponseDto> getTransactionStatus(@Body FWPaymentVerificationRequestDto fwPaymentVerificationRequestDto);

    @POST("v2/gpx/subaccounts/create")
    Call<FWApiResponseDto> createSubAccount(@Body FWSubAccountRequestDto fwSubAccountRequestDto);

    @POST("v2/gpx/subaccounts/edit")
    Call<FWApiResponseDto> updateSubAccount(@Body FWSubAccountUpdateRequestDto fwSubAccountUpdateRequestDto);
}
