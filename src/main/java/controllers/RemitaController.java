package controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.MerchantDao;
import dao.PaymentTransactionDao;
import extractors.ContentExtract;
import ninja.Context;
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
public class RemitaController {
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

//
}
