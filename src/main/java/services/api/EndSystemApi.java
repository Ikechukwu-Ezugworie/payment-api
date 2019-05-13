package services.api;

import okhttp3.ResponseBody;
import pojo.ApiResponse;
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

    @POST("booking/detail")
    Call<ApiResponse<TransactionRequestPojo>> validateFlutterWavePayment(@Body FWEndsystemTransactionRequestDto transactionRequestDto);
}
