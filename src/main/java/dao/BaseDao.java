package dao;

import com.bw.workorder.entity.ActivityType;
import com.bw.workorder.entity.Setting;
import com.bw.workorder.enumeration.ActivityTypeConstant;
import com.bw.workorder.service.WorkOrderService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import ninja.jpa.UnitOfWork;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import utils.TransactionManager;
import utils.sequence.SequenceService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class BaseDao {
    @Inject
    TransactionManager transactionManager;

    @Inject
    WorkOrderService workOrderService;

    public <T> T getById(Class<T> tClass, Long id) {
        return transactionManager.doForResult(session -> (T) session.createCriteria(tClass)
                .add(Restrictions.eq("id", id))
                .uniqueResult());
    }

    public <T> T getUniqueRecordByProperty(Class<T> tClass, String propertyName, Object propertyValue) {
        return transactionManager.doForResult(session -> (T) session.createCriteria(tClass)
                .add(Restrictions.eq(propertyName, propertyValue))
                .uniqueResult());
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

    public void setSettingsValue(String name, String value) {
        transactionManager.doIntransaction(session -> {
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value);
            setting.setDescription(name);

            session.save(setting);
        });
    }

    public <T> List<T> getAllRecords(Class<T> tClass) {
        return transactionManager.doForResult(session -> (List<T>) session.createCriteria(tClass)
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

    public ActivityType getActivityTypeFromConstant(ActivityTypeConstant activityTypeConstant){
        return getUniqueRecordByProperty(ActivityType.class, "name", activityTypeConstant);
    }


    public String generateMembershipId() {
        return transactionManager.doForResult(session -> {
            SequenceService sequenceService = new SequenceService(session, "membership_id");
            return sequenceService.getNextId("%010d");
        });
    }


    public String generatePortalAccountId() {
        return transactionManager.doForResult(session -> {
            SequenceService sequenceService = new SequenceService(session, "portal_account_id");
            return sequenceService.getNextId("%010d");
        });
    }
}
