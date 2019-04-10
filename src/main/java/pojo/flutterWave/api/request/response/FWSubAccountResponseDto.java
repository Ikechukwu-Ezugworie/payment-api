package pojo.flutterWave.api.request.response;

/*
 * Created by Gibah Joseph on Apr, 2019
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import pojo.flutterWave.FWMetaDto;

import java.util.List;

public class FWSubAccountResponseDto {

    @SerializedName("account_bank")
    @Expose
    private String accountBank;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("business_email")
    @Expose
    private String businessEmail;
    @SerializedName("business_contact")
    @Expose
    private String businessContact;
    @SerializedName("business_contact_mobile")
    @Expose
    private String businessContactMobile;
    @SerializedName("business_mobile")
    @Expose
    private String businessMobile;
    @SerializedName("meta")
    @Expose
    private List<FWMetaDto> meta;
    @SerializedName("seckey")
    @Expose
    private String seckey;

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessContact() {
        return businessContact;
    }

    public void setBusinessContact(String businessContact) {
        this.businessContact = businessContact;
    }

    public String getBusinessContactMobile() {
        return businessContactMobile;
    }

    public void setBusinessContactMobile(String businessContactMobile) {
        this.businessContactMobile = businessContactMobile;
    }

    public String getBusinessMobile() {
        return businessMobile;
    }

    public void setBusinessMobile(String businessMobile) {
        this.businessMobile = businessMobile;
    }

    public List<FWMetaDto> getMeta() {
        return meta;
    }

    public FWSubAccountResponseDto setMeta(List<FWMetaDto> meta) {
        this.meta = meta;
        return this;
    }

    public String getSeckey() {
        return seckey;
    }

    public void setSeckey(String seckey) {
        this.seckey = seckey;
    }

}


