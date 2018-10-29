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
        context.getSession().put("contextPath", context.getContextPath());
        try {
            return filterChain.next(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Results.internalServerError().json().render(new ApiResponse<>("failed", "Oops! an unknown error occurred while processing your request", 500, null));
    }

//    private void sendException(Exception e, Context context) {
//        e.printStackTrace();
//        String[] adminEmails = ninjaProperties.get("app.admin.emails").split(",");
//
//        StringBuilder emailBody = new StringBuilder("");
//
//        emailBody.append("Time: ");
//        emailBody.append(DateUtil.formatForHttpHeader(new Date()));
//        emailBody.append("\n");
//        emailBody.append("Path: ");
//        emailBody.append(context.getRequestPath());
//        emailBody.append("\n");
//        emailBody.append("Error: ");
//        emailBody.append(e.getMessage());
//        emailBody.append("\n");
//
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(stringWriter);
//
//        e.printStackTrace(printWriter);
//
//        printWriter.flush();
//        printWriter.close();
//
//        emailBody.append("Stack Trace: ");
//        emailBody.append("\n");
//        emailBody.append(stringWriter.toString());
//        emailBody.append("\n");
//
//        eMailService.sendMailAsync("ERROR FROM : " + ninjaProperties.get("application.name"), "error@byteworks.com.ng", emailBody.toString(), EMailService.MIME_TYPE_HTML, Arrays.asList(adminEmails));
//
//    }
}
