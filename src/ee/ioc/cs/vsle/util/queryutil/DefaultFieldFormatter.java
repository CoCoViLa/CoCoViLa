package ee.ioc.cs.vsle.util.queryutil;

public class DefaultFieldFormatter implements FieldFormatter, java.io.Serializable {

    public static final String timestampFormatString="dd.MM.yyyy HH:mm:ss";
    public static final String dateFormatString="dd.MM.yyyy";
    public static final String timeFormatString="HH:mm:ss";
    public static final java.text.SimpleDateFormat timestampFormat=new java.text.SimpleDateFormat(timestampFormatString);
    public static final java.text.SimpleDateFormat dateFormat=new java.text.SimpleDateFormat(dateFormatString);
    public static final java.text.SimpleDateFormat timeFormat=new java.text.SimpleDateFormat(timeFormatString);
    public static final long serialVersionUID = -4134322799836540081L;

    public DefaultFieldFormatter() {
    }

    public static String formatTimestamp(java.util.Date d){
        String ret=null;
        if (d==null)return ret;
        synchronized(timestampFormat){
            ret=timestampFormat.format(d);
        }
        return ret;
    }

    public static String formatDate(java.util.Date d){
        String ret=null;
        if (d==null)return ret;
        synchronized(dateFormat){
            ret=dateFormat.format(d);
        }
        return ret;
    }

    public static String formatTime(java.util.Date d){
        String ret=null;
        if (d==null)return ret;
        synchronized(timeFormat){
            ret=timeFormat.format(d);
        }
        return ret;

    }
    public String asString(java.util.Date d){
        return formatDate(d);
    }

    public String asString(java.sql.Date d){
        return formatDate(d);
    }

    public String asString(java.sql.Timestamp d){
        return formatTimestamp(d);
    }

    public String asString(Object o){
        if (o==null)return null;
        if (java.sql.Timestamp.class.isAssignableFrom(o.getClass())){
            return asString((java.sql.Timestamp)o);
        }
        if (java.sql.Date.class.isAssignableFrom(o.getClass())){
            return asString((java.sql.Date)o);
        }
        if (java.util.Date.class.isAssignableFrom(o.getClass())){
            return asString((java.util.Date)o);
        }
        return o.toString();
    }
}