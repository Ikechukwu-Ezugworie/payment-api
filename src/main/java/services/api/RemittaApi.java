package services.api;

import pojo.remitta.RemittaRrrResponse;
import pojo.remitta.RemittaGenerateRequestRRRPojo;
import pojo.remitta.RemittaTransactionStatusPojo;
import retrofit2.Call;
import retrofit2.http.*;

public interface RemittaApi {


    @POST("/remita/exapp/api/v1/send/api/echannelsvc/merchant/api/paymentinit")
    Call<RemittaRrrResponse> postToGenerateRRR(@Body RemittaGenerateRequestRRRPojo requestData,
                                               @Header("Authorization") String authorizationHeader);

    @GET("/remita/ecomm/{merchantId}/{RRR}/{hash}/status.reg")
    Call<RemittaTransactionStatusPojo> getTransactionStatus(@Path("merchantId") String merchantId, @Path("RRR") String rrr, @Path("hash") String hash);
}
