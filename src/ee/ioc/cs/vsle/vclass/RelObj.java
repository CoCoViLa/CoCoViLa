package ee.ioc.cs.vsle.vclass;
import ee.ioc.cs.vsle.util.VMath;

import java.awt.*;

/**

 */
public class RelObj extends GObj{
    public double angle;
    public Port startPort;
    public Port endPort;

    public RelObj(int x, int y, int width, int height, String name) {
        super (x, y, width, height, name);
    }
    public boolean contains(int x, int y) {
        float f = VMath.calcDistance(startPort.getRealCenterX(),startPort.getRealCenterY(), endPort.getRealCenterX(), endPort.getRealCenterY(), x, y);
        if (f < height+4) {
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
						String[] split = f.value.split("�");
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

    }
}
