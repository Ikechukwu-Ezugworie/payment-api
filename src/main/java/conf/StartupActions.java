package conf;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Singleton
public class StartupActions {

    final static Logger logger = LoggerFactory.getLogger(StartupActions.class);
//    static SessionFactory sessionFactory = null;
    private NinjaProperties ninjaProperties;
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private XmlMapper xmlMapper;

    @Inject
    public StartupActions(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

//    public static SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }

    @Start(order = 10)
    public void configureJsonMapper() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        try {
//            logger.info("About to open hibernate session");
//            sessionFactory = HibernateUtils.getSessionFactory();
//            logger.info("Done with hibernate session");
//
//        } catch (NamingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
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

    }

}
