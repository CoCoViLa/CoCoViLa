package ee.ioc.cs.vsle.synthesize;

import java.util.*;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.ClassField;

public class CodeGenerator {

    private static String offset = "";

    public static final String OT_TAB = "        ";

    private static final TypeToken TOKEN_INT = new TypeToken( "int", "Integer", "intValue" );
    private static final TypeToken TOKEN_DOUBLE = new TypeToken( "double", "Double", "doubleValue" );
    private static final TypeToken TOKEN_FLOAT = new TypeToken( "float", "Float", "floatValue" );
    private static final TypeToken TOKEN_CHAR = new TypeToken( "char", "Character", "charValue" );
    private static final TypeToken TOKEN_BYTE = new TypeToken( "byte", "Byte", "byteValue" );
    private static final TypeToken TOKEN_SHORT = new TypeToken( "short", "Short", "shortValue" );
    private static final TypeToken TOKEN_LONG = new TypeToken( "long", "Long", "longValue" );
    private static final TypeToken TOKEN_BOOLEAN = new TypeToken( "boolean", "Boolean", "booleanValue" );
    private static final TypeToken TOKEN_OBJECT = new TypeToken( null, "", "" );

    private final static int OT_NOC = 0;
    private final static int OT_INC = 1;
    private final static int OT_DEC = 2;

    private static int subCount = 0;

    public static final String ALIASTMP = "alias";
    public static int ALIASTMP_NR = 0;

    private static CodeGenerator s_codeGen = null;

    private CodeGenerator() {}

    public static CodeGenerator getInstance() {
        if ( s_codeGen == null ) {
            s_codeGen = new CodeGenerator();
        }
        offset = "";
        subCount = 0;
        ALIASTMP_NR = 0;
        return s_codeGen;
    }

    public String generate( ArrayList algRelList ) {
        StringBuffer alg = new StringBuffer();
        cOT( OT_INC, 2 );

        for ( int i = 0; i < algRelList.size(); i++ ) {
            Rel rel = ( Rel ) algRelList.get( i );

            if ( rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {
                appendRelToAlg( cOT( OT_NOC, 0 ), rel, alg );
            }

            else if ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                genSubTasks( rel, alg, false );
            }

        }
        return alg.toString();
    }

    private void genSubTasks( Rel rel, StringBuffer alg, boolean isNestedSubtask ) {
        int subNum;
        int start = subCount;

        for ( Iterator iter = rel.getSubtasks().iterator(); iter.hasNext(); ) {
            subNum = subCount++;

            alg.append( cOT( OT_NOC, 0 ) + "class " + Synthesizer.SUBTASK_INTERFACE_NAME + "_" + subNum
                        + " implements " + Synthesizer.SUBTASK_INTERFACE_NAME + " {\n" );
            alg.append( cOT( OT_INC, 1 ) + "public Object[] run(Object[] in) throws Exception {\n" );

            Rel subtask = ( Rel ) iter.next();
            ArrayList subInputs = ( ArrayList ) subtask.getInputs();
            ArrayList subOutputs = ( ArrayList ) subtask.getOutputs();
            ArrayList subAlg = ( ArrayList ) subtask.getAlgorithm();
            cOT( OT_INC, 1 );
            //apend subtask inputs to algorithm
            for ( int i = 0; i < subInputs.size(); i++ ) {
                Var in = ( Var ) subInputs.get( i );
                alg.append( getObjectFromSubtask( in, cOT( OT_NOC, 0 ), i, true ) );
            }
            boolean isSubOutputInAlgorithm = false;
            for ( int i = 0; i < subAlg.size(); i++ ) {
                Rel trel = ( Rel ) subAlg.get( i );
                System.out.println( "rel " + trel + " in " + trel.getInputs() + " out " + trel.getOutputs());
                if ( trel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                    //recursion
                    genSubTasks( trel, alg, true );

                } else if ( trel.getType() == RelType.TYPE_EQUATION ) {
                    if ( trel.getInputs().size() == 1 && trel.getOutputs().size() == 1 ) {
                        Var in = ( Var ) trel.getInputs().get( 0 );
                        Var out = ( Var ) trel.getOutputs().get( 0 );
                        boolean isSubInputInAlgorithm = false;
                        boolean isRelAdded = false;
                        if ( subInputs.contains( in ) ) {
                            //alg.append( cOT( OT_NOC, 0 ) + getObjectFromSubtask( in, 0, true ) );
                            appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                            isSubInputInAlgorithm = true;
                            isRelAdded = true;
                        } //else
                        if ( subOutputs.contains( out ) ) {
                            if ( !isSubInputInAlgorithm ) {
                                appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                            }
                            alg.append( getObjectFromSubtask( out, cOT( OT_NOC, 0 ), 0, false ) );
                            isSubOutputInAlgorithm = true;
                            isRelAdded = true;
                            break; //there should be no axioms after return statement.
                        }

                        if( ! isRelAdded ) {
                            appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                        }
                    } else
                        appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                } else {
                    appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                }

            }
            if( !isSubOutputInAlgorithm ) {
                Var out = ( Var ) subOutputs.get( 0 );
                alg.append( getObjectFromSubtask( out, cOT( OT_NOC, 0 ), 0, false ) );
            }
            alg.append( cOT( OT_DEC, 1 ) + "}\n"
                        + cOT( OT_DEC, 1 ) + "}\n" );

            alg.append( cOT( OT_NOC, 0 ) + "Subtask_" + subNum + " subtask_" + subNum +
                        " = new Subtask_" + subNum + "();\n" );
        }

        appendSubtaskRelToAlg( rel, start, alg, isNestedSubtask );

    }


    private String cOT( int incr, int times ) {
        //0 - no change, 1 - increase, 2 - decrease
        if ( incr == OT_INC ) {
            for ( int i = 0; i < times; i++ ) {
                offset += OT_TAB;
            }
            return offset;
        } else if ( incr == OT_DEC ) {
            for ( int i = 0; i < times; i++ ) {
                offset = offset.substring( OT_TAB.length() );
            }
            return offset;
        } else
            return offset;

    }

    private void appendRelToAlg( String offset, Rel rel, StringBuffer buf ) {
        String s = rel.toString();
        if ( !s.equals( "" ) ) {
            if(rel.getExceptions().size() == 0 ) {
                buf.append( offset + s + ";\n" );
            }
            else {
                buf.append( appendExceptions( rel, s ) );
            }
        }
    }

    private void appendSubtaskRelToAlg( Rel rel, int startInd, StringBuffer buf, boolean isNestedSubtask ) {

        String relString = rel.toString();
        for ( int i = 0; i < rel.getSubtasks().size(); i++ ) {
            relString = relString.replaceFirst( RelType.TAG_SUBTASK, "subtask_" + ( startInd + i ) );
        }
        if(rel.getExceptions().size() == 0 || isNestedSubtask ) {
            buf.append(cOT( OT_NOC, 0 ) + relString + ";\n" );
        }
        else {
            buf.append( appendExceptions( rel, relString ) );
        }
    }

    private String appendExceptions( Rel rel, String s ) {
        StringBuffer buf = new StringBuffer();
        buf.append( cOT( OT_NOC, 0 ) + "try {\n" +
                    cOT( OT_INC, 1 ) + s + ";\n" +
                    cOT( OT_DEC, 1 ) + "}\n" );
        for ( int i = 0; i < rel.getExceptions().size(); i++ ) {
            String excp = ( ( Var ) rel.getExceptions().get( i ) ).getName();
            String instanceName = "ex" + i;
            buf.append( cOT( OT_NOC, 0 ) + "catch( " + excp + " " + instanceName + " ) {\n" +
                        cOT( OT_INC, 1 ) + instanceName + ".printStackTrace();\n" +
                        cOT( OT_NOC, 0 ) + "return;\n" +
                        cOT( OT_DEC, 1 ) + "}\n" );
        }

        return buf.toString();
    }

    private String getObjectFromSubtask( Var var, String offset, int num, boolean isInput ) {

        if ( var.getField().isAlias() ) {
            if ( isInput ) {
                return getAliasSubtaskInput( var, offset, num);
            } else {
                return getAliasSubtaskOutput( var, offset );
            }
        }

        String varType = var.getType();
        TypeToken token = null;

        if ( varType.equals( TOKEN_INT.getType() ) ) {
            token = TOKEN_INT;
        } else if ( varType.equals( TOKEN_DOUBLE.getType() ) ) {
            token = TOKEN_DOUBLE;
        } else if ( varType.equals( TOKEN_FLOAT.getType() ) ) {
            token = TOKEN_FLOAT;
        } else if ( varType.equals( TOKEN_CHAR.getType() ) ) {
            token = TOKEN_CHAR;
        } else if ( varType.equals( TOKEN_BYTE.getType() ) ) {
            token = TOKEN_BYTE;
        } else if ( varType.equals( TOKEN_SHORT.getType() ) ) {
            token = TOKEN_SHORT;
        } else if ( varType.equals( TOKEN_LONG.getType() ) ) {
            token = TOKEN_LONG;
        } else if ( varType.equals( TOKEN_BOOLEAN.getType() ) ) {
            token = TOKEN_BOOLEAN;
        } else {
            token = TOKEN_OBJECT;
        }

        String s = offset;

        if ( isInput ) {
            if ( token == TOKEN_OBJECT ) {
                s += var.toString() + " = (" + varType + ")in[" + num + "];\n";
            } else {
                s += var.toString()
                    + " = ((" + token.getObjType() + ")in[" + num + "])."
                    + token.getMethod() + "();\n";
            }
        } else {
            if ( token == TOKEN_OBJECT ) {
                s += "return new Object[]{ " + var.toString() + "};\n";
            } else {
                s += "return new Object[]{ new "
                    + token.getObjType() + "( " + var.toString() + " )" + "};\n";
            }
        }

        return s;
    }

    private String getAliasSubtaskInput( Var input, String offset, int num ) {
        Alias alias = (Alias)input.getField();
        String aliasType = alias.getRealType();
        String aliasTmp = ALIASTMP + "_" + alias.getName() + "_" + ALIASTMP_NR++;
        String out = offset + aliasType + " " + aliasTmp + " = (" + aliasType
                     + ")in[" + num + "];\n";

        ClassField var;

        for ( int i = 0; i < alias.getVars().size(); i++ ) {
            var = ( ClassField ) alias.getVars().get( i );
            out += offset + Rel.getObject(input.getObject()) + var.getName() + " = " + aliasTmp + "[" + i + "];\n";
        }
        return out;
    }

    String getAliasSubtaskOutput( Var input, String offset ) {
		Alias alias = (Alias)input.getField();
        if(alias.getVars().size() == 0) {
            return offset + "return new Object[]{ }";
        }

        String aliasTmp = ALIASTMP + "_" + alias.getName() + "_" + ALIASTMP_NR++;
        String aliasType = alias.getRealType();
        String out = offset + aliasType + " " + aliasTmp + " = new " + aliasType + "{ ";

        ClassField var;
        for ( int i = 0; i < alias.getVars().size(); i++ ) {
            var = ( ClassField ) alias.getVars().get( i );
            if ( i == 0 ) {
                out += Rel.getObject(input.getObject()) + var.getName();
            } else {
                out += ", " + Rel.getObject(input.getObject()) + var.getName();
            }
        }
        out += " };\n"
                + offset + "return new Object[]{ " + aliasTmp + " };\n";

        return out;
    }

    private static class TypeToken {

        String m_type;
        String m_objType;
        String m_method;

        private TypeToken( String type, String objType, String method ) {
            m_type = type;
            m_objType = objType;
            m_method = method;
        }

        String getType() {
            return m_type;
        }

        String getObjType() {
            return m_objType;
        }

        String getMethod() {
            return m_method;
        }
    }
}
