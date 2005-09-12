package ee.ioc.cs.vsle.editor;

import javax.swing.JFrame;

/**
 * Class currently not in use. Fix reference to ee.ioc.cs.editor.synthesize.Synthesizer.makeProgram method (method
 * commented out in the ee.ioc.cs.editor.synthesize.Synthesizer class)
 */
public class ResultsWindow extends JFrame
	/*implements ActionListener*/ {
	/*JTextArea textArea;
	Object genObject;
	ArrayList watchPorts;
	JButton run;

	ResultsWindow(ObjectList objects) {
		super();
		// ee.ioc.cs.editor.synthesize.Synthesizer synth = new ee.ioc.cs.editor.synthesize.Synthesizer();
		// synth.makeProgram(objects, connections, classes);
		textArea = new JTextArea();
		textArea.setFont(RuntimeProperties.font);
		JScrollPane areaScrollPane = new JScrollPane(textArea);

		areaScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(250, 250));
		JToolBar toolBar = new JToolBar();
		ImageIcon icon;

		icon = new ImageIcon("run.gif");
		run = new JButton(icon);
		run.setActionCommand("run");
		run.addActionListener(this);
		toolBar.add(run);
		genObject = makeGeneratedObject();
		watchPorts = watchablePorts(objects);
		run(genObject, watchPorts);

		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(areaScrollPane);
		validate();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == run) {
			run(genObject, watchPorts);
		}
	}

	Object makeGeneratedObject() {
		CCL classLoader = new CCL();

		try {
			Class clas = classLoader.loadClass("GeneratedClass");
			Object o = clas.newInstance();

			return o;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	ArrayList watchablePorts(ObjectList objects) {
		Port port;
		GObj obj;

		objects = GroupUnfolder.unfold(objects);
		ArrayList watchPorts = new ArrayList();

		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			for (int j = 0; j < obj.ports.size(); j++) {
				port = (Port) obj.ports.get(j);
				if (port.isWatched()) {
					watchPorts.add(obj.name + "." + port.name);
				}
			}
		}
		return watchPorts;
	}

	void run(Object genClass, ArrayList watchPorts) {
		try {
			Class clas = genClass.getClass();
			Method method = clas.getMethod("compute", null);

			method.invoke(genClass, null);
			Field f;
			StringTokenizer st;
			Object lastObj;

			// ee.ioc.cs.editor.util.db.p(genClass.getClass().getFields()[0]);
			for (int i = 0; i < watchPorts.size(); i++) {
				lastObj = genClass;
				clas = genClass.getClass();
				st = new StringTokenizer((String) watchPorts.get(i), ".");
				while (st.hasMoreElements()) {

					f = clas.getDeclaredField(st.nextToken());
					if (st.hasMoreElements()) {
						clas = f.getType();
						lastObj = f.get(lastObj);
					} else {
						textArea.append(
							(String) watchPorts.get(i) + ": " + f.getInt(lastObj)
							+ "\n");
						db.p(
							(String) watchPorts.get(i) + ": " + f.getInt(lastObj));
					}

				}
			}
			textArea.append("----------------------\n");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}*/
}
