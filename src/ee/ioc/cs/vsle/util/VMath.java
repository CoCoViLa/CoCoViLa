package ee.ioc.cs.vsle.util;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 30.03.2004
 * Time: 0:08:38
 * To change this template use Options | File Templates.
 */
public class VMath {
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
        int calc1 = (pointX - x1) * (x2 - x1) + (pointY - y1) * (y2 - y1);
        int calc2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);

        float U = (float) calc1 / (float) calc2;

        float intersectX = x1 + U * (x2 - x1);
        float intersectY = y1 + U * (y2 - y1);

        double distance = Math.sqrt(
            (pointX - intersectX) * (pointX - intersectX)
            + (pointY - intersectY) * (pointY - intersectY));

        double distanceFromEnd1 = Math.sqrt(
            (x1 - pointX) * (x1 - pointX) + (y1 - pointY) * (y1 - pointY));
        double distanceFromEnd2 = Math.sqrt(
            (x2 - pointX) * (x2 - pointX) + (y2 - pointY) * (y2 - pointY));
        double lineLength = Math.sqrt(
            (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

        if (lineLength < Math.max(distanceFromEnd1, distanceFromEnd2)) {
            distance = Math.max(Math.min(distanceFromEnd1, distanceFromEnd2),
                distance);
        }
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
        int realY = y -startY;
        if (realX >= 0 && realY>= 0 ) {
          return (Math.atan((double)realY/(double)realX));
        }

        if (realX <= 0 && realY>= 0 ) {
          return (Math.atan((double)realY/(double)realX) +Math.PI);
        }

        if (realX <= 0 && realY <= 0 ) {
          return(Math.atan((double)realY/(double)realX) +Math.PI);
        }

        if (realX >= 0 && realY <= 0 ) {
          return(Math.atan((double)realY/(double)realX) +2*Math.PI);
        }
        return 0.0;
    }

}
