/**
 * 
 */
package ee.ioc.cs.vsle.vclass;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class CurvedConnection extends Connection {

    /**
     * @param beginPort
     * @param endPort
     * @param strict
     */
    public CurvedConnection( Port beginPort, Port endPort, boolean strict ) {
        super( beginPort, endPort, strict );
    }

    /**
     * @param beginPort
     * @param endPort
     */
    public CurvedConnection( Port beginPort, Port endPort ) {
        super( beginPort, endPort );
    }

    /**
     * @param beginPort
     */
    public CurvedConnection( Port beginPort ) {
        super( beginPort );
        breakPoints.add( new Point( getBeginPort().getAbsoluteX(), getBeginPort().getAbsoluteY() ) );
    }
    
    private CurvedConnection() {
        super();
    }
    
    private final Path2D path = new Path2D.Double();
    
    @Override
    float distanceFromPoint(int x, int y, int[] idx) {
        if(breakPoints.size() > 1) {
            float minDist = Float.MAX_VALUE;
            float dist;
            for(int i = 0; i < breakPoints.size()-1; i+=3) {
                Point p1 = breakPoints.get( i );
                Point p2 = breakPoints.get( i+3 );
                
                if((dist = VMath.pointDistanceFromLine( p1.x, p1.y, p2.x, p2.y, x, y )) < minDist) {
                    minDist = dist;
                    if(idx != null && idx.length == 1)
                        idx[0] = i;
                }
            }
            return minDist;
        }
        return -1;
    }
    
    @Override
    public List<Point> getBreakPoints() {
        return breakPoints.subList( 1, breakPoints.size()-1 );
    }
    
    @Override
    public int getBreakpointCount() {
        return breakPoints.size() > 2 ? 0 : breakPoints.size()-2;
    }
    
    private void drawRelation(Graphics2D g2, List<Point> points) {
        
        path.reset();
        path.moveTo( points.get( 0 ).x, points.get( 0 ).y );
        for(int i = 1; i < points.size(); i++) {
            path.curveTo( points.get(i).x, points.get(i).y, 
                        points.get(++i).x, points.get(i).y, 
                                points.get(++i).x, points.get( i).y );
        }
        
        Stroke origStroke = g2.getStroke();
        Color origColor = g2.getColor();
        
        if (selected) {
            g2.setStroke(SELECTED_STROKE);
            g2.setColor(Color.cyan);
            g2.draw( path );
        }
        
        g2.setStroke( new BasicStroke( 1f ) );
        g2.setColor(Color.blue);
        g2.draw( path );
        
        if(selected || Editor.getInstance().getCurrentCanvas().showConnectionBreakPoints()) {
            g2.setColor( Color.yellow );
            for(int i = 0; i < points.size()-1; i++) {
                Point p1 = points.get( i );
                Point p2 = points.get( ++i );
                g2.drawLine( p1.x, p1.y, p2.x, p2.y );
                if(i > 0 && (i % 3) == 0) {
                    --i;
                }
            }
            g2.setColor( Color.green );
            g2.setStroke( new BasicStroke( 2f, 0, 0, 1f, new float[] { 2f } , 0f ) );
            for(int i = 0; i < points.size()-1; i+=3) {
                Point p1 = points.get( i );
                Point p2 = points.get( i+3 );
                g2.drawLine( p1.x, p1.y, p2.x, p2.y );
            }

            g2.setColor( Color.red );
            for ( int i = 1; i < points.size()-1; i++ ) {
                Point p = points.get( i );
                g2.fillOval( (int)p.getX()-2, (int)p.getY()-2, 4, 4);
            }        
        }
        g2.setColor( origColor );
        g2.setStroke( origStroke );
    }

    @Override
    public void drawRelation( Graphics2D g, int endX, int endY ) {
        Point p2 = new Point( endX, endY );
        Point ctrl1 = new Point(); 
        Point ctrl2 = new Point();
        positionCtrlPoints( breakPoints.get( breakPoints.size()-1 ), p2, ctrl1, ctrl2 );
        breakPoints.add( ctrl1 );
        breakPoints.add( ctrl2 );
        breakPoints.add( p2 );
        drawRelation( g, breakPoints );
        breakPoints.subList( breakPoints.size() - 3, breakPoints.size() ).clear();
    }

    @Override
    public void drawRelation( Graphics2D g ) {
        shiftPortCtrls( breakPoints.get( 0 ), 
                beginPort.getAbsoluteX(), beginPort.getAbsoluteY(), 
                breakPoints.get( 1 ) );
        shiftPortCtrls( breakPoints.get( breakPoints.size()-1 ), 
                endPort.getAbsoluteX(), endPort.getAbsoluteY(), 
                breakPoints.get( breakPoints.size()-2 ) );
        drawRelation( g, breakPoints );
    }
    
    /**
     * This is the helper method for syncing the position of port 
     * and the corresponding points in the list.
     * Control points are moved together with ports.
     * @param old
     * @param x
     * @param y
     * @param ctrl
     */
    private void shiftPortCtrls(Point old, int x, int y, Point ctrl) {
        ctrl.move( ctrl.x + (x - old.x), ctrl.y + (y - old.y) );
        old.move( x, y );
    }
    
    @Override
    public void addBreakPoint( int idx, Point p2 ) {
        
        Point ctrl1 = new Point();
        positionCtrlPoints( breakPoints.get( idx ), p2, null, ctrl1 );
        breakPoints.add( idx+=2, ctrl1 );
        breakPoints.add( ++idx, p2 );
        Point ctrl2 = new Point();
        positionCtrlPoints( p2, breakPoints.get( idx+2 ), ctrl2, null );
        breakPoints.add( ++idx, ctrl2 );
    }

    @Override
    public void addBreakPoint( Point p2 ) {
        Point ctrl1 = new Point(); 
        Point ctrl2 = new Point();
        positionCtrlPoints( breakPoints.get( breakPoints.size()-1 ), p2, ctrl1, ctrl2 );
        breakPoints.add( ctrl1 );
        breakPoints.add( ctrl2 );
        breakPoints.add( p2 );
    }

    /**
     * Moves control points along the line towards each other. 
     * This is the default positioning for new breakpoints and
     * the method is also used to straighten the curve
     * @param p1
     * @param p2
     * @param ctrl1
     * @param ctrl2
     */
    private void positionCtrlPoints(Point p1, Point p2, Point ctrl1, Point ctrl2) {
        double angle = VMath.calcAngle( p1.x, p1.y, p2.x, p2.y );
        double shift = 25d;
        double dist;
        if((dist = VMath.distanceBetweenPoints( p1, p2 )) < shift)
            shift = dist/2;
        if(ctrl1 != null) {
            ctrl1.move( (int)(p1.x + shift*Math.cos( angle )), (int)(p1.y + shift*Math.sin( angle )) );
        }
        if(ctrl2 != null) {
            double beta = angle + Math.PI;
            ctrl2.move( (int)(p2.x + shift*Math.cos( beta )), (int)(p2.y + shift*Math.sin( beta )) );
        }
    }
    
    @Override
    public void setEndPort( Port endPort ) {
        super.setEndPort( endPort );
        Point p2 = new Point( endPort.getAbsoluteX(), endPort.getAbsoluteY() );
        Point ctrl1 = new Point(); 
        Point ctrl2 = new Point();
        positionCtrlPoints( breakPoints.get( breakPoints.size()-1 ), p2, ctrl1, ctrl2 );
        breakPoints.add( ctrl1 );
        breakPoints.add( ctrl2 );
        breakPoints.add( p2 );
    }
    
    public int checkPoint( int x, int y ) {
        int i = 0;
        for ( Point point : breakPoints ) {
            if(point.distance( x, y ) < 5) {
                return i;
            }
            i++;
        }
        return -1;
    }
    
    public void movePoint(int idx, int x, int y, boolean moveClrtPoints) {
        Point p = breakPoints.get( idx );
        if(moveClrtPoints && (idx % 3 == 0) ) {
            int moveX = x - p.x;
            int moveY = y - p.y;
            Point ctrl1 = breakPoints.get( idx-1 );
            Point ctrl2 = breakPoints.get( idx+1 );
            ctrl1.move( ctrl1.x + moveX, ctrl1.y + moveY );
            ctrl2.move( ctrl2.x + moveX, ctrl2.y + moveY );
        }
        p.move( x, y );
    }
    
    @Override
    public Connection copyAndAdjust( Port beginPort_, Port endPort_, int shift) {
        Connection newCon = new CurvedConnection();
        newCon.beginPort = beginPort_;
        newCon.endPort = endPort_;
        newCon.strict = isStrict();
        
        for ( Point p : breakPoints )
            newCon.breakPoints.add( new Point( p.x + shift, p.y + shift ) );
        return newCon;
    }
    
    public void makeStraight() {
        for(int i = 0; i < breakPoints.size()-3;) {
            Point p1 = breakPoints.get( i );
            Point ctrl1 = breakPoints.get( ++i ); 
            Point ctrl2 = breakPoints.get( ++i );
            Point p2 = breakPoints.get( ++i );
            positionCtrlPoints( p1, p2, ctrl1, ctrl2);
        }
    }
}
