package ee.ioc.cs.vsle.vclass;

import java.util.ArrayList;
import java.io.Serializable;
import java.awt.*;

public class PackageClass implements Serializable {
    private static final long serialVersionUID = 1L;
	public String name;
	public String icon;
	public ArrayList<ClassField> fields = new ArrayList<ClassField>();
	public ClassGraphics graphics;
	public ArrayList<Port> ports = new ArrayList<Port>();
	public String description;
	public boolean relation = false;
    public String painterName;
    private ClassPainter painterPrototype;

	public PackageClass(String name) {
		this.name = name;
	}

	public PackageClass() {
		super();
	}

	@Override
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
			Port port = ports.get(i);

			if (port.getType().equals("in")) {
				g.setColor(Color.blue);
			} else if (port.getType().equals("out")) {
				g.setColor(Color.red);
			}

			g.setColor(Color.black);
		}
	}

	/**
	 * Sets the {@code ClassPainter} instance that is used to create
	 * new instances by cloning the first instacne.
	 * @param painter the painter prototype for this visual class
	 */
	public void setPainterPrototype(ClassPainter painter) {
		this.painterPrototype = painter;
	}
	
	/**
	 * Creates and returns a new {@code ClassPainter} instace.
	 * The returned instance is initialized with given arguments.
	 * @param scheme reference to the scheme
	 * @param obj the object the new painter is bound to
	 * @return new {@code ClassPainter} instance; {@code null} if there
	 * is no painter for this class
	 */
	public ClassPainter getPainterFor(Scheme scheme, GObj obj) {
		ClassPainter painter = null;
		if (painterPrototype != null) {
			painter = painterPrototype.clone();
			painter.setVClass(obj);
			painter.setScheme(scheme);
		}
		return painter;
	}
}
