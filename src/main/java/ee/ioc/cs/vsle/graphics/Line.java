package ee.ioc.cs.vsle.graphics;

import java.awt.*;
import java.io.*;

public class Line extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int fixedX1;
    private int fixedX2;
    private int fixedY1;
    private int fixedY2;

    public Line( int x1, int y1, int x2, int y2, Color color, float strokeWidth, float lineType ) {
        super( Math.min( x1, x2 ), Math.min( y1, y2 ) );
        this.setWidth( Math.max( x1, x2 ) - this.getX() ); // endX - startX;
        this.setHeight( Math.max( y1, y2 ) - this.getY() ); // endY - startY;

        setStartX( x1 );
        setStartY( y1 );
        setEndX( x2 );
        setEndY( y2 );
        setColor( color );

        setStroke( strokeWidth, lineType );
    } // Line

    @Override
    public boolean isInside( int x1, int y1, int x2, int y2 ) {
        int minx = Math.min( getStartX(), getEndX() );
        int miny = Math.min( getStartX(), getStartY() );
        int maxx = Math.max( getStartX(), getEndX() );
        int maxy = Math.max( getStartX(), getStartY() );

        if ( x1 > minx && y1 > miny && x2 < maxx && y2 < maxy ) {
            return true;
        }
        return false;
    } // isInside

    @Override
    public boolean isInsideRect( int x1, int y1, int x2, int y2 ) {
        int minx = Math.min( getStartX(), getEndX() );
        int miny = Math.min( getStartY(), getEndY() );
        int maxx = Math.max( getStartX(), getEndX() );
        int maxy = Math.max( getEndY(), getStartY() );
        if ( x1 < minx && y1 < miny && x2 > maxx && y2 > maxy ) {
            return true;
        }
        return false;
    } // isInsideRect

    /**
     * Set size using zoom multiplication.
     * 
     * @param s1
     *            float - set size using zoom multiplication.
     * @param s2
     *            float - set size using zoom multiplication.
     */
    @Override
    public void setMultSize( float s1, float s2 ) {
        setStartX( getStartX() * (int) s1 / (int) s2 );
        setStartY( getStartY() * (int) s1 / (int) s2 );
        setEndX( getEndX() * (int) s1 / (int) s2 );
        setEndY( getEndY() * (int) s1 / (int) s2 );
        setWidth( getWidth() * (int) s1 / (int) s2 );
        setHeight( getHeight() * (int) s1 / (int) s2 );
    } // setMultSize

    /**
     * Returns the transparency of the shape.
     * 
     * @return double - the transparency of the shape.
     */

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
    @Override
    public void resize( int deltaW, int deltaH, int cornerClicked ) {
        if ( !isFixed() ) {
            if ( cornerClicked == 1 ) { // TOP-LEFT
                this.setStartX( this.getStartX() + deltaW );
                this.setStartY( this.getStartY() + deltaH );
            } else if ( cornerClicked == 2 ) { // TOP-RIGHT
                setEndX( getEndX() + deltaW );
                setEndY( getEndY() + deltaH );
            }
        }
    } // resize

    /**
     * Returns the number representing a corner the mouse was clicked in. 1:
     * top-left, 2: top-right, 3: bottom-left, 4: bottom-right. Returns 0 if the
     * click was not in the corner.
     * 
     * @param pointX
     *            int - mouse x coordinate.
     * @param pointY
     *            int - mouse y coordinate.
     * @return int - corner number the mouse was clicked in.
     */
    @Override
    public int controlRectContains( int pointX, int pointY ) {
        //db.p( pointX + " " + pointY + " " + endX + " " + endY + " " + startX + " " + startY + " " );
        if ( pointX >= getStartX() - 2 && pointY >= getStartY() - 2 && pointX <= getStartX() + 2 && pointY <= getStartY() + 2 ) {
            return 1;
        }
        if ( pointX >= getEndX() - 2 && pointY >= getEndY() - 2 && pointX <= getEndX() + 2 && pointY <= getEndY() + 2 ) {
            return 2;
        }
        return 0;
    } // controlRectContains

    @Override
    public void setPosition( int x, int y ) {
        setEndX( getEndX() + x );
        setStartX( getStartX() + x );
        setEndY( getEndY() + y );
        setStartY( getStartY() + y );
    } // setPosition

    @Override
    public boolean contains( int pointX, int pointY ) {
        float distance = calcDistance( getStartX(), getStartY(), getEndX(), getEndY(), pointX, pointY );
        if ( distance <= 3 ) {
            return true;
        }
        return false;

    } // contains

    /**
     * Calculates the distance of a point from a line give by 2 points.
     * 
     * @param x1
     *            int
     * @param y1
     *            int
     * @param x2
     *            int
     * @param y2
     *            int
     * @param pointX
     *            int
     * @param pointY
     *            int
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

    void setLine( int x1, int y1, int x2, int y2 ) {
        setStartX( x1 );
        setStartY( y1 );
        setEndX( x2 );
        setEndY( y2 );
    } // setLine

    public int getStartX() {
        return startX;
    } // getStartX

    public int getStartY() {
        return startY;
    } // getStartY

    /**
     * Returns the line end x coordinate.
     * 
     * @return int - line end x coordinate.
     */
    public int getEndX() {
        return endX;
    } // getEndX

    /**
     * Returns the line end y coordinate.
     * 
     * @return int - line end y coordinate.
     */
    public int getEndY() {
        return endY;
    } // getEndY

    int getX1() {
        return getStartX();
    } // getX1

    int getY1() {
        return getStartY();
    } // getY1

    int getX2() {
        return getEndX();
    } // getX2

    int getY2() {
        return getEndY();
    } // getY2

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
    @Override
    public String toFile( int boundingboxX, int boundingboxY ) {
        int colorInt = 0;

        if ( getColor() != null )
            colorInt = getColor().getRGB();

        return "<line x1=\"" + ( getStartX() - boundingboxX ) + "\" y1=\"" + ( getStartY() - boundingboxY ) + "\" x2=\"" + ( getEndX() - boundingboxX )
                + "\" y2=\"" + ( getEndY() - boundingboxY ) + "\" colour=\"" + colorInt + "\" fixed=\"" + isFixed() + "\" stroke=\""
                + (int) getStroke().getLineWidth() + "\" linetype=\"" + this.getLineType() + "\" transparency=\"" + getTransparency()
                + "\"/>\n";
    } // toFile

    @Override
    public String toText() {
        int colorInt = 0;
        if ( getColor() != null )
            colorInt = getColor().getRGB();
        return "LINE:" + getStartX() + ":" + getStartY() + ":" + getEndX() + ":" + getEndY() + ":" + colorInt + ":" + (int) getStroke().getLineWidth() + ":"
                + getLineType() + ":" + getTransparency() + ":" + isFixed();
    } // toText

    /**
     * Draw the selection markers if object selected.
     * 
     * @param g2
     *            Graphics2D - shape graphics.
     */
    @Override
    public void drawSelection( Graphics2D g2 ) {
        g2.setStroke( new BasicStroke( 2 ) );
        g2.setColor( Color.black );
        g2.setStroke( new BasicStroke( (float) 1.0 ) );
        g2.fillRect( getStartX() - 2, getStartY() - 2, 4, 4 );
        g2.fillRect( getEndX() - 2, getEndY() - 2, 4, 4 );
    } // drawSelection

    @Override
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2 ) {
        g2.setColor( getColor() );
        g2.setStroke( getStroke() );

        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        if ( getFixedX1() == -1 )
            x1 = xModifier + getStartX();
        else
            x1 = xModifier + (int) ( Xsize * getStartX() ) - getFixedX1();

        if ( getFixedX2() == -1 )
            x2 = xModifier + getEndX();
        else
            x2 = xModifier + (int) ( Xsize * getEndX() ) - getFixedX2();

        if ( getFixedY1() == -1 )
            y1 = yModifier + getStartY();
        else
            y1 = yModifier + (int) ( Ysize * getStartY() ) - getFixedY1();

        if ( getFixedY2() == -1 )
            y2 = yModifier + getEndY();
        else
            y2 = yModifier + (int) ( Ysize * getEndY() ) - getFixedY2();

        g2.drawLine( x1, y1, x2, y2 );

        if ( isSelected() ) {
            drawSelection( g2 );
        }

    } // draw

    @Override
    public Line clone() {
        return (Line) super.clone();
    } // clone

    public void shift( int offsetX, int offsetY ) {
        setStartX( getStartX() + offsetX );
        setStartY( getStartY() + offsetY );
        setEndX( getEndX() + offsetX );
        setEndY( getEndY() + offsetY );
    }

    /**
     * @return (endY - startY) / (endX - startX)
     */    
    public double getK() {
        double k = (double) (getEndY() - getStartY()) / (getEndX() - getStartX());
        if (Double.isInfinite(k) || Double.isNaN(k)) {
            k = 0;
        }
        return k;
    }    

    
    @Override
    public Shape getCopy() {
        return new Line( getStartX(), getStartY(), getEndX(), getEndY(), getColor(), getStrokeWidth(), getLineType() );
    }

    /**
     * @param startX the startX to set
     */
    public void setStartX( int startX ) {
        this.startX = startX;
    }

    /**
     * @param startY the startY to set
     */
    public void setStartY( int startY ) {
        this.startY = startY;
    }

    /**
     * @param endX the endX to set
     */
    public void setEndX( int endX ) {
        this.endX = endX;
    }

    /**
     * @param endY the endY to set
     */
    public void setEndY( int endY ) {
        this.endY = endY;
    }

    /**
     * @param fixedX1 the fixedX1 to set
     */
    public void setFixedX1( int fixedX1 ) {
        this.fixedX1 = fixedX1;
    }

    /**
     * @return the fixedX1
     */
    public int getFixedX1() {
        return fixedX1;
    }

    /**
     * @param fixedX2 the fixedX2 to set
     */
    public void setFixedX2( int fixedX2 ) {
        this.fixedX2 = fixedX2;
    }

    /**
     * @return the fixedX2
     */
    public int getFixedX2() {
        return fixedX2;
    }

    /**
     * @param fixedY1 the fixedY1 to set
     */
    public void setFixedY1( int fixedY1 ) {
        this.fixedY1 = fixedY1;
    }

    /**
     * @return the fixedY1
     */
    public int getFixedY1() {
        return fixedY1;
    }

    /**
     * @param fixedY2 the fixedY2 to set
     */
    public void setFixedY2( int fixedY2 ) {
        this.fixedY2 = fixedY2;
    }

    /**
     * @return the fixedY2
     */
    public int getFixedY2() {
        return fixedY2;
    }

}
