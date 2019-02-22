package dao;

import com.bw.payment.entity.PaymentTransaction;
import com.bw.payment.entity.RawDump;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/*
 * Created by Gibah Joseph on Nov, 2018
 */
public class RawDumpDao extends BaseDao {
    public RawDump findLastByDescription(String description) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RawDump> clientCriteriaQuery = criteriaBuilder.createQuery(RawDump.class);
        Root<RawDump> clientRoot = clientCriteriaQuery.from(RawDump.class);
        Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.equal(clientRoot.get("description"), description)
        );
        clientCriteriaQuery.where(predicate).orderBy(criteriaBuilder.desc(clientRoot.get("dateCreated")));
        return uniqueResultOrNull(entityManager.createQuery(clientCriteriaQuery).setMaxResults(1));
    }

    public List<RawDump> getAllByPaymentTransaction(PaymentTransaction paymentTransaction) {
        EntityManager entityManager = entityManagerProvider.get();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RawDump> clientCriteriaQuery = criteriaBuilder.createQuery(RawDump.class);
        Root<RawDump> clientRoot = clientCriteriaQuery.from(RawDump.class);
        Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.equal(clientRoot.get("paymentTransaction"), paymentTransaction)
        );
        clientCriteriaQuery.where(predicate).orderBy(criteriaBuilder.desc(clientRoot.get("dateCreated")));
        return resultsList(entityManager.createQuery(clientCriteriaQuery));
    }
}
