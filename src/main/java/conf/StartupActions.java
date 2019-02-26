package conf;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import dao.BaseDao;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import services.SetupService;
import services.api.WebPayApi;
import utils.Constants;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.TimeZone;

@Singleton
public class StartupActions {
    final static Logger logger = LoggerFactory.getLogger(StartupActions.class);

    private ObjectMapper objectMapper;
    private XmlMapper xmlMapper;
    private Provider<EntityManager> entityManagerProvider;
    private SetupService setupService;

    @Inject
    public StartupActions(
            Provider<EntityManager> entityManagerProvider,
            ObjectMapper objectMapper,
            XmlMapper xmlMapper,
            SetupService setupService) {
        this.entityManagerProvider = entityManagerProvider;
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
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new StdSerializer<HibernateProxy>(HibernateProxy.class) {

            @Override
            public void serialize(HibernateProxy value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeObject(Collections.singletonMap("id", value.getHibernateLazyInitializer().getIdentifier()));
            }
        });
        objectMapper.registerModule(simpleModule);
    }

    @Start(order = 10)
    public void configureXmlMapper() {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        xmlMapper.setDateFormat(dateFormat);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    @Start(order = 100)
    public void setupProject() {
        setupService.setUp();
    }

}
