package ee.ioc.cs.vsle.synthesize;

import java.awt.*;

public interface IPlanner {
	public EvaluationAlgorithm invokePlaning( Problem problem, boolean computeAll );
	
	public String getDescription();
	
	public Component getCustomOptionComponent();
}
