package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;

import java.awt.Color;
import java.awt.Graphics;

public class GObjGroup
	extends GObj
	implements Serializable {
	public ArrayList objects;

	public GObjGroup(ArrayList objects) {
		super();
		this.objects = objects;
		setBounds();
	}

	public void setBounds() {
		int x1, x2, y1, y2;
		GObj obj;

		obj = (GObj) objects.get(0);
		x1 = obj.getX() + obj.portOffsetX1;
		y1 = obj.getY() + obj.portOffsetY1;
		x2 = obj.getX() + obj.getRealWidth() + obj.portOffsetX2;
		y2 = obj.getY() + obj.getRealHeight() + obj.portOffsetY2;

		for (int i = 1; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			if (obj.getX() < x1) {
				x1 = obj.getX() + obj.portOffsetX1;
			}
			if (obj.getY() < y1) {
				y1 = obj.getY() + obj.portOffsetY1;
			}
			if (obj.getY() + obj.getRealHeight() > y2) {
				y2 = obj.getY() + obj.getRealHeight() + obj.portOffsetX2;
			}
			if (obj.getX() + obj.getRealWidth() > x2) {
				x2 = obj.getX() + obj.getRealWidth() + obj.portOffsetY2;

			}
		}
		setX(x1);
		setY(y1);
		setHeight(y2 - y1);
		setWidth(x2 - x1);
		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			obj.difWithMasterX = obj.getX() - getX();
			obj.difWithMasterY = obj.getY() - getY();
		}
	}

	public boolean contains(int pointX, int pointY) {
		if ( (pointX > getX()) && (pointY > getY())) {
			if ( (pointX < getX() + (int) (getXsize() * getWidth())) && (pointY < getY() + (int) (getYsize() * getHeight()))) {
				return true;
			}
		}
		return false;
	}

	public boolean isInside(int x1, int y1, int x2, int y2) {
		if ( (x1 < getX()) && (y1 < getY())) {
			if ( (x2 > getX() + (int) (getXsize() * getWidth())) && (y2 > getY() + (int) (getYsize() * getHeight()))) {
				return true;
			}
		}
		return false;
	}

	public Port portContains(int pointX, int pointY) {
		Port port;
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			port = obj.portContains(pointX, pointY);
			if (port != null) {
				return port;
			}

			/* for (int i = 0; i<obj.ports.size(); i++) {
			 port = (ee.ioc.cs.editor.vclass.Port)obj.ports.get(i);
			 if ((pointX > obj.x + (int)(size*port.getX())-obj.PORTSIZE) && (pointY > obj.y + (int)(size*port.getY()) -PORTSIZE)) {
			 if ((pointX < obj.x + (int)(size*port.getX()) + PORTSIZE) && (pointY < obj.y + (int)(size*port.getY()) + PORTSIZE)) {
			 return port;
			 }
			 }
			 }*/
		}
		return null;
	}

	public void setPosition(int x, int y) {
		GObj obj;
		int changeX = x - this.getX();
		int changeY = y - this.getY();

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			obj.setPosition(obj.getX() + changeX, obj.getY() + changeY);
		}
		this.setX(x);
		this.setY(y);

	}

	public ArrayList getConnections() {
		ArrayList c = new ArrayList();
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			c.addAll(obj.getConnections());
		}
		return c;
	}

	public ArrayList getComponents() {
		ArrayList c = new ArrayList();
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			c.addAll(obj.getComponents());
		}
		return c;

	}

	public boolean includesObject(GObj checkObj) {
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			if (obj.includesObject(checkObj)) {
				return true;
			}
		}
		return false;

	}

	public ArrayList getPorts() {
		ArrayList c = new ArrayList();
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			c.addAll(obj.getPorts());
		}
		return c;

	}

	public void drawClassGraphics(Graphics g) {
		g.setColor(Color.gray);
		g.drawRect(getX(), getY(), (int) (getWidth() * getXsize()), (int) (getHeight() * getYsize()));
		g.setColor(Color.black);
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			obj.drawClassGraphics(g);
		}
		if (isSelected() == true) {
			g.drawRect(getX() - 2, getY() - 2, 4, 4);
			g.drawRect(getX() + (int) (getXsize() * getWidth()) - 2, getY() - 2, 4, 4);
			g.drawRect(getX() - 2, getY() + (int) (getYsize() * getHeight()) - 2, 4, 4);
			g.drawRect(getX() + (int) (getXsize() * getWidth()) - 2, getY() + (int) (getYsize() * getHeight()) - 2, 4, 4);
		}
	}

	public void setXSize(float s) {
		float change = s / getXsize();

		setXsize(s);

		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);

			obj.setMultXSize(change);
			obj.setX(getX() + (int) (getXsize() * obj.difWithMasterX));
		}
	}

	public void setYSize(float s) {
		float change = s / getYsize();

		setYsize(s);

		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);

			obj.setMultYSize(change);
			obj.setY(getY() + (int) (getYsize() * obj.difWithMasterY));
		}
	}

	public void setMultYSize(float s) {
		setYsize(s * getYsize());

		float change = s;

		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);

			obj.setMultYSize(change);
			obj.setY(getY() + (int) (getYsize() * obj.difWithMasterY));
		}
	}

	public void setMultXSize(float s) {
		setXsize(s * getXsize());

		float change = s;

		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);

			obj.setMultXSize(change);
			obj.setX(getX() + (int) (getXsize() * obj.difWithMasterX));
		}
	}

	public String toString() {
		GObj obj;
		String s = getName();

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			s += " " + obj;
		}
		return s;
	}

	public Object clone() {
		GObjGroup g = (GObjGroup)super.clone();
		GObj obj;
		ArrayList newList = new ArrayList();

		for (int j = 0; j < objects.size(); j++) {
			obj = (GObj) objects.get(j);
			obj = (GObj) obj.clone();
			newList.add(obj);
		}
		g.objects = newList;
		return g;

	}

}
