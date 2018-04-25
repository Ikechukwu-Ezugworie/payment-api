package services;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import dao.BaseDao;
import utils.Constants;

/**
 * CREATED BY GIBAH
 */
public class SetupService {
    private BaseDao baseDao;

    @Inject
    public SetupService(BaseDao baseDao) {
        this.baseDao = baseDao;
    }

    public void setUp() {
        createInterswitchWhitelist();
    }

    @Transactional
    private void createInterswitchWhitelist() {
        String whitelist = "";
        baseDao.saveToSettings(Constants.INTERSWITCH_IPS, whitelist, false);
    }
}
