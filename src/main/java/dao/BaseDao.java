package dao;

import com.bw.payment.entity.Setting;
import com.bw.payment.service.PaymentService;
import com.google.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import utils.TransactionManager;

import java.util.List;

@SuppressWarnings("unchecked")
public class BaseDao {
    @Inject
    protected TransactionManager transactionManager;
    @Inject
    protected PaymentService paymentService;


    public <T> T getRecordById(Class<T> tClass, Long id) {
        return transactionManager.doForResult(session -> (T) session.createCriteria(tClass)
                .add(Restrictions.eq("id", id))
                .uniqueResult());
    }

    public <T> T getUniqueRecordByProperty(Class<T> tClass, String propertyName, Object propertyValue) {
        return transactionManager.doForResult(session -> (T) session.createCriteria(tClass)
                .add(Restrictions.eq(propertyName, propertyValue))
                .uniqueResult());
    }

    public <T> T getUniqueRecordByCriteria(Class<T> tClass, Criterion... restrictions) {
        return transactionManager.doForResult(session -> {
            Criteria criteria = session.createCriteria(tClass);
            for (Criterion restriction : restrictions) {
                criteria.add(restriction);
            }
            return (T) criteria.uniqueResult();
        });
    }

    public <T> T getAllRecordByCriteria(Class<T> tClass, Criterion... restrictions) {
        return transactionManager.doForResult(session -> {
            Criteria criteria = session.createCriteria(tClass);
            for (Criterion restriction : restrictions) {
                criteria.add(restriction);
            }
            return (T) criteria.list();
        });
    }

    public void setSettingsValue(String name, String value) {
        transactionManager.doInTransaction(session -> {
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value);
            setting.setDescription(name);

            session.save(setting);
        });
    }

    public String getSettingsValue(String name, String defaultValue, boolean createIfNotExist, Session session) {
        Setting setting = (Setting) session.createCriteria(Setting.class)
                .add(Restrictions.eq("name", name))
                .uniqueResult();

        if (setting == null && createIfNotExist) {
            setting.setName(name);
            setting.setValue(defaultValue);
            session.save(setting);
        }

        return setting == null ? defaultValue : setting.getValue();
    }

    public String getSettingsValue(String name, String defaultValue, boolean createIfNotExist) {
        return transactionManager.doForResult(session -> {
            Setting setting = (Setting) session.createCriteria(Setting.class)
                    .add(Restrictions.eq("name", name))
                    .uniqueResult();

            if (setting == null && createIfNotExist) {
                setting = new Setting();
                setting.setName(name);
                setting.setDescription(name);
                setting.setValue(defaultValue);
                session.save(setting);
            }

            return setting == null ? defaultValue : setting.getValue();
        });
    }

    public <T> List<T> getAllRecords(Class<T> tClass) {
        return transactionManager.doForResult(session -> (List<T>) session.createCriteria(tClass)
                .list());
    }

    public <T> List<T> getRecords(Class<T> tClass, int start, int length) {
        return transactionManager.doForResult(session -> (List<T>) session.createCriteria(tClass)
                .setFirstResult(start)
                .setMaxResults(length)
                .list());
    }

    public <T> Long getAllRecordsCount(Class<T> tClass) {
        return transactionManager.doForResult(session -> (Long) session.createCriteria(tClass)
                .setProjection(Projections.rowCount())
                .uniqueResult());
    }

    public <T> T saveObject(T obj) {
        try {
            return transactionManager.doForResult((session -> (T) session.save(obj)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T saveObject(T obj, Session session) {
        return (T) session.save(obj);
    }

//    public ActivityType getActivityTypeFromConstant(ActivityTypeConstant activityTypeConstant){
//        return getUniqueRecordByProperty(ActivityType.class, "name", activityTypeConstant);
//    }
//
//
//    public String generateMembershipId() {
//        return transactionManager.doForResult(session -> {
//            SequenceService sequenceService = new SequenceService(session, "membership_id");
//            return sequenceService.getNextId("%010d");
//        });
//    }
//
//
//    public String generatePortalAccountId() {
//        return transactionManager.doForResult(session -> {
//            SequenceService sequenceService = new SequenceService(session, "portal_account_id");
//            return sequenceService.getNextId("%010d");
//        });
//    }
}
