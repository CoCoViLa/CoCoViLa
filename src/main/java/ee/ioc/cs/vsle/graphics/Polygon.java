package ee.ioc.cs.vsle.graphics;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 15.06.2004
 * Time: 16:14:17
 * To change this template use Options | File Templates.
 */
public class Polygon extends Shape {

    private static final long serialVersionUID = 1L;

    private int[] xPoints;
    private int[] yPoints;
    private int[] xFixed;
    private int[] yFixed;

    public Polygon( Color color, boolean b, float strokeWidth, float lineType ) {
        super( 0, 0 );
        setFilled( b );
        setColor( color );
        setStroke( strokeWidth, lineType );
    }

    public void setPoints( int[] xs, int[] ys, int[] fxs, int[] fys ) {
        setXPoints( xs );
        setYPoints( ys );
        setXFixed( fxs );
        setYFixed( fys );
    }

    @Override
    public boolean isInside( int x1, int y1, int x2, int y2 ) {
        return false;
    } // isInside

    @Override
    public boolean isInsideRect( int x1, int y1, int x2, int y2 ) {
        return false;
    } // isInsideRect

    /**
     * Set size using zoom multiplication.
     * @param s1 float - set size using zoom multiplication.
     * @param s2 float - set size using zoom multiplication.
     */
    @Override
    public void setMultSize( float s1, float s2 ) {
        setWidth( getWidth() * (int) s1 / (int) s2 );
        setHeight( getHeight() * (int) s1 / (int) s2 );
    } // setMultSize

    /**
     * Resizes current object.
     * @param deltaW int - change of object with.
     * @param deltaH int - change of object height.
     * @param cornerClicked int - number of the clicked corner.
     */
    @Override
    public void resize( int deltaW, int deltaH, int cornerClicked ) {
    } // resize

    /**
     * Returns the number representing a corner the mouse was clicked in.
     * 1: top-left, 2: top-right, 3: bottom-left, 4: bottom-right.
     * Returns 0 if the click was not in the corner.
     * @param pointX int - mouse x coordinate.
     * @param pointY int - mouse y coordinate.
     * @return int - corner number the mouse was clicked in.
     */
    @Override
    public int controlRectContains( int pointX, int pointY ) {
        return 0;
    } // controlRectContains

    @Override
    public void setPosition( int x, int y ) {
    } // setPosition

    @Override
    public boolean contains( int pointX, int pointY ) {
        return false;
    } // contains

    /**
     * Calculates the distance of a point from a line give by 2 points.
     * @param x1 int
     * @param y1 int
     * @param x2 int
     * @param y2 int
     * @param pointX int
     * @param pointY int
     * @return float
     */
    float calcDistance( int x1, int y1, int x2, int y2, int pointX, int pointY ) {
        int calc1 = ( pointX - x1 ) * ( x2 - x1 ) + ( pointY - y1 ) * ( y2 - y1 );
        int calc2 = ( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 );

        float U = (float) calc1 / (float) calc2;

        float intersectX = x1 + U * ( x2 - x1 );
        float intersectY = y1 + U * ( y2 - y1 );

        double distance = Math.sqrt( ( pointX - intersectX ) * ( pointX - intersectX ) + ( pointY - intersectY ) * ( pointY - intersectY ) );

        double distanceFromEnd1 = Math.sqrt( ( x1 - pointX ) * ( x1 - pointX ) + ( y1 - pointY ) * ( y1 - pointY ) );
        double distanceFromEnd2 = Math.sqrt( ( x2 - pointX ) * ( x2 - pointX ) + ( y2 - pointY ) * ( y2 - pointY ) );
        double lineLength = Math.sqrt( ( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 ) );

        if ( lineLength < Math.max( distanceFromEnd1, distanceFromEnd2 ) ) {
            distance = Math.max( Math.min( distanceFromEnd1, distanceFromEnd2 ), distance );
        }
        return (float) distance;
    } // pointDistanceFromLine

    /**
     * Return a specification of the shape to be written into a file in XML format.
     * @param boundingboxX - x coordinate of the bounding box.
     * @param boundingboxY - y coordinate of the bounding box.
     * @return String - specification of a shape.
     */
    @Override
    public String toFile( int boundingboxX, int boundingboxY ) {

        return null;
    } // toFile

    @Override
    public String toText() {
        return null;
    } // toText

    @Override
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2 ) {
        g2.setColor( getColor() );
        g2.setStroke( getStroke() );

        int[] x = new int[xPoints.length];
        int[] y = new int[xPoints.length];

        for ( int i = 0; i < xPoints.length; i++ ) {
            if ( xFixed[i] == 0 )
                x[i] = (int) ( xModifier + Xsize * xPoints[i] );
            else if ( xFixed[i] == -1 )
                x[i] = xModifier + xPoints[i];
            else
                x[i] = (int) ( xModifier + Xsize * xPoints[i] ) - xFixed[i];

            if ( yFixed[i] == 0 )
                y[i] = (int) ( yModifier + Ysize * yPoints[i] );
            else if ( yFixed[i] == -1 )
                y[i] = yModifier + yPoints[i];
            else
                y[i] = (int) ( yModifier + Ysize * yPoints[i] ) - yFixed[i];
        }

        if ( isFilled() ) {
            g2.fillPolygon( x, y, xPoints.length );
        } else {
            g2.drawPolygon( x, y, xPoints.length );
        }

        if ( isSelected() ) {
            drawSelection( g2 );
        }

    } // draw

    @Override
    public Polygon clone() {
        return (Polygon) super.clone();
    } // clone

    @Override
    public Shape getCopy() {
        throw new IllegalStateException( "Copying not implemented for Polygon" );
    }

    /**
     * @param xPoints the xPoints to set
     */
    void setXPoints( int[] xPoints ) {
        this.xPoints = xPoints;
    }

    /**
     * @return the xPoints
     */
    int[] getXPoints() {
        return xPoints;
    }

    /**
     * @param yPoints the yPoints to set
     */
    void setYPoints( int[] yPoints ) {
        this.yPoints = yPoints;
    }

    /**
     * @return the yPoints
     */
    int[] getYPoints() {
        return yPoints;
    }

    /**
     * @param xFixed the xFixed to set
     */
    void setXFixed( int[] xFixed ) {
        this.xFixed = xFixed;
    }

    /**
     * @return the xFixed
     */
    int[] getXFixed() {
        return xFixed;
    }

    /**
     * @param yFixed the yFixed to set
     */
    void setYFixed( int[] yFixed ) {
        this.yFixed = yFixed;
    }

    /**
     * @return the yFixed
     */
    int[] getYFixed() {
        return yFixed;
    }

}
