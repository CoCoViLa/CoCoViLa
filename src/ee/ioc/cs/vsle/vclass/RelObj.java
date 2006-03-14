package ee.ioc.cs.vsle.vclass;

import java.awt.Graphics2D;

import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.util.*;

/**

 */
public class RelObj extends GObj {
    private static final long serialVersionUID = 1L;
    public double angle;
	public Port startPort;
	public Port endPort;
	public int endX, endY;

	public RelObj(int x, int y, int width, int height, String name) {
		super(x, y, width, height, name);
	}

	public RelObj() {
	}

	public boolean contains(int pointX, int pointY) {
		float f = VMath.pointDistanceFromLine(x, y, endX, endY, pointX, pointY);
		if (f < height + 4) {
			return true;
		}
		return false;
	}

	void draw(int xPos, int yPos, float Xsize, float Ysize, Graphics2D g2) {
		g2.translate(xPos, yPos);
		g2.rotate(angle);
		g2.translate(-1 * (xPos), -1 * (yPos));

		for (Shape s: shapes)
			s.draw(xPos, yPos, Xsize, Ysize, g2);

        g2.translate(xPos, yPos);
		g2.rotate(-1 * angle);
		g2.translate(-1 * (xPos), -1 * (yPos));
	} // draw


	public void drawClassGraphics(Graphics2D g) {
		draw(getX(), getY(), getXsize(), getYsize(), g);
		int xModifier = getX();
		int yModifier = getY();

		for (ClassField field: fields) {
			if (field.defaultGraphics != null) {
				field.defaultGraphics.angle = angle;
				field.defaultGraphics.drawSpecial(xModifier,
					yModifier, getXsize(), getYsize(), g, field.getName(), field.value);
			}
			if (field.isKnown() && field.knownGraphics != null) {
				field.knownGraphics.angle = angle;
				field.knownGraphics.drawSpecial(xModifier,
					yModifier, getXsize(), getYsize(), g, field.getName(), field.value);
			}
		}
	}

	public RelObj clone() {
		RelObj obj = (RelObj) super.clone();
		obj.startPort = startPort.clone();
		obj.endPort = endPort.clone();
		return obj;
	}

	public String toXML() {
		String xml = "<relobject name=\"" + name + "\" type=\"" + className + "\" >\n";
		xml += "  <relproperties x=\"" + x + "\" y=\"" + y + "\" endX=\"" + endX + "\" endY=\"" + endY + "\" angle=\"" + angle + "\" width=\"" + width + "\" height=\"" + height + "\" xsize=\"" + Xsize + "\" ysize=\"" + Ysize + "\" strict=\"" + strict + "\" />\n";
		xml += "  <fields>\n";
		for (ClassField field: fields) {
			xml += StringUtil.indent(4) + field.toXML();
		}
		xml += "  </fields>\n";
		xml += "</relobject>\n";
		return xml;
	}
}
