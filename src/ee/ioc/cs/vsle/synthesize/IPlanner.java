package ee.ioc.cs.vsle.synthesize;

import java.util.*;

public interface IPlanner {
	public ArrayList invokePlaning( Problem problem, boolean computeAll );
	
	public String getDescription();
}
