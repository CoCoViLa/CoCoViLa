package ee.ioc.cs.vsle.table;

/**
 * Class for table variables
 */
public class TableField {

    private String id;
    private String type;
    private Object defaultValue;
    
    public TableField( String id, String type ) {
        this.id = id;
        this.type = type;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param f
     * @return
     */
    public boolean equalsByID( TableField f ) {
        return id.equals( f.id );
    }
    
    @Override
    public boolean equals( Object obj ) {
        return id.equals( ((TableField)obj).id ) && type.equals( ((TableField)obj).type );
    }

    private int lazyHash = -1;
    
    @Override
    public int hashCode() {
        if( lazyHash == -1 )
            return lazyHash = id.concat( type ).hashCode();
        
        return lazyHash;
    }

    @Override
    public String toString() {
        return "TableField: id= " + id + ", type=" + type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValueFromString( String value ) {
        defaultValue = Table.createDataObjectFromString( type, value );
    }
}
