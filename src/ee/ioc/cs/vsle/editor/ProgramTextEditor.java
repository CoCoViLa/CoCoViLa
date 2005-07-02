package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.ccl.*;

import java.util.*;
import java.util.regex.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import javax.swing.text.*;

/**
 */
public class ProgramTextEditor extends JFrame implements ActionListener {

	JButton parseSpec, runProg, computeAll, propagate, invoke;
	JTextArea textArea, programTextArea, runResultArea;
	JPanel progText, specText, runResult;
	JTextField invokeField;
	VPackage vPackage;
	JTabbedPane tabbedPane;
	ObjectList objects;
	Object runnableObject;
	ProgramRunner runner;
	ClassList classList;
	String mainClassName = new String();
	Editor editor;

	public ProgramTextEditor(ArrayList relations, ObjectList objs, VPackage vPackage, Editor ed) {
		super("Specification");
		editor = ed;
		this.vPackage = vPackage;
		objects = GroupUnfolder.unfold(objs);

		tabbedPane = new JTabbedPane();

		textArea = new JTextArea();
                textArea.addKeyListener( new CommentKeyListener() );
		textArea.setFont(RuntimeProperties.font);
		JScrollPane areaScrollPane = new JScrollPane(textArea);

		areaScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		specText = new JPanel();
		specText.setLayout(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		JToolBar progToolBar = new JToolBar();

		parseSpec = new JButton("Parse spec");
		parseSpec.addActionListener(this);
		progToolBar.add(parseSpec);
		computeAll = new JButton("Compute all");
		computeAll.addActionListener(this);
		progToolBar.add(computeAll);
                progToolBar.add(new FontResizePanel(textArea));
		specText.add(progToolBar, BorderLayout.NORTH);
		tabbedPane.addTab("Specification", specText);

		programTextArea = new JTextArea();
                programTextArea.addKeyListener( new CommentKeyListener() );
		programTextArea.setFont(RuntimeProperties.font);
		JToolBar toolBar = new JToolBar();

		runProg = new JButton("Compile & Run");
		runProg.addActionListener(this);
		toolBar.add(runProg);
                toolBar.add(new FontResizePanel(programTextArea));
		JScrollPane programAreaScrollPane = new JScrollPane(programTextArea);

		programAreaScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		progText = new JPanel();
		progText.setLayout(new BorderLayout());
		progText.add(programAreaScrollPane, BorderLayout.CENTER);
		progText.add(toolBar, BorderLayout.NORTH);

		tabbedPane.addTab("Program", progText);

		runResultArea = new JTextArea();
		runResultArea.setFont(RuntimeProperties.font);
		JToolBar resultToolBar = new JToolBar();

		propagate = new JButton("Propagate values");
		propagate.addActionListener(this);
		resultToolBar.add(propagate);
		invoke = new JButton("Invoke");
		invoke.addActionListener(this);
		resultToolBar.add(invoke);
		invokeField = new JTextField(4);
		resultToolBar.add(invokeField);
//                resultToolBar.add(new FontResizePanel(runResultArea));
		JScrollPane runResultAreaScrollPane = new JScrollPane(runResultArea);

		runResultAreaScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		runResult = new JPanel();
		runResult.setLayout(new BorderLayout());
		runResult.add(runResultAreaScrollPane, BorderLayout.CENTER);
		runResult.add(resultToolBar, BorderLayout.NORTH);

		tabbedPane.addTab("Run results", runResult);

		SpecGenerator sgen = new SpecGenerator();
		textArea.append(sgen.generateSpec(objects, relations, vPackage.name));


		getContentPane().add(tabbedPane);
		validate();
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == parseSpec) {
			Synthesizer synth = new Synthesizer();
			SpecParser sp = new SpecParser();
			HashSet hs = new HashSet();

			try {
				String fullSpec = textArea.getText();
				Pattern pattern = Pattern.compile(
					"class[ \t\n]+([a-zA-Z_0-9-]+)[ \t\n]+");
				Matcher matcher = pattern.matcher(fullSpec);

				if (matcher.find()) {
					mainClassName = matcher.group(1);
				}
				String spec = sp.refineSpec(fullSpec);

				classList = sp.parseSpecification(spec, "this", null, hs);
				programTextArea.setText("");
				programTextArea.append(
					synth.makeProgramText(fullSpec, false, classList,
						mainClassName));
				tabbedPane.setSelectedComponent(progText);
			} catch (UnknownVariableException uve) {
				db.p("Fatal error: variable " + uve.excDesc + " not declared");
				ErrorWindow ew = new ErrorWindow(
					"Fatal error: variable " + uve.excDesc + " not declared");

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (LineErrorException lee) {
				db.p("Fatal error on line " + lee.excDesc);
				ErrorWindow ew = new ErrorWindow(
					"Syntax error on line '" + lee.excDesc + "'");

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (MutualDeclarationException lee) {
				db.p(
					"Mutual recursion in specifications, between classes "
					+ lee.excDesc);
				ErrorWindow ew = new ErrorWindow(
					"Mutual recursion in specifications, classes " + lee.excDesc);

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (EquationException ee) {
				ErrorWindow ew = new ErrorWindow(ee.excDesc);

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (SpecParseException spe) {
				db.p(spe.excDesc);
				ErrorWindow ew = new ErrorWindow(spe.excDesc);

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			validate();
		}
		if (e.getSource() == computeAll) {
			Synthesizer synth = new Synthesizer();
			SpecParser sp = new SpecParser();
			HashSet hs = new HashSet();

			try {
				String fullSpec = textArea.getText();
				Pattern pattern = Pattern.compile(
					"class[ \t\n]+([a-zA-Z_0-9-]+)[ \t\n]+");
				Matcher matcher = pattern.matcher(fullSpec);

				if (matcher.find()) {
					mainClassName = matcher.group(1);
				}
				String spec = sp.refineSpec(fullSpec);

				classList = sp.parseSpecification(spec, "this", null, hs);
				programTextArea.setText("");
				programTextArea.append(
					synth.makeProgramText(fullSpec, true, classList, mainClassName));
				tabbedPane.setSelectedComponent(progText);
			} catch (UnknownVariableException uve) {

				db.p("Fatal error: variable " + uve.excDesc + " not declared");
				ErrorWindow ew = new ErrorWindow(
					"Fatal error: variable " + uve.excDesc + " not declared");

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (LineErrorException lee) {
				db.p("Fatal error on line " + lee.excDesc);
				ErrorWindow ew = new ErrorWindow(
					"Syntax error on line '" + lee.excDesc + "'");

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (EquationException ee) {
				ErrorWindow ew = new ErrorWindow(ee.excDesc);

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (MutualDeclarationException lee) {
				db.p(
					"Mutual recursion in specifications, between classes "
					+ lee.excDesc);
				ErrorWindow ew = new ErrorWindow(
					"Mutual recursion in specifications, classes " + lee.excDesc);

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (SpecParseException spe) {
				db.p(spe.excDesc);
				ErrorWindow ew = new ErrorWindow(spe.excDesc);

				ew.setSize(600, 300);
				ew.setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			validate();
		}

		if (e.getSource() == runProg) {
			Synthesizer synth = new Synthesizer();

			synth.makeProgram(programTextArea.getText(), classList,
				mainClassName);
			runner = new ProgramRunner();
			ArrayList watchFields = watchableFields(objects);

			try {
				runnableObject = runner.compileAndRun("_" + mainClassName + "_",
					watchFields, runResultArea);
				tabbedPane.setSelectedComponent(runResult);
			} catch (CompileException ce) {
				ErrorWindow ew = new ErrorWindow(
					"Compilation failed:\n " + ce.excDesc);

				ew.setSize(600, 300);
				ew.setVisible(true);
			}

		}

		if (e.getSource() == propagate) {
			db.p("propageerin");
			if (runnableObject != null) {
				runner.runPropagate(runnableObject, objects);
			}
			editor.repaint();
		}
		if (e.getSource() == invoke) {
			ArrayList watchFields = watchableFields(objects);

			if (runnableObject != null) {
				if (!invokeField.getText().equals("")) {
					int k = Integer.parseInt(invokeField.getText());

					for (int i = 0; i < k; i++) {
						runner.run(watchFields, runResultArea);
					}
				} else {
					runner.run(watchFields, runResultArea);
				}
				runner.runPropagate(runnableObject, objects);
			}
			editor.repaint();
		}
	}

	ArrayList watchableFields(ObjectList objects) {
		ClassField field;
		GObj obj;

		objects = GroupUnfolder.unfold(objects);
		ArrayList watchFields = new ArrayList();

		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			for (int j = 0; j < obj.fields.size(); j++) {
				field = (ClassField) obj.fields.get(j);
				if (field.isWatched()) {
					watchFields.add(obj.name + "." + field.name);
				}
			}
		}
		return watchFields;
	}

        private class CommentKeyListener implements KeyListener {

            public void keyTyped( KeyEvent e ) {
            }


            public void keyPressed( KeyEvent e ) {
            }


            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyChar() == '/'
                     && ( ( e.getModifiers() & KeyEvent.CTRL_MASK ) > 0 )
                     && ( e.getSource() instanceof JTextArea ) ) {

                    JTextArea area = ( JTextArea ) e.getSource();

                    try {
                        int line = area.getLineOfOffset( area.getCaretPosition() );
                        int start = area.getLineStartOffset( line );
                        int end = area.getLineEndOffset( line );
                        int length = end - start;
                        String text = area.getText( start, length );

                        if ( text.trim().startsWith( "//" ) ) {
                            int ind = text.indexOf( "//" );
                            area.replaceRange( "", start + ind, start + ind + 2 );
                        } else {
                            area.insert( "//", start );
                        }

                        area.setCaretPosition( area.getLineEndOffset(
                                area.getLineOfOffset( area.getCaretPosition() ) ) );

                    } catch ( BadLocationException ex ) {
                    }
                }
            }

        }

}
