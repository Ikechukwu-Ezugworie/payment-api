package controllers;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.CurrencyDao;
import dao.PaymentTransactionDao;
import extractors.IPAddress;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.ReverseRouter;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;
import pojo.PayerPojo;
import pojo.PaymentTransactionFilterResponseDto;
import pojo.TransactionRequestPojo;
import pojo.flutterWave.FWApiResponseDto;
import pojo.flutterWave.FWEndsystemTransactionRequestDto;
import pojo.flutterWave.FWPaymentRequestDto;
import pojo.flutterWave.FWPaymentVerificationRequestDto;
import pojo.flutterWave.api.request.FWSubAccountRequestDto;
import pojo.flutterWave.api.request.FWSubAccountUpdateRequestDto;
import pojo.flutterWave.api.response.FWSubAccountResponseDto;
import retrofit2.Response;
import services.*;
import services.api.EndSystemApi;
import utils.PaymentUtil;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class FlutterWaveController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private CurrencyDao currencyDao;
    @Inject
    private FlutterWaveService flutterWaveService;
    @Inject
    private PaymentService paymentService;
    @Inject
    private TransactionTemplate transactionTemplate;
    @Inject
    private ReverseRouter reverseRouter;
    @Inject
    private EndSystemService endSystemService;

    public Result createSubAccount(@JSR303Validation FWSubAccountRequestDto fwSubAccountRequestDto, Validation validation) {
        if (validation.hasViolations()) {
            FWApiResponseDto fwApiResponseDto = new FWApiResponseDto();
            fwApiResponseDto.setStatus("400");
            fwApiResponseDto.setMessage(validation.getViolations().iterator().next().getDefaultMessage());

            return Results.badRequest().json().render(fwApiResponseDto);
        }
        try {
            String secretKey = paymentService.getFlutterWaveServiceCredential(null).getSecretKey();
            fwSubAccountRequestDto.setSeckey(secretKey);
            Response<FWApiResponseDto> response = flutterWaveService.getApiCaller().createSubAccount(fwSubAccountRequestDto).execute();
            if (response.isSuccessful()) {
                return Results.json().status(response.code()).render(response.body());
            } else {
                return Results.json().status(response.code()).render(response.errorBody().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }


    public Result updateSubAccount(@JSR303Validation FWSubAccountUpdateRequestDto fwSubAccountUpdateRequestDto, Validation validation) {
        if (validation.hasViolations()) {
            FWApiResponseDto fwApiResponseDto = new FWApiResponseDto();
            fwApiResponseDto.setStatus("400");
            fwApiResponseDto.setMessage(validation.getViolations().iterator().next().getDefaultMessage());

            return Results.badRequest().json().render(fwApiResponseDto);
        }
        try {
//            String secretKey = paymentService.getFlutterWaveServiceCredential(null).getSecretKey();
//            fwSubAccountUpdateRequestDto.setSeckey(secretKey);
            Response<FWApiResponseDto> response = flutterWaveService.getApiCaller().updateSubAccount(fwSubAccountUpdateRequestDto).execute();
            if (response.isSuccessful()) {
                return Results.json().status(response.code()).render(response.body());
            } else {
                return Results.json().status(response.code()).render(response.errorBody().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }


    public Result paymentPage(@IPAddress String ipAddress, Context context) {
        try {


            FWEndsystemTransactionRequestDto data = new FWEndsystemTransactionRequestDto();
            data.setAccountCode(context.getParameter("accountCode"));
            data.setMerchantTransactionReferenceId(context.getParameter("paymentRetrievalReference"));
            data.setHash(context.getParameter("hash"));
            data.setRedirectUrl(context.getParameter("redirectUrl"));
            data.setCustomerEmail(context.getParameter("customerEmail"));
            data.setCustomerPhone(context.getParameter("customerPhone"));
            data.setCustomerLastname(context.getParameter("customerLastname"));
            data.setCustomerFirstname(context.getParameter("customerFirstname"));

            Set<ConstraintViolation<FWEndsystemTransactionRequestDto>> constraintViolations = javax.validation.Validation.buildDefaultValidatorFactory().getValidator().validate(data);

            if (!constraintViolations.isEmpty()) {
                for (ConstraintViolation<FWEndsystemTransactionRequestDto> constraintViolation : constraintViolations) {
                    ApiResponse apiResponse = new ApiResponse();
                    apiResponse.setCode(400);
                    apiResponse.setMessage(constraintViolation.getMessage());
                    return Results.ok().json().render(apiResponse);
                }
            }

            Response<ApiResponse<TransactionRequestPojo>> response = endSystemService.getApiCaller().validateFlutterWavePayment( data).execute();
            if (!response.isSuccessful()) {
                if (response.code() == 400)
                    return Results.html()
                            .render("error", "Bad request");
                if (response.code() == HttpStatus.SC_UNAUTHORIZED)
                    return Results.html()
                            .render("error", "Unauthorized");
                if (response.code() == HttpStatus.SC_NOT_FOUND)
                    return Results.html()
                            .render("error", response.errorBody() != null ? response.errorBody().string() : null);
            }

            TransactionRequestPojo transactionRequestPojo = null;
            if (response.body() != null) {
                transactionRequestPojo = response.body().getData();
            }
            if (transactionRequestPojo == null) {
                return Results.html()
                        .render("error", "No response gotten from server");
            }

            PaymentTransaction tx = paymentTransactionDao.getByMerchantTransactionReference(context.getParameter("paymentRetrievalReference"));
            if(tx != null) {
                if (tx.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
                    return Results.html()
                            .render("error", "Bill is Fully Paid");
                }
            }

            transactionRequestPojo.setPaymentProvider(PaymentProviderConstant.FLUTTERWAVE.value());
            transactionRequestPojo.setPaymentChannel(PaymentChannelConstant.CARD.getValue());
            transactionRequestPojo.setMerchantTransactionReferenceId(data.getMerchantTransactionReferenceId());
            PayerPojo payer = new PayerPojo();
            payer.setFirstName(data.getCustomerFirstname());
            payer.setLastName(data.getCustomerLastname());
            payer.setEmail(data.getCustomerEmail());
            transactionRequestPojo.setPayer(payer);

            PaymentTransaction paymentTransaction = paymentTransactionService.createTransaction(transactionRequestPojo, null);

            if (paymentTransaction == null) {
                return Results.ok().html().render("error", "Transaction not found");
            }
            TransactionRequestPojo fullPaymentTransactionDetailsAsPojo = paymentTransactionService.getFullPaymentTransactionDetailsAsPojo(paymentTransaction);
            if (paymentTransaction.getPaymentTransactionStatus().equals(PaymentTransactionStatus.SUCCESSFUL)) {
                return Results.ok().html().render("success", "Payment has already been made")
                        .render("transactionData", fullPaymentTransactionDetailsAsPojo);
            }

            FWPaymentRequestDto fwPaymentRequestDto = flutterWaveService.constructFormRequest(paymentTransaction, data, transactionRequestPojo.getSplit(), context);
            return Results.html()
                    .render("fwBaseUrl", flutterWaveService.getBaseUrl())
                    .render("data", fwPaymentRequestDto)
                    .render("transactionData", fullPaymentTransactionDetailsAsPojo);

        } catch (Exception e) {
            e.printStackTrace();
            return Results.html()
                    .render("error", e.getMessage());
        }
    }

    public Result paymentCompleted(@PathParam("tRef") String transactionRef, @Param("redirectUrl") String rUrl, @IPAddress String ipAddress, Context context) {
        logger.info("===> Redirect URL {}", rUrl);

        PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionByTransactionId(transactionRef);
        logger.info("Merchant txnRef " + paymentTransaction.getMerchantTransactionReferenceId());

        FWPaymentVerificationRequestDto paymentVerificationRequestDto = new FWPaymentVerificationRequestDto();
        paymentVerificationRequestDto.setTransactionReference(transactionRef);
        paymentVerificationRequestDto.setSecretKey(paymentService.getFlutterWaveServiceCredential(null).getSecretKey());

        String prr = paymentTransaction.getMerchantTransactionReferenceId();
        try {
            FWApiResponseDto fwApiResponseDto = flutterWaveService.getPaymentData(paymentTransaction);
            logger.info("response {}", fwApiResponseDto.toString());
            paymentTransaction = flutterWaveService.processPaymentData(paymentTransaction, fwApiResponseDto, true);

            URIBuilder b = new URIBuilder(rUrl);
            b.addParameter("prr", prr);
            String redirectUrl = b.build().toString();
            return Results.redirect(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.redirect(rUrl + "?prr=" + prr);
    }

    public Result getTransaction(@Param("transactionReference") String transactionId) {
        ApiResponse<PaymentTransactionFilterResponseDto> apiResponse = new ApiResponse<>();
        if (StringUtils.isBlank(transactionId)) {
            apiResponse.setCode(400);
            apiResponse.setMessage("Transaction reference cannot be blank");
            return Results.badRequest().json().render(apiResponse);
        }
        PaymentTransaction paymentTransaction = flutterWaveService.getPaymentTransactionByMerchantRef(transactionId);
        if (paymentTransaction == null) {
            apiResponse.setCode(404);
            apiResponse.setMessage("Transaction not found");
            return Results.notFound().json().render(apiResponse);
        }
        try {
            FWApiResponseDto fwApiResponseDto = flutterWaveService.getPaymentData(paymentTransaction);
            paymentTransaction = flutterWaveService.processPaymentData(paymentTransaction, fwApiResponseDto, true);

            PaymentTransactionFilterResponseDto data = PaymentTransactionFilterResponseDto.from(paymentTransaction);
            data.setAdditionalData(null);
            apiResponse.setData(data);
            apiResponse.setCode(200);
            logger.info("===> Transaction Query response " + new Gson().toJson(apiResponse));
            return Results.json().render(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }

    public Result requeryTransaction(@Param("merchantTransactionRef") String transactionId) {
        logger.info("===> Checking transaction ID " + transactionId);
        ApiResponse<PaymentTransactionFilterResponseDto> apiResponse = new ApiResponse<>();
        if (StringUtils.isBlank(transactionId)) {
            apiResponse.setCode(400);
            apiResponse.setMessage("Transaction ID cannot be blank");
            return Results.badRequest().json().render(apiResponse);
        }
        PaymentTransaction paymentTransaction = flutterWaveService.getPaymentTransactionByMerchantRef(transactionId);
        if (paymentTransaction == null) {
            apiResponse.setCode(404);
            apiResponse.setMessage("Transaction not found");
            return Results.notFound().json().render(apiResponse);
        }
        try {
            FWApiResponseDto fwApiResponseDto = flutterWaveService.getPaymentData(paymentTransaction);
            paymentTransaction = flutterWaveService.processPaymentData(paymentTransaction, fwApiResponseDto, true);

            apiResponse.setData(PaymentTransactionFilterResponseDto.from(paymentTransaction));
            apiResponse.setCode(200);
            logger.info("===> Transaction Query response " + new Gson().toJson(apiResponse));
            return Results.json().render(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json();
    }

    public Result test(Context context) {

        return Results.html().render("url", reverseRouter.with(FlutterWaveController::paymentPage));
    }
}
