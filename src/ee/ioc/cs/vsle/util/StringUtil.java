package ee.ioc.cs.vsle.util;

import java.util.Collection;

/**
 * Title: String Utility class.
 * Description: The String Utility class used by the DBResult.
 * Copyright: Copyright (c) 2004
 * @author Aulo Aasmaa
 * @version 1.0
 */

public class StringUtil {

    public static final String timestampFormatString = "dd.MM.yyyy HH:mm:ss";
    public static final String timestampFormatStringForSQL = "dd.MM.yyyy hh24:mi:ss";
    public static final String dateFormatString = "dd.MM.yyyy";
    public static final String dateFormatStringForSQL = "dd.MM.yyyy";
    public static final String timeFormatString = "HH:mm:ss";
    public static final String timeFormatStringForSQL = "HH24:MI:SS";
    public static final java.text.SimpleDateFormat timestampFormat = new java.text.SimpleDateFormat(timestampFormatString);
    public static final java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(dateFormatString);
    public static final java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat(timeFormatString);

    public static String formatTimestamp(java.util.Date d) {
        if (d == null) return "null";
        String ret = null;
        synchronized (timestampFormat) {
            ret = timestampFormat.format(d);
        }
        return ret;
    } // formatTimestamp

    public static String formatDate(java.util.Date d) {
        if (d == null) return "null";
        String ret = null;
        synchronized (dateFormat) {
            ret = dateFormat.format(d);
        }
        return ret;
    } // formatDate

    public static String formatTime(java.util.Date d) {
        if (d == null) return "null";
        String ret = null;
        synchronized (timeFormat) {
            ret = timeFormat.format(d);
        }
        return ret;
    } // formatTime

    public static String replace(String src, String token, String newToken) {
        if (token == null || token.equals("") || newToken == null || src == null) return src;
        StringBuffer sb = new StringBuffer();
        int pos = 0;
        int lastPos = 0;
        while ((pos = src.indexOf(token, pos)) >= 0) {
            sb.append(src.substring(lastPos, pos));
            sb.append(newToken);
            lastPos = pos + token.length();
            pos += token.length();
        }
        sb.append(src.substring(lastPos));
        return sb.toString();
    } // replace

    public static String replace(Object src, Object token, Object newToken) {
        return replace("" + src, "" + token, "" + newToken);
    } // replace

    public static String xmlTypeString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    } // xmlTypeString

    public static String asXML(String s) {
        return replace(replace(replace(replace(s, "&", "&amp;"), "<", "&lt;"), ">", "&gt;"), "\"", "&quot;");
    } // asXML

    public static String asUTF(String s) {
        return replace(replace(replace(replace(s, "�", "&amp;"), "�", "&lt;"), ">", "&gt;"), "\"", "&quot;");
    } // asUTF

    public static String asSQL(Object o) {
        if (o == null) return "null";
        if (String.class.isAssignableFrom(o.getClass())) {
            return asSQL((String) o);
        } else if (java.sql.Timestamp.class.isAssignableFrom(o.getClass())) {
            return asSQL((java.sql.Timestamp) o);
        } else if (java.sql.Time.class.isAssignableFrom(o.getClass())) {
            return asSQL((java.sql.Time) o);
        } else if (java.sql.Date.class.isAssignableFrom(o.getClass())) {
            return asSQL((java.sql.Date) o);
        } else if (java.util.Date.class.isAssignableFrom(o.getClass())) {
            return asSQL((java.util.Date) o);
        } else if (java.math.BigDecimal.class.isAssignableFrom(o.getClass())) {
            return asSQL((java.math.BigDecimal) o);
        } else if (Boolean.class.isAssignableFrom(o.getClass())) {
            return asSQL((Boolean) o);
        } else if (Integer.class.isAssignableFrom(o.getClass())) {
            return asSQL((Integer) o);
        } else if (Double.class.isAssignableFrom(o.getClass())) {
            return asSQL((Double) o);
        } else if (java.util.Collection.class.isAssignableFrom(o.getClass())) {
            return asSQL((Collection) o);
        } else if (Object[].class.isAssignableFrom(o.getClass())) {
            return asSQL((Object[]) o);
        } else if (Character.class.isAssignableFrom(o.getClass())) {
            return asSQL(o.toString());
        }
        return o.toString();
    } // asSQL

    public static String asSQL(String s, int length) {
        if (s == null) return "null";
        //kui ainult tyhikud, siis ei trimmita
        if(s.trim().length()>0)s=s.trim();
        if (length > 0 && s.length() > length) s = s.substring(0, length);
        s = replace(s, "'", "''");
        return "'" + s + "'";
    } // asSQL

    public static String asSQL(String s) {
        return asSQL(s, -1);
    } // asSQL

    public static String asSQL(java.sql.Timestamp ts) {
        if (ts == null) return "null";
        String ret = "to_date('" + formatTimestamp(ts) + "','" + timestampFormatStringForSQL + "')";
        return ret;
    } // asSQL

    public static String asSQL(java.sql.Time t) {
        if (t == null) return "null";
        String ret = "to_date('" + formatTime(t) + "','" + timeFormatStringForSQL + "')";
        return ret;
    } // asSQL

    public static String asSQL(java.sql.Date d) {
        if (d == null) return "null";
        //String ret = "to_date('" + formatDate(d) + "','" + dateFormatString + "')";
        String ret = "to_date('" + formatDate(d) + "','" + dateFormatStringForSQL + "')";
        return ret;
    } // asSQL

    public static String asSQL_Tms(java.util.Date d) {
        if (d == null) return "null";
        String ret = "to_date('" + formatTimestamp(d) + "','" + timestampFormatStringForSQL + "')";
        return ret;
    } // asSQL

    public static String asSQL(java.util.Date d) {
        if (d == null) return "null";
        return asSQL(new java.sql.Date(d.getTime()));
    } // asSQL

    public static String asSQL(java.math.BigDecimal bd) {
        if (bd == null) return "null";
        return bd.toString();
    } // asSQL

    public static String asSQL(Boolean b) {
        if (b == null) return "null";
        return b.toString();
    } // asSQL

    public static String asSQL(Integer i) {
        if (i == null) return "null";
        return i.toString().trim();
    } // asSQL

    public static String asSQL(Double d) {
        if (d == null) return "null";
        return asSQL(new java.math.BigDecimal(d.doubleValue()));
    } // asSQL

    public static String asSQL(Collection c) {
        if (c == null || c.size() == 0) return null;
        return asSQL(c.toArray());
    } // asSQL

    public static String asSQL(Object[] array) {
        StringBuffer sb = new StringBuffer();
        if (array == null || array.length == 0) return null;
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(asSQL(array[i]));
        }
        return sb.toString();
    } // asSQL

    public static String asSybaseDate(java.util.Date d) {
        if (d == null) return "null";
        String ret = "DATE('" + formatDate(d) + "')";
        return ret;
    } // asSybaseDate

    /** Returns string right-padded with given character to length specified by
     * parameter length. If input string is longer than length, it is not truncated.
     * @param source -
     * @param length -
     * @param padCh  -
     * @return String -
     */
    public static String rightPadString(String source, int length, char padCh) {
        if (source.length() >= length) return source;
        StringBuffer sb = new StringBuffer(source);
        for (int i = 0; i < (length - source.length()); i++) sb.append(padCh);
        return sb.toString();
    } // rightPadString

    /** Returns string left-padded with given character to length specified by
     * parameter length. If input string is longer than length, it is not truncated.
     * @param source -
     * @param length -
     * @param padCh -
     * @return String -
     */
    public static String leftPadString(String source, int length, char padCh) {
        if (source.length() >= length) return source;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < (length - source.length()); i++) sb.append(padCh);
        sb.append(source);
        return sb.toString();
    } // leftPadString

	public static Object getNoString(final String s) {
		return new Object() {
			public String toString() {
				return s;
			}
		};
	} // getNoString

	public static void main(String[] args) {
		java.util.Date date=new java.sql.Timestamp(System.currentTimeMillis());
		System.out.println(asSQL(date));
	} // main

}