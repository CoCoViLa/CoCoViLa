package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.*;

import java.awt.*;
import java.util.ArrayList;

/**

 */
public class RelObj extends GObj {
	public double angle;
	public Port startPort;
	public Port endPort;
	public int endX, endY;

	public RelObj(int x, int y, int width, int height, String name) {
		super(x, y, width, height, name);
	}

	public boolean contains(int pointX, int pointY) {
		float f = VMath.calcDistance(x, y, endX, endY, pointX, pointY);
		if (f < height + 4) {
			return true;
		}
		return false;
	}

	public void drawClassGraphics(Graphics g) {
		graphics.angle = angle;
		getGraphics().draw(getX(), getY(), getXsize(), getYsize(), g);
		int xModifier = getX();
		int yModifier = getY();

		if (getGraphics().showFields == true) {
			int textOffset = 4;
			for (int i = 0; i < getFields().size(); i++) {
				ClassField f = (ClassField) getFields().get(i);
				if (f.value != null) {
					if (f.isPrimOrStringArray()) {
						String[] split = f.value.split("%%");
						for (int j = 0; j < split.length; j++) {
							g.drawString(split[j], getX() + 5, getY() + 8 + textOffset);
							textOffset += 12;
						}
						textOffset += 6;
					} else if (f.isPrimitiveOrString()) {
						g.drawString(f.value, getX() + 5, getY() + 8 + textOffset);
						textOffset += 18;
					}
				}
			}
		}
		int len = fields.size();
		for (int i = 0; i < len; i++) {
			ClassField field = (ClassField) fields.get(i);

			if (field.defaultGraphics != null) {
				field.defaultGraphics.angle = angle;
				field.defaultGraphics.drawSpecial(xModifier,
					yModifier, getXsize(), getYsize(), g, field.name, field.value);
			}
			if (field.isKnown() && field.knownGraphics != null) {
				field.knownGraphics.angle = angle;
				field.knownGraphics.drawSpecial(xModifier,
					yModifier, getXsize(), getYsize(), g, field.name, field.value);
			}
		}
	}
	public Object clone() {
		RelObj obj = (RelObj) super.clone();
		obj.startPort = (Port)startPort.clone();
		obj.startPort.obj = obj;
		obj.endPort = (Port)endPort.clone();
		obj.endPort.obj = obj;
		return obj;
	}
}
