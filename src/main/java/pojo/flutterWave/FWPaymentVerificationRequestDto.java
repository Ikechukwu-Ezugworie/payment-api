package pojo.flutterWave;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class FWPaymentVerificationRequestDto {
    @SerializedName("txref")
    private String transactionReference;
    @SerializedName("SECKEY")
    private String secretKey;

    public String getTransactionReference() {
        return transactionReference;
    }

    public FWPaymentVerificationRequestDto setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public FWPaymentVerificationRequestDto setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }
}
