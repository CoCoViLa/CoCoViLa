package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;

/**

 */
public class Scheme implements Serializable {
	public String packageName;
	public int objCount;
	public ObjectList objects = new ObjectList();
	public ConnectionList connections = new ConnectionList();
	public String name;

	public String toString() {
		return "Objects: " + objects + " Connections: " + connections;
	}
}
