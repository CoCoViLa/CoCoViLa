package ee.ioc.cs.vsle.graphics;

import java.awt.*;
import java.io.*;

public class BoundingBox extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String name = "BoundingBox";
    private static final Color BB_COLOR = new Color( Color.lightGray.getRed(), Color.lightGray.getGreen(), Color.lightGray.getBlue(), 200 );
    public int ownerClassId;
    
    public BoundingBox( int x, int y, int width, int height ) {
        super( x, y, width, height );
        this.setColor( BB_COLOR );
        this.setFilled( true );
    } // BoundingBox   
    
   
	public int getOwnerClassId() {
		return ownerClassId;
	}


	public void setOwnerClassId(int ownerClassId) {
		this.ownerClassId = ownerClassId;
	}


	@Override
    public String getName() {
        return name;
    } // getName

    /**
     * Return a specification of the shape to be written into a file in XML format.
     * @param boundingboxX - x coordinate of the bounding box.
     * @param boundingboxY - y coordinate of the bounding box.
     * @return String - specification of a shape.
     */
    @Override
    public String toFile( int boundingboxX, int boundingboxY ) {
        return "<bounds x=\"0\" y=\"0\" width=\"" + getWidth() + "\" height=\"" + getHeight() + "\"/>\n";
    } // toFile

    /**
     * Return a text string representing the shape. Required for storing
     * scheme of shapes on a disk for later loading into the IconEditor
     * for continuing the work.
     * @return String - text string representing the shape.
     */
    @Override
    public String toText() {
        return "BOUNDS:" + getX() + ":" + getY() + ":" + getWidth() + ":" + getHeight();
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

        // Set the box color: light-gray, with a transparency defined by "alpha" value.
        g2.setColor( getColor() );

        int a = xModifier + (int) ( Xsize * getX() );
        int b = yModifier + (int) ( Ysize * getY() );
        int c = (int) ( Xsize * getWidth() );
        int d = (int) ( Ysize * getHeight() );

        // draw the bounding box rectangle.
        g2.fillRect( a, b, c, d );

        // Draw selection markers if object selected.
        if ( isSelected() ) {
            drawSelection( g2 );
        }

    } // draw

    @Override
    public Shape getCopy() {
        throw new IllegalStateException( "Not allowed to copy " + name );
    }

}
