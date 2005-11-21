package ee.ioc.cs.vsle.synthesize;

import java.util.*;

//temporary
public class PlannerFactory {

	private IPlanner m_currentPanner = Planner.getInstance();
	
	private static PlannerFactory s_instance = new PlannerFactory();
	
	private PlannerFactory() {}
	
	public static PlannerFactory getInstance() {
		return s_instance;
	}

	public List<IPlanner> getAllInstances() {
		List<IPlanner> list = new ArrayList<IPlanner>();
		list.add(Planner.getInstance());
		list.add(DepthFirstPlanner.getInstance());
		return list;
	}
	
	public void setCurrentPlanner( IPlanner planner ) {
		m_currentPanner = planner;
	}
	
	public IPlanner getCurrentPlanner() {
		return m_currentPanner;
	}
}
