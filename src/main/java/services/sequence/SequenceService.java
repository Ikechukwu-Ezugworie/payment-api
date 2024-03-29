package services.sequence;

import com.google.inject.Provider;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public abstract class SequenceService {

    private final String sequenceName;
    private Provider<EntityManager> entityManagerProvider;
    private boolean initialized;

    SequenceService(Provider<EntityManager> entityManagerProvider, String sequenceTableName) {
        this.entityManagerProvider = entityManagerProvider;
        this.sequenceName = sequenceTableName.toLowerCase() + "_sequence";
    }

    private void init() {
//        this.entityManagerProvider.get().getTransaction().begin();
        this.entityManagerProvider.get().createNativeQuery(String.format("DO $$ BEGIN CREATE SEQUENCE %s; EXCEPTION WHEN duplicate_table THEN END $$ LANGUAGE plpgsql;", sequenceName)).executeUpdate();
//        this.entityManagerProvider.get().getTransaction().commit();
    }

    Long getNextLong() {
//        if (!initialized) {
            this.init();
//            initialized = true;
//        }
        EntityManager entityManager = this.entityManagerProvider.get();
        Query query = entityManager.createNativeQuery(String.format("select nextval ('%s')", sequenceName));
        Number number = (Number) query.getSingleResult();
        return number.longValue();
    }

    public String getNext() {
        return getNextLong().toString();
    }
}
