package ee.ioc.cs.vsle.vclass;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.graphics.Image;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.graphics.Text;
import ee.ioc.cs.vsle.util.*;

public class GObj implements Serializable, Cloneable, 
                                ee.ioc.cs.vsle.api.SchemeObject, ISpecExtendable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(GObj.class);

    private float Xsize = 1; // percentage for resizing, 1 means real size
    private float Ysize = 1;
    protected static final int CORNER_SIZE = 6;
    private static final float MIN_SCALE = 0.1f; // the minimal value for
                                                    // X|Ysize

    // Default coordinates to be used when there are no better values 
    private static final int DEFAULT_X = 10;
    private static final int DEFAULT_Y = 10;

    private int x, y;
    
    /*
     * difWithMasterX, difWithMasterY variables are for resizeing an object
     * group, we need to know the intitial difference to make it work correctly
     */
    private int difWithMasterX, difWithMasterY;
    private int width;

    private int height;
    public String className;
    private String name;
    private boolean isStatic = false;

    private ArrayList<Port> ports = new ArrayList<Port>();
    // fields declared in the xml
    private Map<String, ClassField> fields = new LinkedHashMap<String, ClassField>();
    // fields declared in the specification of the corresponding java class
    private Map<String, ClassField> specFields = new LinkedHashMap<String, ClassField>();
    private ArrayList<Shape> shapes = new ArrayList<Shape>();

    private boolean selected;
    private boolean group = false;
    private boolean strict;
    /**
     * Is this object the superclass of the scheme?
     */
    private boolean superClass;

    private int portOffsetX1 = 0;
    private int portOffsetX2 = 0;
    private int portOffsetY1 = 0;
    private int portOffsetY2 = 0;
    
    private  int selectOffset = 3;

    /**
     * The rotation of the object in radians
     */
    private double angle = 0d;

    private boolean drawPorts = true;
    private boolean drawOpenPorts = true;
    private boolean drawInstanceName = false;    
    
    private String extendedSpec;
    
    public GObj() {
        // default constructor
    }

    public boolean contains( int pointX, int pointY ) {
        Point p = toObjectSpace( pointX, pointY );
        if ( ( p.x > getX() + (int) ( getXsize() * getPortOffsetX1() ) ) && ( p.y > getY() + (int) ( getYsize() * getPortOffsetY1() ) ) ) {
            if ( ( p.x < getX() + (int) ( getXsize() * ( getWidth() + getPortOffsetX2() ) ) && ( p.y < getY()
                    + (int) ( getYsize() * ( getHeight() + getPortOffsetY2() ) ) ) ) ) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInsideSelect( int pointX, int pointY ) {
        Point p = toObjectSpace( pointX, pointY );
        if ( ( p.x >= getX() - selectOffset) && ( p.y >= getY() - selectOffset ) && ( p.x <= getX() + getWidth() + selectOffset)
        		 && ( p.y <= getY() + getHeight() + selectOffset)) {
            return true;
        }
        return false;
    }


    public boolean isInside( int x1, int y1, int x2, int y2 ) {
        if ( ( x1 < getX() + getPortOffsetX1() ) && ( y1 < getY() + getPortOffsetY1() ) ) {
            if ( ( x2 > getX() + (int) ( getXsize() * getWidth() ) + getPortOffsetX2() )
                    && ( y2 > getY() + (int) ( getYsize() * getHeight() ) + getPortOffsetY2() ) ) {
                return true;
            }
        }
        return false;
    }

    public void resize( int changeX, int changeY, int corner ) {
        /*
         * All these special cases and formulas are for making sure that the
         * object's dimensions will not become negative even when the mouse is
         * moved very quickly. If there is a cleaner way to do it then please
         * implement it.
         */
        switch ( corner ) { // changeX
        case 1: // top left
        case 3: // bottom left
            if ( changeX > 0 && ( getXsize() - changeX / getWidth() < MIN_SCALE ) )
                changeX = (int) ( ( getXsize() - MIN_SCALE ) * getWidth() + .5 );
            if ( changeX != 0 ) {
                setX( getX() + changeX );
                setXsize( getXsize() - (float) changeX / getWidth() );
            }
            break;
        case 2: // top right
        case 4: // bottom right
            if ( changeX < 0 && ( getXsize() + changeX / getWidth() < MIN_SCALE ) )
                changeX = (int) ( ( MIN_SCALE - getXsize() ) * getWidth() - .5 );
            if ( changeX != 0 )
                setXsize( getXsize() + (float) changeX / getWidth() );
            break;
        default:
            throw new IllegalArgumentException( "The argument corner can have values 1, 2, 3 or 4." );
        }

        switch ( corner ) { // changeY
        case 1: // top left
        case 2: // top right
            if ( changeY > 0 && ( getYsize() - changeY / getWidth() < MIN_SCALE ) )
                changeY = (int) ( ( getYsize() - MIN_SCALE ) * getWidth() + .5 );
            if ( changeY != 0 ) {
                setY( getY() + changeY );
                setYsize( getYsize() - (float) changeY / getHeight() );
            }
            break;
        case 3: // bottom left
        case 4: // bottom right
            if ( changeY < 0 && ( getYsize() + changeY / getWidth() < MIN_SCALE ) )
                changeY = (int) ( ( MIN_SCALE - getYsize() ) * getHeight() - .5 );
            if ( changeY != 0 )
                setYsize( getYsize() + (float) changeY / getHeight() );
            break;
        default:
            // an exception was already thrown in previous switch statement
        }
    }

    public Port portContains( int pointX, int pointY ) {
        Point p = toObjectSpace( pointX, pointY );
        Port port;

        for ( int i = 0; i < getPortList().size(); i++ ) {
            port = getPortList().get( i );
            if ( port.inBoundsX( p.x ) && port.inBoundsY( p.y ) ) {
                return port;
            }
        }
        return null;
    }

    public int controlRectContains( int pointX, int pointY ) {
        Point p = toObjectSpace( pointX, pointY );

        if ( ( p.x >= getX() + getPortOffsetX1() - CORNER_SIZE - 1 ) && ( p.y >= getY() + getPortOffsetY1() - CORNER_SIZE - 1 ) ) {
            if ( ( p.x <= getX() + getPortOffsetX1() - 1 ) && ( p.y <= getY() + getPortOffsetY1() - 1 ) ) {
                return 1;
            }
        }
        if ( ( p.x >= getX() + (int) ( getXsize() * ( getWidth() + getPortOffsetX2() ) ) + 1 )
                && ( p.y >= getY() + getPortOffsetY1() - CORNER_SIZE - 1 ) ) {
            if ( ( p.x <= getX() + (int) ( getXsize() * ( getWidth() + getPortOffsetX2() ) + CORNER_SIZE + 1 ) )
                    && ( p.y <= getY() + getPortOffsetY1() + CORNER_SIZE ) ) {
                return 2;
            }
        }
        if ( ( p.x >= getX() + getPortOffsetX1() - CORNER_SIZE - 1 )
                && ( p.y >= getY() + (int) ( getYsize() * ( getHeight() + getPortOffsetY2() ) ) + 1 ) ) {
            if ( ( p.x <= getX() + getPortOffsetX1() - 1 )
                    && ( p.y <= getY() + (int) ( getYsize() * ( getHeight() + getPortOffsetY2() ) + CORNER_SIZE + 1 ) ) ) {
                return 3;
            }
        }
        if ( ( p.x >= getX() + (int) ( getXsize() * ( getWidth() + getPortOffsetX2() ) ) + 1 )
                && ( p.y >= getY() + (int) ( getYsize() * ( getHeight() + getPortOffsetY2() ) ) + 1 ) ) {
            if ( ( p.x <= getX() + (int) ( getXsize() * ( getWidth() + getPortOffsetX2() ) + CORNER_SIZE + 1 ) )
                    && ( p.y <= getY() + (int) ( getYsize() * ( getHeight() + getPortOffsetY2() ) + CORNER_SIZE + 1 ) ) ) {
                return 4;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
    	 return getName();
    }

    public boolean isStrict() {
        return strict;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int getRealWidth() {
        return (int) ( getWidth() * getXsize() );
    }

    @Override
    public int getRealHeight() {
        return (int) ( getHeight() * getYsize() );
    }

    public void setPosition( int x, int y ) {
        this.setX( x );
        this.setY( y );
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setClassName( String name ) {
        this.className = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public void setSelected( boolean set ) {
        selected = set;
    }

    public void setMultXSize( float s ) {
        this.setXsize( this.getXsize() * s );
    }

    public void setMultYSize( float s ) {
        this.setYsize( this.getYsize() * s );
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isGroup() {
        return group;
    }

    public Collection<Connection> getConnections() {
        Set<Connection> c = new LinkedHashSet<Connection>();
        Port port;

        for ( int i = 0; i < getPortList().size(); i++ ) {
            port = getPortList().get( i );
            c.addAll(port.getConnectionList());
        }
        return c;
    }

    public boolean includesObject( GObj obj ) {
        return obj == this;
    }

    public ArrayList<GObj> getComponents() {
        ArrayList<GObj> c = new ArrayList<GObj>(1);
        c.add( this );
        return c;
    }

    public ArrayList<Port> getPortList() {
        return ports;
    }

    public List<ee.ioc.cs.vsle.api.Port> getPorts() {
        return new ArrayList<ee.ioc.cs.vsle.api.Port>(ports);
    }

    protected void draw( int xPos, int yPos, float _Xsize, float _Ysize, Graphics2D g2 ) {
        Shape s;
        for ( int i = 0; i < getShapes().size(); i++ ) {
            s = getShapes().get( i );
            s.draw( xPos, yPos, _Xsize, _Ysize, g2 );
        }
    } // draw

    public double getCenterX() {
        return getX() + getRealWidth() / 2.0;
    }

    public double getCenterY() {
        return getY() + getRealHeight() / 2.0;
    }

    public void drawClassGraphics( Graphics2D g2, float scale ) {
        AffineTransform origTransform = g2.getTransform();
        g2.rotate( getAngle(), getCenterX(), getCenterY() );

        // hilight superclass
        if ( isSuperClass() ) {
            Composite c = g2.getComposite();
            g2.setColor( Color.GREEN );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
            g2.fillRect( getX(), getY(), getRealWidth(), getRealHeight() );
            g2.setComposite( c );
        }

        draw( getX(), getY(), getXsize(), getYsize(), g2 );

        int xModifier = getX();
        int yModifier = getY();
        g2.setColor( Color.black );

        Font origFont = g2.getFont();
        
        if ( isStatic() ) {
            g2.setFont( RuntimeProperties.getFont( RuntimeProperties.Fonts.STATIC ) );
            g2.drawString( "s", xModifier, yModifier );
            g2.setFont( origFont );
        }
        
        if( drawInstanceName ) {
            g2.setFont( RuntimeProperties.getFont( RuntimeProperties.Fonts.OBJECTS ) );
            g2.drawString( getName(), xModifier + ( isStatic() ? 20 : 5 ), yModifier - 5 );
            g2.setFont( origFont );
        }
        
        if( isDrawPorts() ) {
            for ( int i = 0; i < getPortList().size(); i++ ) {
                ClassGraphics graphics;
                Port port = getPortList().get( i );

             /*   if ( port.isSelected() || port.isConnected() || port.isHilighted() ) {

                    graphics = port.getClosedGraphics();

                } else
                    graphics = port.getOpenGraphics();*/
                
                if(drawOpenPorts){
                	 graphics = port.getOpenGraphics();
                } else {
                	 graphics = port.getClosedGraphics();
                }
                graphics.draw( xModifier + (int) ( getXsize() * port.getX() ), 
                        yModifier + (int) ( getYsize() * port.getY() ), 
                        getXsize(), getYsize(), g2 );
            }
        }
        
        for ( ClassField field : getFields() ) { // print all field values
            if ( field.getDefaultGraphics() != null ) {
                if ( !TypeUtil.isArray( field.type ) ) {
                    field.getDefaultGraphics().drawSpecial( xModifier, yModifier, getXsize(), getYsize(), g2, field.getName(), field.value );
                } else {
                    String[] split = field.value.split( TypeUtil.ARRAY_TOKEN );
                    int textOffset = 0;
                    for ( int j = 0; j < split.length; j++ ) {
                        field.getDefaultGraphics().drawSpecial( xModifier, yModifier + textOffset, getXsize(), getYsize(), g2,
                                field.getName(), split[j] );
                        textOffset += 12;
                    }
                }
            }
            if ( field.isKnown() && field.getKnownGraphics() != null ) {
                if ( !TypeUtil.isArray( field.type ) ) {
                    field.getKnownGraphics().drawSpecial( xModifier, yModifier, getXsize(), getYsize(), g2, field.getName(), field.value );
                } else {
                    String[] split = field.value.split( TypeUtil.ARRAY_TOKEN );
                    int textOffset = 0;
                    for ( int j = 0; j < split.length; j++ ) {
                        field.getKnownGraphics().drawSpecial( xModifier, yModifier + textOffset, getXsize(), getYsize(), g2,
                                field.getName(), split[j] );
                        textOffset += 12;
                    }
                }
            }
        }

        g2.setColor( Color.black );
        if ( isSelected() == true ) {
            drawSelectionMarks( g2, scale );
        }

        g2.setTransform( origTransform );
    }

    private void drawSelectionMarks( Graphics g, float scale ) {
    	
    	/*  int scaledX =  Math.round(getX() / scale );
    	  int scaledY =  Math.round(getY() / scale );*/
    	
        g.fillRect( getX() + getPortOffsetX1() - CORNER_SIZE - 1, getY() + getPortOffsetY1() - CORNER_SIZE - 1, CORNER_SIZE, CORNER_SIZE );

        g.fillRect( getX() + (int) ( getXsize() * ( getWidth() + getPortOffsetX2() ) ) + 1, getY() + getPortOffsetY1() - CORNER_SIZE - 1,
                CORNER_SIZE, CORNER_SIZE );

        g.fillRect( getX() + getPortOffsetX1() - CORNER_SIZE - 1, getY() + (int) ( getYsize() * ( getPortOffsetY2() + getHeight() ) ) + 1,
                CORNER_SIZE, CORNER_SIZE );

        g.fillRect( getX() + (int) ( getXsize() * ( getPortOffsetX2() + getWidth() ) ) + 1, getY()
                + (int) ( getYsize() * ( getPortOffsetY2() + getHeight() ) ) + 1, CORNER_SIZE, CORNER_SIZE );
    }
    
    private void drawSelectionMarksPort( Graphics g, float scale ) {
    	
    	/*  int scaledX =  Math.round(getX() / scale );
    	  int scaledY =  Math.round(getY() / scale );*/
    	
        g.fillRect( getX() + getPortOffsetX1() - CORNER_SIZE - 1, getY() + getPortOffsetY1() - CORNER_SIZE - 1, CORNER_SIZE, CORNER_SIZE );

        g.fillRect( getX() + (int) ( getXsize() * ( getWidth() + getPortOffsetX2() ) ) + 1, getY() + getPortOffsetY1() - CORNER_SIZE - 1,
                CORNER_SIZE, CORNER_SIZE );

        g.fillRect( getX() + getPortOffsetX1() - CORNER_SIZE - 1, getY() + (int) ( getYsize() * ( getPortOffsetY2() + getHeight() ) ) + 1,
                CORNER_SIZE, CORNER_SIZE );

        g.fillRect( getX() + (int) ( getXsize() * ( getPortOffsetX2() + getWidth() ) ) + 1, getY()
                + (int) ( getYsize() * ( getPortOffsetY2() + getHeight() ) ) + 1, CORNER_SIZE, CORNER_SIZE );
    }

    @Override
    public GObj clone() {
        try {
            GObj obj = (GObj) super.clone();

            obj.fields = new LinkedHashMap<String, ClassField>();
            obj.specFields = new LinkedHashMap<String, ClassField>();
            // deep clone fields list
            for ( ClassField field : specFields.values() ) {
                ClassField newField = field.clone();
                if ( fields.containsValue( field ) ) {
                    obj.addField( newField );
                }
                obj.addSpecField( newField );
            }

            obj.setPorts( new ArrayList<Port>() );
            for ( Port port : getPortList() ) {
                port = port.clone();
                obj.getPortList().add( port );
                port.setObject( obj );
                port.setConnections( new ArrayList<Connection>() );
            }

            // Only one superclass is permitted
            obj.setSuperClass(false);

            return obj;
        } catch ( CloneNotSupportedException e ) {
            logger.error( "Unable to clone." );
            return null;
        }
    }

    public float getXsize() {
        return Xsize;
    }

    public void setXsize( float xsize ) {
        Xsize = xsize;
    }

    public float getYsize() {
        return Ysize;
    }

    public void setYsize( float ysize ) {
        Ysize = ysize;
    }

    @Override
    public void setX( int x ) {
        // Editor GUI does not support negative coordinates properly
        this.x = x < 0 ? DEFAULT_X : x;
    }

    @Override
    public void setY( int y ) {
        // Editor GUI does not support negative coordinates properly
        this.y = y < 0 ? DEFAULT_Y : y;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    /**
     * Replaces the current port list with the specified one. Further
     * modifications of the list might not be reflected correctly in the state
     * of this object. Therefore, it is recommended that the list is not
     * modified directly. New ports can be added through the {@code addPort()}
     * method.
     * 
     * @param ports
     *                list of the ports
     */
    public void setPorts( ArrayList<Port> ports ) {
        this.ports = ports;
        for ( Port port : ports ) {
            if ( port.isStrict() ) {
                this.strict = true;
                return;
            }
        }
    }

    /**
     * Appends the specified port at the end of the list of the ports.
     * 
     * @param port
     *                a port
     */
    public void addPort( Port port ) {
        ports.add( port );
        if ( !strict && port.isStrict() )
            strict = true;
    }

    public Collection<ClassField> getFields() {
        return fields.values();
    }

    public void addField( ClassField field ) {
        this.fields.put( field.getName(), field );
    }

    /**
     * @param specField The specField to be added
     */
    public void addSpecField( ClassField specField ) {
        this.specFields.put( specField.getName(), specField );
    }

    /**
     * @return the specFields
     */
    public ClassField getSpecField( String specName ) {
        return specFields.get( specName );
    }

    public void setGroup( boolean group ) {
        this.group = group;
    }

    /**
     * Generates SAX events that are necessary to serialize this object to XML.
     * 
     * @param th
     *                the receiver of events
     * @throws SAXException
     */
    public void toXML( TransformerHandler th ) throws SAXException {
        AttributesImpl attrs = new AttributesImpl();

        attrs.addAttribute("", "", "name", StringUtil.CDATA, getName());
        attrs.addAttribute("", "", "type", StringUtil.CDATA, getClassName());
        attrs.addAttribute("", "", "static", StringUtil.CDATA, Boolean.toString(isStatic()));

        th.startElement("", "", "object", attrs);

        attrs.clear();
        attrs.addAttribute("", "", "x", StringUtil.CDATA, Integer.toString(getX()));
        attrs.addAttribute("", "", "y", StringUtil.CDATA, Integer.toString(getY()));
        attrs.addAttribute("", "", "xsize", StringUtil.CDATA, Double.toString(getXsize()));
        attrs.addAttribute("", "", "ysize", StringUtil.CDATA, Double.toString(getYsize()));
        attrs.addAttribute("", "", "strict", StringUtil.CDATA, Boolean.toString(isStrict()));
        if ( getAngle() != 0.0 )
            attrs.addAttribute("", "", "angle", StringUtil.CDATA, Double.toString(getAngle()));

        th.startElement("", "", "properties", attrs);
        th.endElement("", "", "properties");

        attrs.clear();
        th.startElement("", "", "fields", attrs);

        for ( ClassField field : getFields() )
            field.toXML( th );

        th.endElement("", "", "fields");
        th.endElement("", "", "object");
    }

    /**
     * Returns {@code true} if one or more ports are strictly connected.
     * 
     * @return true if this object is strictly connected, {@code false}
     *         otherwise
     */
    public boolean isStrictConnected() {
        for ( Port port : ports ) {
            for (Connection con : port.getConnectionList()) {
                if ( con.isStrict() )
                    return true;
            }
        }
        return false;
    }

    public boolean isFixed() {
      /*  for ( Shape s : getShapes() ) {            
               if ( s.isFixed() )
                    return true;            
        } // Always false for now 22.01.AM */
        return false;
    }
    
    /**
     * Is this object the superclass of the scheme?
     * 
     * @return true, if the object is superclass, false otherwise
     */
    public boolean isSuperClass() {
        return superClass;
    }

    /**
     * Set or unset the object as a scheme superclass. There should be only one
     * superclass at a time.
     * 
     * @param superClass
     *                true to set the class as a superclass
     */
    public void setSuperClass( boolean superClass ) {
        this.superClass = superClass;
    }

    /**
     * Returns fields by name.
     * 
     * @param fldName
     *                the name of the field
     * @return the field with the specified name or null
     */
    public ClassField getField( String fldName ) {
        if ( fldName == null )
            return null;

        return fields.get( fldName );
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic( boolean isStatic ) {
        this.isStatic = isStatic;
    }

    /**
     * Transforms the specified point coordinates to the object coordinate space
     * (inverse of toCanvasSpace()).
     * 
     * @param pointX
     *                the x coordinate
     * @param pointY
     *                the y coordinate
     * @return transformed point
     */
    public Point toObjectSpace( int pointX, int pointY ) {
        Point p = new Point( pointX, pointY );

        if ( getAngle() != 0.0 ) {
            double cx = getCenterX();
            double cy = getCenterY();

            double sin = Math.sin( getAngle() );
            double cos = Math.cos( getAngle() );

            p.x = (int) Math.round( pointX * cos + pointY * sin + cx - cx * cos - cy * sin );
            p.y = (int) Math.round( -pointX * sin + pointY * cos + cy + cx * sin - cy * cos );
        }
        return p;
    }

    /**
     * Transform the specified point coordinates to the canvas coordinate space
     * (inverse of toObjectSpace()).
     * 
     * @param pointX
     *                the x coordinate
     * @param pointY
     *                the y coordinate
     * @return transformed point
     */
    public Point toCanvasSpace( int pointX, int pointY ) {
        Point p = new Point( pointX, pointY );

        if ( getAngle() != 0.0 ) {
            double cx = getCenterX();
            double cy = getCenterY();

            double sin = Math.sin( getAngle() );
            double cos = Math.cos( getAngle() );

            p.x = (int) Math.round( pointX * cos - pointY * sin + cx - cx * cos + cy * sin );
            p.y = (int) Math.round( pointX * sin + pointY * cos + cy - cx * sin - cy * cos );
        }
        return p;
    }
    
    public boolean resizable(){
    	if (this.getShapes() != null && this.getShapes().size() > 0 ){
    		if(((this.getShapes().get(0) instanceof Image) || (this.getShapes().get(0) instanceof Text)) && !this.getShapes().get(0).isAllowResize())
    			return false;
    	}
    	return true;
    }

    /**
     * Sets the rotation of this object.
     * 
     * @param theta
     *                the rotation in radians
     */
    public void setAngle( double theta ) {
        angle = theta;
    }

    /**
     * Returns the rotation of this object.
     * 
     * @return rotation in radians
     */
    public double getAngle() {
        return angle;
    }

    /*
     * @see ee.ioc.cs.vsle.api.SchemeObject#getFieldValue(java.lang.String)
     */
    @Override
    public Object getFieldValue( String fieldName ) {
        ClassField f = getField( fieldName );
        if ( f == null )
            throw new RuntimeException( "No such field: " + fieldName );

        return f.getValue();
    }

    /*
     * @see ee.ioc.cs.vsle.api.SchemeObject#setFieldValue(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setFieldValue( String fieldName, String value ) {
        ClassField f = getField( fieldName );
        if ( f == null )
            throw new RuntimeException( "No such field: " + fieldName );

        f.setValue( value );
    }

    /**
     * @return the drawPorts
     */
    public boolean isDrawPorts() {
        return drawPorts;
    }

    /**
     * @param drawPorts the drawPorts to set
     */
    public void setDrawPorts( boolean drawPorts ) {
        this.drawPorts = drawPorts;
    }

    /**
     * @param drawInstanceName the drawInstanceName to set
     */
    public void setDrawInstanceName( boolean drawInstanceName ) {
        this.drawInstanceName = drawInstanceName;
    }

    /**
     * @param difWithMasterX the difWithMasterX to set
     */
    public void setDifWithMasterX( int difWithMasterX ) {
        this.difWithMasterX = difWithMasterX;
    }

    /**
     * @return the difWithMasterX
     */
    public int getDifWithMasterX() {
        return difWithMasterX;
    }

    /**
     * @param difWithMasterY the difWithMasterY to set
     */
    public void setDifWithMasterY( int difWithMasterY ) {
        this.difWithMasterY = difWithMasterY;
    }

    /**
     * @return the difWithMasterY
     */
    public int getDifWithMasterY() {
        return difWithMasterY;
    }

    /**
     * @param shapes the shapes to set
     */
    public void setShapes( ArrayList<Shape> shapes ) {
        this.shapes = shapes;
    }

    /**
     * @return the shapes
     */
    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    /**
     * @param portOffsetX1 the portOffsetX1 to set
     */
    public void setPortOffsetX1( int portOffsetX1 ) {
        this.portOffsetX1 = portOffsetX1;
    }

    /**
     * @return the portOffsetX1
     */
    public int getPortOffsetX1() {
        return portOffsetX1;
    }

    /**
     * @param portOffsetX2 the portOffsetX2 to set
     */
    public void setPortOffsetX2( int portOffsetX2 ) {
        this.portOffsetX2 = portOffsetX2;
    }

    /**
     * @return the portOffsetX2
     */
    public int getPortOffsetX2() {
        return portOffsetX2;
    }

    /**
     * @param portOffsetY1 the portOffsetY1 to set
     */
    public void setPortOffsetY1( int portOffsetY1 ) {
        this.portOffsetY1 = portOffsetY1;
    }

    /**
     * @return the portOffsetY1
     */
    public int getPortOffsetY1() {
        return portOffsetY1;
    }

    /**
     * @param portOffsetY2 the portOffsetY2 to set
     */
    public void setPortOffsetY2( int portOffsetY2 ) {
        this.portOffsetY2 = portOffsetY2;
    }

    /**
     * @return the portOffsetY2
     */
    public int getPortOffsetY2() {
        return portOffsetY2;
    }

    @Override
    public String getSpecText() {
        return extendedSpec;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public void setSpecText(String spec) {
        extendedSpec = spec;
    }

	public boolean isDrawOpenPorts() {
		return drawOpenPorts;
	}

	public void setDrawOpenPorts(boolean drawOpenPorts) {
		this.drawOpenPorts = drawOpenPorts;
	}
    
}
