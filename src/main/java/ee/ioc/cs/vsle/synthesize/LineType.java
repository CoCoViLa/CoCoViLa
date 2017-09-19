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
 */
public class LineType {
    public static final String
            TYPE_DECLARATION = "DECLARATION",
            TYPE_ASSIGNMENT = "ASSIGNMENT",
            TYPE_AXIOM = "AXIOM",
            TYPE_EQUATION = "EQUATION",
            TYPE_ALIAS = "ALIAS",
            TYPE_SPECAXIOM = "SPECAXIOM",
            TYPE_CONST = "CONST",
            TYPE_SUPERCLASSES = "SUPERCLASSES",
            TYPE_ERROR = "ERROR";

	private String type;
	private Statement statement;
	private String origSpecLine;

	LineType(String t, Statement s, String o) {
		type = t;
		statement = s;
		origSpecLine = o;
	}

	public String toString() {
		return ( "Type=" + type + " Line=" + statement );
	}

	String getType() {
        return type;
    }

    Statement getStatement() {
        return statement;
    }

    public String getOrigSpecLine() {
        return origSpecLine;
    }
    
    public interface Statement {}
    
    public static class Declaration implements Statement {
        private boolean isStatic;
        private String type;
        private String[] names;
        /**
         * @param isStatic the isStatic to set
         */
        void setStatic( boolean isStatic ) {
            this.isStatic = isStatic;
        }
        /**
         * @return the isStatic
         */
        boolean isStatic() {
            return isStatic;
        }
        /**
         * @param type the type to set
         */
        void setType( String type ) {
            this.type = type;
        }
        /**
         * @return the type
         */
        String getType() {
            return type;
        }
        /**
         * @param names the names to set
         */
        void setNames( String[] names ) {
            this.names = names;
        }
        /**
         * @return the names
         */
        String[] getNames() {
            return names;
        }
    }
    
    public static class Alias implements Statement {
        
        private boolean isDeclaration;
        private boolean isAssignment;
        private String name;
        private String componentType;
        private String[] components;// = new ArrayList<String>();
        private boolean isWildcard;
        private String wildcardId;
        
        /**
         * @param parsedString the componentNames to set
         */
        void setComponents( String[] components ) {
            this.components = components;
        }
        /**
         * @return the componentNames
         */
        String[] getComponents() {
            return components;
        }
        /**
         * @param isDeclaration the isDeclaration to set
         */
        void setDeclaration( boolean isDeclaration ) {
            this.isDeclaration = isDeclaration;
        }
        /**
         * @return the isDeclaration
         */
        boolean isDeclaration() {
            return isDeclaration;
        }
        /**
         * @param isAssignment the isAssignment to set
         */
        void setAssignment( boolean isAssignment ) {
            this.isAssignment = isAssignment;
        }
        /**
         * @return the isAssignment
         */
        boolean isAssignment() {
            return isAssignment;
        }
        /**
         * @param name the name to set
         */
        void setName( String name ) {
            this.name = name;
        }
        /**
         * @return the name
         */
        String getName() {
            return name;
        }
        /**
         * @param componentType the componentType to set
         */
        void setComponentType( String componentType ) {
            this.componentType = componentType;
        }
        /**
         * @return the componentType
         */
        String getComponentType() {
            return componentType;
        }
        /**
         * @param isWildcard the isWildcard to set
         */
        void setWildcard( boolean isWildcard ) {
            this.isWildcard = isWildcard;
        }
        /**
         * @return the isWildcard
         */
        boolean isWildcard() {
            return isWildcard;
        }
        /**
         * @param wildcardId the wildcardId to set
         */
        void setWildcardId( String wildcardId ) {
            this.wildcardId = wildcardId;
        }
        /**
         * @return the wildcardId
         */
        String getWildcardId() {
            return wildcardId;
        }
    }
    
    public static class Superclasses implements Statement {
        private String[] classNames;

        /**
         * @param classNames the classNames to set
         */
        void setClassNames( String[] classNames ) {
            this.classNames = classNames;
        }

        /**
         * @return the classNames
         */
        String[] getClassNames() {
            return classNames;
        }
    }
    
    public static class Constant implements Statement {
        private String name;
        private String type;
        private String value;
        /**
         * @param name the name to set
         */
        void setName( String name ) {
            this.name = name;
        }
        /**
         * @return the name
         */
        String getName() {
            return name;
        }
        /**
         * @param type the type to set
         */
        void setType( String type ) {
            this.type = type;
        }
        /**
         * @return the type
         */
        String getType() {
            return type;
        }
        /**
         * @param value the value to set
         */
        void setValue( String value ) {
            this.value = value;
        }
        /**
         * @return the value
         */
        String getValue() {
            return value;
        }
    }
    
    public static class Assignment implements Statement {
        private String name;
        private String value;
        /**
         * @param name the name to set
         */
        void setName( String name ) {
            this.name = name;
        }
        /**
         * @return the name
         */
        String getName() {
            return name;
        }
        /**
         * @param value the value to set
         */
        void setValue( String value ) {
            this.value = value;
        }
        /**
         * @return the value
         */
        String getValue() {
            return value;
        }
    }
    
    public static class Equation implements Statement {
        private String eq;

        /**
         * @param eq the eq to set
         */
        void setEq( String eq ) {
            this.eq = eq;
        }

        /**
         * @return the eq
         */
        String getEq() {
            return eq;
        }
    }
    
    public static class Axiom implements Statement {
        private boolean isSpecAxiom;
        private Map<String, String[][]> subtasks = new LinkedHashMap<String, String[][]>();
        private String[] inputs;
        private String[] outputs;
        private String method;
        /**
         * @param isSpecAxiom the isSpecAxiom to set
         */
        void setSpecAxiom( boolean isSpecAxiom ) {
            this.isSpecAxiom = isSpecAxiom;
        }
        /**
         * @return the isSpecAxiom
         */
        boolean isSpecAxiom() {
            return isSpecAxiom;
        }
        /**
         * @param subtasks the subtasks to set
         */
        void setSubtasks( Map<String, String[][]> subtasks ) {
            this.subtasks = subtasks;
        }
        /**
         * @return the subtasks
         */
        Map<String, String[][]> getSubtasks() {
            return subtasks;
        }
        /**
         * @param inputs the inputs to set
         */
        void setInputs( String[] inputs ) {
            this.inputs = inputs;
        }
        /**
         * @return the inputs
         */
        String[] getInputs() {
            return inputs;
        }
        /**
         * @param outputs the outputs to set
         */
        void setOutputs( String[] outputs ) {
            this.outputs = outputs;
        }
        /**
         * @return the outputs
         */
        String[] getOutputs() {
            return outputs;
        }
        /**
         * @param method the method to set
         */
        void setMethod( String method ) {
            this.method = method;
        }
        /**
         * @return the method
         */
        String getMethod() {
            return method;
        }
    }
}
