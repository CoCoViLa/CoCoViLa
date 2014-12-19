package ee.ioc.cs.vsle.table.exception;

public class TableCellValueUndefinedException extends TableException {

    public TableCellValueUndefinedException() {
    }

    public TableCellValueUndefinedException( String message ) {
        super( message );
    }

    public TableCellValueUndefinedException( Throwable cause ) {
        super( cause );
    }

    public TableCellValueUndefinedException( String message, Throwable cause ) {
        super( message, cause );
    }

}
