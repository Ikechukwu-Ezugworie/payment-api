package extractors;

import extractors.impl.ContentExtractor;
import ninja.params.WithArgumentExtractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by emmanuel on 2/2/17.
 */
@WithArgumentExtractor(ContentExtractor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ContentExtract {
}
