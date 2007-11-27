package ee.ioc.cs.vsle.graphics;

import java.io.*;

import java.awt.*;

/**
 * <p>
 * Title: ee.ioc.cs.editor.graphics.Dot
 * </p>
 * <p>
 * Description: Shape of type ee.ioc.cs.editor.graphics.Dot
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class Dot extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Class constructor.
     * 
     * @param x
     *            int - x coordinate of the dot.
     * @param y
     *            int - y coordinate of the dot.
     * @param colorInt
     *            int - dot color.
     * @param strokeWidth
     *            double - width of the line the dot is drawn with.
     * @param transp
     *            double - transparency (Alpha) value (0..100%).
     */
    public Dot( int x, int y, int colorInt, float strokeWidth, int transp ) {
        super( x, y, (int) strokeWidth / 2, (int) strokeWidth / 2 );
        this.setColor( new Color( colorInt ) );
        setTransparency( transp );
        
        setStroke( strokeWidth, 0.0f );
    } // Dot

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
        int colorInt = 0;

        if ( getColor() != null ) {
            colorInt = getColor().getRGB();
        }
        return "<rect x=\"" + ( getX() - boundingboxX ) + "\" y=\"" + ( getY() - boundingboxY ) + "\" width=\"" + getWidth()
                + "\" height=\"" + getHeight() + "\" colour=\"" + colorInt + "\" fixed=\"" + isFixed() + "\" stroke=\""
                + (int) getStrokeWidth() + "\" transparency=\"" + getTransparency() + "\"/>\n";
    } // toFile

    public String toText() {
        int colorInt = 0;
        if ( getColor() != null )
            colorInt = getColor().getRGB();
        return "DOT:" + getX() + ":" + getY() + ":" + getWidth() + ":" + getHeight() + ":" + colorInt + ":" + (int) (int) getStrokeWidth() + ":"
                + getTransparency() + ":" + isFixed();
    } // toText

    /**
     * Draw the dot. Supports drawing with transparent colors.
     * 
     * @param xModifier
     *            int -
     * @param yModifier
     *            int -
     * @param sizeX
     *            float - defines the resizing multiplier for x coordinate (used
     *            at zooming), default: 1.0
     * @param sizeY
     *            float - defines the resizing multiplier for y coordinate (used
     *            at zooming), default: 1.0
     * @param g2
     *            Graphics
     */
    public void draw( int xModifier, int yModifier, float sizeX, float sizeY, Graphics2D g2 ) {
        g2.setStroke( getStroke() );
        g2.setColor( getColor() );

        int w = (int) getStrokeWidth() / 2;

        int a = xModifier + (int) ( sizeX * getX() );
        int b = yModifier + (int) ( sizeY * getY() );

        g2.drawRect( a, b, w, w );

        // Draw selection markers if object selected.
        if ( isSelected() ) {
            drawSelection( g2 );
        }
    }

    public Dot clone() {
        return (Dot) super.clone();
    } // clone

    @Override
    public Shape getCopy() {
        return new Dot( getX(), getY(), getColor().getRGB(), getStrokeWidth(), getTransparency() );
    }
}

