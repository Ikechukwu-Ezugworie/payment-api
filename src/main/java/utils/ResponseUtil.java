package utils;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ApiResponse;

public class ResponseUtil {
    final static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    public static Result returnJsonResult(int code, String message) {
        return returnJsonResult(code, message, null);
    }

    public static Result returnLocalisedJsonResult(int code, String messageKey, Messages messages, Context context) {
        return returnJsonResult(code, LocalizationUtils.getLocalizedMessage(messageKey, context, messages), null);
    }

    public static Result returnJsonResult(int code) {
        return returnJsonResult(code, null, null);
    }

    public static Result returnJsonResult(int code, Object data) {
        return returnJsonResult(code, null, data);
    }

    public static Result returnJsonResult(int code, String message, Object data) {
        ApiResponse response = new ApiResponse();
        response.setMessage(message);
        response.setCode(code);
        response.setData(data);

        return Results.status(code).json().render(response);
    }

    public static Result returnRaw(int code, Object data) {
        return Results.status(code).json().render(data);
    }
}
