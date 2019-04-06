package pojo.flutterWave;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class FWTransactionResponseDto {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private FWTransactionResponseDataDto data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FWTransactionResponseDataDto getData() {
        return data;
    }

    public void setData(FWTransactionResponseDataDto data) {
        this.data = data;
    }

}
