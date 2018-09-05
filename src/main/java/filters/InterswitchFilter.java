package filters;

import com.google.inject.Inject;
import dao.BaseDao;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.utils.NinjaProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.ResponseUtil;

/**
 * CREATED BY GIBAH
 */
public class InterswitchFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private BaseDao baseDao;
    @Inject
    private NinjaProperties ninjaProperties;

    @Override
    public Result filter(FilterChain filterChain, Context context) {

        String requestIp = "";
        if (ninjaProperties.isDev()) {
            logger.info("<== Dev mode detected. Allowing all IPs");
            return filterChain.next(context);
        } else if (ninjaProperties.isTest()) {
            requestIp = context.getRemoteAddr();
        } else {
            requestIp = context.getHeader("x-forwarded-for");
        }

        if (StringUtils.isBlank(requestIp)) {
            return ResponseUtil.returnJsonResult(403, "Cannot read request IP address");
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
                logger.info(String.format("<== Comparing request IP [%s] to whitelisted IP [%s]", requestIp, ip));

                if (requestIp.equalsIgnoreCase(ip)) {
                    logger.info(String.format("<== Allowing IP %s", requestIp));
                    return filterChain.next(context);
                }
            }
        } else {
            logger.info(String.format("<== Comparing request IP [%s] to whitelisted IP [%s]", requestIp, whitelist));
            if (whitelist.equalsIgnoreCase(requestIp)) {
                logger.info(String.format("<== Allowing IP %s", requestIp));
                return filterChain.next(context);
            }
        }

        return ResponseUtil.returnJsonResult(403, "Invalid ip");
    }
}
