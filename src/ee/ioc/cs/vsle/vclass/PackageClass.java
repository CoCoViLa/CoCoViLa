package ee.ioc.cs.vsle.vclass;

import java.util.ArrayList;
import java.io.Serializable;
import java.awt.*;

public class PackageClass implements Serializable {
	int x, y;
	public String name;
	public String icon;
	public ArrayList fields = new ArrayList();
	public ClassGraphics graphics;
	public ArrayList ports = new ArrayList();
	public String description;
	public boolean relation = false;

	public PackageClass(String name) {
		this.name = name;
	}

	public PackageClass() {
		super();
	}

	public String toString() {
		return name;
	}

	public void addPort(Port port) {
		ports.add(port);
	}

	public void addGraphics(ClassGraphics gr) {
		graphics = gr;
	}

	void drawClassGraphics(int x, int y, Graphics2D g) {
		graphics.draw(x, y, 1, 1, g);
		for (int i = 0; i < ports.size(); i++) {
			Port port = (Port) ports.get(i);

			if (port.type.equals("in")) {
				g.setColor(Color.blue);
			} else if (port.type.equals("out")) {
				g.setColor(Color.red);
			}

			g.setColor(Color.black);
		}
	}
}
