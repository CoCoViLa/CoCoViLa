package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.db;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.ccl.*;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.equations.EquationSolver;

/**
 * This class takes care of parsing the specification and translating it into a graph on which planning can be run.
 * @author Ando Saabas
 */
public class SpecParser {
	public final int declaration = 1, assignment = 2, axiom = 3, equation = 4, alias = 5, specaxiom = 6, error = 10;

	public static void main(String[] args) {
		SpecParser p = new SpecParser();

		try {
			String s = new String(p.getStringFromFile(args[0]));
			ArrayList a = p.getSpec(p.refineSpec(s));

			while (!a.isEmpty()) {
				if (! (a.get(0)).equals("")) {
					db.p(p.getLine(a));
				}
				else {
					a.remove(0);

				}
			}
		}
		catch (Exception e) {
			db.p(e);
		}
	}

	/**
	 Return the contents of a file as a String object.
	 @param	fileName	name of the file name
	 */
	public String getStringFromFile(String fileName) throws IOException {
		db.p("Retrieving " + fileName);

		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String lineString, fileString = new String();

		while ( (lineString = in.readLine()) != null) {
			fileString += lineString;
		}
		in.close();
		return fileString;
	}

	/**
	 @return ArrayList of lines in specification
	 @param	text	Secification text as String
	 */
	public ArrayList getSpec(String text) {
		String[] s = text.trim().split(";", -1);
		ArrayList a = new ArrayList();

		for (int i = 0; i < s.length; i++) {
			a.add(s[i].trim());
		}
		return a;

	}

	/**
	 Reads a line from an arraylist of specification lines, removes it from the arraylist and returns the line
	 together with its type information
	 @return	a specification line with its type information
	 @param	a	arraylist of specification lines
	 *
	 LineType getFieldLine(ArrayList a) {
	 Matcher matcher2;
	 Pattern pattern;

	 while ((a.get(0)).equals("")) {
	 a.remove(0);
	 if (a.isEmpty()) {
	 return null;
	 }
	 }
	 String line = (String)a.get(0);
	 a.remove(0);
	 if (line.indexOf("=")>=0) {
	 pattern = Pattern.compile(" *([^= ]+) *= *(([0-9.]+)|(\".*\")|(new .*\\(.*\\))) *$");
	 matcher2 = pattern.matcher(line);
	 if (matcher2.find()) {
	 return new LineType(2, matcher2.group(1)+":"+matcher2.group(2));
	 }
	 }
	 else {
	 pattern = Pattern.compile("^ *([a-zA-Z_$][0-9a-zA-Z_$]*(\\[\\])?) (([a-zA-Z_$][0-9a-zA-Z_$]* ?, ?)* ?[a-zA-Z_$][0-9a-zA-Z_$]* ?$)");
	 matcher2 = pattern.matcher(line);
	 if (matcher2.find()) {
	 return new LineType(1, matcher2.group(1)+":"+matcher2.group(3));
	 }
	 else {
	 return new LineType(10, line);
	 }
	 }
	 return new LineType(10, line);
	 }*/


	/**
	 Reads a line from an arraylist of specification lines, removes it from the arraylist and returns the line
	 together with its type information
	 @return	a specification line with its type information
	 @param	a	arraylist of specification lines
	 */
	public LineType getLine(ArrayList a) {
		Matcher matcher2;
		Pattern pattern;

		while ( (a.get(0)).equals("")) {
			a.remove(0);
			if (a.isEmpty()) {
				return null;
			}
		}
		String line = (String) a.get(0);

		a.remove(0);
		if (line.indexOf("alias ") >= 0) {
			pattern = Pattern.compile("alias ([^= ]+) ? = \\((.*)\\) *");
			matcher2 = pattern.matcher(line);
			if (matcher2.find()) {
				String returnLine = matcher2.group(1) + ":" + matcher2.group(2);

				return new LineType(5, returnLine);
			}
			else {
				return new LineType(10, line);
			}
		} // Extract on solve equations
		else if (line.indexOf("=") >= 0) {
			pattern = Pattern.compile(" *([^= ]+) *= *((\".*\")|(new .*\\(.*\\))|(\\{.*\\})) *$");
			matcher2 = pattern.matcher(line);
			if (matcher2.find()) {
				return new LineType(2, matcher2.group(1) + ":" + matcher2.group(2));
			}
			else {

				pattern = Pattern.compile(" *([^=]+) *= *([-_0-9a-zA-Z.()\\+\\*/^ ]+) *$");
				matcher2 = pattern.matcher(line);
				if (matcher2.find()) {
					return new LineType(4, line);
				}
				else {
					return new LineType(10, line);
				}

			}

		}
		else if (line.indexOf("->") >= 0) {
			pattern = Pattern.compile("(.*) *-> *(.+) *\\{(.+)\\}");
			matcher2 = pattern.matcher(line);
			if (matcher2.find()) {
				return new LineType(3, line);
			}
			else { // check if its an axiom without method specification
				pattern = Pattern.compile("(.*) *-> *([ -_a-zA-Z0-9.,]+) *$");
				matcher2 = pattern.matcher(line);
				if (matcher2.find()) {
					return new LineType(6, line);

				}
				else {
					return new LineType(10, line);
				}
			}
		}
		else {
			pattern = Pattern.compile("^ *([a-zA-Z_$][0-9a-zA-Z_$]*(\\[\\])?) (([a-zA-Z_$][0-9a-zA-Z_$]* ?, ?)* ?[a-zA-Z_$][0-9a-zA-Z_$]* ?$)");
			matcher2 = pattern.matcher(line);
			if (matcher2.find()) {
				return new LineType(1, matcher2.group(1) + ":" + matcher2.group(3));
			}
			else {
				return new LineType(10, line);
			}
		}
	}

	/**
	 Extracts the specification from the java file, also removing unnecessary whitespaces
	 @return	specification text
	 @param fileString	a (Java) file containing the specification
	 */
	public String refineSpec(String fileString) throws IOException {
		Matcher matcher;
		Pattern pattern;

		// remove unneeded whitespace
		pattern = Pattern.compile("[ \t\n]+");
		matcher = pattern.matcher(fileString);
		fileString = matcher.replaceAll(" ");

		// find spec
		pattern = Pattern.compile(".*/\\*@.*specification [a-zA-Z_0-9-.]+ ?\\{ ?(.+) ?\\} ?@\\*/ ?");
		matcher = pattern.matcher(fileString);
		if (matcher.find()) {
			fileString = matcher.group(1);
		}
		return fileString;
	}

	/**
	 Creates the problem - a graph-like data structure on which planning can be applied. The method is recursively
	 applied to dig through the class tree.
	 @return	problem which can be given to the planner
	 @param classes the list of classes that exist in the problem setting.
	 @param type the type of object which is currently being added to the problem.
	 @param caller caller, or the "parent" of the current object. The objects name will be caller + . + obj.name
	 @param problem the problem itself (needed because of recursion).
	 */
	Problem makeProblem(ClassList classes, String type, String caller, Problem problem) throws SpecParseException {
		// ee.ioc.cs.editor.util.db.p("CLASSES: "+classes);
		// ee.ioc.cs.editor.util.db.p("TYPE: "+type);
		AnnotatedClass ac = classes.getType(type);
		ClassField cf = null;
		ClassRelation classRelation;
		Var var, var1, var2;
		Rel rel;
		HashSet relSet = new HashSet();

		for (int j = 0; j < ac.fields.size(); j++) {
			cf = (ClassField) ac.fields.get(j);
			if (classes.getType(cf.type) != null) {
				problem = makeProblem(classes, cf.type, caller + "." + cf.name, problem);
			}
			var = new Var();
			var.setObj(caller);
			var.setField(cf);
			var.setName(cf.name);
			var.setType(cf.type);
			problem.addVar(var);
		}

		for (int j = 0; j < ac.classRelations.size(); j++) {
			classRelation = (ClassRelation) ac.classRelations.get(j);
			cf = null;
			String obj = caller;

			rel = new Rel();
			boolean isAliasRel = false;

			/* If we have a relation alias = alias, we rewrite it into new relations, ie we create
			 a relation for each component of the alias structure*/
			if (classRelation.inputs.size() == 1 && classRelation.outputs.size() == 1) {
				ClassField cf1 = (ClassField) classRelation.inputs.get(0);
				ClassField cf2 = (ClassField) classRelation.outputs.get(0);

				if (problem.getAllVars().containsKey(obj + "." + cf1.name)) {
					Var v1 = (Var) problem.getAllVars().get(obj + "." + cf1.name);
					Var v2 = (Var) problem.getAllVars().get(obj + "." + cf2.name);

					if (v1.field.isAlias() && v2.field.isAlias()) {
						db.p( ( (Alias) v1.field).getAliasType() + " " + ( (Alias) v2.field).getAliasType());
						if (! ( (Alias) v1.field).getAliasType().equals( ( (Alias) v2.field).getAliasType())) {
							throw new AliasException("Differently typed aliases connected: " + obj + "." + cf1.name + " and " + obj + "." + cf2.name);
						}
						isAliasRel = true;
						for (int i = 0; i < v1.field.vars.size(); i++) {
							String s1 = ( (ClassField) v1.field.vars.get(i)).name;
							String s2 = ( (ClassField) v2.field.vars.get(i)).name;

							var1 = (Var) problem.getAllVars().get(v1.object + "." + s1);
							var2 = (Var) problem.getAllVars().get(v2.object + "." + s2);
							rel = new Rel();
							rel.setFlag(classRelation.inputs.size());
							rel.setSubtaskFlag(classRelation.subtasks.size());
							rel.setObj(obj);
							rel.setType(5);

							rel.addInput(var2);
							rel.addOutput(var1);
							var2.addRel(rel);
							problem.addRel(rel);
						}
					}
				}
			}


			if (!isAliasRel) {
				String s = checkIfRightWildcard(classRelation);
				if (s!=null) {
					relSet = makeRightWildcardRel(ac, classes, classRelation, problem, obj, s);
					rel = null;
				} else
					rel = makeRel(classRelation, problem, obj);
				if (classRelation.subtasks.size() > 0) {
					Rel subtaskRel = new Rel();

					for (int l = 0; l < classRelation.subtasks.size(); l++) {
						ClassRelation subtask = (ClassRelation) classRelation.subtasks.get(l);
						subtaskRel = makeRel(subtask, problem, obj);
						if (rel!=null) {
							rel.addSubtask(subtaskRel);
						} else {
							Iterator varsIter = relSet.iterator();
						    while (varsIter.hasNext()) {
                                Rel r = (Rel)varsIter.next();
								r.addSubtask(subtaskRel);
							}
						}
					}
				}
			}

			// if it is not a "real" relation (type 7), we just set the result as target, and inputs as known variables
			if (classRelation.type == 7) {
				setTargets(problem, classRelation, obj);
			}
			else if (classRelation.inputs.isEmpty()) { // if class relation doesnt have inputs, its an axiom
				problem.addAxiom(rel);
			}
			else {
				if (rel!=null)
					problem.addRel(rel);
				else
					problem.addAllRels(relSet);
			}

		}
		return problem;
	}

	private void isRightWildcard(ClassRelation classRelation, AnnotatedClass ac, ClassList classes, String type) {
		ClassField cf;
		String s = checkIfRightWildcard(classRelation);
		//if the right side of the axiom contains a wildcard, we'll rewrite the axiom

	}

	private String checkIfRightWildcard(ClassRelation classRelation) {
		String s =   ((ClassField)classRelation.outputs.get(0)).name;
		if(s.startsWith("*."))
        	return s.substring(2);
		return null;
	}

	/**
	 In case of a goal specification is included (eg a -> b), the right hand side is added to problem
	 targets, left hand side is added to known variables.
	 @param problem problem to be changed
	 @param classRelation the goal specification is extracted from it.
	 @param obj the name of the object where the goal specification was declared.
	 */
	void setTargets(Problem problem, ClassRelation classRelation, String obj) throws UnknownVariableException {
		Var var;
		ClassField cf;

		for (int k = 0; k < classRelation.inputs.size(); k++) {
			cf = (ClassField) classRelation.inputs.get(k);
			if (problem.getAllVars().containsKey(obj + "." + cf.name)) {
				var = (Var) problem.getAllVars().get(obj + "." + cf.name);
				problem.addKnown(var);
			}
			else {
				throw new UnknownVariableException(cf.name);
			}
		}
		for (int k = 0; k < classRelation.outputs.size(); k++) {
			cf = (ClassField) classRelation.outputs.get(k);
			if (problem.getAllVars().containsKey(obj + "." + cf.name)) {
				var = (Var) problem.getAllVars().get(obj + "." + cf.name);
				problem.addTarget(var);
			}
			else {
				throw new UnknownVariableException(cf.name);
			}
		}

	}


	HashSet makeRightWildcardRel(AnnotatedClass ac, ClassList classes, ClassRelation classRelation, Problem problem, String obj, String wildcardVar) throws UnknownVariableException {
        ClassField clf;
		HashSet set = new HashSet();
		for (int i = 0; i < ac.fields.size(); i++) {
			clf = (ClassField) ac.fields.get(i);
			AnnotatedClass anc = classes.getType(clf.type);
			if (anc != null) {
				if (anc.hasField(wildcardVar)) {

					Var var;
					Rel rel = new Rel();

					rel.setMethod(classRelation.method);
					rel.setFlag(classRelation.inputs.size());
					rel.setSubtaskFlag(classRelation.subtasks.size());
					rel.setObj(obj);
					rel.setType(classRelation.type);
					ClassField cf;

					for (int k = 0; k < classRelation.inputs.size(); k++) {
						cf = (ClassField) classRelation.inputs.get(k);
						if (problem.getAllVars().containsKey(obj + "." + cf.name)) {
							var = (Var) problem.getAllVars().get(obj + "." + cf.name);
							var.addRel(rel);
							rel.addInput(var);
						}
						else {
							throw new UnknownVariableException(cf.name);
						}
					}
					for (int k = 0; k < classRelation.outputs.size(); k++) {
						if (k==0) {
							if (problem.getAllVars().containsKey(obj + "." + clf.name+"."+wildcardVar)) {
								var = (Var) problem.getAllVars().get(obj + "." + clf.name+"."+wildcardVar);
								rel.addOutput(var);
							} else {
								throw new UnknownVariableException(obj + "." + clf.name+"."+wildcardVar);
							}
						} else{
							cf = (ClassField) classRelation.outputs.get(k);
							if (problem.getAllVars().containsKey(obj + "." + cf.name)) {
								var = (Var) problem.getAllVars().get(obj + "." + cf.name);
								rel.addOutput(var);
							} else {
								throw new UnknownVariableException(cf.name);
							}
						}

					}
					set.add(rel);
				}
			}
		}
		return set;
	}



	/**
	 creates a relation that will be included in the problem.
	 @param problem that will include relation (its needed to get variable information from it)
	 @param classRelation the implementational information about this relation
	 @param obj the name of the object where the goal specification was declared.
	 */

	Rel makeRel(ClassRelation classRelation, Problem problem, String obj) throws UnknownVariableException {
		Var var;
		Rel rel = new Rel();

		rel.setMethod(classRelation.method);
		rel.setFlag(classRelation.inputs.size());
		rel.setSubtaskFlag(classRelation.subtasks.size());
		rel.setObj(obj);
		rel.setType(classRelation.type);
		ClassField cf;

		for (int k = 0; k < classRelation.inputs.size(); k++) {
			cf = (ClassField) classRelation.inputs.get(k);
			if (problem.getAllVars().containsKey(obj + "." + cf.name)) {
				var = (Var) problem.getAllVars().get(obj + "." + cf.name);
				var.addRel(rel);
				rel.addInput(var);
			}
			else {
				throw new UnknownVariableException(cf.name);
			}
		}
		for (int k = 0; k < classRelation.outputs.size(); k++) {
			cf = (ClassField) classRelation.outputs.get(k);
			if (problem.getAllVars().containsKey(obj + "." + cf.name)) {
				var = (Var) problem.getAllVars().get(obj + "." + cf.name);
				rel.addOutput(var);
			}
			else {
				throw new UnknownVariableException(cf.name);
			}
		}
		return rel;
	}

	/**
	 A recrusve method that does the actual parsing. It creates a list of annotated classes that
	 carry infomation about the fields and relations in a class specification.
	 @param spec a specfication to be parsed. If it includes a declaration of an annotated class, it will be
	 recursively parsed.
	 @param	className the name of the class being parsed
	 @param	parent
	 @param	checkedClasses the list of classes that parser has started to check. Needed to prevent infinite loop
	 in case of mutual declarations.
	 */ public ClassList parseSpecification(String spec, String className, AnnotatedClass parent, HashSet checkedClasses) throws IOException, SpecParseException, EquationException {
		Matcher matcher2;
		Pattern pattern;
		String[] split;
		ArrayList vars = new ArrayList();
		ArrayList subtasks = new ArrayList();
		AnnotatedClass annClass = new AnnotatedClass(className, parent);
		ClassList classList = new ClassList();

		ArrayList specLines = getSpec(spec);

		try {

			while (!specLines.isEmpty()) {
				LineType lt = getLine(specLines);

				if (lt != null) {
					if (lt.type == assignment) {
						split = lt.specLine.split(":", -1);
						ClassRelation classRelation = new ClassRelation(3);

						classRelation.setOutput(split[0], vars);
						classRelation.setMethod(split[0] + " = " + split[1]);
						annClass.addClassRelation(classRelation);
						db.p(classRelation);

					}
					else if (lt.type == declaration) {
						split = lt.specLine.split(":", -1);
						String[] vs = split[1].trim().split(" *, *", -1);
						String type = split[0].trim();

						db.p("Checking existence of " + RuntimeProperties.packageDir + type + ".java");
						if (checkedClasses.contains(type)) {
							throw new MutualDeclarationException(className + " <-> " + type);
						}
						File file = new File(RuntimeProperties.packageDir + type + ".java");
						boolean specClass = false;

						// if a file by this name exists in the package directory and it includes a specification, we're gonna check it
						if (file.exists() && isSpecClass(type)) {
							specClass = true;
							if (classList.getType(type) == null) {
								checkedClasses.add(type);
								String s = new String(getStringFromFile(RuntimeProperties.packageDir + type + ".java"));

								classList.addAll(parseSpecification(refineSpec(s), type, annClass, checkedClasses));
								checkedClasses.remove(type);
								specClass = true;
							}
						}
						for (int i = 0; i < vs.length; i++) {
							if (varListIncludes(vars, vs[i])) {
								throw new SpecParseException("Variable " + vs[i] + " declared more than once in class " + className);
							}
							ClassField var = new ClassField(vs[i], type, specClass);

							vars.add(var);
						}

					}
					else if (lt.type == alias) {
						split = lt.specLine.split(":", -1);
						String[] list = split[1].trim().split(" *, *", -1);
						String name = split[0];
						Alias a = new Alias(name);

						a.addAll(list, vars);
						vars.add(a);
						ClassRelation classRelation = new ClassRelation(4);

						classRelation.addInputs(list, vars);
						classRelation.setMethod("alias");
						classRelation.setOutput(name, vars);
						annClass.addClassRelation(classRelation);
						db.p(classRelation);

						classRelation = new ClassRelation(4);
						classRelation.addOutputs(list, vars);
						classRelation.setMethod("alias");
						classRelation.setInput(name, vars);
						annClass.addClassRelation(classRelation);
						db.p(classRelation);

					}
					else if (lt.type == equation) {
						pattern = Pattern.compile("(\\*\\.[_a-zA-Z]|[_a-zA-Z]\\.\\*\\.[_a-zA-Z]|[_a-zA-Z]*\\.\\*)");
						matcher2 = pattern.matcher(lt.specLine);
						subtasks.clear();
						if (matcher2.find()) {
							String[] sides = lt.specLine.split("=");
							String leftSide = sides[0].trim();
							String rightSide = sides[1].trim();

						}
						EquationSolver.solve(lt.specLine);
						for (int i = 0; i < EquationSolver.relations.size(); i++) {
							String result = (String) EquationSolver.relations.get(i);
							String[] pieces = result.split(":");
							// if its actually alias
							/* if (getVar(pieces[2].trim(), vars).isAlias()) {
							 String[] inputs = pieces[1].trim().split(" ");
							 if (geVar(inputs[0].trim(), vars).isAlias()) {
							 if () {
							 }
							 }
							 } else {*/

							ClassRelation classRelation = new ClassRelation(3);

							classRelation.setOutput(pieces[2].trim(), vars);

							String[] inputs = pieces[1].trim().split(" ");

							if (!inputs[0].equals("")) {
								classRelation.addInputs(inputs, vars);
							}
							classRelation.setMethod(pieces[0]);
							annClass.addClassRelation(classRelation);
							db.p("Equation: " + classRelation);

						}
					}
					else if (lt.type == axiom) {
						pattern = Pattern.compile("\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]");
						matcher2 = pattern.matcher(lt.specLine);
						int a = 0;

						subtasks.clear();
						while (matcher2.find()) {
							db.p("matching " + matcher2.group(0));
							subtasks.add(matcher2.group(0));
						}
						lt.specLine = lt.specLine.replaceAll("\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]", "#");
						pattern = Pattern.compile("(.*) *-> ?(.*)\\{(.*)\\}");
						matcher2 = pattern.matcher(lt.specLine);
						if (matcher2.find()) {
							ClassRelation classRelation = new ClassRelation(2);

							if (matcher2.group(2).trim().equals("")) {
								throw new SpecParseException("Error in line \n" + lt.specLine + "\nin class " + className + ".\nAn axiom can not have an empty output.");
							}
							String[] outputs = matcher2.group(2).trim().split(" *, *", -1);

							if (!outputs[0].equals("")) {
								classRelation.addOutputs(outputs, vars);
							}

							String[] inputs = matcher2.group(1).trim().split(" *, *", -1);

							if (!inputs[0].equals("")) {
								classRelation.addInputs(inputs, vars);
							}
							if (subtasks.size() != 0) {
								classRelation.addSubtasks(subtasks, vars);
								classRelation.type = 6;
							}
							classRelation.setMethod(matcher2.group(3).trim());
							db.p(classRelation);
							annClass.addClassRelation(classRelation);
						}

					}
					else if (lt.type == specaxiom) {
						pattern = Pattern.compile("(.*) *-> *([-_a-zA-Z0-9.,]+) *$");
						matcher2 = pattern.matcher(lt.specLine);
						if (matcher2.find()) {
							ClassRelation classRelation = new ClassRelation(7);
							String[] outputs = matcher2.group(2).trim().split(" *, *", -1);

							if (!outputs[0].equals("")) {
								classRelation.addOutputs(outputs, vars);
							}

							String[] inputs = matcher2.group(1).trim().split(" *, *", -1);

							if (!inputs[0].equals("")) {
								classRelation.addInputs(inputs, vars);
							}
							db.p(classRelation);
							annClass.addClassRelation(classRelation);
						}
					}
					else if (lt.type == error) {
						throw new LineErrorException(lt.specLine);
					}
				}
			}
		}
		catch (UnknownVariableException uve) {
			throw new UnknownVariableException(className + "." + uve.excDesc);

		}
		annClass.addVars(vars);
		classList.add(annClass);
		return classList;
	}

	/**
	 @return list of fields declared in a specification.
	 */
	public ArrayList getFields(String fileName) throws IOException {
		ArrayList vars = new ArrayList();
		String s = new String(getStringFromFile(fileName));
		ArrayList specLines = getSpec(refineSpec(s));
		String[] split;

		while (!specLines.isEmpty()) {
			LineType lt = getLine(specLines);

			if (lt != null) {
				if (lt.type == assignment) {
					split = lt.specLine.split(":", -1);
					for (int i = 0; i < vars.size(); i++) {
						if ( ( (ClassField) vars.get(i)).name.equals(split[0])) {
							 ( (ClassField) vars.get(i)).value = split[1];
						}
					}
				}
				else if (lt.type == declaration) {
					split = lt.specLine.split(":", -1);
					String[] vs = split[1].trim().split(" *, *", -1);
					String type = split[0].trim();

					for (int i = 0; i < vs.length; i++) {
						ClassField var = new ClassField(vs[i], type);

						vars.add(var);
					}
				}
			}
		}
		return vars;
	}

	boolean isSpecClass(String file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(RuntimeProperties.packageDir + file + ".java"));
			String lineString, fileString = new String();

			while ( (lineString = in.readLine()) != null) {
				fileString += lineString;
			}
			in.close();
			if (fileString.matches(".*specification +" + file + ".*")) {

				return true;
			}
		}
		catch (IOException ioe) {
			db.p(ioe);
		}
		return false;
	}

	boolean varListIncludes(ArrayList vars, String varName) {
		ClassField cf;

		for (int i = 0; i < vars.size(); i++) {
			cf = (ClassField) vars.get(i);
			if (cf.name.equals(varName)) {
				return true;
			}
		}
		return false;
	}
}
