package ee.ioc.cs.vsle.synthesize;

import java.util.*;
import ee.ioc.cs.vsle.util.db;

public class CodeGenerator {

    private static String offset = "";

    private static final String OT_TAB = "        ";

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

    private static CodeGenerator s_codeGen = null;

    private CodeGenerator() {}

    public static CodeGenerator getInstance() {
        if ( s_codeGen == null ) {
            s_codeGen = new CodeGenerator();
        }
        offset = "";
        subCount = 0;
        return s_codeGen;
    }

    public String generate( ArrayList algRelList ) {
        StringBuffer alg = new StringBuffer();
        cOT( OT_INC, 2 );

        for ( int i = 0; i < algRelList.size(); i++ ) {
            Rel rel = ( Rel ) algRelList.get( i );

            if ( rel.type != RelType.method_with_subtask ) {
                appendRelToAlg( cOT( OT_NOC, 0 ), rel, alg );
            }

            else if ( rel.type == RelType.method_with_subtask ) {
                genSubTasks( rel, alg );
            }

        }
        return alg.toString();
    }

    private void genSubTasks( Rel rel, StringBuffer alg ) {
        int subNum;
        int start = subCount;

        for ( Iterator iter = rel.getSubtasks().iterator(); iter.hasNext(); ) {
            subNum = subCount++;

            alg.append( cOT( OT_NOC, 0 ) + "class Subtask_" + subNum + " implements Subtask {\n" );
            alg.append( cOT( OT_INC, 1 ) + "public Object[] run(Object[] in) throws Exception {\n" );

            Rel subtask = ( Rel ) iter.next();
            ArrayList subInputs = ( ArrayList ) subtask.getInputs();
            ArrayList subOutputs = ( ArrayList ) subtask.getOutputs();
            ArrayList subAlg = ( ArrayList ) subtask.getAlgorithm();
            cOT( OT_INC, 1 );
            for ( int i = 0; i < subAlg.size(); i++ ) {
                Rel trel = ( Rel ) subAlg.get( i );

                if ( trel.type == RelType.method_with_subtask ) {
                    //recursion
                    genSubTasks( trel, alg );

                } else if ( trel.type == RelType.equation ) {
                    if ( trel.getInputs().size() == 1 && trel.getOutputs().size() == 1 ) {
                        Var in = ( Var ) trel.getInputs().get( 0 );
                        Var out = ( Var ) trel.getOutputs().get( 0 );

                        if ( subInputs.contains( in ) ) {
                            alg.append( cOT( OT_NOC, 0 ) + getObjectFromSubInput( in, true ) );
                            appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                        } else if ( subOutputs.contains( out ) ) {
                            appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                            alg.append( cOT( OT_NOC, 0 ) + getObjectFromSubInput( out, false ) );
                            break; //there should be no axioms after return statement.
                        }
                    } else
                        appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                } else {
                    appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                }

            }

            alg.append( cOT( OT_DEC, 1 ) + "}\n"
                        + cOT( OT_DEC, 1 ) + "}\n" );

            alg.append( cOT( OT_NOC, 0 ) + "Subtask_" + subNum + " subtask_" + subNum +
                        " = new Subtask_" + subNum + "();\n" );
        }

        String relString = rel.toString();
        for ( int i = 0; i < rel.getSubtasks().size(); i++ ) {
            relString = relString.replaceFirst( RelType.TAG_SUBTASK, "subtask_" + ( start + i ) );
        }
        alg.append( appendExceptions( rel, relString ) );
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
            if(rel.exceptions.size() == 0 ) {
                buf.append( offset + s + ";\n" );
            }
            else {
                buf.append( appendExceptions( rel, s ) );
            }
        }
    }

    private String appendExceptions( Rel rel, String s ) {
        StringBuffer buf = new StringBuffer();
        buf.append( cOT( OT_NOC, 0 ) + "try {\n" +
                    cOT( OT_INC, 1 ) + s + ";\n" +
                    cOT( OT_DEC, 1 ) + "}\n" );
        for ( int i = 0; i < rel.exceptions.size(); i++ ) {
            String excp = ( ( Var ) rel.exceptions.get( i ) ).name;

            buf.append( cOT( OT_NOC, 0 ) + "catch( " + excp + " ex" + i + " ) {\n" +
                        cOT( OT_NOC, 0 ) + "}\n" );
        }

        return buf.toString();
    }

    private String getObjectFromSubInput( Var var, boolean isInput ) {

        String varType = var.type;
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

        String s = null;

        if ( isInput ) {
            if ( token == TOKEN_OBJECT ) {
                s = var.toString() + " = (" + var.type + ")in[0];\n";
            } else {
                s = var.toString()
                    + " = ((" + token.getObjType() + ")in[0])."
                    + token.getMethod() + "();\n";
            }
        } else {
            if ( token == TOKEN_OBJECT ) {
                s = "return new Object[]{ " + var.toString() + "};\n";
            } else {
                s = "return new Object[]{ new "
                    + token.getObjType() + "( " + var.toString() + " )" + "};\n";
            }
        }

        return s;
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
