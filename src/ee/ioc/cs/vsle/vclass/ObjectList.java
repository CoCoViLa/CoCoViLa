package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.*;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjectList extends ArrayList
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
			obj = (GObj) this.get(i);
			if (obj.contains(x, y)) {
				return obj;
			}
		}
		return null;
	}

	public GObj checkInside(int x, int y, GObj asker) {
		GObj obj;

		for (int i = 0; i < this.size(); i++) {
			obj = (GObj) this.get(i);
			if (obj.contains(x, y) && obj != asker) {
				return obj;
			}
		}
		return null;
	}

	public void selectObjectsInsideBox(int x1, int y1, int x2, int y2) {
		GObj obj;

		for (int i = 0; i < this.size(); i++) {
			obj = (GObj) this.get(i);
			if (obj.isInside(x1, y1, x2, y2)) {
				obj.setSelected(true);
			}
		}
	}

	public void updateSize(float newXSize, float newYSize) {
		GObj obj;

		for (int i = 0; i < this.size(); i++) {
			obj = (GObj) this.get(i);
			obj.setXsize(obj.getXsize() * newXSize);
			obj.setYsize(obj.getYsize() * newYSize);
			obj.setX((int) (obj.getX() * newXSize));
			obj.setY((int) (obj.getY() * newYSize));
		}
	}

	public void clearSelected() {
		GObj obj;

		for (int i = 0; i < this.size(); i++) {
			obj = (GObj) this.get(i);
			obj.setSelected(false);
		}
	}

	public ArrayList getSelected() {
		ArrayList a = new ArrayList();
		GObj obj;

		for (int i = 0; i < this.size(); i++) {
			obj = (GObj) this.get(i);
			if (obj.isSelected()) {
				a.add(obj);
			}
		}
		return a;
	}

    public void updateRelObjs() {
  		GObj obj;
		Point endPoint;
		int endPointX;
		int endPointY;
        for (int i = 0; i < this.size(); i++) {
            obj = (GObj) this.get(i);
            if (obj instanceof RelObj) {
				db.p("rel on "+ obj+ " tema beginPort "+ ((RelObj)obj).startPort.hashCode());
				if (!((RelObj)obj).startPort.area) {
	                obj.x = ((RelObj)obj).startPort.getAbsoluteX();
    	            obj.y = ((RelObj)obj).startPort.getAbsoluteY();
				} else {
					endPoint = VMath.nearestPointOnRectangle
					(((RelObj)obj).startPort.getStartX(), ((RelObj)obj).startPort.getStartY(),
					 ((RelObj)obj).startPort.getWidth(), ((RelObj)obj).startPort.getHeight(),
						((RelObj)obj).endPort.getAbsoluteX(), ((RelObj)obj).endPort.getAbsoluteY());
					obj.x = endPoint.x;
					obj.y = endPoint.y;

				}

				if (!((RelObj)obj).endPort.area) {
					((RelObj)obj).endX = ((RelObj)obj).endPort.getAbsoluteX();
					((RelObj)obj).endY = ((RelObj)obj).endPort.getAbsoluteY();
				} else {
					endPoint = VMath.nearestPointOnRectangle
					(((RelObj)obj).endPort.getStartX(), ((RelObj)obj).endPort.getStartY(),
					 ((RelObj)obj).endPort.getWidth(), ((RelObj)obj).endPort.getHeight(), obj.x, obj.y);
					((RelObj)obj).endX = endPoint.x;
					((RelObj)obj).endY = endPoint.y;
				}
				obj.Xsize = (float)Math.sqrt(Math.pow((obj.x - ((RelObj)obj).endX), 2.0) + Math.pow((obj.y - ((RelObj)obj).endY), 2.0))/obj.width;
				((RelObj)obj).angle = VMath.calcAngle(obj.x, obj.y, ((RelObj)obj).endX, ((RelObj)obj).endY);

            }
        }
    }


	public void deleteExcessRels(ConnectionList con) {
   		GObj obj;
		ArrayList toBeRemoved = new ArrayList();
        for (int i = 0; i < this.size(); i++) {
            obj = (GObj) this.get(i);
            if (obj instanceof RelObj) {
				if (!(contains(((RelObj)obj).startPort.obj) && contains(((RelObj)obj).endPort.obj))) {
					toBeRemoved.add(obj);
					con.removeAll(obj.getConnections());
				}
            }
        }
		removeAll(toBeRemoved);
	}
}
