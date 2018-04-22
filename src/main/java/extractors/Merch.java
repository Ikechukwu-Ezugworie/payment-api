package extractors;

import ninja.params.WithArgumentExtractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Agwasim Emmanuel
 * <p>
 * on 28/11/17.
 */
@WithArgumentExtractor(extractors.impl.MerchantExtractor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Merch {
}
