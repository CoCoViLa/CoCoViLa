/*
 *
 */
package ee.ioc.cs.vsle.editor;

import java.util.ArrayList;

import ee.ioc.cs.vsle.vclass.*;

public interface ISpecGenerator {
    public String generateSpec(ObjectList objects, ArrayList<Connection> relations, VPackage pack);
}
