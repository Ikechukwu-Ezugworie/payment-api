package utils;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;

import java.util.Optional;

public class LocalizationUtils {
    public static String getLocalizedMessage(String key, Context context, Messages messages) {
        Optional<String> language = Optional.of("en");
        Optional<Result> optResult = Optional.empty();
        Optional<String> result = messages.get(key, context, optResult);

        if (result.isPresent()) {
            return result.get();
        } else {
            result = messages.get(key, language);

            if (result.isPresent()) {
                return result.get();
            }
        }

        return key;

    }

    public static String getLocalizedMessage(String key, Context context, Messages messages, Object... objects) {
        Optional<String> language = Optional.of("en");
        Optional<Result> optResult = Optional.empty();
        Optional<String> result = messages.get(key, context, optResult, objects);

        if (result.isPresent()) {
            return result.get();
        } else {
            result = messages.get(key, language, objects);
            if (result.isPresent()) {
                return result.get();
            }
        }
        return key;
    }

    public static String getLocalizedMessage(String key, String defaultValue, Context context, Messages messages) {
        Optional<Result> optResult = Optional.empty();
        return messages.getWithDefault(key, defaultValue, context, optResult);
    }

    public static String getLocalizedMessage(String key, String defaultValue, Context context, Messages messages, Object... objects) {
        Optional<Result> optResult = Optional.empty();
        return messages.getWithDefault(key, defaultValue, context, optResult, objects);
    }
}
