package conf;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.SetupService;
import utils.Constants;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Singleton
public class StartupActions {

    final static Logger logger = LoggerFactory.getLogger(StartupActions.class);
    private NinjaProperties ninjaProperties;
    private ObjectMapper objectMapper;
    private XmlMapper xmlMapper;
    private Provider<EntityManager> entityManagerProvider;
    private SetupService setupService;

    @Inject
    public StartupActions(
            Provider<EntityManager> entityManagerProvider,
            NinjaProperties ninjaProperties,
            ObjectMapper objectMapper,
            XmlMapper xmlMapper,
            SetupService setupService) {
        this.entityManagerProvider = entityManagerProvider;
        this.ninjaProperties = ninjaProperties;
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
        this.setupService = setupService;
    }

    @Start(order = 10)
    public void configureJsonMapper() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ISO_DATE_TIME_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Start(order = 10)
    public void configureXmlMapper() {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        xmlMapper.setDateFormat(dateFormat);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Start(order = 100)
    public void setupProject() {
        setupService.setUp();
    }

}
