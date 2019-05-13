package pojo.flutterWave;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.StringJoiner;

/*
 * Created by Gibah Joseph on Apr, 2019
 */
public class FWApiResponseDto {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private FWTransactionDto data;

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

    public FWTransactionDto getData() {
        return data;
    }

    public void setData(FWTransactionDto data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FWApiResponseDto.class.getSimpleName() + "[", "]")
                .add("status='" + status + "'")
                .add("message='" + message + "'")
                .add("data=" + data)
                .toString();
    }
}
