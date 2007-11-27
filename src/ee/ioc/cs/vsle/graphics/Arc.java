package ee.ioc.cs.vsle.graphics;

import java.io.*;

import java.awt.*;

public class Arc extends Shape implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private int startAngle;

    private int arcAngle;

    /**
     * Class constructor.
     * 
     * @param x
     *            int - x coordinate of the beginning of the arc.
     * @param y
     *            int - y coordinate of the beginning of the arc.
     * @param width
     *            int - arc width (arc end x coordinate - arc start x
     *            coordinate).
     * @param height
     *            int - arc height (arc end y coordinate - arc start y
     *            coordinate).
     * @param startAngle
     *            int - arc starting angle, zero by default.
     * @param arcAngle
     *            int - arc ending angle, 180 by default.
     * @param colorInt
     *            int - arc color.
     * @param fill
     *            boolean - boolean value indicating whether to fill the
     *            constructed arc with a specified color or not.
     * @param strokeWidth
     *            double - width of the line the arc is drawn with.
     * @param transp
     *            double - transparency (Alpha) value (0..100%).
     * @param lineType -
     *            shape line type.
     */
    public Arc( int x, int y, int width, int height, int startAngle, int arcAngle, int colorInt, boolean fill, float strokeWidth,
            int transp, float lineType ) {
        super( x, y, width, height );
        this.setStartAngle( startAngle );
        this.setArcAngle( arcAngle );
        setColor( new Color( colorInt ) );
        setTransparency( transp );

        this.setFilled( fill );

        setStroke( strokeWidth, lineType );
        
    } // Arc

    public int getStartAngle() {
        return this.startAngle;
    } // getStartAngle

    /**
     * Returns the angle of the arc.
     * 
     * @return int - angle of the arc.
     */
    public int getArcAngle() {
        return this.arcAngle;
    } // getArcAngle

    /**
     * Resizes current object.
     * 
     * @param deltaW
     *            int - change of object with.
     * @param deltaH
     *            int - change of object height.
     * @param cornerClicked
     *            int - number of the clicked corner.
     */
    public void resize( int deltaW, int deltaH, int cornerClicked ) {
        if ( !isFixed() ) {
            if ( cornerClicked == 1 ) { // TOP-LEFT
                if ( this.getWidth() - deltaW > 0 && this.getHeight() - 2 * deltaH > 0 ) {
                    this.setX( this.getX() + deltaW );
                    this.setY( this.getY() + deltaH );
                    this.setWidth( this.getWidth() - deltaW );
                    this.setHeight( this.getHeight() - ( 2 * deltaH ) );
                }
            } else if ( cornerClicked == 2 ) { // TOP-RIGHT
                if ( this.getWidth() + deltaW > 0 && this.getHeight() - 2 * deltaH > 0 ) {
                    this.setY( this.getY() + deltaH );
                    this.setWidth( this.getWidth() + deltaW );
                    this.setHeight( this.getHeight() - ( 2 * deltaH ) );
                }
            } else if ( cornerClicked == 3 ) { // BOTTOM-LEFT
                if ( this.getWidth() - deltaW > 0 && this.getHeight() + deltaH > 0 ) {
                    this.setX( this.getX() + deltaW );
                    this.setWidth( this.getWidth() - deltaW );
                    this.setHeight( this.getHeight() + deltaH );
                }
            } else if ( cornerClicked == 4 ) { // BOTTOM-RIGHT
                if ( this.getWidth() + deltaW > 0 && this.getHeight() + deltaH > 0 ) {
                    this.setWidth( this.getWidth() + deltaW );
                    this.setHeight( this.getHeight() + deltaH );
                }
            }
        }
    } // resize

    /**
     * Return a specification of the shape to be written into a file in XML
     * format.
     * 
     * @param boundingboxX -
     *            x coordinate of the bounding box.
     * @param boundingboxY -
     *            y coordinate of the bounding box.
     * @return String - specification of a shape.
     */
    public String toFile( int boundingboxX, int boundingboxY ) {
        String fill = "false";

        if ( isFilled() ) {
            fill = "true";
        }
        int colorInt = 0;

        if ( getColor() != null ) {
            colorInt = getColor().getRGB();
        }
        return "<arc x=\"" + ( getX() - boundingboxX ) + "\" y=\"" + ( getY() - boundingboxY ) + "\" width=\"" + getWidth()
                + "\" height=\"" + getHeight() + "\" startAngle=\"" + getStartAngle() + "\" arcAngle=\"" + getArcAngle() + "\" colour=\"" + colorInt
                + "\" filled=\"" + fill + "\" fixed=\"" + isFixed() + "\" stroke=\"" + (int) getStroke().getLineWidth() + "\" linetype=\""
                + this.getLineType() + "\" transparency=\"" + getTransparency() + "\"/>\n";
    } // toFile

    public String toText() {
        String fill = "false";
        if ( isFilled() )
            fill = "true";
        int colorInt = 0;
        if ( getColor() != null )
            colorInt = getColor().getRGB();
        return "ARC:" + getX() + ":" + getY() + ":" + getWidth() + ":" + getHeight() + ":" + getStartAngle() + ":" + getArcAngle() + ":" + colorInt
                + ":" + fill + ":" + getStroke().getLineWidth() + ":" + this.getLineType() + ":" + getColor().getTransparency() + ":"
                + isFixed();
    } // toText

    /**
     * Draw the selection markers if object selected.
     * 
     * @param g2
     *            Graphics2D - shape graphics.
     */

    /**
     * Draw the arc. Supports drawing with transparent colors.
     * 
     * @param xModifier
     *            int -
     * @param yModifier
     *            int -
     * @param Xsize
     *            float - defines the resizing multiplier (used at zooming),
     *            default: 1.0
     * @param Ysize
     *            float - defines the resizing multiplier (uset at zooming),
     *            default: 1.0
     * @param g2
     *            Graphics - shape graphics.
     */
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2 ) {
        g2.setStroke( getStroke() );
        g2.setColor( getColor() );

        int a = xModifier + (int) ( Xsize * getX() );
        int b = yModifier + (int) ( Ysize * getY() );
        int c = (int) ( Xsize * getWidth() );
        int d = (int) ( Ysize * getHeight() );

        if ( isFilled() ) {
            g2.fillArc( a, b, c, d, getStartAngle(), getArcAngle() );
        } else {
            g2.drawArc( a, b, c, d, getStartAngle(), getArcAngle() );
        }

        // Draw selection markers if object selected.
        if ( isSelected() ) {
            drawSelection( g2 );
        }

    } // draw

    public Arc clone() {
        return (Arc) super.clone();
    } // clone

    @Override
    public Shape getCopy() {
        
        return new Arc( getX(), getY(), getWidth(), getHeight(), getStartAngle(), getArcAngle(), getColor().getRGB(), isFilled(), 
                getStrokeWidth(), getTransparency(), getLineType() );
    }

    /**
     * @param startAngle the startAngle to set
     */
    public void setStartAngle( int startAngle ) {
        this.startAngle = startAngle;
    }

    /**
     * @param arcAngle the arcAngle to set
     */
    public void setArcAngle( int arcAngle ) {
        this.arcAngle = arcAngle;
    }

} // end of class
