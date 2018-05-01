package dao;

import com.bw.payment.entity.Setting;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class BaseDao {

    @Inject
    protected Provider<EntityManager> entityManagerProvider;

    public <T> T getRecordById(Class<T> tClass, Long id) {
        return entityManagerProvider.get().find(tClass, id);
    }

    public <T> T getUniqueRecordByProperty(Class<T> tClass, String propertyName, Object propertyValue) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> clientCriteriaQuery = criteriaBuilder.createQuery(tClass);
        Root<T> clientRoot = clientCriteriaQuery.from(tClass);
        Predicate predicate = criteriaBuilder.equal(clientRoot.get(propertyName), propertyValue);
        clientCriteriaQuery.where(predicate);

        return uniqueResultOrNull(entityManager.createQuery(clientCriteriaQuery));
    }

    <T> T uniqueResultOrNull(TypedQuery<T> tTypedQuery) {
        try {
            List<T> tList = tTypedQuery.getResultList();
            return tList.isEmpty() ? null : tList.iterator().next();
        } catch (NoResultException | NonUniqueResultException ignore) {
            return null;
        }
    }

    <T> T uniqueResultOrNull(Query query, Class<T> tClass) {
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ignore) {
            return null;
        }
    }

    long getCount(Query query) {
        try {
            return (long) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ignore) {
            return 0;
        }
    }

    <T> List<T> resultsList(TypedQuery<T> tTypedQuery) {
        try {
            return tTypedQuery.getResultList();
        } catch (NoResultException ignore) {
            return new ArrayList<>();
        }
    }

    public String getSettingsValue(String name, String defaultValue) {
        return getSettingsValue(name, defaultValue, false);
    }

    @Transactional
    public String getSettingsValue(String name, String defaultValue, boolean createIfNotExist) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Setting> clientCriteriaQuery = criteriaBuilder.createQuery(Setting.class);
        Root<Setting> clientRoot = clientCriteriaQuery.from(Setting.class);
        Predicate predicate = criteriaBuilder.equal(clientRoot.get("name"), name);
        clientCriteriaQuery.where(predicate);

        Setting setting = uniqueResultOrNull(entityManager.createQuery(clientCriteriaQuery));

        if (setting == null && createIfNotExist) {
            setting = new Setting();
            setting.setName(name);
            setting.setValue(defaultValue);
            setting.setDescription(name);

            entityManagerProvider.get().persist(setting);
        }

        return setting == null ? defaultValue : setting.getValue();
    }

    public void saveToSettings(String name, String value) {
        saveToSettings(name, value, true);
    }

    public void saveToSettings(String name, String value, boolean overwrite) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Setting> clientCriteriaQuery = criteriaBuilder.createQuery(Setting.class);
        Root<Setting> clientRoot = clientCriteriaQuery.from(Setting.class);
        Predicate predicate = criteriaBuilder.equal(clientRoot.get("name"), name);
        clientCriteriaQuery.where(predicate);

        Setting setting = uniqueResultOrNull(entityManager.createQuery(clientCriteriaQuery));

        if (setting == null) {
            setting = new Setting();
            setting.setName(name);
            setting.setValue(value);
        } else if (overwrite) {
            setting.setValue(value);
        }

        entityManagerProvider.get().persist(setting);
    }

    public <T> List<T> getAllRecords(Class<T> tClass) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> clientCriteriaQuery = criteriaBuilder.createQuery(tClass);
        Root<T> clientRoot = clientCriteriaQuery.from(tClass);

        return resultsList(entityManager.createQuery(clientCriteriaQuery));
    }

    public <T> T saveObject(T obj) {
        entityManagerProvider.get().persist(obj);
        return obj;
    }

    public <T> T updateObject(T obj) {
        return entityManagerProvider.get().merge(obj);
    }
}
