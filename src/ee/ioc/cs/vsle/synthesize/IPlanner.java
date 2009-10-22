package ee.ioc.cs.vsle.synthesize;

import java.awt.Component;
import java.util.*;

public interface IPlanner {
	public ArrayList<Rel> invokePlaning( Problem problem, boolean computeAll );
	
	public String getDescription();
	
	public Component getCustomOptionComponent();
}
