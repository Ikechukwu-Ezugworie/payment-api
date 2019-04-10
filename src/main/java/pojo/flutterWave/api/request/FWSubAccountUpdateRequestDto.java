package pojo.flutterWave.api.request;

/*
 * Created by Gibah Joseph on Apr, 2019
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class FWSubAccountUpdateRequestDto {

    public static final String SPLIT_PERCENTAGE = "percentage";
    public static final String SPLIT_FLAT = "flat";

    @SerializedName("id")
    @Expose
    @NotBlank(message = "Id cannot be empty")
    private String id;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("business_email")
    @Expose
    private String businessEmail;
    @SerializedName("account_bank")
    @Expose
    private String accountBank;
    @SerializedName("split_type")
    @Expose
    private String splitType;
    @SerializedName("split_value")
    @Expose
    private String splitValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public String getSplitValue() {
        return splitValue;
    }

    public void setSplitValue(String splitValue) {
        this.splitValue = splitValue;
    }

}
