package filters;

import com.google.inject.Inject;
import ninja.*;
import ninja.utils.NinjaProperties;
import pojo.ApiResponse;

public class ExceptionFilter implements Filter {
    private NinjaProperties ninjaProperties;

    @Inject
    public ExceptionFilter(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        try {
            return filterChain.next(context);
        } catch (Exception e) {
            e.printStackTrace();
            return Results.internalServerError().json().render(new ApiResponse<>("failed", e.getMessage(), 500, null));
        }
    }
}
