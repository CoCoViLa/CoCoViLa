package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.editor.RuntimeProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 This class is responsible for  planning and code generation.
 @author Ando Saabas
 */
public class Synthesizer {
	final int declaration = 1, assignment = 2, axiom = 3, equation = 4, alias = 5, error = 10;
    public static boolean tempIsDone = false; //indicates if it is needed to declare a TEMP variable in Rel.java when generating code, or it has already been done


    /** @link dependency */
    /*# Planner lnkPlanner; */

    /** @link dependency */
    /*# SpecParser lnkSpecParser; */

	/** Does the planning.
	 @return a program implementing the specification.
	 @param problem the specification unfolded as a graph.
	 @param computeAll set to true, if we try to find everything that can be computed on the �roblem graph.
	 */

	String planner(Problem problem, boolean computeAll) {

		/* while iterating through hashset, items cant be removed from/added to that set.
		 Theyre collected into these sets and added/removedall together after iteration
		 is finished*/
		HashSet newComponents = new HashSet();
		HashSet removableComponents = new HashSet();
		HashSet removableTargets = new HashSet();

		HashSet targets = new HashSet();
		// the set of variables we know in the graph.
		HashSet foundVars = new HashSet();

		foundVars.addAll(problem.knownVars);

		ArrayList algorithm = new ArrayList();

		Iterator knownVarsIter;
		Iterator relIter;
		Iterator targetIter;
		Iterator axiomIter;

		// String algorithm ="";

		Var var, targetVar, relVar;
		Rel rel;
		// Iterate through all components/variables
		boolean changed = true;

		targetIter = problem.targetVars.iterator();
		while (targetIter.hasNext()) {
			targets.add(targetIter.next());
		}

		Iterator allVarsIter = problem.allVars.values().iterator();

		while (allVarsIter.hasNext()) {
			var = (Var) allVarsIter.next();
		}

		db.p("------Starting planning--------");
		axiomIter = problem.axioms.iterator();
		while (axiomIter.hasNext()) {
			rel = (Rel) axiomIter.next();
			problem.addKnown(rel.outputs);
			algorithm.add(rel);
			foundVars.addAll(rel.outputs);
		}
		// ee.ioc.cs.editor.util.db.p("alustan probleem on:"+problem);
		int counter = 1;

		while ( (!computeAll && changed && !problem.targetVars.isEmpty()) || (changed && computeAll)) {
			db.p("----Iteration " + counter + "----");
			counter++;
			changed = false;
			targetIter = problem.targetVars.iterator();
			while (targetIter.hasNext()) {
				targetVar = (Var) targetIter.next();
				if (problem.knownVars.contains(targetVar)) {
					removableTargets.add(targetVar);
				}
			}
			problem.targetVars.removeAll(removableTargets);
			db.p("Known:" + problem.knownVars);
			knownVarsIter = problem.knownVars.iterator();
			while (knownVarsIter.hasNext()) {
				var = (Var) knownVarsIter.next();
				// Check the relations of all components
				relIter = var.rels.iterator();
				while (relIter.hasNext()) {
					rel = (Rel) relIter.next();
					if (problem.allRels.contains(rel)) {
						rel.flag--;
						removableComponents.add(var);
						if (rel.flag == 0) {
							db.p("rel on see "+ rel);

							boolean relIsNeeded = false;

							for (int i = 0; i < rel.outputs.size(); i++) {
								db.p("tema outputsid "+ rel.outputs);
								relVar = (Var) rel.outputs.get(i);
								if (!foundVars.contains(relVar)) {
									relIsNeeded = true;
								}
							}
							if (rel.outputs.isEmpty()) {
								relIsNeeded = true;
							}
							if (relIsNeeded && rel.subtaskFlag < 1) {
								db.p("ja vajati "+ rel);
								if (!rel.outputs.isEmpty()) {
									newComponents.addAll(rel.outputs);
									foundVars.addAll(rel.outputs);
								}
								algorithm.add(rel);
							}
                            if(rel.subtaskFlag > 0)
                            {
                                db.p("subtaskid: " + rel.subtasks);
                            }

							problem.allRels.remove(rel);
							changed = true;
						}
					}
				}
			}

			// relIter = var.rels.iterator();

			// problem.allRels.contains(rel)) {


			db.p("foundvars " +foundVars);
			problem.knownVars.addAll(newComponents);
			problem.knownVars.removeAll(removableComponents);
			newComponents.clear();
		}
		if (problem.targetVars.isEmpty()) {
			db.p("Problem was solved");
		}
		else {
			db.p("Problem not solved");
		}
		StringBuffer alg = new StringBuffer();

		if (!computeAll) {
			Optimizer optimizer = new Optimizer();
			algorithm = optimizer.optimize(algorithm, targets);
		}
		for (int i = 0; i < algorithm.size(); i++) {
			alg.append("        ");
			alg.append(algorithm.get(i)).toString();
			alg.append(";\n");
		}
        db.p("Algorithm: \n" + alg.toString());
		return alg.toString();

	}




	/**
	 Takes an algorithm and optimizes it to only calculate the variables that are targets.
	 @return an algorithm for calculating the target variables
	 @param algorithm an unoptimized algorithm
	 @param targets the variables which the algorithm has to calculate (other branches are removed)
	 */ static ArrayList optimizer(ArrayList algorithm, HashSet targets) {
		HashSet stuff = targets;
		Rel rel;
		Var relVar;
		ArrayList removeThese = new ArrayList();

		for (int i = algorithm.size() - 1; i >= 0; i--) {
			rel = (Rel) algorithm.get(i);
			boolean relIsNeeded = false;

			for (int j = 0; j < rel.outputs.size(); j++) {
				relVar = (Var) rel.outputs.get(j);
				if (stuff.contains(relVar)) {
					relIsNeeded = true;
				}
			}

			if (relIsNeeded) {
				stuff.addAll(rel.inputs);
			}
			else {
				removeThese.add(rel);
			}
		}
		algorithm.removeAll(removeThese);
		return algorithm;
	}

	/**
	 This method makes a compilable class from problem specification, calling createProblem,
	 planner, generates needed classes(_Class_ notation), putting it all together and writing into
	 a file.

	 void makeProgram(ObjectList objects, ee.ioc.cs.editor.vclass.ConnectionList connections, Hashtable classes) {
	 objects = ee.ioc.cs.editor.vclass.GroupUnfolder.unfold(objects);
	 String prog = makeProgramText(objects, connections, true);
	 generateSubclasses(classes);
	 writeFile(prog);
	 }
	 */

	/**
	 * @param progText -
	 * @param classes -
	 * @param mainClassName -
	 */ public void makeProgram(String progText, ClassList classes, String mainClassName) {
		generateSubclasses(classes);
		writeFile(progText, mainClassName);
	}

	/** Takes care of steps needed for planning and algorithm extracting, calling problem creator
	 * and planner and	returning compilable java source.
	 * Creating a problem means parsing the specification(s recursively), unfolding it, and making
	 * a flat representation of the specification (essentially a graph). Planning is run on this graph.
	 * @param fileString -
	 * @param computeAll -
	 * @param classList -
	 * @param mainClassName -
	 * @return String -
	 * @throws SpecParseException -
	 */
	public String makeProgramText(String fileString, boolean computeAll, ClassList classList, String mainClassName) throws SpecParseException {
		SpecParser sp = new SpecParser();
		Problem problem = new Problem();

		// call the packageParser to create a problem from the specification
		try {
			problem = sp.makeProblem(classList, "this", "this", problem);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// run the planner on the obtained problem
        System.out.println(problem);
//		String algorithm = planner(problem, computeAll);
//        String algorithm = null;// = lin_planner(problem, computeAll);
        Planner planner = new Planner(problem, computeAll, null);
        String algorithm = planner.getAlgorithm();
//        System.exit(0);//temporary
        String prog2 = "";

        String prog = "";

        ClassField field;
		// start building the main source file.
		AnnotatedClass ac = classList.getType("this");

		// check all the fields and make declarations accordingly
		for (int i = 0; i < ac.fields.size(); i++) {

			field = (ClassField) ac.fields.get(i);
			if (! (field.type.equals("alias") || field.type.equals("void"))) {
				if (field.isSpecField()) {
					prog += "    public _" + field.type + "_ " + field.name + " = new _" + field.type + "_();\n";
				}
				else if (isPrimitive(field.type)) {
					prog += "    public " + field.type + " " + field.name + ";\n";
				}
				else {
					prog += "    public " + field.type + " " + field.name + " = new " + field.type + "();\n";
				}
			}
		}

		prog += "    public void compute() {\n";
		prog += prog2;
		prog += algorithm;
		prog += "    }";
		Pattern pattern;
		Matcher matcher;

		pattern = Pattern.compile("class[ \t\n]+" + mainClassName + "|" + "public class[ \t\n]+" + mainClassName);
		matcher = pattern.matcher(fileString);

		if (matcher.find()) {
			fileString = matcher.replaceAll("public class _" + mainClassName + "_ implements IGeneratedClass");
		}

		pattern = Pattern.compile("/\\*@.*specification[ \t\n]+[a-zA-Z_0-9-.]+[ \t\n]*\\{[ \t\n]*(.+)[ \t\n]*\\}[ \t\n]*@\\*/ *", Pattern.DOTALL);
		matcher = pattern.matcher(fileString);

		if (matcher.find()) {
			fileString = matcher.replaceAll("\n    " + prog);
		}

		return fileString;

	}

	/**
	 Generates compilable java classes from the annotated classes that have been used in the specification.
	 @param classes List of classes obtained from the ee.ioc.cs.editor.synthesize.SpecParser
	 */
	void generateSubclasses(ClassList classes) {
		AnnotatedClass pClass;
		String lineString = "", fileString;
		Pattern pattern;
		Matcher matcher;

		// for each class generate new one used in synthesis

		for (int h = 0; h < classes.size(); h++) {
			pClass = (AnnotatedClass) classes.get(h);
			if (!pClass.name.equals("this")) {
				fileString = "";
				try {
					BufferedReader in = new BufferedReader(new FileReader(RuntimeProperties.packageDir + File.separator + pClass.name + ".java"));

					while ( (lineString = in.readLine()) != null) {
						fileString += lineString + "\n";
					}
					in.close();

				}
				catch (IOException io) {
					db.p(io);
				}
				// find the class declaration
				pattern = Pattern.compile("class +" + pClass.name);
				matcher = pattern.matcher(fileString);

				// replace it with _classname_
				if (matcher.find()) {
					fileString = matcher.replaceAll("public class _" + pClass.name + "_");
				}
				SpecParser specParser = new SpecParser();
				String declars = "";

				try {
					ArrayList specLines = specParser.getSpec(specParser.refineSpec(fileString));

					while (!specLines.isEmpty()) {
						LineType lt = specParser.getLine(specLines);

						if (! (specLines.get(0)).equals("")) {
							if (lt.type == declaration) {
								String[] split = lt.specLine.split(":", -1);
								String[] vs = split[1].trim().split(" *, *", -1);
								String type = split[0].trim();

								if (!type.equals("void")) {
									for (int i = 0; i < vs.length; i++) {
										if (isPrimitive(type)) {
											declars += "    public " + type + " " + vs[i] + ";\n";
										}
										else if (isArray(type)) {
											declars += "    public " + type + " " + vs[i] + " ;\n";
										}
										else if (classes.getType(type) != null) {
											declars += "    public _" + type + "_ " + vs[i] + ";\n";

										}
										else {
											declars += "    public " + type + " " + vs[i] + ";\n";

										}

									}
								}
							}
						}
					}
				}
				catch (Exception e) {}

				// find spec
				pattern = Pattern.compile("/\\*@.*specification[ \t\n]+[a-zA-Z_0-9-.]+[ \t\n]*\\{[ \t\n]*(.+)[ \t\n]*\\}[ \t\n]*@\\*/ *", Pattern.DOTALL);
				matcher = pattern.matcher(fileString);
				if (matcher.find()) {
					fileString = matcher.replaceAll("\n    " + declars);
				}

				try {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RuntimeProperties.genFileDir + System.getProperty("file.separator") + "_" + pClass.name + "_.java")));

					out.println(fileString);
					out.close();
				}
				catch (Exception e) {
					db.p(e);
				}
			}
		}
	}

	String getTypeWithoutArray(String type) {
		return type.substring(0, type.length() - 2);
	}

	void writeFile(String prog, String mainClassName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RuntimeProperties.genFileDir + System.getProperty("file.separator") + "_" + mainClassName + "_" + ".java")));

			out.println(prog);
			out.close();
		}
		catch (Exception e) {
			db.p(e);
		}
	}

	boolean isPrimitive(String type) {
		if (type.equals("int") || type.equals("double") || type.equals("float") || type.equals("long") || type.equals("short") || type.equals("boolean") || type.equals("char")) {
			return true;
		}
		else {
			return false;
		}
	}

	boolean isArray(String type) {
		int length = type.length();

		if (type.substring(length - 2, length).equals("[]")) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @param fileName -
	 */
	public void parseFromCommandLine(String fileName) {
		try {
			SpecParser sp = new SpecParser();
			HashSet hs = new HashSet();
			String mainClassName = new String();
			String file = sp.getStringFromFile( RuntimeProperties.packageDir + fileName);
			Pattern pattern = Pattern.compile("class[ \t\n]+([a-zA-Z_0-9-]+)[ \t\n]+");
			Matcher matcher = pattern.matcher(file);

			if (matcher.find()) {
				mainClassName = matcher.group(1);
			}
			String spec = sp.refineSpec(file);
			ClassList classList = sp.parseSpecification(spec, "this", null, hs);
			String prog = makeProgramText(file, true, classList, mainClassName);//changed to true

			makeProgram(prog, classList, mainClassName);
		}
		catch (UnknownVariableException uve) {
			db.p("Fatal error: variable " + uve.excDesc + " not declared");
		}
		catch (LineErrorException lee) {
			db.p("Fatal error on line " + lee.excDesc);
		}
		catch (MutualDeclarationException lee) {
			db.p("Mutual recursion in specifications, between classes " + lee.excDesc);
		}
		catch (EquationException ee) {
			db.p(ee.excDesc);
		}
		catch (SpecParseException spe) {
			db.p(spe);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
