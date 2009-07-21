package ee.ioc.cs.vsle.graphics;

import java.io.*;

import java.awt.*;
import java.awt.geom.*;

public class Text extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Text string represented by the shape.
     */
    private int fixedX;
    private int fixedY;
    private String text;
    private Font font;
    private int h; //* Height of the text.
    private int w;//	 * Width of the text.

    public Text( int x, int y, Font font, Color color, int transp, String s ) {
        super( x, y );
        this.setFont( font );
        this.setColor( new Color( color.getRed(), color.getGreen(), color.getBlue(), transp ) );
        this.setText( s );

    } // Text

    public Text( int x, int y, Font font, Color color, int transp, String s, boolean fixed ) {
        super( x, y );
        this.setFont( font );
        this.setColor( new Color( color.getRed(), color.getGreen(), color.getBlue(), transp ) );
        this.setText( s );
        setFixed( fixed );
    } // Text

    /**
     * Set size using zoom multiplication.
     * @param s1 float - set size using zoom multiplication.
     * @param s2 float - set size using zoom multiplication.
     */
    @Override
    public void setMultSize( float s1, float s2 ) {
        setX( getX() * (int) s1 / (int) s2 );
        setY( getY() * (int) s1 / (int) s2 );
        this.w = this.w * (int) s1 / (int) s2;
        this.h = this.h * (int) s1 / (int) s2;
        int fontsize = getFont().getSize() * (int) s1 / (int) s2;

        this.setFont( new Font( getFont().getName(), getFont().getStyle(), fontsize ) );
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
     * Returns the used font.
     * @return Font - used font.
     */
    public Font getFont() {
        return this.font;
    } // getFont

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
        if ( pointX >= getX() && pointY >= getY() && pointX <= getX() + 4 && pointY <= getY() + 4 ) {
            return 1;
        }
        if ( pointX >= getX() + getWidth() - 4 && pointY >= getY() && pointX <= getX() + getWidth() && pointY <= getY() + 4 ) {
            return 2;
        }
        if ( pointX >= getX() && pointY >= getY() + getHeight() / 2 - 4 && pointX <= getX() + 4 && pointY <= getY() + getHeight() / 2 ) {
            return 3;
        }
        if ( pointX >= getX() + getWidth() - 4 && pointY >= getY() + getHeight() / 2 - 4 && pointX <= getX() + getWidth()
                && pointY <= getY() + getHeight() / 2 ) {
            return 4;
        }
        return 0;
    } // controlRectContains

    /**
     * Returns the text string.
     * @return String - the text string.
     */
    public String getText() {
        return this.text;
    } // getText

    /**
     * Set string font.
     * @param f Font - string font.
     */
    public void setFont( Font f ) {
        this.font = f;
    } // setFont

    /**
     * Set string text.
     * @param s String - string text.
     */
    public void setText( String s ) {
        this.text = s;
    } // setText

    /**
     * @param fixedX the fixedX to set
     */
    public void setFixedX( int fixedX ) {
        this.fixedX = fixedX;
    }

    /**
     * @return the fixedX
     */
    public int getFixedX() {
        return fixedX;
    }

    @Override
    public boolean contains( int pointX, int pointY ) {
        if ( pointX >= getX() && pointX <= getX() + this.w && pointY >= getY() - this.h && pointY <= getY() ) {
            return true;
        }
        return false;
    } // contains

    @Override
    public boolean isInside( int x1, int y1, int x2, int y2 ) {
        return getX() >= x1 && getY() >= y1 && getX() + this.w <= x2 && getY() - this.h <= y2;
    } // isInside

    @Override
    public boolean isInsideRect( int x1, int y1, int x2, int y2 ) {
        if ( x1 < getX() && y1 < getY() && x2 > getX() + this.w && y2 > getY() + this.h ) {
            return true;
        }
        return false;
    } // isInsideRect

    /**
     * Draw the selection markers if object selected.
     */
    public void drawSelection( Graphics2D g2, Rectangle r ) {
        g2.setColor( Color.black );
        g2.setStroke( new BasicStroke( (float) 1.0 ) );
        g2.drawRect( r.x -1, r.y - r.height + 1, r.width, r.height );
    } // drawSelection

    /**
     * Specify the line type used at drawing the shape.
     * @param lineType int
     */
    public void setLineType( int lineType ) {
    } // setLineType

    /**
     * Return a specification of the shape to be written into a file in XML format.
     * @param boundingboxX - x coordinate of the bounding box.
     * @param boundingboxY - y coordinate of the bounding box.
     * @return String - specification of a shape.
     */
    @Override
    public String toFile( int boundingboxX, int boundingboxY ) {
        int colorInt = 0;

        if ( getColor() != null )
            colorInt = getColor().getRGB();

        return "<text string=\"" + getText() + "\" colour=\"" + colorInt + "\" x=\"" + ( getX() - boundingboxX ) + "\" y=\""
                + ( getY() - boundingboxY ) + "\" fontname=\"" + getFont().getName() + "\" fontstyle=\"" + getFont().getStyle() + "\" fontsize=\""
                + getFont().getSize() + "\" transparency=\"" + getTransparency() + "\"/>\n";
    } // toFile

    @Override
    public String toText() {
        int colorInt = 0;

        if ( getColor() != null )
            colorInt = getColor().getRGB();

        return "TEXT:" + getX() + ":" + getY() + ":" + colorInt + ":" + getFont().getName() + ":" + getFont().getStyle() + ":" + getFont().getSize() + ":"
                + getColor().getTransparency() + ":" + getText();
    } // toText

    @Override
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2 ) {

        java.awt.font.FontRenderContext frc = g2.getFontRenderContext();

        Rectangle2D r = this.getFont().getStringBounds( getText(), 0, getText().length(), frc );

        this.h = (int) r.getHeight();
        this.w = (int) r.getWidth();

        Font origFont = g2.getFont();
        
        g2.setFont( getFont() );
        g2.setColor( getColor() );

        int a = xModifier + (int) ( Xsize * getX() );
        int b = yModifier + (int) ( Ysize * getY() );

        g2.drawString( getText(), a, b );

        if ( isSelected() ) {
            drawSelection( g2, new Rectangle( a, b, w, h ) );
        }
        
        g2.setFont( origFont );

    } // draw

    public void drawSpecial( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2, String name, String value, double angle ) {

        java.awt.font.FontRenderContext frc = g2.getFontRenderContext();

        Rectangle2D r = this.getFont().getStringBounds( getText(), 0, getText().length(), frc );

        this.h = (int) r.getHeight();
        this.w = (int) r.getWidth();
        if ( !isFixed() ) {
            g2.setFont( getFont().deriveFont( (float) Math.sqrt( Xsize * Ysize ) * getFont().getSize() ) );
        } else {
            g2.setFont( getFont() );
        }
        g2.setColor( getColor() );

        /*g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
        		java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);*/

        //int a = xModifier + (int) (Xsize * x * Math.cos(angle));
        //int b = yModifier + (int) (Ysize * y + (Xsize * x * Math.sin(angle)));
        int a = 0, b = 0;
        if ( getFixedX() == 0 )
            a = xModifier + (int) ( Xsize * getX() * Math.cos( angle ) );
        else if ( getFixedX() == -1 )
            a = xModifier + (int) ( getX() * Math.cos( angle ) );
        else
            a = xModifier + (int) ( Xsize * getX() * Math.cos( angle ) ) - getFixedX();

        if ( getFixedY() == 0 )
            b = yModifier + (int) ( Ysize * getY() + ( Xsize * getX() * Math.sin( angle ) ) );
        else if ( getFixedY() == -1 )
            b = yModifier + (int) ( getY() + ( Xsize * getX() * Math.sin( angle ) ) );
        else
            b = yModifier + (int) ( Ysize * getY() + ( Xsize * getX() * Math.sin( angle ) ) ) - getFixedY();

        //	g2.translate(xModifier, yModifier);
        //		g2.rotate(-1*angle);
        //		g2.translate(-1 * (xModifier), -1 * (yModifier));

        if ( getText().equals( "*self" ) )
            g2.drawString( value, a, b );
        else if ( getText().equals( "*selfWithName" ) )
            g2.drawString( name + " = " + value, a, b );
        else
            g2.drawString( getText(), a, b );

        if ( isSelected() ) {
            drawSelection( g2, r.getBounds() );
        }

        //		g2.translate(xModifier, yModifier);
        //		g2.rotate(angle);
        //		g2.translate(-1 * (xModifier), -1 * (yModifier));

    }

    @Override
    public Text clone() {
        return (Text) super.clone();
    } // clone

    @Override
    public Shape getCopy() {
        return new Text( getX(), getY(), getFont(), getColor(), getTransparency(), getText() );
    }

    /**
     * @param fixedY the fixedY to set
     */
    public void setFixedY( int fixedY ) {
        this.fixedY = fixedY;
    }

    /**
     * @return the fixedY
     */
    public int getFixedY() {
        return fixedY;
    }

}
