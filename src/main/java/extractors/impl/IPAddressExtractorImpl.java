package extractors.impl;

import ninja.Context;
import ninja.params.ArgumentExtractor;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gibah on 12/1/2017.
 */
public class IPAddressExtractorImpl implements ArgumentExtractor<String> {

    @Override
    public String extract(Context context) {
        String ip = context.getHeader("x-forwarded-for");

        if (StringUtils.isBlank(ip)) {
            ip = context.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public Class<String> getExtractedType() {
        return String.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }
}
