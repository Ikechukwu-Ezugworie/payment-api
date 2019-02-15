package pojo.webPay;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class BwPaymentsWebPayRequest {
    @NotNull(message = "Amount may not be empty")
    @Min(1)
    private Long amount;
    @NotBlank(message = "notification URL may not be empty")
    private String notificationUrl;
    @NotBlank(message = "Product id may not be empty")
    private String productId;
    @NotBlank (message = "Payer name not found")
    private String payerName;
    @NotBlank (message = "Payer email cannot be empty")
    private String payerEmail;
    @NotBlank (message = "Customer reference cannot be empty")
    private String customerReference;
    @NotBlank (message = "Payment ID cannot be empty")
    private String paymentItemId;

    public Long getAmount() {
        return amount;
    }

    public BwPaymentsWebPayRequest setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public BwPaymentsWebPayRequest setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public BwPaymentsWebPayRequest setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getPayerName() {
        return payerName;
    }

    public BwPaymentsWebPayRequest setPayerName(String payerName) {
        this.payerName = payerName;
        return this;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public BwPaymentsWebPayRequest setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
        return this;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public BwPaymentsWebPayRequest setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
        return this;
    }

    public String getPaymentItemId() {
        return paymentItemId;
    }

    public BwPaymentsWebPayRequest setPaymentItemId(String paymentItemId) {
        this.paymentItemId = paymentItemId;
        return this;
    }
}
