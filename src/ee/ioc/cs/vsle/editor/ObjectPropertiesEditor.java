package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.util.db;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.*;


/**
 */
public class ObjectPropertiesEditor extends JFrame
	implements ActionListener, KeyListener {
	ArrayList<JTextField> textFields = new ArrayList<JTextField>();
    ArrayList<JCheckBox> watchFields = new ArrayList<JCheckBox>();
	ArrayList<JComboBox> comboBoxes = new ArrayList<JComboBox>();
	ArrayList<ClassField> primitiveNameList = new ArrayList<ClassField>();
	ArrayList<ClassField> arrayNameList = new ArrayList<ClassField>();

	GObj controlledObject;
	JTextField nameTextField;
	JButton clear, ok;
	Canvas canvas;

	public ObjectPropertiesEditor(GObj object, Canvas canvas) {
		super( object.getName() );
		this.canvas = canvas;
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

		nameTextField = new JTextField(object.getName(), 6);
		labelPane.add(label);
		textFieldPane.add(nameTextField);
		label = new JLabel("(String)");
		typePane.add(label);

		label = new JLabel("Watch");
		watchPane.add(label);

		JTextField textField;
		JComboBox comboBox;
		JCheckBox watch;

		for (ClassField field: object.fields) {
			if (field.isArray()) {
				comboBox = new JComboBox();
				comboBox.setEditable(true);
				comboBox.addActionListener(this);

				if (field.getValue() != "" && field.getValue() != null) {
					String[] split = field.getValue().split( ClassField.ARRAY_TOKEN );
					for (int j = 0; j < split.length; j++) {
						comboBox.addItem(split[j]);
					}
				}
				Component jtf = comboBox.getEditor().getEditorComponent();
				jtf.addKeyListener(this);

				comboBoxes.add(comboBox);
				label = new JLabel(field.getName(), SwingConstants.CENTER);
				label.setToolTipText(field.getDescription());
				arrayNameList.add(field);
				labelPane.add(label);
				label = new JLabel("(" + field.getType() + ")");
				typePane.add(label);

				watch = new JCheckBox("");
				watch.setEnabled(false);
				watchPane.add(watch);
				watchFields.add(watch);

				textFieldPane.add(comboBox);
			} else if (field.isPrimitiveOrString()) {
				textField = new JTextField();
				textField.addKeyListener(this);
				textField.setName(field.getName());
				primitiveNameList.add(field);
				textField.setText(field.getValue());
				textFields.add(textField);
				label = new JLabel(field.getName(), SwingConstants.CENTER);
				label.setToolTipText(field.getDescription());
				labelPane.add(label);
				textFieldPane.add(textField);
				label = new JLabel("(" + field.getType() + ")");
				typePane.add(label);
				boolean b = field.isWatched();

				watch = new JCheckBox("", b);
				watchPane.add(watch);
				watchFields.add(watch);
			}
		}
		JPanel contentPane = new JPanel();
		JScrollPane areaScrollPane = new JScrollPane(contentPane,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
        //contentPane.setPreferredSize(new Dimension(300,200));
		fullPane.add(areaScrollPane, BorderLayout.CENTER);
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
                        if( thisComboBox != null ) {
                            thisComboBox.addItem( jtf.getText() );
                        }
		} else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
			JTextField jtf = (JTextField) (e.getSource());
			JComboBox thisComboBox = getComboBox(jtf);
                        if( thisComboBox != null ) {
                            thisComboBox.removeItem( thisComboBox.getSelectedItem() );
                        }
		}
	}

	public void actionPerformed(ActionEvent e) {
		JTextField textField;
		ClassField field;
		boolean b;
		db.p(e);
		if (e.getSource() == ok) {
			for (int i = 0; i < textFields.size(); i++) {
				textField = textFields.get(i);
				field = primitiveNameList.get(i);
				b = watchFields.get(i).isSelected();
				field.setWatched( b );
				if (!textField.getText().equals("")) {
					field.setValue( textField.getText() );
					//field.updateGraphics();
				} else {
					field.setValue( null );
				}
			}
			for (int i = 0; i < comboBoxes.size(); i++) {
				JComboBox comboBox = comboBoxes.get(i);
				field = arrayNameList.get(i);
				String s = "";
				for (int j = 0; j < comboBox.getItemCount(); j++) {
					s += (String) comboBox.getItemAt(j) + ClassField.ARRAY_TOKEN;
				}
				if (!s.equals("")) {
					field.setValue( s );

				} else {
					field.setValue( null );
				}
			}
			controlledObject.setName(nameTextField.getText());
			controlledObject = null;
			this.dispose();
			canvas.repaint();
		}
		if (e.getSource() == clear) {
			// Clears object properties except the object name.
			for (int i = 0; i < textFields.size(); i++) {
				textField = textFields.get(i);
				field = controlledObject.fields.get(i);
				watchFields.get(i).setSelected(false);
				field.setWatched( false );
				field.setValue( null );
				textField.setText("");
			}
		}
	}

	JComboBox getComboBox(JTextField jtf) {
		for (JComboBox jcb: comboBoxes) {
			if (jtf == jcb.getEditor().getEditorComponent()) {
				return jcb;
			}
		}
		return null;
	}
}
