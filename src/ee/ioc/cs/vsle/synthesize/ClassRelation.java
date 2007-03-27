package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;
import static ee.ioc.cs.vsle.synthesize.SpecParser.*;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.ClassRelation</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author  Ando Saabas
 * @version 1.0
 */
class ClassRelation
	implements Serializable {

    private Collection<ClassField> inputs = new LinkedHashSet<ClassField>();
    private Collection<SubtaskClassRelation> subtasks = new LinkedHashSet<SubtaskClassRelation>();
    private Collection<ClassField> outputs = new LinkedHashSet<ClassField>();
    private Collection<ClassField> exceptions = new LinkedHashSet<ClassField>();

	/**
	 * Type of the relation.
	 * 2 - javamethod
	 * 3 - equation(and assignment)
	 * 4 - alias
	 * 5 - subtask
	 * 6 - method with subtask
	 * 7 - unimplemented
	 */
        private int type;
	private String method;
	private String specLine;

	/**
	 * Class constructor.
	 * @param type int - relation type.
	 */
        ClassRelation(int type, String specLine ) {
		this.type = type;
		this.specLine = specLine;
	} // ee.ioc.cs.editor.synthesize.ClassRelation

        int getType() {
            return type;
        }

        void setType( int value ) {
            type = value;
        }

        String getMethod() {
            return method;
        }

        /**
         * @return first input
         */
        ClassField getInput() {
            return inputs.iterator().next();
        }
        
        Collection<ClassField> getInputs() {
            return inputs;
        }

        /**
         * @return first output
         */
        ClassField getOutput() {
            return outputs.iterator().next();
        }
        
        Collection<ClassField> getOutputs() {
            return outputs;
        }

        Collection<SubtaskClassRelation> getSubtasks() {
            return subtasks;
        }

        Collection<ClassField> getExceptions() {
            return exceptions;
        }

        /**
         * Adds a new input to the list of inputs.
         * @param input String
         * @param vars ArrayList
         * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
         */
        void addInput(String input, Collection<ClassField> vars) throws UnknownVariableException {
        	
        	if (!input.equals("#")) {
				ClassField var = getVar(input, vars);

				if (var != null) {
					inputs.add(var);
				}
				else if ( input.indexOf(".") >= 1 ) {
					ClassField cf = new ClassField( input );

					inputs.add(cf);
				}
				else {
					throw new UnknownVariableException( input );
				}
			}
        } // setInput

    	/**
    	 * @param input String[]
    	 * @param varList ArrayList
    	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
    	 */
    	void addInputs(String[] input, Collection<ClassField> varList) throws UnknownVariableException {
    		for (int i = 0; i < input.length; i++) {
    			addInput( input[i], varList );
    		}
    	} // addInputs
	/**
	 * Specify output.
	 * @param output String
	 * @param vars ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void addOutput(String output, Collection<ClassField> vars) throws UnknownVariableException {
		ClassField f = getVar(output, vars);

		if (f != null) {
			outputs.add(f);
		}
		else if (output.indexOf(".") >= 1) {
			ClassField cf = new ClassField( output );

			outputs.add(cf);
		} else if (output.startsWith("*.")) {
			ClassField cf = new ClassField( output );

			outputs.add(cf);
		}
		else {
			throw new UnknownVariableException(output);
		}
	} // setOutput

	/**
	 * @param outputs String[]
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
        void addOutputs( String[] outputs, Collection<ClassField> varList ) throws UnknownVariableException {
            for ( int i = 0; i < outputs.length; i++ ) {
                //we must have at least one output, others will be considered as exceptions
                String output = outputs[ i ];
                Pattern pattern = Pattern.compile( ".*\\([ .A-Za-z0-9]+\\).*" );
                Matcher matcher = pattern.matcher( output );
                if ( matcher.find() ) {
                	
                	String ex = output.replaceAll( "[ ()]+", "" );
                    ClassField cf = new ClassField( ex, "exception" );

                    exceptions.add( cf );

                } else {
                	addOutput(output, varList);
                }
            }
        } // addOutputs

	/**
	 * @param subtaskList ArrayList
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void addSubtask( SubtaskClassRelation subtask ) {
		subtasks.add( subtask );
	} // addSubtasks

	/**
	 * @param method String
	 */
	void setMethod(String method) {
		this.method = method;
	} // setMethod
	
	public String getSpecLine() {
		return specLine;
	}
	
	/**
	 * @return String
	 */ public String toString() {
		if (type == RelType.TYPE_METHOD_WITH_SUBTASK) {
			return "[Subtasks: " + subtasks + "][Inputs: " + inputs + "][Output: " + outputs + "][Method: " + method + "][Type: " + type + "]";
		}
		return "[Inputs: " + inputs + "][Output: " + outputs + "][Method: " + method + "][Type: " + type + "]";
	} // toString

}
