package utils;

import org.hibernate.Session;

import java.math.BigInteger;

public class SequenceService {

    private final String sequenceName;
    private Session session;

    public SequenceService(Session session, String sequenceTableName) {
        this.session = session;
        this.sequenceName = sequenceTableName.toLowerCase() + "_sequence";
        this.init();
    }

    public void init() {
        this.session.createSQLQuery(String.format("DO $$ BEGIN CREATE SEQUENCE %s; EXCEPTION WHEN duplicate_table THEN END $$ LANGUAGE plpgsql;", sequenceName))
                .executeUpdate();
    }

    public Long getNextId() {
        return ((BigInteger) this.session.createSQLQuery(String.format("select nextval ('%s')", sequenceName)).uniqueResult()).longValue();
    }

    public String getNextId(String format) {
        return String.format(format, getNextId());
    }
}
