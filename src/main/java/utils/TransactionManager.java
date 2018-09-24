/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.bw.payment.HibernateUtils;
import com.google.inject.Singleton;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Olaleye Afolabi <oafolabi@byteworks.com.ng>
 */
@Singleton
public class TransactionManager {
    final static Logger logger = LoggerFactory.getLogger(TransactionManager.class);


    public void doInTransaction(TransactionalOperation operation) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtils.getSessionFactory().openSession();
            tx = session.beginTransaction();
            operation.execute(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            if (session != null) {
                logger.info("<=== Session is connected: "+session.isConnected());
                session.close();
                logger.info("<=== Session is connected: "+session.isConnected());
            }
        }
    }

    public <E> E doForResult(TransactionalOperationForResult<E> operation) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtils.getSessionFactory().openSession();
            tx = session.beginTransaction();
            E e = operation.execute(session);
            tx.commit();
            return e;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            if (session != null) {
                logger.info("<=== Session is connected: "+session.isConnected());
                session.close();
                logger.info("<=== Session is connected: "+session.isConnected());
            }
        }
    }

    public static interface TransactionalOperation {

        void execute(Session session) throws Exception;
    }

    public static interface TransactionalOperationForResult<E> {

        E execute(Session session) throws Exception;
    }
}
