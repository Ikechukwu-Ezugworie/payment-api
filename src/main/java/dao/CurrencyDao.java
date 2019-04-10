package dao;

import com.bw.payment.entity.Currency;
import com.bw.payment.enumeration.GenericStatusConstant;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.utils.NinjaProperties;

import javax.persistence.Query;

/**
 * CREATED BY GIBAH
 */
@Singleton
public class CurrencyDao extends BaseDao {
    @Inject
    private NinjaProperties ninjaProperties;

    public Currency findByCode(String code, GenericStatusConstant status) {
        Query q = entityManagerProvider.get().createQuery("select x from Currency x where x.code=:code and x.status=:status");
        q.setParameter("code", code).setParameter("status", status.getValue());

        return uniqueResultOrNull(q, Currency.class);
    }
}
