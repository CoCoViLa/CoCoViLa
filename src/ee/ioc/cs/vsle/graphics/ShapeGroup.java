package ee.ioc.cs.vsle.graphics;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ShapeGroup extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name = "GROUP";

    private ArrayList<Shape> shapes;

    public ShapeGroup() {
        this( new ArrayList<Shape>() );
    } // ShapeGroup
    
    public ShapeGroup( ArrayList<Shape> shapes ) {
        super( 0, 0 );
        this.shapes = shapes;
        
        setBounds();
    } // ShapeGroup
    
    public void drawSelection() {
    } // drawSelection

    @Override
    public String toText() {
        StringBuffer text = new StringBuffer();
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            text.append( shape.toText() );
        }
        return text.toString();
    } // toText

    @Override
    public void setFixed( boolean b ) {
        super.setFixed( b );
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            shape.setFixed( b );
        }
    } // setFixed

    @Override
    public void setStrokeWidth( float d ) {
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            shape.setStrokeWidth( d );
        }
    } // setStrokeWidth

    @Override
    public void setColor( Color col ) {
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            shape.setColor( col );
        }
    } // setColor

    @Override
    public void setTransparency( int d ) {
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            shape.setTransparency( d );
        }
    } // setTransparency

    /**
     * Specify the line type used at drawing the shape.
     * @param lineType float
     */
    @Override
    public void setLineType( float lineType ) {
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            shape.setLineType( lineType );
        }
    } // setLineType

    @Override
    public String toFile( int boundingboxX, int boundingboxY ) {
        StringBuffer text = new StringBuffer();
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            text.append( shape.toFile( boundingboxX, boundingboxY ) );
        }
        return text.toString();
    } // toFile

    @Override
    public int getRealHeight() {
        return 0;
    } // getRealHeight

    @Override
    public int getRealWidth() {
        return 0;
    } // getRealWidth

    @Override
    public void resize( int deltaW, int deltaH, int cornerClicked ) {
    } // resize

    @Override
    public int controlRectContains( int pointX, int pointY ) {
        return 0;
    } // controlRectContains

    public void removeAll() {
        shapes.removeAll( shapes );
    } // removeAll

    public void removeAll( ArrayList<Shape> a ) {
        shapes.removeAll( a );
    } // removeAll

    public void addAll( ArrayList<Shape> a ) {
        shapes.addAll( a );
    } // addAll

    public Shape checkInside( int x, int y ) {
        Shape shape;
        for ( int i = shapes.size() - 1; i >= 0; i-- ) {
            shape = shapes.get( i );
            if ( shape.contains( x, y ) ) {
                return shape;
            }
        }
        return null;
    } // checkInside

    public int indexOf( Shape s ) {
        for ( int i = 0; i < shapes.size(); i++ ) {
            if ( shapes.get( i ) == s ) {
                return i;
            }
        }
        return 0;
    } // indexOf

    public boolean isLocatedAtPoint( int x, int y ) {
        for ( int i = shapes.size() - 1; i >= 0; i-- ) {
            Shape shape = shapes.get( i );

            if ( shape.contains( x, y ) ) {
                return true;
            }
        }
        return false;
    } // isLocatedAtPoint

    public void sendToBack( Shape shape ) {
        shapes.remove( shape );
        shapes.add( 0, shape );
    } // sendToBack

    public void bringToFront( Shape shape ) {
        shapes.remove( shape );
        shapes.add( shape );
    } // bringToFront

    public void bringForward( Shape shape, int step ) {
        int shapeIndex = shapes.indexOf( shape );

        if ( shapeIndex + step < shapes.size() ) {
            shapes.remove( shape );
            shapes.add( shapeIndex + step, shape );
        }
    } // bringForward

    public void sendBackward( Shape shape, int step ) {
        int shapeIndex = shapes.indexOf( shape );

        if ( shapeIndex - step >= 0 ) {
            shapes.remove( shape );
            shapes.add( shapeIndex - step, shape );
        }
    } // sendBackward

    public void eraseShape( int x, int y ) {
        if ( isLocatedAtPoint( x, y ) ) {
            Shape theShape = checkInside( x, y );

            if ( theShape != null ) {
                if ( theShape.getName() != null ) {
                    if ( !theShape.getName().equals( BoundingBox.name ) ) {
                        shapes.remove( theShape );
                    }
                } else {
                    shapes.remove( theShape );
                }
            }
        }
    } // eraseShape

    public void clearSelected() {
        Shape shape;
        for ( int i = 0; i < shapes.size(); i++ ) {
            shape = shapes.get( i );
            shape.setSelected( false );
        }
    } // clearSelected

    public Shape checkInside( int x, int y, Shape asker ) {
        Shape shape;
        for ( int i = 0; i < shapes.size(); i++ ) {
            shape = shapes.get( i );
            if ( shape.contains( x, y ) && shape != asker ) {
                return shape;
            }
        }
        return null;
    } // checkInside

    public void add( Shape s ) {
        shapes.add( s );
    } // add

    public void removeAll( ShapeGroup sg ) {
        removeAll( sg.shapes );
    } // removeAll

    public void remove( Shape s ) {
        shapes.remove( s );
    } // remove

    public Shape get( int i ) {
        return shapes.get( i );
    } // get

    public int size() {
        return shapes.size();
    } // size

    public ArrayList<Shape> getSelected() {
        ArrayList<Shape> a = new ArrayList<Shape>();
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            if ( shape != null && shape.isSelected() ) {
                a.add( shape );
            }
        }
        return a;
    } // getSelected

    void setBounds() {
        int x1, x2, y1, y2;
        Shape shape;
        if ( shapes != null && shapes.size() > 0 ) {
            shape = shapes.get( 0 );
            x1 = shape.getX();
            y1 = shape.getY();
            x2 = shape.getX() + shape.getRealWidth();
            y2 = shape.getY() + shape.getRealHeight();

            for ( int i = 1; i < shapes.size(); i++ ) {
                shape = shapes.get( i );
                if ( shape.getX() < x1 ) {
                    x1 = shape.getX();
                }
                if ( shape.getY() < y1 ) {
                    y1 = shape.getY();
                }
                if ( shape.getY() + shape.getRealHeight() > y2 ) {
                    y2 = shape.getY() + shape.getRealHeight();
                }
                if ( shape.getX() + shape.getRealWidth() > x2 ) {
                    x2 = shape.getX() + shape.getRealWidth();

                }
            }
            setX( x1 );
            setY( y1 );
            setHeight( y2 - y1 );
            setWidth( x2 - x1 );
        }
    } // setBounds

    @Override
    public boolean contains( int pointX, int pointY ) {
        if ( ( pointX > getX() ) && ( pointY > getY() ) ) {
            if ( ( pointX < getX() + getWidth() ) && ( pointY < getY() + getHeight() ) ) {
                return true;
            }
        }
        return false;
    } // contains

    @Override
    public boolean isInside( int x1, int y1, int x2, int y2 ) {
        if ( x1 > getX() && y1 > getY() && x2 < getX() + getWidth() && y2 < getY() + getHeight() ) {
            return true;
        }
        return false;
    } // isInside

    @Override
    public boolean isInsideRect( int x1, int y1, int x2, int y2 ) {
        if ( x1 < getX() && y1 < getY() && x2 > getX() + getWidth() && y2 > getY() + getHeight() ) {
            return true;
        }
        return false;
    } // isInsideRect

    @Override
    public ArrayList<Shape> getShapes() {
        return this.shapes;
    } // getShapes

    @Override
    public void setPosition( int deltaX, int deltaY ) {
        Shape shape;
        for ( int j = 0; j < shapes.size(); j++ ) {
            shape = shapes.get( j );
            if ( shape instanceof ShapeGroup && shape.isSelected() ) {
                for ( int k = 0; k < shape.getShapes().size(); k++ ) {
                    Shape s = shape.getShapes().get( k );
                    s.setPosition( s.getX() + deltaX, s.getY() + deltaY );
                }
            } else {
                shape.setPosition( deltaX, deltaY );
            }
        }
        setXY();
    } // setPosition

    private void setXY() {
        int x = 0;
        int y = 0;
        for ( int i = 0; i < shapes.size(); i++ ) {
            Shape shape = shapes.get( i );
            if ( i == 0 ) {
                x = shape.getX();
                y = shape.getY();
            } else {
                if ( shape.getX() < x )
                    x = shape.getX();
                if ( shape.getY() < y )
                    y = shape.getY();
            }
        }
        this.setX( x );
        this.setY( y );
    } // setXY

    ArrayList<Shape> getComponents() {
        ArrayList<Shape> c = new ArrayList<Shape>();
        Shape shape;

        for ( int j = 0; j < shapes.size(); j++ ) {
            shape = shapes.get( j );
            c.add( shape );
        }
        return c;
    } // getComponents

    boolean includesObject( Shape checkObj ) {
        for ( int j = 0; j < shapes.size(); j++ ) {
            Shape shape = shapes.get( j );
            if ( shape == checkObj ) {
                return true;
            }
        }
        return false;
    } // includesObject

    @Override
    public void draw( int x, int y, float Xsize, float Ysize, Graphics2D g ) {
        Shape shape;
        for ( int j = 0; j < shapes.size(); j++ ) {
            shape = shapes.get( j );
            shape.draw( 0, 0, 1, 1, g );
        }
    } // draw

    @Override
    public void setMultSize( float s1, float s2 ) {
        for ( int j = 0; j < shapes.size(); j++ ) {
            Shape shape = shapes.get( j );
            shape.setMultSize( s1, s2 );
        }
    } // setMultSize

    @Override
    public String toString() {
        Shape shape;
        String s = name;

        for ( int j = 0; j < shapes.size(); j++ ) {
            shape = shapes.get( j );
            s += " " + shape;
        }
        return s;
    } // toString

    @Override
    public ShapeGroup clone() {
        ShapeGroup g = (ShapeGroup) super.clone();
        Shape shape;
        ArrayList<Shape> newList = new ArrayList<Shape>();

        for ( int j = 0; j < shapes.size(); j++ ) {
            shape = shapes.get( j );
            shape = shape.clone();
            newList.add( shape );
        }
        g.shapes = newList;
        return g;
    } // clone

    public void shift( int offsetX, int offsetY ) {
        Shape shape;
        for ( int i = 0; i < this.size(); i++ ) {
            shape = this.get( i );
            shape.setPosition( offsetX, offsetY );

        }
    }

    @Override
    public ShapeGroup getCopy() {
        
        ShapeGroup sl = new ShapeGroup( new ArrayList<Shape>() );

        for ( int i = 0; i < size(); i++ ) {

            Shape shape = get( i );
            boolean isFixed = shape.isFixed();
            sl.add( shape );

            if ( shape.isSelected() ) {

                shape.setSelected( false );
                
                if ( shape instanceof BoundingBox ) {
                    continue;
                }
                
                shape = shape.getCopy();
                
                shape.setSelected( true );
                
                shape.setPosition( 10, 10 );
                shape.setFixed( isFixed );
                sl.add( shape );
            }

        }
        
        return sl;
    }

}
