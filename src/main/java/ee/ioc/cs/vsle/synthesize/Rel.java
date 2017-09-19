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

class Rel {

    private Collection<Var> outputs = new LinkedHashSet<Var>();
    private Collection<Var> inputs = new LinkedHashSet<Var>();
    //
    private int relID = 0;
    protected Var parent;
    private String method;
    //declaration in specification
    private String declaration;
    //see RelType
    private RelType type;
    private int hashcode;
    
    /* -------------------- LAZILY INITIALIZED VALUES -------------------- */
    private CodeGenerator.RelCodeProducer producer;
    private Var input;
    private Var output;
    private Collection<Var> exceptions;
    private Collection<SubtaskRel> subtasks;
    //Substitutions appear in the case of alias element access and are used
    //during the code generation, see CodeEmitter.emitEquation()
    //For instance, a.1 is replaced by x in the equation y=a.1
    //EquationSolver produces a method string that is directly used by CodeEmitter. 
    //Variable name a.1 has to be replaced by x in the method string to make it y=x;
    private Map<String, String> substitutions = null;
    /* ------------------------------------------------------------------- */
    
    Rel( Var parent, String declaration ) {
        relID = RelType.nextRelNr();
        this.parent = parent;
        this.declaration = declaration;
        hashcode = RelType.REL_HASH + relID;
    }

    int getId() {
        return relID;
    }
    
    RelType getType() {
        return type;
    }

    void setType(RelType t) {
        type = t;
    }
    
    void addInput(Var var) {
        inputs.add( var );
    }
    void addInputs( Collection<Var> vars ) {
        inputs.addAll( vars );
    }
    
    /**
     * NB! inputs set cannot be managed outside
     * @return inputs
     */
    Collection<Var> getInputs() {
        return inputs;
    }
    
    void addOutput(Var var) {
        outputs.add(var);
    }

    void addOutputs( Collection<Var> vars ) {
    	outputs.addAll( vars );
    }
    
    /**
     * NB! outputs set cannot be managed outside
     * @return outputs
     */
    Collection<Var> getOutputs() {
        return outputs;
    }
    
    void addSubtask(SubtaskRel rel) {
        getSubtasks().add(rel);
    }
    
    Collection<Var> getExceptions() {
        if( exceptions == null)
            exceptions = new LinkedHashSet<Var>();
        return exceptions;
    }

    Collection<SubtaskRel> getSubtasks() {
        if( subtasks == null )
            subtasks = new LinkedHashSet<SubtaskRel>();
        return subtasks;
    }

    int getSubtaskCount() {
        return subtasks == null ? 0 : subtasks.size();
    }
    
    void setMethod(String m) {
        method = m;
    }

    String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object e) {
        return this == e || (e != null && this.relID == ((Rel) e).relID);
    }

    @Override
    public int hashCode() {
        return hashcode;
    }
    
    Var getFirstInput() {
        if( input == null )
            input = inputs.iterator().next();
        return input;
    }
    
    Var getFirstOutput() {
        if( output == null )
            output = outputs.iterator().next();
        return output;
    }

    public String getDeclaration() {
        return declaration;
    }
    
    public Var getParent() {
        return parent;
    }
    
    void addSubstitutions( Map<String, String> substs ) {
        if ( substs != null )
            getSubstitutions().putAll( substs );
    }
    
    Map<String, String> getSubstitutions() {
        
        if(substitutions==null)
            substitutions = new LinkedHashMap<String, String>();
        
        return substitutions;
    }
    
    /**
     * This is for debug only!!!
     */
    @Override
    public String toString()
    {
        if(producer==null)
            return new CodeGenerator.RelCodeProducer(this).emit();
        
        return producer.emit();
    }
}
