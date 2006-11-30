package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;

/**
 * The scheme description
 */
public class Scheme implements Serializable {

    private static final long serialVersionUID = 1L;
    public String packageName;
	public ObjectList objects = new ObjectList();
	public ConnectionList connections = new ConnectionList();
	public String name;

	public Scheme(ObjectList objects, ConnectionList connections) {
		this.objects = objects;
		this.connections = connections;
	}

	public Scheme() {
		// conctruct an empty scheme
	}

	@Override
	public String toString() {
		return "Objects: " + objects + " Connections: " + connections;
	}
}
