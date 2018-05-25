package controllers;

import com.bw.payment.entity.RawDump;
import com.bw.payment.enumeration.PaymentChannelConstant;
import com.bw.payment.enumeration.PaymentProviderConstant;
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
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import services.PayDirectService;
import services.PaymentTransactionService;
import utils.PaymentUtil;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
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
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private PayDirectService payDirectService;

    @Inject
    private XmlMapper xmlMapper;

    public Result doPayDirectRequest(@ContentExtract String payload, Context context) {
        RawDump rawDump = new RawDump();
        rawDump.setRequest(payload);
        rawDump.setDateCreated(Timestamp.from(Instant.now()));
        rawDump.setPaymentProvider(PaymentProviderConstant.INTERSWITCH);
        rawDump.setPaymentChannel(PaymentChannelConstant.PAYDIRECT);

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
                                logger.debug("<=== PAYMENT NOTIFICATION: " + request.toString());
                                PaymentNotificationResponse paymentNotificationResponsePojo = payDirectService.processPaymentNotification(request, context);
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
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
            rawDump.setDescription("An error occurred: " + e.getMessage());
            paymentTransactionService.dump(rawDump);
        }
        return Results.badRequest().xml();
    }
}
