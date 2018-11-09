package pojo;

import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;

import java.io.IOException;
import java.sql.Timestamp;

/*
 * Created by Gibah Joseph on Nov, 2018
 */
public class CustomerValidationStatistics {
    private CustomerInformationRequest request;
    private CustomerInformationResponse response;
    private Timestamp dateCreated;
    private PaymentProviderConstant paymentProvider;
    private PaymentChannelConstant paymentChannel;
    private String description;
    private String requestIp;
    private Long id;

    public static CustomerValidationStatistics from(RawDump rawDump) {
        if (rawDump == null) {
            return null;
        }
        XmlMapper xmlMapper = new XmlMapper();
        CustomerValidationStatistics customerValidationStatistics = new CustomerValidationStatistics();
        try {
            customerValidationStatistics.setRequest(xmlMapper.readValue(rawDump.getRequest(), CustomerInformationRequest.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        customerValidationStatistics.setResponse(new Gson().fromJson(rawDump.getResponse(), CustomerInformationResponse.class));
        customerValidationStatistics.setDateCreated(rawDump.getDateCreated());
        customerValidationStatistics.setPaymentProvider(rawDump.getPaymentProvider());
        customerValidationStatistics.setPaymentChannel(rawDump.getPaymentChannel());
        customerValidationStatistics.setDescription(rawDump.getDescription());
        customerValidationStatistics.setRequestIp(rawDump.getRequestIp());
        customerValidationStatistics.setId(rawDump.getId());

        return customerValidationStatistics;
    }

    public CustomerInformationRequest getRequest() {
        return request;
    }

    public CustomerValidationStatistics setRequest(CustomerInformationRequest request) {
        this.request = request;
        return this;
    }

    public CustomerInformationResponse getResponse() {
        return response;
    }

    public CustomerValidationStatistics setResponse(CustomerInformationResponse response) {
        this.response = response;
        return this;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public CustomerValidationStatistics setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public PaymentProviderConstant getPaymentProvider() {
        return paymentProvider;
    }

    public CustomerValidationStatistics setPaymentProvider(PaymentProviderConstant paymentProvider) {
        this.paymentProvider = paymentProvider;
        return this;
    }

    public PaymentChannelConstant getPaymentChannel() {
        return paymentChannel;
    }

    public CustomerValidationStatistics setPaymentChannel(PaymentChannelConstant paymentChannel) {
        this.paymentChannel = paymentChannel;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CustomerValidationStatistics setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public CustomerValidationStatistics setRequestIp(String requestIp) {
        this.requestIp = requestIp;
        return this;
    }

    public Long getId() {
        return id;
    }

    public CustomerValidationStatistics setId(Long id) {
        this.id = id;
        return this;
    }
}
