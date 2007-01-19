package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;

import java.io.Serializable;
import java.util.ArrayList;
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

	private ArrayList<ClassField> inputs = new ArrayList<ClassField>();
	private ArrayList<ClassRelation> subtasks = new ArrayList<ClassRelation>();
	private ArrayList<ClassField> outputs = new ArrayList<ClassField>();
	private ArrayList<ClassField> exceptions = new ArrayList<ClassField>();

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
	 * @param i int - relation type.
	 */
        ClassRelation(int i, String specLine ) {
		type = i;
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

        ArrayList<ClassField> getInputs() {
            return inputs;
        }

        ArrayList<ClassField> getOutputs() {
            return outputs;
        }

        ArrayList<ClassRelation> getSubtasks() {
            return subtasks;
        }

        ArrayList<ClassField> getExceptions() {
            return exceptions;
        }

	/**
	 * Adds a new input to the list of inputs.
	 * @param field ClassField - a new input to be added to the list of inputs.
	 */
        void addInput(ClassField field) {
		inputs.add(field);
	} // addInput

	/**
	 * Specify output.
	 * @param s String
	 * @param vars ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void setOutput(String s, ArrayList<ClassField> vars) throws UnknownVariableException {
		ClassField f = getVar(s, vars);

		if (f != null) {
			outputs.add(f);
		}
		else if (s.indexOf(".") >= 1) {
			ClassField cf = new ClassField( s );

			outputs.add(cf);
		} else if (s.startsWith("*.")) {
			ClassField cf = new ClassField( s );

			outputs.add(cf);
		}
		else {
			throw new UnknownVariableException(s);
		}
	} // setOutput

	/**
	 * Specify input.
	 * @param s String
	 * @param vars ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void setInput(String s, ArrayList<ClassField> vars) throws UnknownVariableException {
		if (!s.equals("")) {
			ClassField f = getVar(s, vars);

			if (f != null) {
				inputs.add(f);
			}
			else if (s.indexOf(".") >= 1) {
				ClassField cf = new ClassField( s );

				inputs.add(cf);
			}
			else {
				throw new UnknownVariableException(s);
			}
		}
	} // setInput

	/**
	 * <UNCOMMENTED>
	 * @param method String
	 */
	void setMethod(String method) {
		this.method = method;
	} // setMethod

	/**
	 * <UNCOMMENTED>
	 * @param input String[]
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void addInputs(String[] input, ArrayList<ClassField> varList) throws UnknownVariableException {
		for (int i = 0; i < input.length; i++) {
			if (!input[i].equals("#")) {
				ClassField var = getVar(input[i], varList);

				if (var != null) {
					inputs.add(var);
				}
				else if (input[i].indexOf(".") >= 1) {
					ClassField cf = new ClassField( input[i] );

					inputs.add(cf);
				}
				else {
					throw new UnknownVariableException(input[i]);
				}
			}
		}
	} // addInputs

	/**
	 * <UNCOMMENTED>
	 * @param output String[]
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
        void addOutputs( String[] output, ArrayList<ClassField> varList ) throws UnknownVariableException {
            for ( int i = 0; i < output.length; i++ ) {
                //we must have at least one output, others will be considered as exceptions
                String s = output[ i ];
                Pattern pattern = Pattern.compile( ".*\\([ .A-Za-z0-9]+\\).*" );
                Matcher matcher = pattern.matcher( s );
                if ( matcher.find() ) {
                	
                	String ex = s.replaceAll( "[ ()]+", "" );
                    ClassField cf = new ClassField( ex, "exception" );

                    exceptions.add( cf );

                } else {

                    ClassField var = getVar( output[ i ], varList );

                    if ( var != null ) {
                        outputs.add( var );
                    } else if ( output[ i ].indexOf( "." ) >= 1 ) {
                        ClassField cf = new ClassField( output[ i ] );

                        outputs.add( cf );
                    } else if ( output[ i ].startsWith( "*." ) ) {
                        ClassField cf = new ClassField( output[ i ] );

                        outputs.add( cf );
                    } else {
                        throw new UnknownVariableException( output[ i ] );
                    }
                }
            }
        } // addOutputs

	/**
	 * <UNCOMMENTED>
	 * @param subtaskList ArrayList
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void addSubtasks(ArrayList<String> subtaskList, ArrayList<ClassField> varList) throws UnknownVariableException {
		ClassRelation subtask;
		String subtaskString;
		Pattern pattern;
		Matcher matcher2;

		for (int i = 0; i < subtaskList.size(); i++) {
			subtaskString = subtaskList.get(i);
			pattern = Pattern.compile("\\[(.*) *-> ?(.*)\\]");
			matcher2 = pattern.matcher(subtaskString);
			if (matcher2.find()) {
				subtask = new ClassRelation( RelType.TYPE_SUBTASK, subtaskString );
				
				String[] inputs = matcher2.group(1).trim().split(" *, *", -1);
				String[] outputs = matcher2.group(2).trim().split(" *, *", -1);
				for (int j = 0; j < outputs.length; j++) {
					subtask.setOutput(outputs[j], varList);
				}
				
				if (!inputs[0].equals("")) {
					subtask.addInputs(inputs, varList);
				}
				subtasks.add(subtask);
			}
		}
	} // addSubtasks

	/**
	 * <UNCOMMENTED>
	 * @param input String[]
	 * @param varList ArrayList
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException
	 */
	void addAll(String[] input, ArrayList<ClassField> varList) throws UnknownVariableException {
		for (int i = 0; i < input.length; i++) {
			ClassField var = getVar(input[i], varList);

			if (var != null) {
				inputs.add(var);
			}
			else {
				throw new UnknownVariableException(input[i]);
			}
		}
	} // addAll

	/**
	 * <UNCOMMENTED>
	 * @param varName String
	 * @param varList ArrayList
	 * @return ClassField
	 */
	static ClassField getVar(String varName, ArrayList<ClassField> varList) {
		ClassField var;

		for (int i = 0; i < varList.size(); i++) {
			var = varList.get(i);
			if (var.getName().equals(varName)) {
				return var;
			}
		}
		return null;
	} // getVar

	/**
	 * <UNCOMMENTED>
	 * @return String
	 */ public String toString() {
		if (type == RelType.TYPE_METHOD_WITH_SUBTASK) {
			return "[Subtasks: " + subtasks + "][Inputs: " + inputs + "][Output: " + outputs + "][Method: " + method + "][Type: " + type + "]";
		}
		return "[Inputs: " + inputs + "][Output: " + outputs + "][Method: " + method + "][Type: " + type + "]";
	} // toString

	public String getSpecLine() {
		return specLine;
	}

}
