package constraints;

import com.bw.payment.enumeration.PaymentProviderConstant;

import javax.validation.*;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface PaymentProvider {

    String message() default "Enter a valid payment provider";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] regex() default {""};

    public class Validator implements ConstraintValidator<PaymentProvider, String> {

        String[] regexFormat = {""};

        @Override
        public void initialize(PaymentProvider constraintAnnotation) {
            regexFormat = constraintAnnotation.regex();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null)
                return true;
            try {
                PaymentProviderConstant.fromValue(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
