package pojo;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class PaymentTransactionFilterResponseDto {
    private String transactionId;
    private String merchantTransactionReferenceId;
    private String providerTransactionReference;
    private Long amountInKobo;
    private Long amountPaidInKobo;
    private PaymentProviderConstant paymentProvider;
    private PaymentChannelConstant paymentChannel;
    private String serviceTypeId;
    private PaymentTransactionStatus paymentTransactionStatus;
    private Timestamp dateCreated;
    private String customerTransactionReference;
    private Long id;
    private List<RawDump> additionalData;

    public static PaymentTransactionFilterResponseDto from(PaymentTransaction paymentTransaction) {
        PaymentTransactionFilterResponseDto paymentTransactionFilterResponseDto = new PaymentTransactionFilterResponseDto();
        paymentTransactionFilterResponseDto.setTransactionId(paymentTransaction.getTransactionId());
        paymentTransactionFilterResponseDto.setMerchantTransactionReferenceId(paymentTransaction.getMerchantTransactionReferenceId());
        paymentTransactionFilterResponseDto.setProviderTransactionReference(paymentTransaction.getProviderTransactionReference());
        paymentTransactionFilterResponseDto.setAmountInKobo(paymentTransaction.getAmountInKobo());
        paymentTransactionFilterResponseDto.setAmountPaidInKobo(paymentTransaction.getAmountPaidInKobo());
        paymentTransactionFilterResponseDto.setPaymentProvider(paymentTransaction.getPaymentProvider());
        paymentTransactionFilterResponseDto.setPaymentChannel(paymentTransaction.getPaymentChannel());
        paymentTransactionFilterResponseDto.setServiceTypeId(paymentTransaction.getServiceTypeId());
        paymentTransactionFilterResponseDto.setPaymentTransactionStatus(paymentTransaction.getPaymentTransactionStatus());
        paymentTransactionFilterResponseDto.setCustomerTransactionReference(paymentTransaction.getCustomerTransactionReference());
        paymentTransactionFilterResponseDto.setId(paymentTransaction.getId());
        paymentTransactionFilterResponseDto.setDateCreated(paymentTransaction.getDateCreated());

        return paymentTransactionFilterResponseDto;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentTransactionFilterResponseDto setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getMerchantTransactionReferenceId() {
        return merchantTransactionReferenceId;
    }

    public PaymentTransactionFilterResponseDto setMerchantTransactionReferenceId(String merchantTransactionReferenceId) {
        this.merchantTransactionReferenceId = merchantTransactionReferenceId;
        return this;
    }

    public String getProviderTransactionReference() {
        return providerTransactionReference;
    }

    public PaymentTransactionFilterResponseDto setProviderTransactionReference(String providerTransactionReference) {
        this.providerTransactionReference = providerTransactionReference;
        return this;
    }

    public Long getAmountInKobo() {
        return amountInKobo;
    }

    public PaymentTransactionFilterResponseDto setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
        return this;
    }

    public Long getAmountPaidInKobo() {
        return amountPaidInKobo;
    }

    public PaymentTransactionFilterResponseDto setAmountPaidInKobo(Long amountPaidInKobo) {
        this.amountPaidInKobo = amountPaidInKobo;
        return this;
    }

    public PaymentProviderConstant getPaymentProvider() {
        return paymentProvider;
    }

    public PaymentTransactionFilterResponseDto setPaymentProvider(PaymentProviderConstant paymentProvider) {
        this.paymentProvider = paymentProvider;
        return this;
    }

    public PaymentChannelConstant getPaymentChannel() {
        return paymentChannel;
    }

    public PaymentTransactionFilterResponseDto setPaymentChannel(PaymentChannelConstant paymentChannel) {
        this.paymentChannel = paymentChannel;
        return this;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public PaymentTransactionFilterResponseDto setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
        return this;
    }

    public PaymentTransactionStatus getPaymentTransactionStatus() {
        return paymentTransactionStatus;
    }

    public PaymentTransactionFilterResponseDto setPaymentTransactionStatus(PaymentTransactionStatus paymentTransactionStatus) {
        this.paymentTransactionStatus = paymentTransactionStatus;
        return this;
    }

    public String getCustomerTransactionReference() {
        return customerTransactionReference;
    }

    public PaymentTransactionFilterResponseDto setCustomerTransactionReference(String customerTransactionReference) {
        this.customerTransactionReference = customerTransactionReference;
        return this;
    }

    public Long getId() {
        return id;
    }

    public PaymentTransactionFilterResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public List<RawDump> getAdditionalData() {
        return additionalData;
    }

    public PaymentTransactionFilterResponseDto setAdditionalData(List<RawDump> additionalData) {
        this.additionalData = additionalData;
        return this;
    }

    public PaymentTransactionFilterResponseDto addAdditionalData(RawDump additionalDatum) {
        if (additionalData == null) {
            additionalData = new ArrayList<>();
        }
        this.additionalData.add(additionalDatum);
        return this;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("transactionId", transactionId)
                .append("merchantTransactionReferenceId", merchantTransactionReferenceId)
                .append("providerTransactionReference", providerTransactionReference)
                .append("amountInKobo", amountInKobo)
                .append("amountPaidInKobo", amountPaidInKobo)
                .append("paymentProvider", paymentProvider)
                .append("paymentChannel", paymentChannel)
                .append("serviceTypeId", serviceTypeId)
                .append("paymentTransactionStatus", paymentTransactionStatus)
                .append("customerTransactionReference", customerTransactionReference)
                .append("id", id)
                .append("additionalData", additionalData)
                .toString();
    }
}
