package ee.ioc.cs.vsle.synthesize;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
