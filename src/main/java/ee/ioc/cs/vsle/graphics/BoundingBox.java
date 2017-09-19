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

import java.awt.*;
import java.io.*;

public class BoundingBox extends Shape implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String name = "BoundingBox";
    private static final Color BB_COLOR = new Color( Color.lightGray.getRed(), Color.lightGray.getGreen(), Color.lightGray.getBlue(), 200 );
    
    public BoundingBox( int x, int y, int width, int height ) {
        super( x, y, width, height );
        this.setColor( BB_COLOR );
        this.setFilled( true );
    } // BoundingBox

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
