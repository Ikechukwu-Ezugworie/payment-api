
package pojo.flutterWave;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FWChargeTokenDto {

    @SerializedName("user_token")
    @Expose
    private String userToken;
    @SerializedName("embed_token")
    @Expose
    private String embedToken;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getEmbedToken() {
        return embedToken;
    }

    public void setEmbedToken(String embedToken) {
        this.embedToken = embedToken;
    }

}
