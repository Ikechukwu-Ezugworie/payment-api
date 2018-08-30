package controllers;

import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import extractors.ContentExtract;
import filters.InterswitchFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.paymentNotification.request.OtherCustomerInfo;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import services.PayDirectService;
import services.PaymentTransactionService;
import utils.PaymentUtil;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * CREATED BY GIBAH
 */
@Singleton
@FilterWith(InterswitchFilter.class)
public class PayDirectController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private PaymentTransactionService paymentTransactionService;
    private PayDirectService payDirectService;

    private XmlMapper xmlMapper;

    @Inject
    public PayDirectController(PaymentTransactionService paymentTransactionService, PayDirectService payDirectService, XmlMapper xmlMapper) {
        this.paymentTransactionService = paymentTransactionService;
        this.payDirectService = payDirectService;
        this.xmlMapper = xmlMapper;

        SimpleModule simpleModule = new SimpleModule();

        StdDeserializer<OtherCustomerInfo> stdDeserializer = new StdDeserializer<OtherCustomerInfo>(OtherCustomerInfo.class) {
            @Override
            public OtherCustomerInfo deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                JsonToken jsonToken = jsonParser.getCurrentToken();

                if (jsonToken.equals(JsonToken.VALUE_STRING)) {
                    OtherCustomerInfo otherCustomerInfo = new OtherCustomerInfo();
                    otherCustomerInfo.setRawValue(jsonParser.getValueAsString());
                    logger.info("<== string value");
                    return otherCustomerInfo;
                }
                return null;
            }
        };
        simpleModule.addDeserializer(OtherCustomerInfo.class, stdDeserializer);
        this.xmlMapper.registerModule(simpleModule);
    }

    public Result doPayDirectRequest(@ContentExtract String payload, Context context) {
        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.INTERSWITCH);
        rawDump.setPaymentChannel(PaymentChannelConstant.PAYDIRECT);
        paymentTransactionService.dump(rawDump);

        logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        try {
            byte[] byteArray = payload.getBytes("UTF-8");
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray)) {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLEventReader eventReader = factory.createXMLEventReader(inputStream);

                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();

                    logger.info("<=== reading xml " + event.getEventType());
                    switch (event.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT:
                            StartElement startElement = event.asStartElement();
                            String qName = startElement.getName().getLocalPart();
                            if (qName.equalsIgnoreCase("CustomerInformationRequest")) {
                                eventReader.close();
                                CustomerInformationRequest request = xmlMapper.readValue(payload, CustomerInformationRequest.class);
                                System.out.println("<=== VALIDATION: " + request.toString());
                                CustomerInformationResponse customerInformationResponse = payDirectService.processCustomerValidationRequest(request, context);
                                rawDump.setResponse(customerInformationResponse == null ? null : PaymentUtil.toJSON(customerInformationResponse));
                                rawDump.setDescription("CUSTOMER VALIDATION");
                                paymentTransactionService.dump(rawDump);
                                if (customerInformationResponse == null) {
                                    return Results.xml().status(503);
                                }
                                return Results.xml().render(customerInformationResponse);
                            } else if (qName.equalsIgnoreCase("PaymentNotificationRequest")) {
                                eventReader.close();
                                PaymentNotificationRequest request = xmlMapper.readValue(payload, PaymentNotificationRequest.class);
                                logger.info("<=== PAYMENT NOTIFICATION: " + request.toString());
                                PaymentNotificationResponse paymentNotificationResponsePojo = payDirectService.processPaymentNotification(request, rawDump, context);
                                rawDump.setResponse(paymentNotificationResponsePojo == null ? null : PaymentUtil.toJSON(paymentNotificationResponsePojo));
                                rawDump.setDescription("PAYMENT NOTIFICATION");
                                paymentTransactionService.dump(rawDump);
                                return Results.ok().xml().render(paymentNotificationResponsePojo);
                            }
                            break;
                    }
                }
                eventReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            rawDump.setDescription("An error occurred: " + e.getMessage());
            paymentTransactionService.dump(rawDump);
        }
        return Results.badRequest().xml();
    }
}
