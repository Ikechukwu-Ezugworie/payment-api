package extractors.impl;

import ninja.Context;
import ninja.params.ArgumentExtractor;
import org.apache.commons.io.IOUtils;

import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * Created by emmanuel on 2/2/17.
 */
public class ContentExtractor implements ArgumentExtractor<String> {
    @Override
    public String extract(Context context) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(context.getInputStream(), writer, Charset.forName("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
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
