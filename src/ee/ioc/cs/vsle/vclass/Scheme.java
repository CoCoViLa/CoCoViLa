package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;

/**
 * The scheme description
 */
public class Scheme implements Serializable {

    private static final long serialVersionUID = 1L;
    private final VPackage vpackage;
    private ObjectList objects;
	private ConnectionList connections;

	public Scheme(VPackage vpackage, ObjectList objects,
			ConnectionList connections) {

		this.vpackage = vpackage;
		this.objects = objects;
		this.connections = connections;
	}

	public Scheme(VPackage vpackage) {
		this.vpackage = vpackage;
	}

	@Override
	public String toString() {
		return "Objects: " + objects + " Connections: " + connections;
	}

	public VPackage getVPackage() {
		return vpackage;
	}

	public String getPackageName() {
		return vpackage.getName();
	}

	public ObjectList getObjects() {
		if (objects == null)
			objects = new ObjectList();

		return objects;
	}

	public void setObjects(ObjectList objects) {
		this.objects = objects;
	}

	public ConnectionList getConnections() {
		if (connections == null)
			connections = new ConnectionList();

		return connections;
	}

	public void setConnections(ConnectionList connections) {
		this.connections = connections;
	}

	/**
	 * Returns the super class of the scheme if there is one.
	 * @return the super class, null if there is none
	 */
	public GObj getSuperClass() {
		for (GObj obj : objects)
			if (obj.isSuperClass())
				return obj;

		return null;
	}
}
