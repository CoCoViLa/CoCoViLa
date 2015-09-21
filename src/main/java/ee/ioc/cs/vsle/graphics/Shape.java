package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;
import java.awt.*;

public abstract class Shape implements Serializable, Cloneable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7707715861477842170L;
	private int x;
    private int y;
    protected int width;
    protected int height;
    private boolean selected = false;
    private String name;
    private boolean fixed = false;
    private boolean filled = false;
    protected boolean allowResize = true;
    private Color color;
    private BasicStroke stroke;
    private boolean field = false;
    private boolean fieldDefault = false;

    public Shape( int x, int y ) {
        this.setX( x );
        this.setY( y );
    }

    public Shape( int x, int y, int width, int height ) {
        this( x, y );
        this.setWidth( width );
        this.setHeight( height );
    }
    
    public boolean isFieldDefault() {
		return fieldDefault;
	}

	public void setFieldDefault(boolean fieldDefault) {
		this.fieldDefault = fieldDefault;
	}

	public boolean isField() {
		return field;
	}

	public void setField(boolean field) {
		this.field = field;
	}

	public ArrayList<Shape> getShapes() {
        return null;
    }

    public boolean isFilled() {
        return filled;
    } // isFilled

    public boolean isSelected() {
        return selected;
    } // isSelected

    public void setSelected( boolean b ) {
        this.selected = b;
    } // setSelected

    public int getX() {
        return this.x;
    } // getX

    public int getY() {
        return this.y;
    } // getY

    public boolean isAllowResize() {
		return allowResize;
	}

	public void setAllowResize(boolean allowResize) {
		this.allowResize = allowResize;
	}

	/**
         * Returns width of the shape.
         * @return int - shape width.
         */
    public int getWidth() {
        return width;
    } // getWidth

    /**
     * Returns height of the shape.
     * @return int - shape height.
     */
    public int getHeight() {
        return height;
    } // getHeight

    /**
     * Returns the name of the shape.
     * 
     * @return String - the name of the shape.
     */
    public String getName() {
        return this.name;
    } // getName

    /**
     * Specify the name of the shape.
     * 
     * @param s
     *            String - the name of the shape.
     */
    public void setName( String s ) {
        this.name = s;
    } // setName

    /**
     * Set the shape dimensions fixed in which case the shape cannot be resized.
     * 
     * @param b boolean - fix or unfix the shape.
     */
    public void setFixed( boolean b ) {
        this.fixed = b;
    } // setFixed

    /**
     * Returns a boolean value representing if the shape is fixed or not.
     * 
     * @return boolean - boolean value representing if the shape is fixed or
     *         not.
     */
    public boolean isFixed() {
        return this.fixed;
    } // isFixed.

    /**
     * Set the color of a shape.
     * 
     * @param col Color - color of a shape.
     */
    public void setColor( Color col ) {
        this.color = col;
    } // setColor

    /**
     * Returns the color of the arc.
     * 
     * @return Color - color of the arc.
     */
    public Color getColor() {
        return this.color;
    } // getColor

    /**
     * Returns the real height of the shape.
     * 
     * @return int - the real height of the shape.
     */
    public int getRealHeight() {
        return getHeight();
    } // getRealHeight

    /**
     * Returns the real width of the shape.
     * 
     * @return int - the real width of the shape.
     */
    public int getRealWidth() {
        return getWidth();
    } // getRealWidth

    public BasicStroke getStroke() {
        return stroke;
    }

    public abstract void draw( int _x, int _y, float Xsize, float Ysize, Graphics2D g );


    /**
     * Resizes current object.
     * @param deltaW int - change of object with.
     * @param deltaH int - change of object height.
     * @param cornerClicked int - number of the clicked corner.
     */
    public void resize( int deltaW, int deltaH, int cornerClicked ) {
        if ( !isFixed() ) {
            if ( cornerClicked == 1 ) { // TOP-LEFT
                if ( this.getWidth() - deltaW > 0 && this.getHeight() - deltaH > 0 ) {
                    this.setX( this.getX() + deltaW );
                    this.setY( this.getY() + deltaH );
                    this.setWidth( this.getWidth() - deltaW );
                    this.setHeight( this.getHeight() - deltaH );
                }
            } else if ( cornerClicked == 2 ) { // TOP-RIGHT
                if ( this.getWidth() + deltaW > 0 && this.getHeight() - deltaH > 0 ) {
                    this.setY( this.getY() + deltaH );
                    this.setWidth( this.getWidth() + deltaW );
                    this.setHeight( this.getHeight() - deltaH );
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
     * Draw the selection markers if object selected.
     * @param g2 - graphics.
     */
    protected void drawSelection( Graphics2D g2 ) {
        g2.setColor( Color.black );
        g2.setStroke( new BasicStroke( (float) 1.0 ) );
        g2.fillRect( getX(), getY(), 4, 4 );
        g2.fillRect( getX() + getWidth() - 4, getY(), 4, 4 );
        g2.fillRect( getX(), getY() + getHeight() - 4, 4, 4 );
        g2.fillRect( getX() + getWidth() - 4, getY() + getHeight() - 4, 4, 4 );
    } // drawSelection
    
    public abstract String toFile( int boundingboxX, int boundingboxY );

    public abstract String toText();

    public abstract Shape getCopy();
    
    @Override
    public Shape clone() {
        try {
            return (Shape) super.clone();
        } catch ( Exception e ) {
            return null;
        }
    }

    public void setStrokeWidth( float sW ) {
        
        if( getStroke() != null ) {
            setStroke( sW, getLineType() );
        }
    }

    public void setLineType( float lineType ) {
        if( getStroke() != null ) {
            setStroke( getStrokeWidth(), lineType );
        }
    }

    public float getStrokeWidth() {
        return getStroke() != null ? getStroke().getLineWidth() : 1.0f;
    }

    public float getLineType() {
        if ( getStroke() != null && getStroke().getDashArray() != null ) {
            return getStroke().getDashArray()[0];
        }

        return 0.0f;
    } // getLineType

    public int getTransparency() {
        return getColor() != null ? getColor().getAlpha() : 0;
    } // getTransparency

    /**
     * Set shape position.
     */
    public void setPosition( int x, int y ) {
        this.setX( getX() + x );
        this.setY( getY() + y );
    } // setPosition

    public boolean contains( int pointX, int pointY ) {
        return ( pointX > getX() && pointY > getY() && pointX < getX() + getWidth() && pointY < getY() + getHeight() );
    } // contains

    /**
     * Returns a boolean value representing if the mouse was clicked inside the
     * shape.
     */
    public boolean isInside( int x1, int y1, int x2, int y2 ) {
        return ( x1 > getX() && y1 > getY() && x2 < getX() + getWidth() && y2 < getY() + getHeight() );
    } // isInside

    /**
     * Returns a boolean value representing if the shape is in the selection
     * rectangle.
     */
    public boolean isInsideRect( int x1, int y1, int x2, int y2 ) {
        return ( x1 < getX() && y1 < getY() && x2 > getX() + getWidth() && y2 > getY() + getHeight() );
    } // isInsideRect

    /**
     * Set size using zoom multiplication.
     */
    public void setMultSize( float s1, float s2 ) {
        setX( getX() * (int) s1 / (int) s2 );
        setY( getY() * (int) s1 / (int) s2 );
        setWidth( getWidth() * (int) s1 / (int) s2 );
        setHeight( getHeight() * (int) s1 / (int) s2 );
    } // setMultSize

    /**
     * Returns the number representing a corner the mouse was clicked in.
     * 1: top-left, 2: top-right, 3: bottom-left, 4: bottom-right.
     * Returns 0 if the click was not in the corner.
     * @param pointX int - mouse x coordinate.
     * @param pointY int - mouse y coordinate.
     * @return int - corner number the mouse was clicked in.
     */
    public int controlRectContains( int pointX, int pointY ) {
        if( isFixed() ) {
            return 0;
        }
        
        if ( pointX >= getX() && pointY >= getY() && pointX <= getX() + 4 && pointY <= getY() + 4 ) {
            return 1;
        }
        if ( pointX >= getX() + getWidth() - 4 && pointY >= getY() && pointX <= getX() + getWidth() && pointY <= getY() + 4 ) {
            return 2;
        }
        if ( pointX >= getX() && pointY >= getY() + getHeight() - 4 && pointX <= getX() + 4 && pointY <= getY() + getHeight() ) {
            return 3;
        }
        if ( pointX >= getX() + getWidth() - 4 && pointY >= getY() + getHeight() - 4 && pointX <= getX() + getWidth()
                && pointY <= getY() + getHeight() ) {
            return 4;
        }
        return 0;
    } // controlRectContains

    /**
     * Returns the name of the shape. In future implementations should return
     * the textual representation of the shape, ie. the return value of the
     * currently implemented "toText" method.
     * 
     * @return String - the name of the shape.
     */
    @Override
    public String toString() {
        return getName();
    } // toString

    /**
     * @param x the x to set
     */
    public void setX( int x ) {
        this.x = x;
    }

    /**
     * @param y the y to set
     */
    public void setY( int y ) {
        this.y = y;
    }

    /**
     * @param width the width to set
     */
    public void setWidth( int width ) {
        this.width = width;
    }

    /**
     * @param height the height to set
     */
    public void setHeight( int height ) {
        this.height = height;
    }

    /**
     * @param filled the filled to set
     */
    protected void setFilled( boolean filled ) {
        this.filled = filled;
    }

    /**
     * @param stroke the stroke to set
     */
    public void setStroke( float strokeWidth, float lineType ) {
        
        if ( lineType > 0 ) {
            this.stroke = new BasicStroke( strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 50, 
                    new float[] { lineType, lineType }, 0 );
        } else {
            this.stroke = new BasicStroke( strokeWidth );
        }
    }
    
    public static Color createColor( int rgb, int alpha ) {
        
        return createColorWithAlpha( new Color( rgb ), alpha );
    }
    
    public static Color createColorWithAlpha( Color color, int alpha ) {
        
        return ( alpha == 255 ) 
                ? color 
                : new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha );
    }

    
    public void updateShapeAsField(Shape s, String name, boolean fieldDefault) {    
    	s.setField(true);
    	s.setFieldDefault(fieldDefault);
    	s.setName(name);
    }
   
}