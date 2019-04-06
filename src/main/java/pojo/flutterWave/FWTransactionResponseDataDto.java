
package pojo.flutterWave;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FWTransactionResponseDataDto {

    @SerializedName("data")
    @Expose
    private FWResponseDataDto data;
    @SerializedName("FWTransactionDto")
    @Expose
    private FWTransactionDto FWTransactionDto;

    public FWResponseDataDto getData() {
        return data;
    }

    public void setData(FWResponseDataDto data) {
        this.data = data;
    }

    public FWTransactionDto getFWTransactionDto() {
        return FWTransactionDto;
    }

    public void setFWTransactionDto(FWTransactionDto FWTransactionDto) {
        this.FWTransactionDto = FWTransactionDto;
    }
}
