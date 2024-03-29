package filters;

import com.bw.payment.entity.Merchant;
import com.google.inject.Inject;
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
public class MerchantFilter implements Filter {
    @Inject
    MerchantDao merchantDao;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        String merchantIdentifier = context.getHeader(Constants.MERCHANT_CODE_HEADER);
        if (StringUtils.isBlank(merchantIdentifier)) {
            return ResponseUtil.returnJsonResult(400, "Missing merchant code header");
        }

        System.out.println("<=== merchant code = "+ merchantIdentifier);

        Merchant merchant = merchantDao.getMerchantByCode(merchantIdentifier);
        if (merchant == null) {
            return ResponseUtil.returnJsonResult(Result.SC_401_UNAUTHORIZED, "Merchant not found");
        }

        context.setAttribute(Constants.MERCHANT_CONTEXT_KEY, merchant);

        return filterChain.next(context);
    }
}
