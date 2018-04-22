package utils;

import ninja.Context;
import ninja.i18n.Messages;
import ninja.validation.Validation;

public class ValidationUtils {
    public static String getFirstViolationMessage(Context context, Messages msg, Validation validation, Object... objects) {
        String messageKey = validation.getViolations().iterator().next().getDefaultMessage();
        String fieldKey = validation.getViolations().iterator().next().getFieldKey();

        return LocalizationUtils.getLocalizedMessage(messageKey, context, msg, fieldKey, objects);
    }
}
