package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.*;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjectList extends ArrayList<GObj>
	implements Serializable {
	public ObjectList() {
		super();
	}

	public void sendToBack(GObj obj) {
		this.remove(obj);
		this.add(0, obj);
	}

	public void bringToFront(GObj obj) {
		this.remove(obj);
		this.add(obj);
	}

	public void bringForward(GObj obj, int step) {
		int objIndex = this.indexOf(obj);

		if (objIndex + step < this.size()) {
			this.remove(obj);
			this.add(objIndex + step, obj);
		}
	}

	public void sendBackward(GObj obj, int step) {
		int objIndex = this.indexOf(obj);

		if (objIndex - step >= 0) {
			this.remove(obj);
			this.add(objIndex - step, obj);
		}
	}


	public GObj checkInside(int x, int y) {
		GObj obj;

		for (int i = this.size() - 1; i >= 0; i--) {
			obj = this.get(i);
			if (obj.contains(x, y)) {
				return obj;
			}
		}
		return null;
	}

	public GObj checkInside(int x, int y, GObj asker) {
        for (GObj obj: this) {
			if (obj.contains(x, y) && obj != asker) {
				return obj;
			}
		}
		return null;
	}

	public void selectObjectsInsideBox(int x1, int y1, int x2, int y2) {
		for (GObj obj: this) {
			if (obj.isInside(x1, y1, x2, y2)) {
				obj.setSelected(true);
			}
		}
	}

	public void updateSize(float newXSize, float newYSize) {
		for (GObj obj: this) {
			obj.setXsize(obj.getXsize() * newXSize);
			obj.setYsize(obj.getYsize() * newYSize);
			obj.setX((int) (obj.getX() * newXSize));
			obj.setY((int) (obj.getY() * newYSize));
		}
	}

	public void clearSelected() {
		for (GObj obj: this) {
			obj.setSelected(false);
		}
	}

	public ArrayList<GObj> getSelected() {
		ArrayList<GObj> a = new ArrayList<GObj>();
		for (GObj obj: this) {
			if (obj.isSelected()) {
				a.add(obj);
			}
		}
		return a;
	}

	public void updateRelObjs() {
        RelObj obj;
		Point endPoint;
		for (GObj o: this) {
			if (o instanceof RelObj) {
                obj = (RelObj) o;
				if (!obj.startPort.isArea()) {
					obj.x = obj.startPort.getAbsoluteX();
					obj.y = obj.startPort.getAbsoluteY();
				} else {
					endPoint = VMath.nearestPointOnRectangle
						(obj.startPort.getStartX(), obj.startPort.getStartY(),
							obj.startPort.getWidth(), obj.startPort.getHeight(),
							obj.endPort.getAbsoluteX(), obj.endPort.getAbsoluteY());
					obj.x = endPoint.x;
					obj.y = endPoint.y;

				}

				if (!obj.endPort.isArea()) {
					obj.endX = obj.endPort.getAbsoluteX();
					obj.endY = obj.endPort.getAbsoluteY();
				} else {
					endPoint = VMath.nearestPointOnRectangle
						(obj.endPort.getStartX(), obj.endPort.getStartY(),
							obj.endPort.getWidth(), obj.endPort.getHeight(), obj.x, obj.y);
					obj.endX = endPoint.x;
					obj.endY = endPoint.y;
				}
				obj.Xsize = (float) Math.sqrt(Math.pow((obj.x - obj.endX), 2.0) + Math.pow((obj.y - obj.endY), 2.0)) / obj.width;
				obj.angle = VMath.calcAngle(obj.x, obj.y, obj.endX, obj.endY);
			}
		}
	}


	public void deleteExcessRels(ConnectionList con) {
		ArrayList<GObj> toBeRemoved = new ArrayList<GObj>();
		for (GObj obj: this) {
			if (obj instanceof RelObj) {
				if (!(contains(((RelObj) obj).startPort.getObject()) && contains(((RelObj) obj).endPort.getObject()))) {
					toBeRemoved.add(obj);
					con.removeAll(obj.getConnections());
				}
			}
		}
		removeAll(toBeRemoved);
	}

	public int controlRectContains(int x, int y) {
		int corner;
		for (GObj obj: this) {
			corner = obj.controlRectContains(x, y);
			if (corner != 0) {
				return corner;
			}
		}
		return 0;
	}

	public Port getPort(String objName, String portId) {
		Port port;
		for (GObj obj: this) {
			if (obj.getName().equals(objName)) {
				for (int j = 0; j < obj.ports.size(); j++) {
					port = obj.ports.get(j);
					if (port.getId() != null) {
						if (port.getId().equals(portId)) {
							return port;
						}
					} else if (port.getName().equals(portId)) {
						return port;
					}
				}
			}
		}

		return null;
	}
}
