package services.api;

import pojo.remitta.RemittaRrrResponse;
import pojo.remitta.RemittaGenerateRequestRRRPojo;
import retrofit2.Call;
import retrofit2.http.*;

public interface RemittaApi {


    @POST("/webpay/api/v1/gettransaction.json")
    Call<RemittaRrrResponse> postToGenerateRRR(@Body RemittaGenerateRequestRRRPojo requestData,
                                               @Header("Authorization") String authorizationHeader);
}
