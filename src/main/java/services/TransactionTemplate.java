package services;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import dao.BaseDao;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;

/*
 * Created by Gibah Joseph on Feb, 2019
 */
public class TransactionTemplate extends BaseDao {

    @Inject
    protected Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void execute(TransactionalOperation operation) {
        operation.execute(entityManagerProvider.get());
    }

    @Transactional
    public <E> E execute(TransactionalOperationForResult<E> operation) {
        return operation.execute(entityManagerProvider.get());
    }

    public static interface TransactionalOperation {

        void execute(EntityManager entityManager);
    }

    public static interface TransactionalOperationForResult<E> {

        E execute(EntityManager entityManager);
    }
}
