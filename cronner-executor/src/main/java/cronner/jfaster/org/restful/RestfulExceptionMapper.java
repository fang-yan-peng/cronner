package cronner.jfaster.org.restful;


import cronner.jfaster.org.exeception.ExceptionUtil;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * RESTFul API的异常处理器.
 *
 * @author fangyanpeng
 */
@Provider
public final class RestfulExceptionMapper implements ExceptionMapper<Throwable> {
    
    @Override
    public Response toResponse(final Throwable cause) {
        return Response.ok(ExceptionUtil.transform(cause), MediaType.TEXT_PLAIN).status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
