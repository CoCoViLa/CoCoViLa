package ee.ioc.cs.vsle.util;

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

import java.awt.geom.Line2D;

import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.vclass.Port;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 30.03.2004
 * Time: 0:08:38
 */
public class VMath {

	public static Point nearestPointOnRectangle(int x, int y, int width, int height, int pointX, int pointY) {
        float min;
		Point p1 = nearestPointOnLine(x + width, y, x + width, y + height, pointX, pointY);
		float p1Length = distanceBetweenPoints(p1, pointX, pointY);

		Point p2 =  nearestPointOnLine(x, y, x, y + height, pointX, pointY);
		float p2Length = distanceBetweenPoints(p2, pointX, pointY);
        min = Math.min(p1Length, p2Length);

		Point p3 = nearestPointOnLine(x, y + height, x + width, y + height, pointX, pointY);
		float p3Length = distanceBetweenPoints(p3, pointX, pointY);
		min = Math.min(p3Length, min);

		Point p4 = nearestPointOnLine(x, y, x + width, y, pointX, pointY);
		float p4Length = distanceBetweenPoints(p4, pointX, pointY);
		min = Math.min(p4Length, min);

        if (p1Length == min)
			return p1;
		else if (p2Length == min)
			return p2;
        else if (p3Length == min)
			return p3;
		else
			return p4;
	}


	public static Point nearestPointOnRectangle(int x, int y, int width, int height, int pointX, int pointY, double perc) {
		float min;
		Point p1 = nearestPointOnLine(x + width, y, x + width, y + height, pointX, pointY);
		float p1Length = distanceBetweenPoints(p1, pointX, pointY);

		Point p2 =  nearestPointOnLine(x, y, x, y + height, pointX, pointY);
		float p2Length = distanceBetweenPoints(p2, pointX, pointY);
		min = Math.min(p1Length, p2Length);

		Point p3 = nearestPointOnLine(x, y + height, x + width, y + height, pointX, pointY);
		float p3Length = distanceBetweenPoints(p3, pointX, pointY);
		min = Math.min(p3Length, min);

		Point p4 = nearestPointOnLine(x, y, x + width, y, pointX, pointY);
		float p4Length = distanceBetweenPoints(p4, pointX, pointY);
		min = Math.min(p4Length, min);

		if (p1Length == min)
			return p1;
		else if (p2Length == min)
			return p2;
		else if (p3Length == min)
			return p3;
		else
			return p4;

	}




	public static Point nearestPointOnLine(int x1, int y1, int x2, int y2, int pointX, int pointY) {
		double dot_ta, dot_tb;
		dot_ta = (pointX - x1) * (x2 - x1) + (pointY - y1) * (y2 - y1);
		if (dot_ta <= 0) // IT IS OFF THE AVERTEX
		{
			return new Point(x1, y1);
		}
		dot_tb = (pointX - x2) * (x1 - x2) + (pointY - y2) * (y1 - y2);
		// SEE IF b IS THE NEAREST POINT - ANGLE IS OBTUSE
		if (dot_tb <= 0) {
			return new Point(x2, y2);
		}
		// FIND THE REAL NEAREST POINT ON THE LINE SEGMENT -      BASED ON RATIO
		pointX = (int) (x1 + ((x2 - x1) * dot_ta) / (dot_ta + dot_tb));
		pointY = (int) (y1 + ((y2 - y1) * dot_ta) / (dot_ta + dot_tb));
		return new Point(pointX, pointY);
	}

	/**
	 * calculates the distance of the point from the line given by 4 points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param pointX
	 * @param pointY
	 * @return distance
	 */
	public static float pointDistanceFromLine(int x1, int y1, int x2, int y2, int pointX, int pointY) {
		Point p = nearestPointOnLine(x1, y1, x2, y2, pointX, pointY);
		double distance = Math.sqrt(Math.pow((p.x - pointX), 2.0) + Math.pow((p.y - pointY) , 2.0));
		return (float) distance;
	}



	public static float distanceBetweenPoints(Point p1, Point p2) {
		double distance = Math.sqrt(Math.pow((p1.x - p2.x), 2.0) + Math.pow((p1.y - p2.y) , 2.0));
		return (float) distance;
	}

    public static float distanceBetweenPoints(Point p1, int x, int y) {
		double distance = Math.sqrt(Math.pow((p1.x - x), 2.0) + Math.pow((p1.y - y) , 2.0));
		return (float) distance;
	}

	/**
	 * calculates the angle between x axis and the line give by the 4 points
	 * @param startX
	 * @param startY
	 * @param x
	 * @param y
	 * @return angle
	 */
	public static double calcAngle(int startX, int startY, int x, int y) {
		int realX = x - startX;
		int realY = y - startY;
		if (realX == 0 && realY ==0)
			return 0.0;
		if (realX >= 0 && realY >= 0) {
			return (Math.atan((double) realY / (double) realX));
		}

		if (realX <= 0 && realY >= 0) {
			return (Math.atan((double) realY / (double) realX) + Math.PI);
		}

		if (realX < 0 && realY <= 0) {
			return (Math.atan((double) realY / (double) realX) + Math.PI);
		}

		if (realX >= 0 && realY <= 0) {
			if (realX == 0)
				return Math.PI + Math.PI / 2;
			return (Math.atan((double) realY / (double) realX) + 2 * Math.PI);
		}
		return 0.0;
	}

	/**
	 * Calculates the intersection point of two line segments.
	 * 
	 * If the two line segments intersect then {@code true} is returned
	 * and the X and Y coordinates of the intersection point are stored in
	 * the point {@code p}. Otherwise, {@code false} is returned and
	 * the point {@code p} is not modified.
	 * @param point the point where the calculated values are stored
     * @param x1 the X coordinate of the start point of the first
     * 			 line segment
     * @param y1 the Y coordinate of the start point of the first
     * 			 line segment
     * @param x2 the X coordinate of the end point of the first
     *           line segment
     * @param y2 the Y coordinate of the end point of the first
     *           line segment
     * @param x3 the X coordinate of the start point of the second
     *           line segment
     * @param y3 the Y coordinate of the start point of the second
     *           line segment
     * @param x4 the X coordinate of the end point of the second
     *           line segment
     * @param y4 the Y coordinate of the end point of the second
     *           line segment
	 * @return <code>true</code> if the line segments intersect,
	 * 			<code>false</code> otherwise.
	 */
	public static boolean calcLineLineIntersection(Point point,
			double x1, double y1, double x2, double y2,
			double x3, double y3, double x4, double y4) {
		
		if (!Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4))
			return false;
		
		double x12 = x1 - x2;
		double x34 = x3 - x4;
		double y12 = y1 - y2;
		double y34 = y3 - y4;

		double d3 = x12 * y34 - y12 * x34;
		
		if (d3 == 0.0)
			return false;
		
		double d1 = x1 * y2 - y1 * x2;
		double d2 = x3 * y4 - y3 * x4;
		
		point.x = (int) ((d1 * x34 - x12 * d2) / d3 + 0.5);
		point.y = (int) ((d1 * y34 - y12 * d2) / d3 + 0.5);
		
		return true;
	}

	/**
	 * Calculates end point coordinates for relation classes.
	 * 
	 * @param sp
	 *            start port of the relation class
	 * @param ep
	 *            end point of the relation class, can be null
	 * @param ex
	 *            alternative X coordinate of the end port if the real end port
	 *            is not known yet
	 * @param ey
	 *            alternative Y coordinate of the end port if the real end port
	 *            is not known yet
	 * @return point containing the coordinate values of the end point of the
	 *         relation class at the start port end.
	 */
	private static Point getRelClassStartPoint(Port sp, Port ep,
			int ex, int ey) {

		Point endPoint = new Point(0, 0);

		if (!sp.isArea()) {
			endPoint.x = sp.getAbsoluteX();
			endPoint.y = sp.getAbsoluteY();
		} else {
			// Find the intersection point of the line segment connecting
			// the center points of the connected objects and the bounding
			// box of the first object. There is no such point when the
			// center point of the second object is inside the first object.
			int px1 = sp.getObject().getX()
					+ sp.getObject().getRealWidth() / 2;
			int py1 = sp.getObject().getY()
					+ sp.getObject().getRealHeight() / 2;
			int px2 = ep == null ? ex : ep.getObject().getX()
					+ ep.getObject().getRealWidth() / 2;
			int py2 = ep == null ? ey : ep.getObject().getY()
					+ ep.getObject().getRealHeight() / 2;
		
			int x1 = sp.getObject().getX();
			int y1 = sp.getObject().getY();
			int x2 = x1 + sp.getObject().getRealWidth();
			int y2 = y1 + sp.getObject().getRealHeight();
		
			if (calcLineLineIntersection(endPoint,
						px1, py1, px2, py2,
						x1, y1, x2, y1)
					|| calcLineLineIntersection(endPoint,
						px1, py1, px2, py2,
						x2, y1, x2, y2)
					|| calcLineLineIntersection(endPoint,
						px1, py1, px2, py2,
						x2, y2, x1, y2)
					|| calcLineLineIntersection(endPoint,
						px1, py1, px2, py2,
						x1, y2, x1, y1)) {

				// the value is now in endPoint
			} else {
				endPoint = VMath.nearestPointOnRectangle(
						x1, y1, x2 - x1, y2 - y1,
						ep == null ? ex : ep.getAbsoluteX(),
						ep == null ? ey : ep.getAbsoluteY());
			}
		}
		return endPoint;
	}

	/**
	 * Calculates start point coordinates for a relation class
	 * connecting ports startPort and endPort.
	 * @param startPort the start port of the relation class
	 * @param endPort the end port of the relation class
	 * @return point containing the coordinates of the start point
	 */
	public static Point getRelClassStartPoint(Port startPort, Port endPort) {
		return getRelClassStartPoint(startPort, endPort, 0, 0);
	}

	/**
	 * Calculates start point coordinates for a relation class
	 * connected to startPort and not yet connected to an endPort.
	 * Temporary endPort coordinates are specified by the point (ex, ey)
	 * which can be for example the location of the mouse cursor.
	 * @param startPort the start port of the relation class
	 * @param ex the X coordinate of the end point of the relation class
	 * @param ey the Y coordinate of the end point of the relation class
	 * @return point containing the coordinates of the start point 
	 */
	public static Point getRelClassStartPoint(Port startPort, int ex, int ey) {
		return getRelClassStartPoint(startPort, null, ex, ey);
	}
}
