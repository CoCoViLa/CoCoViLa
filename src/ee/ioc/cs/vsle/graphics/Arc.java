package ee.ioc.cs.vsle.graphics;

import java.io.*;

import java.awt.*;

public class Arc extends Shape implements Serializable, Cloneable {

    int startAngle;

    int arcAngle;

    boolean filled = false;

    Color color;

    String name;

    private BasicStroke stroke;

    private boolean selected = false;

    private boolean fixed = false;

    /**
     * Class constructor.
     * 
     * @param x
     *            int - x coordinate of the beginning of the arc.
     * @param y
     *            int - y coordinate of the beginning of the arc.
     * @param width
     *            int - arc width (arc end x coordinate - arc start x
     *            coordinate).
     * @param height
     *            int - arc height (arc end y coordinate - arc start y
     *            coordinate).
     * @param startAngle
     *            int - arc starting angle, zero by default.
     * @param arcAngle
     *            int - arc ending angle, 180 by default.
     * @param colorInt
     *            int - arc color.
     * @param fill
     *            boolean - boolean value indicating whether to fill the
     *            constructed arc with a specified color or not.
     * @param strokeWidth
     *            double - width of the line the arc is drawn with.
     * @param transp
     *            double - transparency (Alpha) value (0..100%).
     * @param lineType -
     *            shape line type.
     */
    public Arc(int x, int y, int width, int height, int startAngle,
            int arcAngle, int colorInt, boolean fill, double strokeWidth,
            int transp, int lineType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startAngle = startAngle;
        this.arcAngle = arcAngle;
        color = new Color(colorInt);
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(),
                transp);
        this.filled = fill;

        if (lineType > 0) {
            stroke = new BasicStroke((float) strokeWidth, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 50, new float[] { lineType,
                            lineType }, 0);
        } else {
            stroke = new BasicStroke((float) strokeWidth);
        }

    } // Arc

    /**
     * Set the shape dimensions fixed in which case the shape cannot be resized.
     * 
     * @param b
     *            boolean - fix or unfix the shape.
     */
    public void setFixed(boolean b) {
        this.fixed = b;
    } // setFixed

    /**
     * Returns a boolean value representing if the shape is fixed or not.
     * 
     * @return boolean - boolean value representing if the shape is fixed or
     *         not.
     */
    public boolean isFixed() {
        return this.fixed;
    } // isFixed.

    /**
     * Returns the name of the shape.
     * 
     * @return String - the name of the shape.
     */
    public String getName() {
        return this.name;
    } // getName

    /**
     * Returns the real height of the shape.
     * 
     * @return int - the real height of the shape.
     */
    public int getRealHeight() {
        return getHeight();
    } // getRealHeight

    /**
     * Returns the real width of the shape.
     * 
     * @return int - the real width of the shape.
     */
    public int getRealWidth() {
        return getWidth();
    } // getRealWidth

    /**
     * Returns a boolean value representing if the shape is selected or not.
     * 
     * @return boolean - boolean value representing if the shape is selected or
     *         not.
     */
    public boolean isSelected() {
        return this.selected;
    } // isSelected

    /**
     * Returns the name of the shape. In future implementations should return
     * the textual representation of the shape, ie. the return value of the
     * currently implemented "toText" method.
     * 
     * @return String - the name of the shape.
     */
    public String toString() {
        return getName();
    } // toString

    /**
     * Returns a boolean value representing if the mouse was clicked inside the
     * shape.
     * 
     * @param x1
     *            int - x1 coordinate of the mouse pointer.
     * @param y1
     *            int - y1 coordinate of the mouse pointer.
     * @param x2
     *            int - x2 coordinate of the mouse pointer.
     * @param y2
     *            int - y2 coordinate of the mouse pointer.
     * @return boolean - boolean value representing if the mouse was clicked
     *         inside the shape.
     */
    public boolean isInside(int x1, int y1, int x2, int y2) {
        if (x1 > x && y1 > y && x2 < x + width && y2 < y + height) {
            return true;
        }
        return false;
    } // isInside

    /**
     * Returns a boolean value representing if the shape is in the selection
     * rectangle.
     * 
     * @param x1
     *            int - x coordinate of the starting corner of the selection
     *            rectangle.
     * @param y1
     *            int - y coordinate of the starting corner of the selection
     *            rectangle.
     * @param x2
     *            int - x coordinate of the ending corner of the selection
     *            rectangle.
     * @param y2
     *            int - y coordinate of the ending corner of the selection
     *            rectangle.
     * @return boolean - boolean value representing if the shape is in the
     *         selection rectangle.
     */
    public boolean isInsideRect(int x1, int y1, int x2, int y2) {
        if (x1 < x && y1 < y && x2 > x + width && y2 > y + height) {
            return true;
        }
        return false;
    } // isInsideRect

    /**
     * Set the shape selected or unselected.
     * 
     * @param b
     *            boolean - boolean value representing if to set the shape
     *            selected or unselected.
     */
    public void setSelected(boolean b) {
        this.selected = b;
    } // setSelected

    /**
     * Specify the name of the shape.
     * 
     * @param s
     *            String - the name of the shape.
     */
    public void setName(String s) {
        this.name = s;
    } // setName

    /**
     * Set size using zoom multiplication.
     * 
     * @param s1
     *            float - set size using zoom multiplication.
     * @param s2
     *            float - set size using zoom multiplication.
     */
    public void setMultSize(float s1, float s2) {
        x = x * (int) s1 / (int) s2;
        y = y * (int) s1 / (int) s2;
        width = width * (int) s1 / (int) s2;
        height = height * (int) s1 / (int) s2;
    } // setMultSize

    /**
     * Set shape position.
     * 
     * @param x
     *            int - new x coordinate of the shape.
     * @param y
     *            int - new y coordinate of the shape.
     */
    public void setPosition(int x, int y) {
        this.x = getX() + x;
        this.y = getY() + y;
    } // setPosition

    /**
     * Set the color of a shape.
     * 
     * @param col
     *            Color - color of a shape.
     */
    public void setColor(Color col) {
        this.color = col;
    } // setColor

    public void setStrokeWidth(double d) {
        stroke = new BasicStroke((float) d, stroke.getEndCap(), stroke
                .getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(),
                stroke.getDashPhase());
    }

    public void setTransparency(int d) {
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), d);
    }

    public void setLineType(int lineType) {
        stroke = new BasicStroke(stroke.getLineWidth(), stroke.getEndCap(),
                stroke.getLineJoin(), stroke.getMiterLimit(), new float[] {
                        lineType, lineType }, stroke.getDashPhase());
    }

    public int getStartAngle() {
        return this.startAngle;
    } // getStartAngle

    /**
     * Returns the angle of the arc.
     * 
     * @return int - angle of the arc.
     */
    public int getArcAngle() {
        return this.arcAngle;
    } // getArcAngle

    /**
     * Returns the color of the arc.
     * 
     * @return Color - color of the arc.
     */
    public Color getColor() {
        return this.color;
    } // getColor

    /**
     * Returns a boolean value representing if the shape is filled or not.
     * 
     * @return boolean - a boolean value representing if the shape is filled or
     *         not.
     */
    public boolean isFilled() {
        return this.filled;
    } // isFilled

    public double getStrokeWidth() {
        return stroke.getLineWidth();
    }

    public int getLineType() {
        if (stroke.getDashArray() != null) {
            return (int) stroke.getDashArray()[0];
        }

        return 0;
    } // getLineType

    public int getTransparency() {
        return color.getAlpha();
    } // getTransparency

    /**
     * Returns the x coordinate of the beginning of the arc.
     * 
     * @return int - x coordinate of the beginning of the arc.
     */
    public int getX() {
        return x;
    } // getX

    /**
     * Returns the y coordinate of the beginning of the arc.
     * 
     * @return int - y coordinate of the beginning of the arc.
     */
    public int getY() {
        return y;
    } // getY

    /**
     * Returns the width of the arc (the difference between the arc's beginning
     * and end x coordinates).
     * 
     * @return int - width of the arc.
     */
    int getWidth() {
        return this.width;
    } // getWidth

    /**
     * Returns the height of the arc (the difference between the arc's beginning
     * and end y coordinates).
     * 
     * @return int - height of the arc.
     */
    int getHeight() {
        return this.height;
    } // getHeight

    /**
     * Resizes current object.
     * 
     * @param deltaW
     *            int - change of object with.
     * @param deltaH
     *            int - change of object height.
     * @param cornerClicked
     *            int - number of the clicked corner.
     */
    public void resize(int deltaW, int deltaH, int cornerClicked) {
        if (!isFixed()) {
            if (cornerClicked == 1) { // TOP-LEFT
                if (this.width - deltaW > 0 && this.height - 2 * deltaH > 0) {
                    this.x += deltaW;
                    this.y += deltaH;
                    this.width -= deltaW;
                    this.height -= 2 * deltaH;
                }
            } else if (cornerClicked == 2) { // TOP-RIGHT
                if (this.width + deltaW > 0 && this.height - 2 * deltaH > 0) {
                    this.y += deltaH;
                    this.width += deltaW;
                    this.height -= 2 * deltaH;
                }
            } else if (cornerClicked == 3) { // BOTTOM-LEFT
                if (this.width - deltaW > 0 && this.height + deltaH > 0) {
                    this.x += deltaW;
                    this.width -= deltaW;
                    this.height += deltaH;
                }
            } else if (cornerClicked == 4) { // BOTTOM-RIGHT
                if (this.width + deltaW > 0 && this.height + deltaH > 0) {
                    this.width += deltaW;
                    this.height += deltaH;
                }
            }
        }
    } // resize

    public boolean contains(int pointX, int pointY) {
        if (pointX > x && pointY > y && pointX < x + width
                && pointY < y + height) {
            return true;
        }
        return false;
    } // contains

    /**
     * Returns the number representing a corner the mouse was clicked in. 1:
     * top-left, 2: top-right, 3: bottom-left, 4: bottom-right. Returns 0 if the
     * click was not in the corner.
     * 
     * @param pointX
     *            int - mouse x coordinate.
     * @param pointY
     *            int - mouse y coordinate.
     * @return int - corner number the mouse was clicked in.
     */
    public int controlRectContains(int pointX, int pointY) {
        if (pointX >= x && pointY >= y && pointX <= x + 4 && pointY <= y + 4) {
            return 1;
        }
        if (pointX >= x + width - 4 && pointY >= y && pointX <= x + width
                && pointY <= y + 4) {
            return 2;
        }
        if (pointX >= x && pointY >= y + height - 4 && pointX <= x + 4
                && pointY <= y + height) {
            return 3;
        }
        if (pointX >= x + width - 4 && pointY >= y + height - 4
                && pointX <= x + width && pointY <= y + height) {
            return 4;
        }
        return 0;
    } // controlRectContains

    /**
     * Return a specification of the shape to be written into a file in XML
     * format.
     * 
     * @param boundingboxX -
     *            x coordinate of the bounding box.
     * @param boundingboxY -
     *            y coordinate of the bounding box.
     * @return String - specification of a shape.
     */
    public String toFile(int boundingboxX, int boundingboxY) {
        String fill = "false";

        if (filled) {
            fill = "true";
        }
        int colorInt = 0;

        if (color != null) {
            colorInt = color.getRGB();
        }
        return "<arc x=\"" + (x - boundingboxX) + "\" y=\""
                + (y - boundingboxY) + "\" width=\"" + width + "\" height=\""
                + height + "\" startAngle=\"" + startAngle + "\" arcAngle=\""
                + arcAngle + "\" colour=\"" + colorInt + "\" filled=\"" + fill
                + "\" fixed=\"" + isFixed() + "\" stroke=\""
                + (int) stroke.getLineWidth() + "\" linetype=\""
                + this.getLineType() + "\" transparency=\"" + getTransparency()
                + "\"/>\n";
    } // toFile

    public String toText() {
        String fill = "false";
        if (filled)
            fill = "true";
        int colorInt = 0;
        if (color != null)
            colorInt = color.getRGB();
        return "ARC:" + x + ":" + y + ":" + width + ":" + height + ":"
                + startAngle + ":" + arcAngle + ":" + colorInt + ":" + fill
                + ":" + stroke.getLineWidth() + ":" + this.getLineType() + ":"
                + color.getTransparency() + ":" + isFixed();
    } // toText

    /**
     * Draw the selection markers if object selected.
     * 
     * @param g2
     *            Graphics2D - shape graphics.
     */
    public void drawSelection(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke((float) 1.0));
        g2.fillRect(x, y, 4, 4);
        g2.fillRect(x + width - 4, y, 4, 4);
        g2.fillRect(x, y + height - 3, 4, 4);
        g2.fillRect(x + width - 4, y + height - 3, 4, 4);
    } // drawSelection

    /**
     * Draw the arc. Supports drawing with transparent colors.
     * 
     * @param xModifier
     *            int -
     * @param yModifier
     *            int -
     * @param Xsize
     *            float - defines the resizing multiplier (used at zooming),
     *            default: 1.0
     * @param Ysize
     *            float - defines the resizing multiplier (uset at zooming),
     *            default: 1.0
     * @param g2
     *            Graphics - shape graphics.
     */
    public void draw(int xModifier, int yModifier, float Xsize, float Ysize,
            Graphics2D g2) {
        g2.setStroke(stroke);
        g2.setColor(color);

        int a = xModifier + (int) (Xsize * x);
        int b = yModifier + (int) (Ysize * y);
        int c = (int) (Xsize * width);
        int d = (int) (Ysize * height);

        if (filled) {
            g2.fillArc(a, b, c, d, startAngle, arcAngle);
        } else {
            g2.drawArc(a, b, c, d, startAngle, arcAngle);
        }

        // Draw selection markers if object selected.
        if (selected) {
            drawSelection(g2);
        }

    } // draw

    public BasicStroke getStroke() {
        return stroke;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {

            return null;
        }
    } // clone
} // end of class
