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

		if (pointX >= x + width) {
			return nearestPointOnLine(x + width, y, x + width, y + height, pointX, pointY);
		}

		if (pointX <= x) {
			return nearestPointOnLine(x, y, x, y + height, pointX, pointY);
		}

		if (pointY >= y + height) {
			return nearestPointOnLine(x, y + height, x + width, y + height, pointX, pointY);
		}

		if (pointY <= y) {
			return nearestPointOnLine(x, y, x + width, y, pointX, pointY);
		}

		return nearestPointOnLine(x, y + height, x + width, y + height, pointX, pointY);

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
	public static float calcDistance(int x1, int y1, int x2, int y2, int pointX, int pointY) {
		Point p = nearestPointOnLine(x1, y1, x2, y2, pointX, pointY);
		double distance = Math.sqrt(Math.pow((p.x - pointX), 2.0) + Math.pow((p.y - pointY) , 2.0));
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
