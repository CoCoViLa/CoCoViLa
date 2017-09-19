package ee.ioc.cs.vsle.graphics;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.*;

import java.awt.*;

public class Oval extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    private double rotation = 0.0;

    public Oval( int x, int y, int width, int height, Color color, boolean fill, float strokeWidth, float lineType ) {
        this( x, y, width, height, color, fill, strokeWidth, lineType, false );
    } // Oval

    public Oval( int x, int y, int width, int height, Color color, boolean fill, float strokeWidth, float lineType, boolean fixed ) {
        super( x, y, width, height );
        setColor( color );
        setFilled( fill );
        setStroke( strokeWidth, lineType );
        setFixed( fixed );
    } // Oval
    
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

        return "<oval x=\"" + ( getX() - boundingboxX ) + "\" y=\"" + ( getY() - boundingboxY ) + "\" width=\"" + getWidth()
                + "\" height=\"" + getHeight() + "\" colour=\"" + colorInt + "\" filled=\"" + Boolean.toString( isFilled() ) + "\" fixed=\"" + isFixed()
                + "\" stroke=\"" + (int) getStroke().getLineWidth() + "\" linetype=\"" + this.getLineType() + "\" transparency=\""
                + getTransparency() + "\"/>\n";
    } // toFile

    @Override
    public String toText() {

        int colorInt = 0;

        if ( getColor() != null )
            colorInt = getColor().getRGB();

        return "OVAL:" + getX() + ":" + getY() + ":" + getWidth() + ":" + getHeight() + ":" + colorInt + ":" + Boolean.toString( isFilled() ) + ":"
                + (int) getStroke().getLineWidth() + ":" + this.getLineType() + ":" + getTransparency() + ":" + isFixed();
    } // toText

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
        if ( pointX >= getX() && pointY >= getY() + getHeight() - 4 && pointX <= getX() + 4 && pointY <= getY() + getHeight() ) {
            return 3;
        }
        if ( pointX >= getX() + getWidth() - 4 && pointY >= getY() + getHeight() - 4 && pointX <= getX() + getWidth()
                && pointY <= getY() + getHeight() ) {
            return 4;
        }
        return 0;
    } // controlRectContains

    @Override
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2 ) {
        g2.setStroke( getStroke() );
        g2.setColor( getColor() );

        // Get dimensions. If fixed, do not multiply with size.
        if( isFixed() ) {
            Xsize = 1;
            Ysize = 1;
        }
        int a = xModifier + (int) ( Xsize * getX() );
        int b = yModifier + (int) ( Ysize * getY() );
        int c = (int) ( Xsize * getWidth() );
        int d = (int) ( Ysize * getHeight() );

        // Draw or fill the shape.
        if ( isFilled() ) {
            g2.fillOval( a, b, c, d );
        } else {
            g2.drawOval( a, b, c, d );
        }

        // Draw selection if needed.
        if ( isSelected() ) {
            drawSelection( g2 );
        }

    } // draw

    @Override
    public Oval clone() {
        return (Oval) super.clone();
    } // clone

    @Override
    public Shape getCopy() {
        return new Oval( getX(), getY(), getWidth(), getHeight(), getColor(), 
                isFilled(), getStrokeWidth(), getLineType() );
    }

    /**
     * @param rotation the rotation to set
     */
    void setRotation( double rotation ) {
        this.rotation = rotation;
    }

    /**
     * @return the rotation
     */
    double getRotation() {
        return rotation;
    }

}
