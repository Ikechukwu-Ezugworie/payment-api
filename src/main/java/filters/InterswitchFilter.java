package filters;

import com.bw.payment.entity.Merchant;
import com.google.inject.Inject;
import dao.BaseDao;
import dao.MerchantDao;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import org.apache.commons.lang3.StringUtils;
import utils.Constants;
import utils.ResponseUtil;

/**
 * CREATED BY GIBAH
 */
public class InterswitchFilter implements Filter {
    @Inject
    BaseDao baseDao;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        String requestIp = context.getRemoteAddr();

        String whitelist = baseDao.getSettingsValue(Constants.INTERSWITCH_IPS, "");

        if (whitelist.contains(",")) {
            String[] ips = whitelist.split(",");
            for (String ip : ips) {
                if (requestIp.equalsIgnoreCase(ip)) {
                    return filterChain.next(context);
                }
            }
        }

        return ResponseUtil.returnJsonResult(403);
    }
}
