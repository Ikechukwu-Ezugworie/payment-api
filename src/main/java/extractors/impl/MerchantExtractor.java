package extractors.impl;

import com.bw.payment.entity.Merchant;
import ninja.Context;
import ninja.params.ArgumentExtractor;
import utils.GeneralConstants;

public class MerchantExtractor implements ArgumentExtractor<Merchant> {

    @Override
    public Merchant extract(Context context) {

        return (Merchant) context.getAttribute(GeneralConstants.MERCHANT_CONTEXT_KEY);

    }

    @Override
    public Class<Merchant> getExtractedType() {
        return Merchant.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }
}
