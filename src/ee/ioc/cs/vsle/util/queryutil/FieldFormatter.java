package ee.ioc.cs.vsle.util.queryutil;

/**
 * Title: Field Formatter.
 * Description: DBResult field formatter. Can also be used for formatting fields of other components.
 * Copyright:    Copyright (c) 2004
 * @author Aulo Aasmaa
 * @version 1.0
 */
public interface FieldFormatter extends java.io.Serializable {
    public String asString(Object o);
    public static final long serialVersionUID = -4134322799836540081L;
}