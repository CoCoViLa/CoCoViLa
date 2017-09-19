/**
 * 
 */
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

import java.util.*;

/**
 * The context is the only thing that holds the current state
 * of planner and the only thing that gets copied
 * 
 * @author pavelg
 */
public class PlanningContext {

    private Set<Var> knownVars = new LinkedHashSet<Var>();
    private Set<Var> foundVars = new LinkedHashSet<Var>();
    private Map<Rel, Set<Var>> availableRelsWithUnknownInputs = new LinkedHashMap<Rel, Set<Var>>();
    private Set<Var> allGoals = new LinkedHashSet<Var>();
    private Set<Var> remainingGoals = new LinkedHashSet<Var>();
    
    boolean isRelReadyToUse(Rel rel) {
        return availableRelsWithUnknownInputs.get( rel ).size() == 0;
    }

    void removeUnknownInput( Rel rel, Var var ) {
        getRelInputsSet(rel).remove( var );
    }

    void addUnknownInputs( Rel rel, Collection<Var> vars ) {
        Set<Var> unknownInputs = getRelInputsSet(rel);
        for (Var var : vars) {
            if( !var.getField().isConstant() ) {
                unknownInputs.add( var );
            }
        }
    }
    
    boolean isAvailableRel(Rel rel) {
        return availableRelsWithUnknownInputs.containsKey( rel );
    }
    
    void removeRel(Rel rel) {
        availableRelsWithUnknownInputs.remove( rel );
    }
    
    /**
     * @return the availableRels
     */
    public Set<Rel> getAvailableRels() {
        return availableRelsWithUnknownInputs.keySet();
    }

    Set<Var> getKnownVars() {
        return knownVars;
    }

    Set<Var> getFoundVars() {
        return foundVars;
    }

    public void addGoals(Collection<Var> goals) {
        allGoals.addAll( goals );
        remainingGoals.addAll( goals );
    }
    
    /**
     * @return the allGoals
     */
    public Set<Var> getAllGoals() {
        return allGoals;
    }

    /**
     * @return the remainingGoals
     */
    public Set<Var> getRemainingGoals() {
        return remainingGoals;
    }
    
    @Override
    public String toString() {
        return ("Known: " + knownVars + "\n Targets:" + allGoals );
    }
    
    //copies the context skipping goals
    PlanningContext getCopy() {
        PlanningContext newContext = new PlanningContext();
        newContext.foundVars.addAll( foundVars );
        newContext.knownVars.addAll( knownVars );
        for ( Rel rel : availableRelsWithUnknownInputs.keySet() ) {
            newContext.availableRelsWithUnknownInputs.put( rel, 
                    new LinkedHashSet<Var>( availableRelsWithUnknownInputs.get( rel ) ) );
        }
        return newContext;
    }
    
    private Set<Var> getRelInputsSet(Rel rel) {
        Set<Var> unknownInputs;
        if( (unknownInputs = availableRelsWithUnknownInputs.get( rel )) == null) {
            unknownInputs = new LinkedHashSet<Var>();
            availableRelsWithUnknownInputs.put( rel, unknownInputs );
        }
        
        return unknownInputs;
    }
    
}
