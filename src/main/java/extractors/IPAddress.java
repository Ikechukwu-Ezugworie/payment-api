package extractors;

import extractors.impl.IPAddressExtractorImpl;
import ninja.params.WithArgumentExtractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Gibah on 12/1/2017.
 */
@WithArgumentExtractor(IPAddressExtractorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface IPAddress {
}
