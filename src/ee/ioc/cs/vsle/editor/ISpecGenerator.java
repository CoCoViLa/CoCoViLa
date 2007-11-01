package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.Scheme;

public interface ISpecGenerator {
    public String generateSpec(Scheme scheme, String className);
    
    public CustomFileFilter getFileFilter();
}
