package controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import extractors.ContentExtract;
import filters.InterswitchFilter;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.payDirect.customerValidation.request.CustomerInformationRequest;
import pojo.payDirect.customerValidation.response.CustomerInformationResponse;
import pojo.payDirect.paymentNotification.request.PaymentNotificationRequest;
import pojo.payDirect.paymentNotification.response.PaymentNotificationResponse;
import services.PayDirectService;
import services.PaymentTransactionService;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * CREATED BY GIBAH
 */
@Singleton
@FilterWith(InterswitchFilter.class)
public class PayDirectController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private Messages messages;
    @Inject
    private MerchantDao merchantDao;
    @Inject
    private PaymentTransactionDao paymentTransactionDao;
    @Inject
    private PaymentTransactionService paymentTransactionService;
    @Inject
    private PayDirectService payDirectService;

    @Inject
    private XmlMapper xmlMapper;

    public Result doPayDirectRequest(@ContentExtract String payload, Context context) {
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
                            logger.info("<===" + qName);
                            if (qName.equalsIgnoreCase("CustomerInformationRequest")) {
                                eventReader.close();
                                CustomerInformationRequest request = xmlMapper.readValue(payload, CustomerInformationRequest.class);
                                CustomerInformationResponse customerInformationResponse = payDirectService.processCustomerValidationRequest(request, context);
                                return Results.xml().render(customerInformationResponse);
                            } else if (qName.equalsIgnoreCase("PaymentNotificationRequest")) {
                                eventReader.close();
                                PaymentNotificationRequest request = xmlMapper.readValue(payload, PaymentNotificationRequest.class);
                                logger.info(request.toString());
                                PaymentNotificationResponse paymentNotificationResponsePojo = payDirectService.processPaymentNotification(request, context);
                                return Results.ok().xml().render(paymentNotificationResponsePojo);
                            }
                            break;
                    }
                }
                eventReader.close();
            }
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
        return Results.badRequest().xml();
    }
}
