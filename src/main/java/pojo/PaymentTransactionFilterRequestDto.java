package pojo;

import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.bw.payment.enumeration.PaymentTransactionStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import utils.PaymentUtil;

import java.sql.Timestamp;
import java.util.Optional;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class PaymentTransactionFilterRequestDto{
    private String transactionId;
    private String merchantTransactionReferenceId;
    private String providerTransactionReference;
    private Long amountInKobo;
    private Long amountPaidInKobo;
    private PaymentProviderConstant paymentProvider;
    private PaymentChannelConstant paymentChannel;
    private String serviceTypeId;
    private PaymentTransactionStatus paymentTransactionStatus;
    private String customerTransactionReference;
    private Long id;
    private Integer offset;
    private Integer limit;
    private String dateCreatedStart;
    private String dateCreatedEnd;

    public Optional<String> getTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public PaymentTransactionFilterRequestDto setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Optional<String> getMerchantTransactionReferenceId() {
        return Optional.ofNullable(merchantTransactionReferenceId);
    }

    public PaymentTransactionFilterRequestDto setMerchantTransactionReferenceId(String merchantTransactionReferenceId) {
        this.merchantTransactionReferenceId = merchantTransactionReferenceId;
        return this;
    }

    public Optional<String> getProviderTransactionReference() {
        return Optional.ofNullable(providerTransactionReference);
    }

    public PaymentTransactionFilterRequestDto setProviderTransactionReference(String providerTransactionReference) {
        this.providerTransactionReference = providerTransactionReference;
        return this;
    }

    public Optional<Long> getAmountInKobo() {
        return Optional.ofNullable(amountInKobo);
    }

    public PaymentTransactionFilterRequestDto setAmountInKobo(Long amountInKobo) {
        this.amountInKobo = amountInKobo;
        return this;
    }

    public Optional<Long> getAmountPaidInKobo() {
        return Optional.ofNullable(amountPaidInKobo);
    }

    public PaymentTransactionFilterRequestDto setAmountPaidInKobo(Long amountPaidInKobo) {
        this.amountPaidInKobo = amountPaidInKobo;
        return this;
    }

    public Optional<PaymentProviderConstant> getPaymentProvider() {
        return Optional.ofNullable(paymentProvider);
    }

    public PaymentTransactionFilterRequestDto setPaymentProvider(PaymentProviderConstant paymentProvider) {
        this.paymentProvider = paymentProvider;
        return this;
    }

    public Optional<PaymentChannelConstant> getPaymentChannel() {
        return Optional.ofNullable(paymentChannel);
    }

    public PaymentTransactionFilterRequestDto setPaymentChannel(PaymentChannelConstant paymentChannel) {
        this.paymentChannel = paymentChannel;
        return this;
    }

    public Optional<String> getServiceTypeId() {
        return Optional.ofNullable(serviceTypeId);
    }

    public PaymentTransactionFilterRequestDto setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
        return this;
    }

    public Optional<PaymentTransactionStatus> getPaymentTransactionStatus() {
        return Optional.ofNullable(paymentTransactionStatus);
    }

    public PaymentTransactionFilterRequestDto setPaymentTransactionStatus(PaymentTransactionStatus paymentTransactionStatus) {
        this.paymentTransactionStatus = paymentTransactionStatus;
        return this;
    }

    public Optional<String> getCustomerTransactionReference() {
        return Optional.ofNullable(customerTransactionReference);
    }

    public PaymentTransactionFilterRequestDto setCustomerTransactionReference(String customerTransactionReference) {
        this.customerTransactionReference = customerTransactionReference;
        return this;
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public PaymentTransactionFilterRequestDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Optional<Integer> getOffset() {
        return Optional.ofNullable(offset);
    }

    public PaymentTransactionFilterRequestDto setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }

    public PaymentTransactionFilterRequestDto setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Optional<Timestamp> getDateCreatedStart(String format) {
        if(StringUtils.isBlank(dateCreatedStart)){
            return Optional.empty();
        }
        return Optional.ofNullable(PaymentUtil.getTimestamp(dateCreatedStart,format));
    }

    public PaymentTransactionFilterRequestDto setDateCreatedStart(String dateCreatedStart) {
        this.dateCreatedStart = dateCreatedStart;
        return this;
    }

    public Optional<Timestamp> getDateCreatedEnd(String format) {
        if(StringUtils.isBlank(dateCreatedEnd)){
            return Optional.empty();
        }
        return Optional.ofNullable(PaymentUtil.getTimestamp(dateCreatedEnd,format));
    }

    public PaymentTransactionFilterRequestDto setDateCreatedEnd(String dateCreatedEnd) {
        this.dateCreatedEnd = dateCreatedEnd;
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
                .toString();
    }
}
