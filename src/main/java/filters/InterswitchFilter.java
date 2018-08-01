package filters;

import com.google.inject.Inject;
import dao.BaseDao;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.utils.NinjaProperties;
import org.apache.commons.lang3.StringUtils;
import utils.Constants;
import utils.ResponseUtil;

/**
 * CREATED BY GIBAH
 */
public class InterswitchFilter implements Filter {
    @Inject
    BaseDao baseDao;
    @Inject
    NinjaProperties ninjaProperties;

    @Override
    public Result filter(FilterChain filterChain, Context context) {

        if (ninjaProperties.isDev()) {
            return filterChain.next(context);
        }

        String requestIp = context.getRemoteAddr();

        if (StringUtils.isBlank(requestIp)) {
            return ResponseUtil.returnJsonResult(403, "Invalid ip");
        }

        String whitelist = baseDao.getSettingsValue(Constants.INTERSWITCH_IPS, "");

        if (StringUtils.isBlank(whitelist)) {
            return ResponseUtil.returnJsonResult(403, "No IP whitelisted");
        }

        if (ninjaProperties.isTest()) {
            if ("ALL".equalsIgnoreCase(whitelist)) {
                return filterChain.next(context);
            }
        }

        if (whitelist.contains(",")) {
            String[] ips = whitelist.split(",");
            for (String ip : ips) {
                if (requestIp.equalsIgnoreCase(ip)) {
                    return filterChain.next(context);
                }
            }
        } else {
            if (whitelist.equalsIgnoreCase(requestIp)) {
                return filterChain.next(context);
            }
        }

        return ResponseUtil.returnJsonResult(403, "Invalid ip");
    }
}
