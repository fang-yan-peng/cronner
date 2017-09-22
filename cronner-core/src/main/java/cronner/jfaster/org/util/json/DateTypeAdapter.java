package cronner.jfaster.org.util.json;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author fangyanpeng
 */
public class DateTypeAdapter extends TypeAdapter<Date> {

    private static final String SIMPLE_NAME = "CostumeDateTypeAdapter";

    private final Class<? extends Date> dateType;
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;

    DateTypeAdapter(Class<? extends Date> dateType) {
        this(dateType,
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US),
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT));
    }

    DateTypeAdapter(Class<? extends Date> dateType, String datePattern) {
        this(dateType, new SimpleDateFormat(datePattern, Locale.US), new SimpleDateFormat(datePattern));
    }

    DateTypeAdapter(Class<? extends Date> dateType, int style) {
        this(dateType, DateFormat.getDateInstance(style, Locale.US), DateFormat.getDateInstance(style));
    }

    public DateTypeAdapter(int dateStyle, int timeStyle) {
        this(Date.class,
                DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US),
                DateFormat.getDateTimeInstance(dateStyle, timeStyle));
    }

    public DateTypeAdapter(Class<? extends Date> dateType, int dateStyle, int timeStyle) {
        this(dateType, DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US),
                DateFormat.getDateTimeInstance(dateStyle, timeStyle));
    }

    DateTypeAdapter(final Class<? extends Date> dateType, DateFormat enUsFormat, DateFormat localFormat) {
        if ( dateType != Date.class && dateType != java.sql.Date.class && dateType != Timestamp.class ) {
            throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
        }
        this.dateType = dateType;
        this.enUsFormat = enUsFormat;
        this.localFormat = localFormat;
    }

    // These methods need to be synchronized since JDK DateFormat classes are not thread-safe
    // See issue 162
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        synchronized (localFormat) {
            String dateFormatAsString = "0";
            if(value != null){
                dateFormatAsString = enUsFormat.format(value);
            }
            out.value(dateFormatAsString);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.STRING) {
            throw new JsonParseException("The date should be a string value");
        }
        Date date = deserializeToDate(in.nextString());
        if (dateType == Date.class) {
            return date;
        } else if (dateType == Timestamp.class) {
            return new Timestamp(date.getTime());
        } else if (dateType == java.sql.Date.class) {
            return new java.sql.Date(date.getTime());
        } else {
            // This must never happen: dateType is guarded in the primary constructor
            throw new AssertionError();
        }
    }

    private Date deserializeToDate(String s) {
        synchronized (localFormat) {
            try {
                return localFormat.parse(s);
            } catch (ParseException ignored) {}
            try {
                return enUsFormat.parse(s);
            } catch (ParseException ignored) {}
            try {
                if("0".equals(s)){
                    return null;
                }
                return ISO8601Utils.parse(s, new ParsePosition(0));
            } catch (ParseException e) {
                throw new JsonSyntaxException(s, e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SIMPLE_NAME);
        sb.append('(').append(localFormat.getClass().getSimpleName()).append(')');
        return sb.toString();
    }

}
