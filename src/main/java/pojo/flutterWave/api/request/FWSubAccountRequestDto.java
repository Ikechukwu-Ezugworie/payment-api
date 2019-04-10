package pojo.flutterWave.api.request;

/*
 * Created by Gibah Joseph on Apr, 2019
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.hibernate.validator.constraints.NotBlank;
import pojo.flutterWave.FWMetaDto;

import java.util.List;

public class FWSubAccountRequestDto {

    public static final String SPLIT_PERCENTAGE = "percentage";
    public static final String SPLIT_FLAT = "flat";

    @SerializedName("account_bank")
    @Expose
    @NotBlank(message = "Account bank cannot be empty")
    private String accountBank;
    @SerializedName("account_number")
    @Expose
    @NotBlank(message = "Account number cannot be empty")
    private String accountNumber;
    @SerializedName("business_name")
    @Expose
    @NotBlank(message = "Business name cannot be empty")
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
    @NotBlank(message = "Business mobile cannot be empty")
    private String businessMobile;
    @SerializedName("meta")
    @Expose
    private List<FWMetaDto> meta;
    @SerializedName("seckey")
    @Expose
    @NotBlank(message = "Sec key cannot be empty")
    private String seckey;
    @SerializedName("split_type")
    @Expose
    @NotBlank(message = "Split type cannot be empty")

    private String splitType;
    @SerializedName("split_value")
    @Expose
    @NotBlank(message = "Split value cannot be empty")
    private String splitValue;

    public String getSplitType() {
        return splitType;
    }

    public FWSubAccountRequestDto setSplitType(String splitType) {
        this.splitType = splitType;
        return this;
    }

    public String getSplitValue() {
        return splitValue;
    }

    public FWSubAccountRequestDto setSplitValue(String splitValue) {
        this.splitValue = splitValue;
        return this;
    }

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

    public FWSubAccountRequestDto setMeta(List<FWMetaDto> meta) {
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


