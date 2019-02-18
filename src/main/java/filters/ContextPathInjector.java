package filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;

/*
 * Created by Gibah Joseph on Feb, 2019
 */

/**
 * Injects the context path into rendered html and sessions
 * Can be used to resolve assets.
 */
public class ContextPathInjector implements Filter {
    @Override
    public Result filter(FilterChain filterChain, Context context) {
        context.getSession().put("contextPath", context.getContextPath());
        Result result = filterChain.next(context);
        if (result.getContentType().contains("html")) {
            result.render("contextPath", context.getContextPath());
        }
        return result;
    }
}
