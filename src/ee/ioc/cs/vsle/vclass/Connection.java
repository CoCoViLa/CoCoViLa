package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.VMath;
import ee.ioc.cs.vsle.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.awt.Graphics;

/**

 */
public class Connection implements Serializable {

	public Port beginPort;
	public Port endPort;
	public int numOfBreakPoints = 0;
	public boolean selected;
	public ArrayList breakPoints = new ArrayList();
	public static boolean objActive = false;

	/**
	 * Class constructor.
	 */
	public Connection() {
	} // ee.ioc.cs.editor.vclass.Connection

	/**
	 * Class constructor.
	 * @param beginPort ee.ioc.cs.editor.vclass.Port
	 * @param endPort ee.ioc.cs.editor.vclass.Port
	 */
	public Connection(Port beginPort, Port endPort) {
		this.beginPort = beginPort;
		this.endPort = endPort;
	} // ee.ioc.cs.editor.vclass.Connection

	/**
	 * Find, if point is on or near the relation line.
	 * @param pointX int - x coordinate of the point.
	 * @param pointY int - y coordinate of the point.
	 * @return float - distance between the point and the relation line.
	 */
	float distanceFromPoint(int pointX, int pointY) {
		Point p1;
		Point p2;
		float distance;
		float minDistance = 100;

		for (int i = 0; i < breakPoints.size() - 1; i++) {
			p1 = (Point) breakPoints.get(i);
			p2 = (Point) breakPoints.get(i + 1);
			distance = VMath.pointDistanceFromLine(p1.x, p1.y, p2.x, p2.y, pointX, pointY);
			if (distance < minDistance) {
				minDistance = distance;
			}
		}

		return minDistance;
	} // distanceFromPoint

	/**
	 * Calculates the distance of a point from a line give by 2 points.
	 * @param x1 int
	 * @param y1 int
	 * @param x2 int
	 * @param y2 int
	 * @param pointX int
	 * @param pointY int
	 * @return float
	 */

	/**
	 * Adds a new breakpoint to the connection line.
	 * The breakpoint locations are recalculated to cover the connection line equally.
	 */
	public void addBreakPoint() {
		if (numOfBreakPoints < 2) {
			numOfBreakPoints++;
		}
		calcEndBreakPoints();
	} // addBreakPoint

	/**
	 * Adds a new breakpoint to the connection line into a specific location.
	 * @param p Point
	 */
	public void addBreakPoint(Point p) {
		breakPoints.add(p);
	} // addBreakPoint

	/**
	 * Removes a breakpoint from the connection line.
	 */
	public void removeBreakPoint() {
		if (numOfBreakPoints > 0) {
			numOfBreakPoints--;
		}
		calcEndBreakPoints();
	} // removeBreakPoint

	/**
	 * Calculates breakpoint locations to cover the connection line equally.
	 */
	public void calcAllBreakPoints() {
		Point p;
		if (breakPoints.size() > 1) {
			int port1X = beginPort.getX() + beginPort.obj.getX();
			int port1Y = beginPort.getY() + beginPort.obj.getY();
			Point oldPoint1 = (Point) breakPoints.get(0);
			int oldx = oldPoint1.x;
			int oldy = oldPoint1.y;

			for (int i = 0; i < breakPoints.size(); i++) {

				p = (Point) breakPoints.get(i);
				p.x += (port1X - oldx);
				p.y += (port1Y - oldy);

				/*
				if (p.x == oldPoint1.x) {
					p.x = port1X;
				} else if (p.x == oldPoint2.x) {
					p.x = port2X;
				}
				if (p.y == oldPoint1.y) {
					p.y = port1Y;
				} else if (p.y == oldPoint2.y) {
					p.y = port2Y;
				}*/
			}
		}

		/* if (numOfBreakPoints == 1) {
		 if (beginPort.obj.getX()+beginPort.obj.getRealWidth() == port1X  || beginPort.obj.getX() == port1X) {
		 break1X = port2X;
		 break1Y = port1Y;
		 } else {
		 break1X = port1X;
		 break1Y = port2Y;
		 }
		 } else if (numOfBreakPoints == 2) {
		 if (beginPort.obj.getX()+ beginPort.obj.getRealWidth() == port1X  ||
		 endPort.obj.getX()+ endPort.obj.getRealWidth() == port2X) {
		 break1X = port1X + (port2X-port1X)/2;
		 break1Y = port1Y;
		 break2X = port1X + (port2X-port1X)/2;
		 break2Y = port2Y;

		 } else {
		 break1X = port1X;
		 break1Y = port1Y + ((port2Y-port1Y)/2);
		 break2X = port2X ;
		 break2Y = port1Y + ((port2Y-port1Y)/2);

		 }
		 }*/
	} // calcBreakPoints

	public void calcEndBreakPoints() {
		if (breakPoints.size() > 1) {
			int port1X = beginPort.getX() + beginPort.obj.getX();
			int port1Y = beginPort.getY() + beginPort.obj.getY();
			int port2X = endPort.getX() + endPort.obj.getX();
			int port2Y = endPort.getY() + endPort.obj.getY();
			Point oldPoint1 = (Point) breakPoints.get(0);
			Point oldPoint2 = (Point) breakPoints.get(breakPoints.size() - 1);

			oldPoint1.x += (port1X - oldPoint1.x);
			oldPoint1.y += (port1Y - oldPoint1.y);

			oldPoint2.x += (port2X - oldPoint2.x);
			oldPoint2.y += (port2Y - oldPoint2.y);


		}
	}

	/**
	 * <UNCOMMENTED>
	 * @param x int
	 * @param y int
	 * @return Point
	 */
	public Point breakPointContains(int x, int y) {
		Point p;

		for (int i = 1; i < breakPoints.size() - 1; i++) {
			p = (Point) breakPoints.get(i);
			if (x > p.x - 3 && x < p.x + 3 && y > p.y - 3 && y < p.y + 3) {
				return p;
			}
		}
		return null;
	} // breakPointContains

	public String toXML() {
		String xml = "<connection obj1=\"" + beginPort.obj.name + "\" port1=\"" + beginPort +
			"\" obj2=\"" + endPort.obj.name + "\" port2=\"" + endPort + "\">\n";
		xml += "  <breakpoints>\n";
		for (int i = 0; i < breakPoints.size(); i++) {
			Point point = (Point) breakPoints.get(i);
			xml += StringUtil.indent(4) + "<point x=\"" + point.x + " y=\"" + point.y + "\"/>\n";
		}
		xml += "  </breakpoints>\n";
		xml += "</connection>\n";
		return xml;

	}

	/**
	 * Draw the connection line.
	 * @param g Graphics
	 */
	public void drawRelation
		(Graphics
		g) {
		Point p1, p2;

		for (int i = 0; i < breakPoints.size() - 1; i++) {
			p1 = (Point) breakPoints.get(i);
			p2 = (Point) breakPoints.get(i + 1);
			if (i != 0 && selected) {
				g.drawRect(p1.x - 2, p1.y - 2, 4, 4);
			}
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	} // drawRelation

}
