package ee.ioc.cs.vsle.synthesize;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author pavelg
 *
 */
public class PlannerFactory {

    private Class<? extends IPlanner> currentPlannerClass;
	
	private static PlannerFactory s_instance = new PlannerFactory();
	
	private PlannerFactory() {
	    currentPlannerClass = DepthFirstPlanner.class;
	}
	
	public static PlannerFactory getInstance() {
		return s_instance;
	}

	public List<Class<? extends IPlanner>> getAllInstances() {
		List<Class<? extends IPlanner>> list = new ArrayList<Class<? extends IPlanner>>();
		list.add(DepthFirstPlanner.class);
		return list;
	}
	
	public void setCurrentPlannerClass( Class<? extends IPlanner> _class ) {
	    currentPlannerClass = _class;
	}
	
	public Class<? extends IPlanner> getCurrentPlannerClass() {
        return currentPlannerClass;
    }
	
	public IPlanner getPlannerInstance( Class<? extends IPlanner> planner ) {
	    
	    try {
            
            return (IPlanner) planner.getMethod( "getInstance" ).invoke( null );
            
        } catch ( IllegalArgumentException e ) {
            e.printStackTrace();
        } catch ( SecurityException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        
        return null;
        
	}
	
	public IPlanner getCurrentPlanner() {
	    
	    return getPlannerInstance( currentPlannerClass );
	}
}
