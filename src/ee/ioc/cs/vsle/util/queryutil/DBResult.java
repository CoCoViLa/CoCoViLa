package ee.ioc.cs.vsle.util.queryutil;

import java.util.*;
import java.sql.*;
import java.math.*;
import ee.ioc.cs.vsle.util.StringUtil;
import java.io.*;

/**
 * Title: DBResult.
 * Description: Database result, simulating real database table, enabling various SQL-like functionality,
 *               sorting, adding and removing fields, rows, columns and including dbresults in each other.
 * Copyright:    Copyright (c) 2004
 * @author Aulo Aasmaa
 * @version 1.0
 */
public class DBResult extends Object implements java.io.Serializable{

    /**
     * SerialVersionUID of a current class.
     * Execute the main method for getting
     * the SerialVersionUID
     */
    public static final long serialVersionUID = -4134322799836540081L;

    //Default field formatter. Kui ei ole spetsifitseeritud
    //teisiti, kasutatakse seda.
    private FieldFormatter defFieldFormatter=new DefaultFieldFormatter();

    private FieldFormatter customFieldFormatter=null;
    private Hashtable columnToFormatter=new Hashtable();
    private int[][] indexes=null;

    /** Order type
     */
    public static final int ASC=1;
    /** Order type
     */
    public static final int DESC=-1;

    /**
     * Comparision type constants
     */

    /**
     * EQUALS
     */
    public static final int EQUAL=3;
    /**
     * Greater than
     */
    public static final int GT=4;
    /**
     * Less than
     */
    public static final int LT=5;
    /**
     * Greater than or equal
     */
    public static final int GT_OR_EQUAL=6;
    /**
     * Less than or equal
     */
    public static final int LT_OR_EQUAL=7;
    /**
     * Not equal
     */
    public static final int NOT_EQUAL=8;

    //raporti nimi
    String DBResultName=null;

    //read
    ArrayList rows=new ArrayList();
    //tulpade nimed->tulba index
    Hashtable nameToIndex=new Hashtable();
    //tulpade nimed
    String[] columns=null;
    //raporti atribuudid
    Hashtable attributes=new Hashtable();

    /** Creates new DBResult
     * @param s -
     */
    public DBResult(String s){
        this(s,new String[0]);
    }

    public DBResult(String s,ResultSet rs)throws Exception{
        this(s,new String[0]);
        ResultSetMetaData rsmd=rs.getMetaData();
        int columnCount=rsmd.getColumnCount();
        for (int i=1;i<=columnCount;i++){
            addColumn(rsmd.getColumnName(i));
        }
        while (rs.next()){
            Object[] row=new Object[columnCount];
            for (int i=1;i<=columnCount;i++){
                row[i-1]=rs.getObject(i);
            }
            appendRow(row);
        }
    }

    /**
     * Returns SerialVersionUID of the class
     * @return long
     */
    public static long getSerialVersionUID() {
        long serVerUID = 0;
        try {
            ObjectStreamClass myObj = ObjectStreamClass.lookup(Class.forName("box.queryutil.DBResult"));
            serVerUID = myObj.getSerialVersionUID();
        } catch (ClassNotFoundException e) { }
        return serVerUID;
    }

    public DBResult(String s,Vector columns) {
        String[] ss=new String[columns.size()];
        for (int i=0;i<columns.size();i++){
            ss[i]=(String)columns.get(i);
        }
        DBResultName=s;
        this.columns=ss;
        for (int i=0;i<this.columns.length;i++){
            Integer indx=new Integer(i+1);
            nameToIndex.put(this.columns[i],indx);
            columnToFormatter.put(indx,defFieldFormatter);
        }
    }

    public DBResult(String s,String[] columns){
        DBResultName=s;
        this.columns=columns;
        for (int i=0;i<columns.length;i++){
            nameToIndex.put(columns[i],new Integer(i+1));
            columnToFormatter.put(new Integer(i+1),defFieldFormatter);
        }
    }

    public boolean isIndexed(int column)throws ArrayIndexOutOfBoundsException{
        if (column<1||column>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        if (indexes!=null&&indexes[column-1]!=null)
            return true;
        return false;
    }

    public boolean isIndexed(String columnName)throws ArrayIndexOutOfBoundsException{
        return isIndexed(getColumnIndex(columnName));
    }

    public void addIndex(int column)throws ArrayIndexOutOfBoundsException{
        if (column<1||column>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        if (indexes==null)
            indexes=new int[getColumnCount()][0];
        indexes[column-1]=new int[getRowCount()];
    }

    public void addIndex(String columnName)throws ArrayIndexOutOfBoundsException{
        addIndex(getColumnIndex(columnName));

    }

    public void setAttribute(String key, Object value){
        attributes.put(key,value);
    }

    public void removeAttribute(String key){
        attributes.remove(key);
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public Enumeration getAttributeKeys(){
        return attributes.keys();
    }

    public void removeAllAttributes(){
        attributes=new Hashtable();
    }

    public String getDBResultName(){
        return DBResultName;
    }

    public void setDBResultName(String s){
        DBResultName=s;
    }

    public BigDecimal sum(int col){
        BigDecimal sum=new BigDecimal("0.0");
        for (int i=1;i<=getRowCount();i++){
            BigDecimal bd=getFieldAsBigDecimal(col,i);
            if (bd!=null){
                sum=sum.add(bd);
            }
        }
        return sum;
    }

    public BigDecimal sum(String columnName)throws ArrayIndexOutOfBoundsException{
        return sum(getColumnIndex(columnName));
    }

  /** Tagastab fieldi väärtuse objektina
   * @param col tulba index (1..n)
   * @param row rea index (1..n)
   * @return Object
   */
    public Object getFieldAsObject(int col, int row){
        return ((Object[])rows.get(row-1))[col-1];
    }

    /** Tagastab fieldi objectina
     * @param columnName -
     * @param row rea indeks (1..n)
     * @return Object
     * @throws ArrayIndexOutOfBoundsException
     */
    public Object getFieldAsObject(String columnName, int row)throws ArrayIndexOutOfBoundsException{
        return getFieldAsObject(getColumnIndex(columnName),row);
    }

    public BigDecimal getFieldAsBigDecimal(String columnName,int row)throws ArrayIndexOutOfBoundsException{
        return (BigDecimal)getFieldAsObject(columnName,row);
    }

    public BigDecimal getFieldAsBigDecimal(int col,int row)throws ArrayIndexOutOfBoundsException{
        return (BigDecimal)getFieldAsObject(col,row);
    }

    public java.util.Date getFieldAsDate(String columnName,int row)throws ArrayIndexOutOfBoundsException{
        return (java.util.Date)getFieldAsObject(columnName,row);
    }

    public java.util.Date getFieldAsDate(int col,int row)throws ArrayIndexOutOfBoundsException{
        return (java.util.Date)getFieldAsObject(col,row);
    }

    public java.sql.Timestamp getFieldAsTimestamp(String columnName,int row)throws ArrayIndexOutOfBoundsException{
        return (java.sql.Timestamp)getFieldAsObject(columnName,row);
    }

    public java.sql.Timestamp getFieldAsTimestamp(int col,int row)throws ArrayIndexOutOfBoundsException{
        return (java.sql.Timestamp)getFieldAsObject(col,row);
    }

    public java.sql.Time getFieldAsTime(String columnName,int row)throws ArrayIndexOutOfBoundsException{
        return (java.sql.Time)getFieldAsObject(columnName,row);
    }

    public java.sql.Time getFieldAsTime(int col,int row)throws ArrayIndexOutOfBoundsException{
        return (java.sql.Time)getFieldAsObject(col,row);
    }

    public String getFieldAsString(String columnName,int row)throws ArrayIndexOutOfBoundsException{
        return (String)getFieldAsObject(columnName,row);
    }

    public String getFieldAsString(int col,int row)throws ArrayIndexOutOfBoundsException{
        return (String)getFieldAsObject(col,row);
    }

    /** Tagastab fieldi väärtuse stringina. Kui väärtus on null, siis tagastab ka null-i
     * @param col tulba index (1..n)
     * @param row rea index (1..n)
     * @return String
     */
    public String getField(int col,int row){
        return ((FieldFormatter)columnToFormatter.get(new Integer(col))).asString(getFieldAsObject(col,row));
    }

    /** Tagastab fieldi stringina. Kui v��rtus on null, siis ka tagastab nulli.
     * @param columnName fieldi nimi
     * @param row rea index (1..n)
     * @return String
     */
    public String getField(String columnName,int row){
        return getField(getColumnIndex(columnName),row);
    }

    private DBResult select(int column,int compType, Comparable compValue, boolean asString) throws ArrayIndexOutOfBoundsException{
        if (column<1||column>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        DBResult ret=new DBResult(DBResultName);
        ret.nameToIndex=(Hashtable)nameToIndex.clone();
        ret.columns=(String[])columns.clone();
        ret.attributes=(Hashtable)attributes.clone();
        ret.defFieldFormatter=defFieldFormatter;
        ret.customFieldFormatter=customFieldFormatter;
        ret.columnToFormatter=columnToFormatter;
        FieldComparator fc=new FieldComparator(ASC);
        for (int i=1;i<=this.getRowCount();i++){
            Comparable field;
            if (asString){
                field=defFieldFormatter.asString(this.getFieldAsObject(column,i));
            }else{
                field=(Comparable)this.getFieldAsObject(column,i);
            }
            int compResult=fc.compare(field,compValue);
            switch(compType){
                case EQUAL:{
                    if (compResult==0){
                        ret.rows.add(rows.get(i-1));
                    }
                    break;
                }
                case GT:{
                    if (compResult>0){
                        ret.rows.add(rows.get(i-1));
                    }
                    break;
                }
                case LT:{
                    if (compResult<0){
                        ret.rows.add(rows.get(i-1));
                    }
                    break;
                }
                case GT_OR_EQUAL:{
                    if (compResult>=0){
                        ret.rows.add(rows.get(i-1));
                    }
                    break;
                }
                case LT_OR_EQUAL:{
                    if (compResult<=0){
                        ret.rows.add(rows.get(i-1));
                    }
                    break;
                }
                case NOT_EQUAL:{
                    if (compResult!=0){
                        ret.rows.add(rows.get(i-1));
                    }
                }
            }//endSwitch
        }//end for
        return ret;
    }

    private DBResult remove(int column,int compType, Comparable compValue, boolean asString) throws ArrayIndexOutOfBoundsException{
        if (column<1||column>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        DBResult ret=new DBResult(DBResultName);
        ret.nameToIndex=(Hashtable)nameToIndex.clone();
        ret.columns=(String[])columns.clone();
        ret.attributes=(Hashtable)attributes.clone();
        ret.defFieldFormatter=defFieldFormatter;
        ret.customFieldFormatter=customFieldFormatter;
        ret.columnToFormatter=columnToFormatter;
        FieldComparator fc=new FieldComparator(ASC);
        for (int i=1;i<=this.getRowCount();i++){
            Comparable field;
            if (asString){
                field=defFieldFormatter.asString(this.getFieldAsObject(column,i));
            }else{
                field=(Comparable)this.getFieldAsObject(column,i);
            }
            int compResult=fc.compare(field,compValue);
            switch(compType){
                case EQUAL:{
                    if (compResult==0){
                        ret.rows.add(rows.get(i-1));
                        removeRow(i);
                        i--;
                    }
                    break;
                }
                case GT:{
                    if (compResult>0){
                        ret.rows.add(rows.get(i-1));
                        removeRow(i);
                        i--;
                    }
                    break;
                }
                case LT:{
                    if (compResult<0){
                        ret.rows.add(rows.get(i-1));
                        removeRow(i);
                        i--;
                    }
                    break;
                }
                case GT_OR_EQUAL:{
                    if (compResult>=0){
                        ret.rows.add(rows.get(i-1));
                        removeRow(i);
                        i--;
                    }
                    break;
                }
                case LT_OR_EQUAL:{
                    if (compResult<=0){
                        ret.rows.add(rows.get(i-1));
                        removeRow(i);
                        i--;
                    }
                    break;
                }
                case NOT_EQUAL:{
                    if (compResult!=0){
                        ret.rows.add(rows.get(i-1));
                        removeRow(i);
                        i--;
                    }
                }
            }//endSwitch
        }//end for
        return ret;
    }

    public DBResult remove(int column,int compType, String compValue) throws ArrayIndexOutOfBoundsException{
        return remove(column,compType,compValue,true);
    }

    public DBResult remove(int column,int compType, Comparable compValue) throws ArrayIndexOutOfBoundsException{
        return remove(column,compType,compValue,false);
    }

    public DBResult remove(String columnName, int compType,String compValue)throws ArrayIndexOutOfBoundsException{
        return remove(getColumnIndex(columnName),compType,compValue);
    }

    public DBResult remove(String columnName, int compType, Comparable compValue) throws ArrayIndexOutOfBoundsException{
        return remove(getColumnIndex(columnName),compType,compValue);
    }

    /** Tagastab subseti DBResultist, mis sisaldab ainult tingimust rahuldavaid ridu.
     * @param column -
     * @param compType -
     * @param compValue -
     * @return DBResult
     * @throws ArrayIndexOutOfBoundsException
     */
    public DBResult select(int column,int compType, String compValue) throws ArrayIndexOutOfBoundsException{
        return select(column,compType,compValue,true);
    }

    public DBResult select(int column,int compType, Comparable compValue) throws ArrayIndexOutOfBoundsException{
        return select(column,compType,compValue,false);
    }

    /** Tagastab subseti DBResultist, mis sisaldab ainult tingimust rahuldavaid ridu.
     * @param columnName -
     * @param compType -
     * @param compValue -
     * @return DBResult
     * @throws ArrayIndexOutOfBoundsException
     */
    public DBResult select(String columnName, int compType,String compValue)throws ArrayIndexOutOfBoundsException{
        return select(getColumnIndex(columnName),compType,compValue);
    }

    public DBResult select(String columnName, int compType, Comparable compValue) throws ArrayIndexOutOfBoundsException{
        return select(getColumnIndex(columnName),compType,compValue);
    }

    private class FieldComparator implements java.util.Comparator{
        int fieldIndex=-1;
        int orderType=1;

        // At sorting the comparing is done over rows.
        boolean compareOnRows=true;
        FieldComparator(int i,int orderType){
            fieldIndex=i;
            this.orderType=orderType;
        }

        FieldComparator(int orderType){
            compareOnRows=false;
        }

        public int compare(Object o1,Object o2){
            Comparable f1=null;
            Comparable f2=null;
            if (compareOnRows){
                f1=(Comparable)(((Object[])o1)[fieldIndex]);
                f2=(Comparable)(((Object[])o2)[fieldIndex]);
            }else{
                f1=(Comparable)o1;
                f2=(Comparable)o2;
            }
            int ret=0;
            if(
            f1==null&&f2!=null||
            f1!=null&&f2!=null&&f1.compareTo(f2)>0
            ){
                ret=1;
            }else
                if (
                f1!=null&&f2==null||
                f2!=null&&f1!=null&&f1.compareTo(f2)<0
                ){
                    ret=-1;
                }else{
                    ret=0;
                }
            return ret*orderType;
        }

        public boolean equals(Object obj){
            try{
                if (
                fieldIndex==((FieldComparator)obj).fieldIndex&&
                orderType==((FieldComparator)obj).orderType
                ){
                    return true;
                }else{
                    return false;
                }
            }catch(Exception e){
                return false;
            }
        }
    }

    /** Tagastab sorditud DBResulti
     * @param column -
     * @param orderType -
     * @return DBResult
     * @throws ArrayIndexOutOfBoundsException
     */
    public DBResult orderBy(int column,int orderType)throws ArrayIndexOutOfBoundsException{
        if (column<1||column>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        DBResult ret=new DBResult(DBResultName);
        ret.nameToIndex=(Hashtable)nameToIndex.clone();
        ret.columns=(String[])columns.clone();
        ret.attributes=(Hashtable)attributes.clone();
        ret.defFieldFormatter=defFieldFormatter;
        ret.customFieldFormatter=customFieldFormatter;
        ret.columnToFormatter=columnToFormatter;
        Object[] sortedRows=(Object[])rows.toArray().clone();
        Arrays.sort(sortedRows,new FieldComparator(column-1,orderType));
        ret.rows=new ArrayList(Arrays.asList(sortedRows));
        return ret;
    }

    /** Tagastab sorditud DBResulti
     * @param columnName -
     * @param orderType -
     * @throws ArrayIndexOutOfBoundsException
     * @return DBResult
     */
    public DBResult orderBy(String columnName,int orderType)throws ArrayIndexOutOfBoundsException{
        return orderBy(getColumnIndex(columnName),orderType);
    }

    public int getRowCount(){
        return rows.size();
    }

    public String getColumnName(int i){
        return columns[i-1];
    }

    public int getColumnCount(){
        return columns.length;
    }

    public String[] getColumnNames(){
        return (String[])columns.clone();
    }

    public void setField(int col, int row, Object o){
        Object[] r=(Object[])rows.get(row-1);
        r[col-1]=o;
    }

    public void setField(String columnName, int row,Object o){
        setField(getColumnIndex(columnName),row,o);
    }

    public void appendField(int col, int row, Object o)throws ArrayIndexOutOfBoundsException{
        if (col<1||col>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        if (row<1||(row-1)>this.getRowCount())throw new ArrayIndexOutOfBoundsException("The row index must be between 1 and "+(getRowCount()+1));
        if (row>rows.size()) rows.add(new Object[columns.length]);
        setField(col,row,o);
    }

    public void appendField(String columnName, int row, Object o)throws ArrayIndexOutOfBoundsException{
        Object colPos=nameToIndex.get(columnName);
        if (colPos==null){
            addColumn(columnName);
        }
        appendField(getColumnIndex(columnName),row,o);
    }

    public void removeColumn(int col)throws ArrayIndexOutOfBoundsException{
        if (col<1||col>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        col--;
        String[] columnsNew=new String[columns.length-1];
        System.arraycopy(columns,0,columnsNew,0,col);
        System.arraycopy(columns,col+1,columnsNew,col,columns.length-col-1);
        columns=columnsNew;
        nameToIndex.clear();
        for (int i=0;i<columns.length;i++){
          nameToIndex.put(columns[i],new Integer(i+1));
        }
        for (int i=0;i<rows.size();i++){
            Object[] row=(Object[])rows.get(i);
            Object[] rowNew=new Object[row.length-1];
            System.arraycopy(row,0,rowNew,0,col);
            System.arraycopy(row,col+1,rowNew,col,row.length-col-1);
            rows.set(i,rowNew);
        }
    }

    public void removeColumn(String columnName)throws ArrayIndexOutOfBoundsException{
        removeColumn(this.getColumnIndex(columnName));
    }

    public int addColumn(String columnName){
        Integer ret=(Integer)nameToIndex.get(columnName);
        if (ret!=null)throw new ArrayIndexOutOfBoundsException("Column exists allready");
        ret=new Integer(getColumnCount()+1);
        String[] colNew=new String[columns.length+1];
        System.arraycopy(columns,0,colNew,0,columns.length);
        colNew[colNew.length-1]=columnName;
        columns=colNew;
        nameToIndex.put(columnName,ret);
        FieldFormatter ff=customFieldFormatter;
        if (ff==null)ff=defFieldFormatter;
        columnToFormatter.put(ret,ff);
        for (int i=0;i<getRowCount();i++){
            Object[] o=(Object[])rows.get(i);
            if (o.length<columns.length){
                Object[] oNew=new Object[columns.length];
                System.arraycopy(o,0,oNew,0,o.length);
                rows.set(i,oNew);
            }
        }
        return ret.intValue();
    }

    public void appendRow(Object[] row){
        rows.add(row);
    }

    public String rowToString(int row){
        if (row<1||(row-1)>this.getRowCount())throw new ArrayIndexOutOfBoundsException("The row index must be between 1 and "+(getRowCount()+1));
        StringBuffer sb=new StringBuffer();
        sb.append("<"+getDBResultName()+"_Row row_nr=\""+row+"\">\n");
            for(int j=1;j<=columns.length;j++){
                try{
                    sb.append("<"+StringUtil.asXML(columns[j-1])+">"+(DBResult)getFieldAsObject(j,row)+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }catch(Exception e){
                    sb.append("<"+StringUtil.asXML(columns[j-1])+">"+StringUtil.asXML(getField(j,row))+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }
            }
            sb.append("</"+getDBResultName()+"_Row>\n");
            return sb.toString();
    }

    /**
	 * Tagastab resultseti esitatuna xml-is
     * @return String
     */
    public String toString(){

        StringBuffer sb=new StringBuffer();
        sb.append("<"+StringUtil.asXML(getDBResultName())+" ");
        Enumeration en=attributes.keys();
        while (en.hasMoreElements()){
            String key=(String)en.nextElement();
            sb.append(" "+key+"=\""+StringUtil.asXML(defFieldFormatter.asString(attributes.get(key)))+"\" ");
        }
        sb.append(">\n");
        for (int i=1;i<=rows.size();i++){
            sb.append("<"+getDBResultName()+"_Row row_nr=\""+i+"\">\n");
            for(int j=1;j<=columns.length;j++){
                try{
                    sb.append("<"+StringUtil.asXML(columns[j-1])+">"+(DBResult)getFieldAsObject(j,i)+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }catch(Exception e){
                    sb.append("<"+StringUtil.asXML(columns[j-1])+">"+StringUtil.asXML(getField(j,i))+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }
            }
            sb.append("</"+getDBResultName()+"_Row>\n");
        }
        sb.append("</"+getDBResultName()+">\n");
        return sb.toString();
    }

    public String toStringUTF(){
        StringBuffer sb=new StringBuffer();
        sb.append("<"+StringUtil.asXML(getDBResultName())+" ");
        Enumeration en=attributes.keys();
        while (en.hasMoreElements()){
            String key=(String)en.nextElement();
            sb.append(" "+key+"=\""+StringUtil.asUTF(defFieldFormatter.asString(attributes.get(key)))+"\" ");
        }
        sb.append(">\n");
        for (int i=1;i<=rows.size();i++){
            sb.append("<"+getDBResultName()+"_Row row_nr=\""+i+"\">\n");
            for(int j=1;j<=columns.length;j++){
                try{
                    sb.append("<"+StringUtil.asXML(columns[j-1])+">"+(DBResult)getFieldAsObject(j,i)+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }catch(Exception e){
                    sb.append("<"+StringUtil.asXML(columns[j-1])+">"+StringUtil.asXML(getField(j,i))+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }
            }
            sb.append("</"+getDBResultName()+"_Row>\n");
        }
        sb.append("</"+getDBResultName()+">\n");
        return sb.toString();

    }

    public String toStringWithMetadata(){
        StringBuffer sb=new StringBuffer();
        sb.append("<"+StringUtil.asXML(getDBResultName())+" ");
        Enumeration en=attributes.keys();
        while (en.hasMoreElements()){
            String key=(String)en.nextElement();
            sb.append(" "+key+"=\""+StringUtil.asXML(defFieldFormatter.asString(attributes.get(key)))+"\" ");
        }
        sb.append(">\n");
        for (int i=1;i<=rows.size();i++){
            sb.append("<"+getDBResultName()+"_Row row_nr=\""+i+"\">\n");
            for(int j=1;j<=columns.length;j++){
                try{
                    sb.append("<"+StringUtil.asXML(columns[j-1])+">"+(DBResult)getFieldAsObject(j,i)+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }catch(Exception e){
                    Object o=getFieldAsObject(j,i);
                    String ss="";
                    if (o!=null)ss=o.getClass().getName();
                    sb.append("<"+StringUtil.asXML(columns[j-1])+" class=\""+ss+"\">"+StringUtil.asXML(getField(j,i))+"</"+StringUtil.asXML(columns[j-1])+">\n");
                }
            }
            sb.append("</"+getDBResultName()+"_Row>\n");
        }
        sb.append("</"+getDBResultName()+">\n");
        return sb.toString();
    }

    public void setFieldFormatter(FieldFormatter ff){
        customFieldFormatter=ff;
        Enumeration en=columnToFormatter.keys();
        for (;en.hasMoreElements();){
            columnToFormatter.put(en.nextElement(),ff);
        }
    }
    public void setFieldFormatter(int col,FieldFormatter ff){
        columnToFormatter.put(new Integer(col),ff);
    }
    public void setFieldFormatter(String columnName,FieldFormatter ff)throws ArrayIndexOutOfBoundsException{
        setFieldFormatter(getColumnIndex(columnName),ff);
    }

    public void renameColumn(int column, String newName)throws ArrayIndexOutOfBoundsException{
        if (column<1||column>this.getColumnCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        Enumeration en=nameToIndex.keys();
        while (en.hasMoreElements()){
            String key=(String)en.nextElement();
            if (((Integer)nameToIndex.get(key)).intValue()==column){
                nameToIndex.remove(key);
                nameToIndex.put(newName,new Integer(column));
                break;
            }
        }
        columns[column-1]=newName;
    }
    public void renameColumn(String oldName,String newName)throws ArrayIndexOutOfBoundsException{
        renameColumn(getColumnIndex(oldName),newName);
    }

    public void removeRow(int rowIndex)throws ArrayIndexOutOfBoundsException{
        if (rowIndex<1||rowIndex>this.getRowCount())throw new ArrayIndexOutOfBoundsException("The column index must be between 1 and "+getColumnCount());
        rows.remove(rowIndex-1);
    }
    public int getColumnIndex(String columnName)throws ArrayIndexOutOfBoundsException{
        int column=-1;
        Object o=nameToIndex.get(columnName);
        if (o!=null){
            column=((Integer)o).intValue();
        }else throw new ArrayIndexOutOfBoundsException("Column called "+columnName+" not found");
        return column;
    }

    public DBResult mergeVertically(DBResult dbr){
        DBResult ret=new DBResult(DBResultName,(String[])columns.clone());
        ret.nameToIndex=(Hashtable)nameToIndex.clone();
        ret.attributes=(Hashtable)attributes.clone();
        ret.defFieldFormatter=defFieldFormatter;
        ret.customFieldFormatter=customFieldFormatter;
        ret.columnToFormatter=columnToFormatter;
        ret.rows=(ArrayList)rows.clone();
        for (int i=1;i<=dbr.getRowCount();i++){
            for (int j=1;j<=dbr.getColumnCount();j++){
                ret.appendField(dbr.getColumnName(j),getRowCount()+i,dbr.getFieldAsObject(j,i));
            }
        }
        return ret;
    }

    public boolean equals(Object ob){
        DBResult comp=(DBResult)ob;
        if (comp==null) return false;
        if (this.getRowCount()!=comp.getRowCount()||this.getColumnCount()!=comp.getColumnCount())return false;
        for (int i=1;i<=this.getRowCount();i++){
            for (int j=1;j<=this.getColumnCount();j++){
                Object o1=getFieldAsObject(j,i);
                Object o2=comp.getFieldAsObject(j,i);
                if (o1!=null){
                    if (!o1.equals(o2))return false;
                }else if (o2!=null){
                    return false;
                }
            }
        }
        return true;
    }

	/**
	 * For debugging.
	 */
	public static void main(String args[]){
		// System.out.println("Serial Version UID: " + DBResult.getSerialVersionUID());
	}

}