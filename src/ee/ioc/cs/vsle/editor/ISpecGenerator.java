/*
 *
 */
package ee.ioc.cs.vsle.editor;

import java.util.ArrayList;

import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.VPackage;

public interface ISpecGenerator {
    public String generateSpec(ObjectList objects, ArrayList relations, VPackage pack);
}
