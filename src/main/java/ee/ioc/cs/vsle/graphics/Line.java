package ee.ioc.cs.vsle.graphics;

import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;

import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.Point;

public class Line extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    private int endX;
    private int endY;   

    public Line( int x1, int y1, int x2, int y2, Color color, float strokeWidth, float lineType ) {
    	/* top to bottom direction */	
    	super(x1 - Math.min( x1, x2 ), 0, Math.abs(x1-x2), Math.max( y1, y2 ) - Math.min( y1, y2 ) );
    	setEndX(x2 - Math.min( x1, x2));    	
        setEndY( height );
        /* bottom to top direction */
        if(y2<y1){
        	  setX(x2 - Math.min( x1, x2 ));
        	  setEndX(x1 - Math.min( x1, x2 ));
    	}
        setColor( color );
        setStroke( strokeWidth, lineType );
    } // Line


    
    @Override
    public boolean isInside( int x1, int y1, int x2, int y2 ) {
       /* int minx = Math.min( getX(), getEndX() );
        int miny = Math.min( getX(), getY() );
        int maxx = Math.max( getX(), getEndX() );
        int maxy = Math.max( getX(), getY() );

        if ( x1 > minx && y1 > miny && x2 < maxx && y2 < maxy ) {
            return true;
        }*/
        return false;
    } // isInside

   
    public void flip(){    	
    	
    	 //	System.out.println("flip incoming  y1 = " + getY() + ", y2 = "+ getEndY() + "; x1 = " + getX() + ", x2 = "+ getEndX());
    	
   		if(getX() == 0){
    		setX(getEndX());
    		setEndX(0);
    	} else {
    		setEndX(getX());
    		setX(0);
    	}
    	
    }

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
        setX( getX() * (int) s1 / (int) s2 );
        setY( getY() * (int) s1 / (int) s2 );
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
                this.setX( this.getX() + deltaW );
                this.setY( this.getY() + deltaH );
            } else if ( cornerClicked == 2 ) { // TOP-RIGHT
                setEndX( getEndX() + deltaW );
                setEndY( getEndY() + deltaH );
            }
        }
    } // resize

    
    @Override
    public void setPosition( int x, int y ) {
        setEndX( getEndX() + x );
        setX( getX() + x );
        setEndY( getEndY() + y );
        setY( getY() + y );
    } // setPosition

   
  
    public String compareCoords(int i, String s){
    	int temp = tryParse(s); 
    	if( temp != i){
    		s = s.replace(temp+"", i+"");
    		return s;
    	}
    	else return s;
    }
  
    
    @Override
    public boolean contains( int pointX, int pointY ) {
        float distance = calcDistance( getX(), getY(), getEndX(), getEndY(), pointX, pointY );
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
        setX( x1 );
        setY( y1 );
        setEndX( x2 );
        setEndY( y2 );
    } // setLine


    
    public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

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

        return "<line x1=\"" + ( getX() - boundingboxX ) + "\" y1=\"" + ( getY() - boundingboxY ) + "\" x2=\"" + ( getEndX() - boundingboxX )
                + "\" y2=\"" + ( getEndY() - boundingboxY ) + "\" colour=\"" + colorInt + "\" fixed=\"" + isFixed() + "\" stroke=\""
                + (int) getStroke().getLineWidth() + "\" linetype=\"" + this.getLineType() + "\" transparency=\"" + getTransparency()
                + "\"/>\n";
    } // toFile

    @Override
    public String toText() {
        int colorInt = 0;
        if ( getColor() != null )
            colorInt = getColor().getRGB();
        return "LINE:" + getX() + ":" + getY() + ":" + getEndX() + ":" + getEndY() + ":" + colorInt + ":" + (int) getStroke().getLineWidth() + ":"
                + getLineType() + ":" + getTransparency() + ":" + isFixed();
    } // toText


    public void drawSelection( Graphics2D g2, float scale ,int xModifier, int yModifier, float Xsize, float Ysize) {
    	switch (getRatio()) {
		case 1:
		
			g2.fillRect( (int) ( Xsize * getX()) + xModifier - GObj.CORNER_SIZE/2,  yModifier - GObj.CORNER_SIZE - 1, GObj.CORNER_SIZE, GObj.CORNER_SIZE );
		
			g2.fillRect(  (int) ( Xsize * getEndX() ) + xModifier - GObj.CORNER_SIZE/2,  yModifier + (int) ( Ysize * getEndY() ) + 2, GObj.CORNER_SIZE, GObj.CORNER_SIZE );
			break;
		case 2:	
		
			g2.fillRect( (int) ( Xsize * getX()) + xModifier - GObj.CORNER_SIZE - 1 + (getEndX() < getX()?GObj.CORNER_SIZE+2:0) ,  yModifier - GObj.CORNER_SIZE/2, GObj.CORNER_SIZE, GObj.CORNER_SIZE );
			g2.fillRect( (int) ( Xsize * getEndX() ) + xModifier + (getEndX() < getX()?(-1 - GObj.CORNER_SIZE):2),  (int)( Ysize * getEndY() ) + yModifier -  GObj.CORNER_SIZE/2, GObj.CORNER_SIZE, GObj.CORNER_SIZE );
			
			break;
		default:
			break;
		}    
    }
    
    public int insideRectSelection(GObj obj, Point p){
    	/*case 1; top */
    	if ( ( p.x >= obj.getX() +getX() - GObj.CORNER_SIZE/2 ) && ( p.y >= obj.getY() - GObj.CORNER_SIZE - 1 ) ) {
            if ( ( p.y <=  obj.getY() + GObj.CORNER_SIZE + 1 )  &&  p.x <= obj.getX() + getX() + GObj.CORNER_SIZE/2) {
                return 1;
            }
        } 
    	//  System.out.println("1 " + (obj.getX() - GObj.CORNER_SIZE/2) + " < " + p.x+ " < " + (obj.getX() + GObj.CORNER_SIZE/2));
    	/* case 2; x1 > x2  top */
    	if ( ( p.x >= obj.getX() - GObj.CORNER_SIZE - 1  ) && ( p.y >= obj.getY() - GObj.CORNER_SIZE/2 ) ) {
            if ( ( p.y <=  obj.getY() + GObj.CORNER_SIZE/2 )  &&  p.x <= obj.getX() + 2) {
                return 1;
            }
        }
    	
    	/*case 2; x2 > x1  top*/
    	if ( ( p.x >= obj.getX() + getX() +1 ) && ( p.y >= obj.getY() - GObj.CORNER_SIZE/2 ) ) {
            if ( ( p.y <=  obj.getY() + GObj.CORNER_SIZE/2 )  &&  p.x <= obj.getX()+ getX() + GObj.CORNER_SIZE + 2) {
                return 1;
            }
        } 
    	
    	/* extra*/
    	if ( ( p.x >= obj.getX()  -  GObj.CORNER_SIZE - 1 ) && ( p.y >= obj.getY() + getEndY() - GObj.CORNER_SIZE/2 ) ) {
            if ( ( p.y <=  obj.getY() + getEndY() + GObj.CORNER_SIZE/2 )  &&  p.x <= obj.getX()+ getX() -1 ) {
                return 2;
            }
        } 
    	
    	/*case 1; bottom */
    	if ( ( p.x >= (obj.getX() + getEndX() -  GObj.CORNER_SIZE/2) ) && ( p.y >= obj.getY() + getEndY() + 2) ) {
            if ( ( p.y <= obj.getY() + getEndY() + 2 + GObj.CORNER_SIZE)  &&  p.x <= (obj.getX() + getEndX() +  GObj.CORNER_SIZE/2)) {
                return 2;
            }
        } 
    	
    	/* case 2; x1 > x2  bottom */
    	if ( ( p.x >= (obj.getX() + Math.max(getX(), getEndX()) +1 ) ) && ( p.y >= obj.getY() + getEndY() -  GObj.CORNER_SIZE/2) ) {
            if ( (  p.y <= obj.getY() + getEndY() +  GObj.CORNER_SIZE/2)  &&  p.x <= (obj.getX() + Math.max(getX(), getEndX()) +1 +  GObj.CORNER_SIZE)) {
                return 2;
            }
        } 
    	
    	/*case 2; x2 > x1  bottom*/
    	if ( ( p.x >= (obj.getX() + getEndX() - 2 ) ) && ( p.y >= obj.getY() + getEndY() -  GObj.CORNER_SIZE/2) ) {
            if ( (  p.y <= obj.getY() + getEndY() +  GObj.CORNER_SIZE/2)  &&  p.x <= (obj.getX() + getEndX() - 2 +  GObj.CORNER_SIZE)) {
                return 2;
            }
        } 
    	
    	return 0;
    }
    
    
    public int getRatio(){
    	if(Math.abs(getX() - getEndX()) == 0)
    			return 1;
    	if(Math.abs(getY() - getEndY()) == 0)
			return 2;
    	
    	/* mostly vertical */    
    	if( ( (Math.abs(getX() - getEndX())) / Math.abs(getY() - getEndY())) < 1)
    			return 1;
    	/* mostly horizontal */
    	if( ( (Math.abs(getX() - getEndX())) / Math.abs(getY() - getEndY())) >= 1)
    		return 2;
    	return 1;    	
    }

    @Override
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2 ) {
        g2.setColor( getColor() );
        g2.setStroke( getStroke() );

        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        
        x1 = xModifier + (int) ( Xsize * getX() ) ;
        x2 = xModifier + (int) ( Xsize * this.endX ) ;
        y1 = yModifier + (int) ( Ysize * getY() ) ;
        y2 = yModifier + (int) ( Ysize * this.endY ) ;
      
        
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
        setX( getX() + offsetX );
        setY( getY() + offsetY );
        setEndX( getEndX() + offsetX );
        setEndY( getEndY() + offsetY );
    }

    /**
     * @return (endY - startY) / (endX - startX)
     */    
    public double getK() {
        double k = (double) (getEndY() - getY()) / (getEndX() - getX());
        if (Double.isInfinite(k) || Double.isNaN(k)) {
            k = 0;
        }
        return k;
    }    

    private int tryParse(String s){
    	try {
    		int i = ((Number)NumberFormat.getInstance().parse(s)).intValue();
    		return i;
    	} catch (ParseException e) {
			return 1;
		}
    }
    
    @Override
    public Shape getCopy() {
        return new Line( getX(), getY(), getEndX(), getEndY(), getColor(), getStrokeWidth(), getLineType() );
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
}
