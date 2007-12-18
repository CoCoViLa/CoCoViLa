/**
 * 
 */
package ee.ioc.cs.vsle.equations;

/**
 * @author pavelg
 *
 */
public enum Function {
    sin("asin"), asin("sin"), cos("acos"), acos("cos"), tan("atan"), atan("tan"), 
    log("exp"), exp("log"), abs("abs"), toDegrees("toRadians"), toRadians("toDegrees");

    private String opposite;
    private final String MATH = "Math.";

    private Function( String opposite ) {
        this.opposite = opposite;
    }

    public String getOpposite() {
        return valueOf( opposite ).getFunction();
    }

    public String getFunction() {
        return MATH.concat( this.name() );
    }
    
    public static boolean contains( String func ) {
        try {
            return valueOf( func ) != null;
        } catch ( IllegalArgumentException e ) {
        }
        return false;
    }
    
    public static Function getFunction( String func ) {
        try {
            return valueOf( func );
        } catch ( IllegalArgumentException e ) {
        }
        return null;
    }
}
