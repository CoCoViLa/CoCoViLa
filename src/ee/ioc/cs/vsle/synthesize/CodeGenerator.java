package ee.ioc.cs.vsle.synthesize;

import java.util.*;
import java.util.regex.*;
import ee.ioc.cs.vsle.util.db;

public class CodeGenerator {
    final String tab = "        ";
    String offset = "";
    int subCount = 0;
    ArrayList subtask_outputs = new ArrayList();
    public CodeGenerator() {}

    public String generate(ArrayList algRelList) {
        boolean subRelisNeeded = false;
        String algorithm = "";
        StringBuffer alg = new StringBuffer();
        offset += tab;
        addOffset(1,1);
        for (int i = 0; i < algRelList.size(); i++) {
            Object temp = algRelList.get(i);
            Rel rel = null;
            if (isRel(temp) && subCount == 0) {
                rel = (Rel) temp;
                alg.append(addOffset(0,0) + rel.toString() + ";\n");
                continue;
            }
            if (isString(temp) && ( (String) temp).equals("<subtask>")) {
                subCount++;
                alg.append(addOffset(0,0) + "class Subtask_" + subCount +
                           " implements Subtask {\n");
                alg.append(addOffset(1,1) +
                           "public Object[] run(Object[] in) throws Exception {\n");
                if (isRel(algRelList.get(i + 1)) &&
                    ( (Rel) algRelList.get(i + 1)).type == RelType.subtask) {
                    Rel subtask = (Rel) algRelList.get(i + 1);
                    subtask_outputs.add(subtask.outputs.get(0));
                    db.p(subtask_outputs);
                    int sub_inputsCount = subtask.inputs.size();
                    offset += tab;
                    for (int j = 1; j < sub_inputsCount + 1; j++) {
                        Var var = (Var) ( (Rel) algRelList.get(i + 1 + j)).
                            inputs.get(0);
                        if (var.type.equals("int")) {
                            alg.append(addOffset(0,0) + var + " = " +
                                       intToObj("in[" + (j - 1) + "]") + ";\n");
                        }
                    }
                    subRelisNeeded = true;
                    i += sub_inputsCount;
                }
                continue;
            }
            if (isRel(temp) && subCount > 0) {
                db.p( ( (Rel) temp).toString());
                db.p( ( (Rel) temp).outputs.get(0).toString());
                rel = (Rel) temp;
                if(subRelisNeeded)
                    alg.append(addOffset(0,0) + rel.toString() + ";\n");
                if (subtask_outputs.contains(rel.outputs.get(0))) {
                    subRelisNeeded = false;
                    alg.append(addOffset(0,0) + "Object[] out = new Object[1];\n" +
                               addOffset(0,0) + "out[0] = " +
                               objFromInt(rel.outputs.get(0).toString()) + ";\n" +
                               addOffset(0,0) + "return out;\n" +
                               addOffset(2,1) + "}\n" +
                               addOffset(2,1) + "}\n" +
                               addOffset(0,0) + "Subtask_" + subCount + " subtask_" + subCount +
                               " = new Subtask_" + subCount + "();\n"
                               );
                }
                continue;
            }
            if (isString(temp) && ( (String) temp).equals("</subtask>")) {
                if (isRel(algRelList.get(i + 1))&&
                    ( (Rel) algRelList.get(i + 1)).type == RelType.method_with_subtask) {
                    Rel method_subtask = (Rel) algRelList.get(i + 1);
                    if(method_subtask.subtasks.size() > 0)
                    {
                        String pars = "";
                        if(method_subtask.inputs.size() > 0)
                            pars = ", ";
                        alg.append(addOffset(0,0) + (Var) method_subtask.outputs.get(0) +
                                   " = " + method_subtask.getObject(method_subtask.object) + method_subtask.method +
                                   "(subtask_" + subCount + pars + method_subtask.getParameters(false) + ");\n"
                                   );
                        subCount--;
                        i++;
                    }
                }
                continue;
            }


        }
        return alg.toString();
    }
    String addOffset(int incr, int times)
    {
        //0 - no change, 1 - increase, 2 - decrease
        if(incr == 1)
        {
            for (int i = 0; i < times; i++) {
                offset += tab;
            }
            return offset;
        }
        else if(incr == 2)
        {
            for (int i = 0; i < times; i++) {
                offset = offset.substring(tab.length());
            }
            return offset;
        }
        else
            return offset;


    }
    boolean isRel(Object ob) {
        return ob instanceof Rel;
    }

    boolean isString(Object ob) {
        return ob instanceof String;
    }

    String objFromInt(String var) {
        return "new Integer(" + var + ")";
    }

    String intToObj(String var) {
        return "((Integer)" + var + ").intValue()";
        //"m.row = ((Integer)in[0]).intValue();"
//        return null;
    }

    String genSubTasks(String alg, int st, ArrayList algRelList) {
        for (int i = st + 1; i < algRelList.size(); i++) {
            Object temp = algRelList.get(i);
            Rel rel = null;
            if (temp instanceof Rel) {

            }

        }
        return null;
    }

    String relPrint(Rel rel) {
        String relString = "";
        return relString;
    }

};
