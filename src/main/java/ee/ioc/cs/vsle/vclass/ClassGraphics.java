package ee.ioc.cs.vsle.vclass;

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

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

import ee.ioc.cs.vsle.graphics.*;

public class ClassGraphics implements Serializable {

	private ArrayList<Shape> shapes = new ArrayList<Shape>();
	private double angle = 0.0;
	private int boundX;
	private int boundY;
	private int boundWidth;
	private int boundHeight;
	private boolean showFields = false;
	private boolean relation = false;//TODO not used?

	/**
	 * Set bounds of the graphical object.
	 * @param x int - x coordinate of the object starting corner.
	 * @param y int - y coordinate of the object starting corner.
	 * @param width int - object width (difference between starting and ending x coordinates).
	 * @param height int - object height (difference between starting and ending y coordinates).
	 */
	public void setBounds(int x, int y, int width, int height) {
		setBoundX( x );
		setBoundY( y );
		setBoundWidth( width );
		setBoundHeight( height );
	} // setBounds

	/**
	 * Add a new shape to the list of shapes.
	 * @param s Shape - shape to be added to the list of shapes.
	 */
	public void addShape(Shape s) {
		shapes.add(s);
	} // addShape

	/**
	 * Returns the width of the object (the difference between the object's beginning and
	 * end x coordinates).
	 * @return int - width of the object.
	 */
	public int getBoundWidth() {
		return boundWidth;
	} // getWidth

	/**
	 * Returns the height of the object (the difference between the object's beginning and
	 * end y coordinates).
	 * @return int - height of the object.
	 */
	public int getBoundHeight() {
		return boundHeight;
	} // getHeight

	/**
	 * Draw shape.
	 * @param xPos int - shape x coordinate value. Specifies the point to start the shape drawing from.
	 * @param yPos int - shape y coordinate value. Specifies the point to start the shape drawing from.
	 * @param Xsize float -
	 * @param Ysize float -
	 * @param g2 Graphics -
	 */
	public void draw(int xPos, int yPos, float Xsize, float Ysize, Graphics2D g2) {
		Shape s;

		for (int i = 0; i < shapes.size(); i++) {
			s = shapes.get(i);
			s.draw(xPos, yPos, Xsize, Ysize, g2);
		}

	} // draw

	void drawSpecial(int xPos, int yPos, float Xsize, float Ysize, Graphics2D g2, String name, String value) {
		Shape s;

		for (int i = 0; i < shapes.size(); i++) {
			s = shapes.get(i);
			if (s instanceof Text)
				((Text)s).drawSpecial(xPos, yPos, Xsize, Ysize, g2, name, value, getAngle());
			else
				s.draw(xPos, yPos, Xsize, Ysize, g2);
		}

	} // draw

    /**
     * @return the shapes
     */
    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    /**
     * @param angle the angle to set
     */
    public void setAngle( double angle ) {
        this.angle = angle;
    }

    /**
     * @return the angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * @param boundX the boundX to set
     */
    public void setBoundX( int boundX ) {
        this.boundX = boundX;
    }

    /**
     * @return the boundX
     */
    public int getBoundX() {
        return boundX;
    }

    /**
     * @param boundY the boundY to set
     */
    public void setBoundY( int boundY ) {
        this.boundY = boundY;
    }

    /**
     * @return the boundY
     */
    public int getBoundY() {
        return boundY;
    }

    /**
     * @param boundWidth the boundWidth to set
     */
    public void setBoundWidth( int boundWidth ) {
        this.boundWidth = boundWidth;
    }

    /**
     * @param boundHeight the boundHeight to set
     */
    public void setBoundHeight( int boundHeight ) {
        this.boundHeight = boundHeight;
    }

    /**
     * @param showFields the showFields to set
     */
    public void setShowFields( boolean showFields ) {
        this.showFields = showFields;
    }

    /**
     * @return the showFields
     */
    public boolean isShowFields() {
        return showFields;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation( boolean relation ) {
        this.relation = relation;
    }

    /**
     * @return the relation
     */
    public boolean isRelation() {
        return relation;
    }

}
