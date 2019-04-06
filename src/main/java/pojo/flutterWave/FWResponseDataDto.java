
package pojo.flutterWave;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FWResponseDataDto {

    @SerializedName("responsecode")
    @Expose
    private String responsecode;
    @SerializedName("responsemessage")
    @Expose
    private String responsemessage;

    public String getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public String getResponsemessage() {
        return responsemessage;
    }

    public void setResponsemessage(String responsemessage) {
        this.responsemessage = responsemessage;
    }

}
