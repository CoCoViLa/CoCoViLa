package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.util.db;

import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;


/**
 */
public class ObjectPropertiesEditor extends JFrame
	implements ActionListener, KeyListener {
	ArrayList textFields = new ArrayList(), watchFields = new ArrayList();
	ArrayList comboBoxes = new ArrayList();
	ArrayList primitiveNameList = new ArrayList();
	ArrayList arrayNameList = new ArrayList();

	GObj controlledObject;
	JTextField nameTextField;
	JButton clear, ok;
	Editor editor;

	public ObjectPropertiesEditor(GObj object, Editor editor) {
		super();
		this.editor = editor;
		controlledObject = object;
		JPanel buttonPane = new JPanel();
		JPanel fullPane = new JPanel();
		JPanel labelPane = new JPanel();
		JPanel watchPane = new JPanel();
		JPanel textFieldPane = new JPanel();
		JPanel typePane = new JPanel();

		labelPane.setLayout(new GridLayout(0, 1));
		watchPane.setLayout(new GridLayout(0, 1));
		typePane.setLayout(new GridLayout(0, 1));
		textFieldPane.setLayout(new GridLayout(0, 1));

		JLabel label = new JLabel("Object name");

		nameTextField = new JTextField(object.getName(), 10);
		labelPane.add(label);
		textFieldPane.add(nameTextField);
		label = new JLabel("(String)");
		typePane.add(label);

		label = new JLabel("Watch");
		watchPane.add(label);

		ClassField field;
		JTextField textField;
		JComboBox comboBox;
		JCheckBox watch;

		for (int i = 0; i < object.fields.size(); i++) {
			field = (ClassField) object.fields.get(i);

			if (field.isArray()) {
				comboBox = new JComboBox();
				comboBox.setEditable(true);
				comboBox.addActionListener(this);

				if (field.value != "" && field.value != null) {
					String[] split = field.value.split("�");
					for (int j = 0; j < split.length; j++) {
						comboBox.addItem(split[j]);
					}
				}
				JTextField jtf = (JTextField) (comboBox.getEditor().getEditorComponent());
				jtf.addKeyListener(this);


				comboBoxes.add(comboBox);
				label = new JLabel(field.name, SwingConstants.CENTER);
				arrayNameList.add(field);
				labelPane.add(label);
				label = new JLabel("(" + field.type + ")");
				typePane.add(label);

				watch = new JCheckBox("");
				watch.setEnabled(false);
				watchPane.add(watch);
				watchFields.add(watch);

				textFieldPane.add(comboBox);
			} else if (field.isPrimitiveOrString()) {
				textField = new JTextField();
				textField.addKeyListener(this);
				textField.setName(field.name);
				primitiveNameList.add(field);
				textField.setText(field.value);
				textFields.add(textField);
				label = new JLabel(field.name, SwingConstants.CENTER);
				labelPane.add(label);
				textFieldPane.add(textField);
				label = new JLabel("(" + field.type + ")");
				typePane.add(label);
				boolean b = field.watched;

				watch = new JCheckBox("", b);
				watchPane.add(watch);
				watchFields.add(watch);
			}


		}
		JPanel contentPane = new JPanel();

		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		fullPane.setLayout(new BorderLayout());
		contentPane.setLayout(new GridLayout(0, 4));
		contentPane.add(labelPane);
		contentPane.add(textFieldPane);
		contentPane.add(typePane);
		contentPane.add(watchPane);
		ok = new JButton("OK");
		ok.addActionListener(this);
		buttonPane.add(ok);
		clear = new JButton("Clear all");
		clear.addActionListener(this);
		if (object.fields.size() == 0) {
			clear.setEnabled(false);
		} else {
			clear.setEnabled(true);
		}
		buttonPane.add(clear);

		fullPane.add(contentPane, BorderLayout.NORTH);
		fullPane.add(buttonPane, BorderLayout.SOUTH);
		setContentPane(fullPane);
		validate();
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			JTextField jtf = (JTextField) (e.getSource());
			JComboBox thisComboBox = getComboBox(jtf);
			//String newSelection = (String)thisComboBox.getSelectedItem();
			thisComboBox.addItem(jtf.getText());

		} else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
			JTextField jtf = (JTextField) (e.getSource());
			JComboBox thisComboBox = getComboBox(jtf);
			thisComboBox.removeItem(thisComboBox.getSelectedItem());

		}
	}

	public void actionPerformed(ActionEvent e) {
		JTextField textField;
		ClassField field;
		boolean b;
		db.p(e);
		if (e.getSource() == ok) {
			for (int i = 0; i < textFields.size(); i++) {
				textField = (JTextField) textFields.get(i);
				field = (ClassField) primitiveNameList.get(i);
				b = ((JCheckBox) watchFields.get(i)).isSelected();
				field.watched = b;
				if (!textField.getText().equals("")) {
					field.value = textField.getText();
					//field.updateGraphics();
				} else {
					field.value = null;
				}
			}
			for (int i = 0; i < comboBoxes.size(); i++) {
				JComboBox comboBox = (JComboBox) comboBoxes.get(i);
				field = (ClassField) arrayNameList.get(i);
				String s = "";
				for (int j = 0; j < comboBox.getItemCount(); j++) {
					s += (String) comboBox.getItemAt(j) + "�";
				}
				if (!s.equals("")) {
					field.value = s;

				} else {
					field.value = null;
				}
			}
			controlledObject.setName(nameTextField.getText());
			controlledObject = null;
			this.dispose();
			editor.repaint();
		}
		if (e.getSource() == clear) {
			// Clears object properties except the object name.
			for (int i = 0; i < textFields.size(); i++) {
				textField = (JTextField) textFields.get(i);
				field = (ClassField) controlledObject.fields.get(i);
				((JCheckBox) watchFields.get(i)).setSelected(false);
				field.watched = false;
				field.value = null;
				textField.setText("");
			}
		}
	}

	JComboBox getComboBox(JTextField jtf) {
		for (int i = 0; i < comboBoxes.size(); i++) {
			JComboBox jcb = (JComboBox) comboBoxes.get(i);
			if (jtf == jcb.getEditor().getEditorComponent()) {
				return jcb;
			}
		}
		return null;
	}

}
