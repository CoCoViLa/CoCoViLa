package ee.ioc.cs.vsle.graphics;

import java.awt.*;
import java.io.*;

public class Rect extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Shape constructor.
     * @param x int - shape x coordinate.
     * @param y int - shape y coordinate.
     * @param width int - width of the shape.
     * @param height int - height of the shape.
     * @param colorInt int - color of the shape.
     * @param filled boolean - the shape is filled or not.
     * @param strokeWidth double - line width of the shape.
     * @param transp double - shape transparency percentage.
     * @param lineType int - shape line type.
     */
    public Rect( int x, int y, int width, int height, int colorInt, boolean filled, float strokeWidth, int transp, float lineType ) {
        super( x, y, width, height );
        setFilled( filled );
        setColor( new Color( colorInt ) );
        setTransparency( transp );
        setStroke( strokeWidth, lineType );
    } // Rect

    /**
     * Return a specification of the shape to be written into a file in XML format.
     * @param boundingboxX - x coordinate of the bounding box.
     * @param boundingboxY - y coordinate of the bounding box.
     * @return String - specification of a shape.
     */
    @Override
    public String toFile( int boundingboxX, int boundingboxY ) {
        String fill = "false";

        if ( isFilled() ) {
            fill = "true";
        }
        int colorInt = 0;

        if ( getColor() != null ) {
            colorInt = getColor().getRGB();
        }
        return "<rect x=\"" + ( getX() - boundingboxX ) + "\" y=\"" + ( getY() - boundingboxY ) + "\" width=\"" + getWidth()
                + "\" height=\"" + getHeight() + "\" colour=\"" + colorInt + "\" filled=\"" + fill + "\" fixed=\"" + isFixed()
                + "\" stroke=\"" + (int) getStroke().getLineWidth() + "\" linetype=\"" + this.getLineType() + "\" transparency=\""
                + getTransparency() + "\"/>\n";
    } // toFile

    @Override
    public String toText() {
        String fill = "false";
        if ( isFilled() )
            fill = "true";
        int colorInt = 0;
        if ( getColor() != null )
            colorInt = getColor().getRGB();
        return "RECT:" + getX() + ":" + getY() + ":" + getWidth() + ":" + getHeight() + ":" + colorInt + ":" + fill + ":"
                + (int) getStroke().getLineWidth() + ":" + this.getLineType() + ":" + getTransparency() + ":" + isFixed();
    } // toText

    /**
     * Draw rectangle.
     * @param xModifier int -
     * @param yModifier int -
     * @param Xsize float - zoom factor.
     * @param Ysize float - zoom factor.
     * @param g2 Graphics - class graphics.
     */
    @Override
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2 ) {
        g2.setStroke( getStroke() );
        g2.setColor( getColor() );

        int a = xModifier + (int) ( Xsize * getX() );
        int b = yModifier + (int) ( Ysize * getY() );
        int c = (int) ( Xsize * getWidth() );
        int d = (int) ( Ysize * getHeight() );

        if ( isFilled() ) {
            g2.fillRect( a, b, c, d );
        } else {
            g2.drawRect( a, b, c, d );
        }

        if ( isSelected() ) {
            drawSelection( g2 );
        }

    } // draw

    @Override
    public Rect clone() {
        return (Rect) super.clone();
    } // clone

    @Override
    public Shape getCopy() {
        return new Rect( getX(), getY(), getWidth(), getHeight(), getColor().getRed(), 
                isFilled(), getStrokeWidth(), getTransparency(), getLineType() );
    }

}
