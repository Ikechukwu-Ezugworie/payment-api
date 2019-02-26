package controllers;

import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.RawDumpDao;
import extractors.ContentExtract;
import extractors.IPAddress;
import filters.InterswitchFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import okhttp3.Response;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.CustomerValidationStatistics;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.paymentNotification.request.OtherCustomerInfo;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import services.PayDirectService;
import services.PaymentTransactionService;
import services.TestService;
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
    private final PayDirectService payDirectService;
    private RawDumpDao rawDumpDao;
    private XmlMapper xmlMapper;
    private TestService testService;

    @Inject
    public PayDirectController(PaymentTransactionService paymentTransactionService, PayDirectService payDirectService,
                               RawDumpDao rawDumpDao, XmlMapper xmlMapper, TestService testService) {
        this.paymentTransactionService = paymentTransactionService;
        this.payDirectService = payDirectService;
        this.rawDumpDao = rawDumpDao;
        this.xmlMapper = xmlMapper;
        this.testService = testService;

        SimpleModule simpleModule = new SimpleModule();

        StdDeserializer<OtherCustomerInfo> stdDeserializer = new StdDeserializer<OtherCustomerInfo>(OtherCustomerInfo.class) {
            @Override
            public OtherCustomerInfo deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                logger.info("<=== parsing other customer info");

                JsonToken jsonToken = jsonParser.getCurrentToken();
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode node = oc.readTree(jsonParser);

                OtherCustomerInfo otherCustomerInfo = new OtherCustomerInfo();

                try {
                    if (jsonToken.equals(JsonToken.VALUE_STRING)) {
                        otherCustomerInfo.setRawValue(jsonParser.getValueAsString());
                        return otherCustomerInfo;
                    }
                    String emailAddress = node.get("EmailAddress") == null ? null : node.get("EmailAddress").asText();
                    String taxOfficeId = node.get("TaxOfficeID") == null ? null : node.get("TaxOfficeID").asText();
                    String nationalId = node.get("NationalID") == null ? null : node.get("NationalID").asText();
                    String notificationMethod = node.get("NotificationMethod") == null ? null : node.get("NotificationMethod").asText();
                    String phoneNumber = node.get("PhoneNumber") == null ? null : node.get("PhoneNumber").asText();

                    otherCustomerInfo.setEmailAddress(emailAddress);
                    otherCustomerInfo.setTaxOfficeId(taxOfficeId);
                    otherCustomerInfo.setNationalId(nationalId);
                    otherCustomerInfo.setNotificationMethod(notificationMethod);
                    otherCustomerInfo.setPhoneNumber(phoneNumber);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return otherCustomerInfo;
            }
        };
        simpleModule.addDeserializer(OtherCustomerInfo.class, stdDeserializer);
        SimpleModule simpleStringModule = new SimpleModule();
        simpleStringModule.addDeserializer(String.class, new JsonDeserializer<String>() {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                System.out.println("<=== " + p.getValueAsString());
                return p.getValueAsString();
            }
        });
        this.xmlMapper.registerModule(simpleStringModule);
    }

    public Result doPayDirectRequest(@ContentExtract String payload, Context context, @IPAddress String ipAddress) {
        if (payload.startsWith("\"")) {
            payload = payload.replaceFirst("\"", "");
        }
        if (payload.endsWith("\"")) {
            payload = payload.substring(0, payload.length() - 1);
        }
        payload = StringEscapeUtils.unescapeXml(payload);
        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.INTERSWITCH);
        rawDump.setPaymentChannel(PaymentChannelConstant.PAYDIRECT);
        rawDump.setRequestIp(ipAddress);

        paymentTransactionService.dump(rawDump);

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
                                rawDump.setResponse(customerInformationResponse == null ? "Could not contact end system (503)" : PaymentUtil.toJSON(customerInformationResponse));
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
                                synchronized (payDirectService) {
                                    PaymentNotificationResponse paymentNotificationResponsePojo = payDirectService.processPaymentNotification(request, rawDump, context);
                                    rawDump.setResponse(paymentNotificationResponsePojo == null ? "Could not contact end system (503)" : PaymentUtil.toJSON(paymentNotificationResponsePojo));
                                    rawDump.setDescription("PAYMENT NOTIFICATION");
                                    paymentTransactionService.dump(rawDump);
                                    return Results.ok().xml().render(paymentNotificationResponsePojo);
                                }
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

    public Result monitor(Context context) {
        RawDump rawDump = rawDumpDao.findLastByDescription("CUSTOMER VALIDATION");
        String payload;
        if (rawDump != null) {
            payload = rawDump.getRequest();
        } else {
            payload = testService.getCustomerValidationTestPayload();
        }
        try (Response response = testService.doCustomerValidation(payload)) {
            if (response.code() == 200) {
                return Results.json().render(CustomerValidationStatistics.from(rawDump));
            }
            return Results.status(response.code()).json();
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError().json().render("message", e.getMessage());
        }
    }
}
