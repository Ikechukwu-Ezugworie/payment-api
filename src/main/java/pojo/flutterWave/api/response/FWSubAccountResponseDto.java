package pojo.flutterWave.api.response;

/*
 * Created by Gibah Joseph on Apr, 2019
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import pojo.flutterWave.FWMetaDto;

import java.util.List;

public class FWSubAccountResponseDto {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("account_bank")
    @Expose
    private String accountBank;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("meta")
    @Expose
    private List<FWMetaDto> meta = null;
    @SerializedName("subaccount_id")
    @Expose
    private String subaccountId;
    @SerializedName("bank_name")
    @Expose
    private String bankName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<FWMetaDto> getMeta() {
        return meta;
    }

    public FWSubAccountResponseDto setMeta(List<FWMetaDto> meta) {
        this.meta = meta;
        return this;
    }

    public String getSubaccountId() {
        return subaccountId;
    }

    public void setSubaccountId(String subaccountId) {
        this.subaccountId = subaccountId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

}
