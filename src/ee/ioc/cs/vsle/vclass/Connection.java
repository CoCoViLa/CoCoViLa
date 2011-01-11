package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.VMath;
import ee.ioc.cs.vsle.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Connection between two ports.
 */
public class Connection implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The stroke used for selected connections.
	 */
	public static final Stroke SELECTED_STROKE = new BasicStroke(3.0f);

	/**
	 * If a point is closer than {@code NEAR_DISTANCE} to this relation
	 * line the point is considered to be close.
	 */
	public static final float NEAR_DISTANCE = 5.0f;
	
	private Port beginPort;
	private Port endPort;
	private boolean selected;
	private ArrayList<Point> breakPoints = new ArrayList<Point>();
	
	/**
	 * Is this an implicit connection between two strict ports 
	 * that were connected by placing them close to each other.
	 */
	private boolean strict;

	/**
	 * Class constructor. The second port is left disconnected by
	 * this constructor.
	 * @param beginPort the first port the connection is connected to
	 */
	public Connection(Port beginPort) {
		this.setBeginPort( beginPort );
	}

	/**
	 * Class constructor.
	 * @param beginPort the first port the connection is connected to
	 * @param endPort the second port the connection is connected to
	 */
	public Connection(Port beginPort, Port endPort) {
		this.setBeginPort( beginPort );
		this.setEndPort( endPort );
	} // ee.ioc.cs.editor.vclass.Connection

	/**
	 * Constructs optionally a strict connection.
	 * @param beginPort the first port the connection is connected to
	 * @param endPort the second port the connection is connected to
	 * @param strict if true the connections is strict
	 */
	public Connection(Port beginPort, Port endPort, boolean strict) {
		this.setBeginPort( beginPort );
		this.setEndPort( endPort );
		this.strict = strict;
	} // ee.ioc.cs.editor.vclass.Connection

	/**
	 * Find, if point is on or near the relation line.
	 * @param pointX x coordinate of the point
	 * @param pointY y coordinate of the point
	 * @return distance between the point and the relation line
	 */
	float distanceFromPoint(int pointX, int pointY) {
		return distanceFromPoint(pointX, pointY, null);
	}

	/**
	 * Find, if point is on or near the relation line.
	 * If {@code sindex} is non-{@code null} the index of the
	 * closest line segment is returned as the first value in this array.
	 * @param pointX x coordinate of the point.
	 * @param pointY y coordinate of the point.
	 * @param sindex an array that holds the line segment index returned
	 * 		  from this method
	 * @return distance between the point and the relation line.
	 */
	float distanceFromPoint(int pointX, int pointY, int[] sindex) {
		float minDistance;

		if (breakPoints == null || breakPoints.size() < 1) {
			// there is only one line between the start and end points
			minDistance = VMath.pointDistanceFromLine(getBeginPort().getAbsoluteX(),
					getBeginPort().getAbsoluteY(), getEndPort().getAbsoluteX(),
					getEndPort().getAbsoluteY(), pointX, pointY);
			if (sindex != null)
				sindex[0] = 0;
		} else {
			float distance;
			Point p1 = breakPoints.get(0);
			
			// line from start point to the first breakpoint
			minDistance = VMath.pointDistanceFromLine(getBeginPort().getAbsoluteX(),
					getBeginPort().getAbsoluteY(), p1.x, p1.y, pointX, pointY);
			if (sindex != null)
				sindex[0] = 0;

			Point p2;
			
			// lines between breapoints
			for (int i = 0; i < breakPoints.size() - 1; i++) {
				p1 = breakPoints.get(i);
				p2 = breakPoints.get(i + 1);
				distance = VMath.pointDistanceFromLine(p1.x, p1.y, p2.x, p2.y,
						pointX, pointY);
				if (distance < minDistance) {
					minDistance = distance;
					if (sindex != null)
						sindex[0] = i + 1;
				}
			}

			// line from last breakpoint to the end point
			p2 = breakPoints.get(breakPoints.size() - 1);

			distance = VMath.pointDistanceFromLine(p2.x, p2.y,
					getEndPort().getAbsoluteX(), getEndPort().getAbsoluteY(),
					pointX, pointY);

			if (distance < minDistance) {
				minDistance = distance;
				if (sindex != null)
					sindex[0] = breakPoints.size();
			}
		}

		return minDistance;
	} // distanceFromPoint

	/**
	 * Adds a new breakpoint to the connection line.
	 * The point is inserted at the specified index in the breakpoint list.
	 * If the specified index is negative then the point is appended to
	 * the list.
	 * @param index the index where the point is inserted
	 * @param p the point
	 */
	public void addBreakPoint(int index, Point p) {
		if (breakPoints == null)
			breakPoints = new ArrayList<Point>();
		
		if (index < 0)
			breakPoints.add(p);
		else
			breakPoints.add(index, p);
	} // addBreakPoint

	/**
	 * Appends a new point to the list of breakpoints.
	 * @param p Point
	 */
	public void addBreakPoint(Point p) {
		if (breakPoints == null)
			breakPoints = new ArrayList<Point>();
		
		breakPoints.add(p);
	} // addBreakPoint

	/**
	 * Removes the last breakpoint from the connection line.
	 */
	public void removeBreakPoint() {
		if (breakPoints != null && breakPoints.size() > 0)
			breakPoints.remove(breakPoints.size() - 1);
	} // removeBreakPoint

	/**
	 * Removes a breakpoint that is near the specified point.
	 * @param pointX X coordinate of the point
	 * @param pointY Y coordinate of the point
	 * @return <code>true</code> if a breakpoint was removed
	 */
	public boolean removeBreakPoint(int pointX, int pointY) {
		Point bp = breakPointContains(pointX, pointY);
		if (bp != null)
			return breakPoints.remove(bp);
		
		return false;
	}

	/**
	 * Removes a beakpoint at the specified index in the breakpoint list.
	 * @param index the index of the breakpoint to remove
	 * @return true if a breakpoint was removed, false otherwise
	 */
	public boolean removeBreakPoint(int index) {
	    if (breakPoints != null) {
	        return breakPoints.remove(index) != null;
	    }
	    return false;
	}

	/**
	 * Shifts the coordinates of the breakpoints by the specified amount. 
	 * @param dx change of the X coordinate
	 * @param dy change of the Y coordinate
	 */
	public void move(int dx, int dy) {
		if (breakPoints != null) {
			for (Point p : breakPoints) {
				p.x += dx;
				p.y += dy; 
			}
		}
	}

	/**
	 * Finds the breakpoint that contains the point (x, y).
	 * @param x X coordinate of the point
	 * @param y Y coordinate of the point
	 * @return the breakpoint that contains (x, y); null if there
	 * 		   is no such breakpoint.
	 */
	public Point breakPointContains(int x, int y) {
		Point p;

		if (breakPoints == null)
			return null;
		
		for (int i = 0; i < breakPoints.size(); i++) {
			p = breakPoints.get(i);
			if (x > p.x - 3 && x < p.x + 3 && y > p.y - 3 && y < p.y + 3) {
				return p;
			}
		}
		return null;
	} // breakPointContains

	/**
	 * Returns the list of breakpoints which can be empty or null if there
	 * are no breakpoints. Start and end points are not counted as breakpoints.
	 * @return the list of breakpoints
	 */
	public ArrayList<Point> getBreakPoints() {
		return breakPoints;
	}

	/**
	 * Generates SAX events that are necessary to serialize this object to XML.
	 * @param th the receiver of events
	 * @throws SAXException
	 */
	public void toXML(TransformerHandler th) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		
		attrs.addAttribute("", "", "obj1", StringUtil.CDATA,
				getBeginPort().getObject().getName());
		attrs.addAttribute("", "", "port1", StringUtil.CDATA,
				getBeginPort().getConnectionId());
		attrs.addAttribute("", "", "obj2", StringUtil.CDATA,
				getEndPort().getObject().getName());
		attrs.addAttribute("", "", "port2", StringUtil.CDATA,
				getEndPort().getConnectionId());

		th.startElement("", "", "connection", attrs);
		attrs.clear();
		th.startElement("", "", "breakpoints", attrs);

		if (breakPoints != null) {
			for (Point point : breakPoints) {
				attrs.clear();
				attrs.addAttribute("", "", "x", StringUtil.CDATA,
						Integer.toString(point.x));
				attrs.addAttribute("", "", "y", StringUtil.CDATA,
						Integer.toString(point.y));
				th.startElement("", "", "point", attrs);
				th.endElement("", "", "point");
			}
		}

		th.endElement("", "", "breakpoints");
		th.endElement("", "", "connection");
	}

	/**
	 * Draw the connection line. The connection can be drawn only when
	 * start and end ports are both present.
	 * @param g Graphics
	 */
	public void drawRelation(Graphics2D g) {
		drawRelation(g, getBeginPort().getAbsoluteX(), getBeginPort().getAbsoluteY(),
				getEndPort().getAbsoluteX(), getEndPort().getAbsoluteY());
	}

	/**
	 * Draw the connection line. The coordinates {@code endX}, {@code endY}
	 * are used instead of the coordinates of the end port for drawing
	 * the end point. This method can be used to draw connections with
	 * disconnected end port.
	 * @param g Graphics
	 * @param endX X coordinate of the end point
	 * @param endY Y coordinate of the end point
	 */
	public void drawRelation(Graphics2D g, int endX, int endY) {
		drawRelation(g, getBeginPort().getAbsoluteX(), getBeginPort().getAbsoluteY(),
				endX, endY);
	}

	/**
	 * Draws the connection. The connection and breakpoints are hilighted
	 * if the connection is selected.
	 * @param g Graphics
	 * @param startX X coordinate of the start point
	 * @param startY Y coordinate of the start point
	 * @param endX X coordinate of the end point
	 * @param endY Y coordinate of the end point
	 */
	private void drawRelation(Graphics2D g, int startX, int startY,
			int endX, int endY) {

		// do not show strict and other connections that should be invisible
		if (isImplicit())
			return;
		
		Point p1, p2;

		if (breakPoints == null || breakPoints.size() < 1) {
			// if there are no breakpoints there is only one line to draw
			if (selected) {
				// hilight selected connections
				Stroke origStroke = g.getStroke();
				Color origColor = g.getColor();
				g.setStroke(SELECTED_STROKE);
				g.setColor(Color.cyan);
				
				g.drawLine(startX, startY, endX, endY);
				
				g.setStroke(origStroke);
				g.setColor(origColor);
			}

			g.drawLine(startX, startY, endX, endY);

		} else { // there are breakpoints
			if (selected) {
				// hilight selected connections
				Stroke origStroke = g.getStroke();
				Color origColor = g.getColor();
				g.setStroke(SELECTED_STROKE);
				g.setColor(Color.cyan);

				p1 = breakPoints.get(0);
				g.drawLine(startX, startY, p1.x, p1.y);

				for (int i = 0; i < breakPoints.size() - 1; i++) {
					p1 = breakPoints.get(i);
					p2 = breakPoints.get(i + 1);
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}

				p2 = breakPoints.get(breakPoints.size() - 1);
				g.drawLine(p2.x, p2.y, endX, endY);
				
				g.setStroke(origStroke);
				g.setColor(origColor);
			}

			// draw the connection lines
			p1 = breakPoints.get(0);
			g.drawLine(startX, startY, p1.x, p1.y);

			for (int i = 0; i < breakPoints.size() - 1; i++) {
				p1 = breakPoints.get(i);
				p2 = breakPoints.get(i + 1);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
			}

			p2 = breakPoints.get(breakPoints.size() - 1);
			g.drawLine(p2.x, p2.y, endX, endY);

			// show breakpoints if selected
			if (selected) {
				for (Point p : breakPoints)
					g.drawRect(p.x - 2, p.y - 2, 4, 4);
			}
		}

		if (selected) {
			// hilight start and end points
			g.drawRect(startX - 2, startY - 2, 4, 4);
			g.drawRect(endX - 2, endY - 2, 4, 4);
		}
	} // drawRelation

	/**
	 * Shows whether this connection was created implicitly.
	 * Implicit connections are invisible and created and removed behind
	 * the scenes.
	 * @return <code>true</code> if this connection was created implicitly.
	 */
	public boolean isImplicit() {
		return strict || isRelclass();
	}

	/**
	 * Returns the number of breakpoints. Start and end points are
	 * not counted as breakpoints.
	 * @return the number of breakpoints.
	 */
	public int getBreakpointCount() {
		return breakPoints == null ? 0 : breakPoints.size();
	}

	/**
	 * Sets the selected value.
	 * 
	 * @param selected
	 *            the value to be set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Returns selection status.
	 * 
	 * @return <code>true</code> if this connection is selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Is this connection an implicit connection between a relation class and an
	 * ordinary class? Implicit connections are invisible.
	 * 
	 * @return <code>true</code> if this connection was created implicitly
	 *         between a class and a relation class.
	 */
	public boolean isRelclass() {
		// the fist port should always be present,
		// the second port can be missing while the connection is being added
		return (getBeginPort().getObject() instanceof RelObj 
				|| (getEndPort() != null && getEndPort().getObject() instanceof RelObj));
	}

	/**
	 * Shows whether this connections is strict.
	 * 
	 * @return <code>true</code> if this connection is strict.
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Finds the line segment being closest to the point and returns the index
	 * of this line segment. If the point is not close enough to this connection
	 * then -1 is returned.
	 * 
	 * @param pointX
	 *            X coordinate of the point
	 * @param pointY
	 *            Y coordinate of the point
	 * @return the 0-based index of the closest line segment, -1 if the point is
	 *         not on the relation line
	 */
	public int indexOf(int pointX, int pointY) {
		int[] sindex = new int[1];

		if (distanceFromPoint(pointX, pointY, sindex) >= NEAR_DISTANCE)
			sindex[0] = -1;

		return sindex[0];
	}

    /**
     * @param beginPort the beginPort to set
     */
    public void setBeginPort( Port beginPort ) {
        this.beginPort = beginPort;
    }

    /**
     * @return the beginPort
     */
    public Port getBeginPort() {
        return beginPort;
    }

    /**
     * @param endPort the endPort to set
     */
    public void setEndPort( Port endPort ) {
        this.endPort = endPort;
    }

    /**
     * @return the endPort
     */
    public Port getEndPort() {
        return endPort;
    }

    @Override
    public String toString() {
        GObj start = getBeginPort().getObject();
        GObj end = getEndPort().getObject();
        return "("+start.getClassName() + ")" + start.getName() + "." + getBeginPort().getName() 
                + " <--> " 
                + "("+end.getClassName() + ")" + end.getName() + "." + getEndPort().getName();
    }
}
