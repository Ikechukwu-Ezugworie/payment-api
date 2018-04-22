package filters;

import com.bw.payment.entity.Merchant;
import com.google.inject.Inject;
import dao.MerchantDao;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import org.apache.commons.lang3.StringUtils;
import utils.GeneralConstants;
import utils.ResponseUtil;

/**
 * CREATED BY GIBAH
 */
public class MerchantFilter implements Filter {
    @Inject
    MerchantDao merchantDao;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        String merchantIdentifier = context.getHeader(GeneralConstants.MERCHANT_IDENTIFIER_HEADER);
        if (StringUtils.isBlank(merchantIdentifier)) {
            return ResponseUtil.returnJsonResult(400, "Missing merchant identifier header");
        }

        Merchant merchant = merchantDao.getUniqueRecordByProperty(Merchant.class, "identifier", merchantIdentifier);
        if (merchant == null) {
            return ResponseUtil.returnJsonResult(Result.SC_401_UNAUTHORIZED, "Merchant not found");
        }

        context.setAttribute(GeneralConstants.MERCHANT_CONTEXT_KEY, merchant);

        return filterChain.next(context);
    }
}
