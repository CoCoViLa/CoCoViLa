package ee.ioc.cs.vsle.util;

import ee.ioc.cs.vsle.vclass.Point;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 30.03.2004
 * Time: 0:08:38
 * To change this template use Options | File Templates.
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
	 * @return
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
	 * @return
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

}
