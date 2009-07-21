package ee.ioc.cs.vsle.vclass;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

import ee.ioc.cs.vsle.util.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class GObjGroup extends GObj implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<GObj> objects;

	public GObjGroup(ArrayList<GObj> objects) {
		super();
		this.objects = objects;
		setBounds();
	}

	private void setBounds() {
		int x1, x2, y1, y2;
		GObj obj;

		obj = objects.get(0);
		x1 = obj.getX() + obj.getPortOffsetX1();
		y1 = obj.getY() + obj.getPortOffsetY1();
		x2 = obj.getX() + obj.getRealWidth() + obj.getPortOffsetX2();
		y2 = obj.getY() + obj.getRealHeight() + obj.getPortOffsetY2();

		for (int i = 1; i < objects.size(); i++) {
			obj = objects.get(i);
			if (obj.getX() < x1) {
				x1 = obj.getX() + obj.getPortOffsetX1();
			}
			if (obj.getY() < y1) {
				y1 = obj.getY() + obj.getPortOffsetY1();
			}
			if (obj.getY() + obj.getRealHeight() > y2) {
				y2 = obj.getY() + obj.getRealHeight() + obj.getPortOffsetX2();
			}
			if (obj.getX() + obj.getRealWidth() > x2) {
				x2 = obj.getX() + obj.getRealWidth() + obj.getPortOffsetY2();

			}
		}
		setX(x1);
		setY(y1);
		setHeight(y2 - y1);
		setWidth(x2 - x1);
		for (int i = 0; i < objects.size(); i++) {
			obj = objects.get(i);
			obj.setDifWithMasterX( obj.getX() - getX() );
			obj.setDifWithMasterY( obj.getY() - getY() );
		}
	}

	@Override
	public boolean contains(int pointX, int pointY) {
		if ((pointX > getX()) && (pointY > getY())) {
			if ((pointX < getX() + (int) (getXsize() * getWidth()))
				&& (pointY < getY() + (int) (getYsize() * getHeight()))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isInside(int x1, int y1, int x2, int y2) {
		if ((x1 < getX()) && (y1 < getY())) {
			if ((x2 > getX() + (int) (getXsize() * getWidth()))
				&& (y2 > getY() + (int) (getYsize() * getHeight()))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Port portContains(int pointX, int pointY) {
		Port port;
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			port = obj.portContains(pointX, pointY);
			if (port != null) {
				return port;
			}
		}
		return null;
	}

	@Override
	public void setPosition(int x, int y) {
		GObj obj;
		int changeX = x - this.getX();
		int changeY = y - this.getY();

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			obj.setPosition(obj.getX() + changeX, obj.getY() + changeY);
		}
		this.setX(x);
		this.setY(y);

	}

	@Override
	public ArrayList<Connection> getConnections() {
		ArrayList<Connection> c = new ArrayList<Connection>();
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			c.addAll(obj.getConnections());
		}
		return c;
	}

	@Override
	public ArrayList<GObj> getComponents() {
		ArrayList<GObj> c = new ArrayList<GObj>();
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			c.addAll(obj.getComponents());
		}
		return c;

	}

	@Override
	public boolean includesObject(GObj checkObj) {
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			if (obj.includesObject(checkObj)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public ArrayList<Port> getPorts() {
		ArrayList<Port> c = new ArrayList<Port>();
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			c.addAll(obj.getPorts());
		}
		return c;

	}

	@Override
	public void drawClassGraphics(Graphics2D g, float scale) {
		g.setColor(Color.gray);
		g.drawRect(getX(), getY(), (int) (getWidth() * getXsize()), (int) (getHeight() * getYsize()));
		g.setColor(Color.black);
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			obj.drawClassGraphics(g, scale);
		}
		if (isSelected()) {
			g.fillRect(getX() - GObj.CORNER_SIZE - 1, getY() - GObj.CORNER_SIZE - 1, GObj.CORNER_SIZE, GObj.CORNER_SIZE);
			g.fillRect(getX() + (int) (getXsize() * getWidth()) + 1,
				getY() - GObj.CORNER_SIZE - 1, GObj.CORNER_SIZE, GObj.CORNER_SIZE);
			g.fillRect(getX() - GObj.CORNER_SIZE - 1,
				getY() + (int) (getYsize() * (getHeight())) + 1, GObj.CORNER_SIZE, GObj.CORNER_SIZE);
			g.fillRect(getX() + (int) (getXsize() * (getWidth())) + 1,
				getY() + (int) (getYsize() * (getHeight())) + 1, GObj.CORNER_SIZE, GObj.CORNER_SIZE);
		}
	}


	@Override
	public void setXsize(float s) {
		float change = s / getXsize();

		super.setXsize( s );

		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);

			obj.setMultXSize(change);
			obj.setX(getX() + (int) (getXsize() * obj.getDifWithMasterX()));
		}
	}


	@Override
	public void setYsize(float s) {
		float change = s / getYsize();

		super.setYsize( s );
		
		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);

			obj.setMultYSize(change);
			obj.setY(getY() + (int) (getYsize() * obj.getDifWithMasterY()));
		}
	}

	@Override
	public void setMultYSize(float s) {
		setYsize(s * getYsize());

		float change = s;

		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);

			obj.setMultYSize(change);
			obj.setY(getY() + (int) (getYsize() * obj.getDifWithMasterY()));
		}
	}

	@Override
	public void setMultXSize(float s) {
		setXsize(s * getXsize());

		float change = s;

		GObj obj;

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);

			obj.setMultXSize(change);
			obj.setX(getX() + (int) (getXsize() * obj.getDifWithMasterX()));
		}
	}

	@Override
	public String toString() {
		GObj obj;
		String s = getName();

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			s += " " + obj;
		}
		return s;
	}

	@Override
	public GObjGroup clone() {
		GObjGroup g = (GObjGroup) super.clone();
		GObj obj;
		ArrayList<GObj> newList = new ArrayList<GObj>();

		for (int j = 0; j < objects.size(); j++) {
			obj = objects.get(j);
			obj = obj.clone();
			newList.add(obj);
		}
		g.objects = newList;
		return g;

	}

	public String getSpec(ConnectionList relations) {

		GObj obj;
		StringBuffer s = new StringBuffer();

		for (int i = 0; i < objects.size(); i++) {
			obj = objects.get(i);
			s.append(
				"    " + obj.getClassName() + " " + obj.getName() + ";\n");
			for ( ClassField field : obj.getFields() ) {
				if (field.value != null) {
					if (field.type.equals(TYPE_STRING)) {
						s.append("        " + obj.getName() + "." + field.getName()
							+ " = \"" + field.value + "\";\n");
					} else if (field.isPrimitiveArray()) {
						s.append(
							"        " + obj.getName() + "." + field.getName()
							+ " = {");
						String[] split = field.value.split( TypeUtil.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append(split[k]);
							} else
								s.append(", " + split[k]);
						}
						s.append("};\n");

					} else if (field.isPrimOrStringArray()) {
						s.append(
							"        " + obj.getName() + "." + field.getName()
							+ " = {");
						String[] split = field.value.split( TypeUtil.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append("\"" + split[k] + "\"");
							} else
								s.append(", \"" + split[k] + "\"");
						}
						s.append("};\n");
					} else {
						s.append(
							"        " + obj.getName() + "." + field.getName() + " = "
							+ field.value + ";\n");
					}
				}
			}

		}
		Connection rel;
		for (int j = 0; j < relations.size(); j++) {
			rel = relations.get(j);
			if (this.includesObject(rel.getEndPort().getObject()) && this.includesObject(rel.getBeginPort().getObject())) {
				if (rel.getEndPort().getName().equals("any")) {
					s.append(
						"    " + rel.getEndPort().getObject().getName() + "." + rel.getBeginPort().getName()
						+ " = " + rel.getBeginPort().getObject().getName() + "."
						+ rel.getBeginPort().getName() + ";\n");
				} else if (rel.getBeginPort().getName().equals("any")) {
					s.append(
						"    " + rel.getEndPort().getObject().getName() + "." + rel.getEndPort().getName()
						+ " = " + rel.getBeginPort().getObject().getName() + "."
						+ rel.getEndPort().getName() + ";\n");
				} else {
					s.append(
						"    " + rel.getEndPort().getObject().getName() + "." + rel.getEndPort().getName()
						+ " = " + rel.getBeginPort().getObject().getName() + "."
						+ rel.getBeginPort().getName() + ";\n");
				}
			}
		}

		return s.toString();
	}

}
