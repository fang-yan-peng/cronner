package cronner.jfaster.org.restful;

import cronner.jfaster.org.util.json.GsonFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * 基于GSON解析JSON的解析器.
 *
 * @author fangyanpeng
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class GsonJsonProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {
    
    private static final String UTF_8 = "UTF-8";
    
    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations,
                           final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) {
        try (InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8)) {
            return GsonFactory.getGson().fromJson(streamReader, type.equals(genericType) ? type : genericType);
        } catch (final IOException ex) {
            throw new RestfulException(ex);
        }
    }
    
    @Override
    public void writeTo(final Object object, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8)) {
            GsonFactory.getGson().toJson(object, type.equals(genericType) ? type : genericType, writer);
        } catch (final IOException ex) {
            throw new RestfulException(ex);
        }
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }
    
    @Override
    public long getSize(final Object object, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1;
    }
}
