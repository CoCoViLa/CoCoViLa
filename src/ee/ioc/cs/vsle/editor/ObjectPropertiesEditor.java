package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

/**
 */
public class ObjectPropertiesEditor extends JFrame implements ActionListener,
		KeyListener {
	
	private ArrayList<JTextField> textFields = new ArrayList<JTextField>();

	private ArrayList<JCheckBox> watchFields = new ArrayList<JCheckBox>();

	private ArrayList<JCheckBox> inputs = new ArrayList<JCheckBox>();
	
	private ArrayList<JCheckBox> goals = new ArrayList<JCheckBox>();
	
	private ArrayList<JComboBox> comboBoxes = new ArrayList<JComboBox>();

	private ArrayList<ClassField> primitiveNameList = new ArrayList<ClassField>();

	private ArrayList<ClassField> arrayNameList = new ArrayList<ClassField>();

	private GObj controlledObject;

	private JTextField nameTextField;

	private JButton clear, ok, close;

	private Canvas canvas;
	
	private JCheckBox isStatic;

	//key - class name, value - last width
	private static Map<String, Integer> s_widths = new HashMap<String, Integer>();
	
	public static void show( GObj object, Canvas canvas ) {
		
		final ObjectPropertiesEditor frame = new ObjectPropertiesEditor( object, canvas );
		frame.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		
		Dimension preferred = frame.getLayout().preferredLayoutSize( frame );
		
		if( s_widths.get( object.getClassName() ) == null ) {
			
			frame.pack();
			
			s_widths.put( object.getClassName(), preferred.width );
		}
		else
		{
			int width = s_widths.get( object.getClassName() );
			
			if( preferred.width > width ) {
				width = preferred.width;
			}
			frame.setSize( width, preferred.height );
		}
		
		//frame positioning
		Point loc = new Point( object.getX(), object.getY() );
		SwingUtilities.convertPointToScreen( loc, canvas );
		
		//Dimension display = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle display = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

		if( ( loc.x + frame.getWidth() ) > display.width ) {
			loc.x = loc.x - ( frame.getWidth() - ( display.width - loc.x ) );
		}
		
		if( ( loc.y + frame.getHeight() ) > display.height ) {
			loc.y = loc.y - ( frame.getHeight() - ( display.height - loc.y ) );
		}
		
		frame.setLocation( loc );

		frame.setVisible(true);
	}
	
	private ObjectPropertiesEditor(GObj object, Canvas canvas) {
		super( object.getName() + " - " + canvas.getTitle() );
		this.canvas = canvas;
		controlledObject = object;
		
		addComponentListener(new ComponentAdapter() {

			public void componentResized(ComponentEvent e) {
				
				Dimension d = getLayout().preferredLayoutSize( ObjectPropertiesEditor.this );
				
				Dimension resized = getSize();
				
				if( d.width > resized.width && d.height > resized.height ) {
					resized.setSize( d.width, d.height );
				} else if ( d.width > resized.width ) {
					resized.setSize( d.width, resized.height );
				} else if ( d.height > resized.height ) {
					resized.setSize( resized.width, d.height );
				}
				
				s_widths.put( controlledObject.getClassName(), resized.width );
				
				ObjectPropertiesEditor.this.setSize( resized );
			}
		});
		
		JPanel westPanel = new JPanel();
		westPanel.setLayout( new BoxLayout( westPanel, BoxLayout.X_AXIS ) );
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.X_AXIS ) );
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout( new BoxLayout( eastPanel, BoxLayout.X_AXIS ) );
		
		JPanel buttonPane = new JPanel();
		JPanel fullPane = new JPanel();

		JPanel labelPane = new JPanel();
		JPanel watchPane = new JPanel();
		JPanel inputPane = new JPanel();
		JPanel goalsPane = new JPanel();
		JPanel textFieldPane = new JPanel();
		JPanel typePane = new JPanel();

		labelPane.setLayout(new GridLayout(0, 1));
		watchPane.setLayout(new GridLayout(0, 1));
		inputPane.setLayout(new GridLayout(0, 1));
		goalsPane.setLayout(new GridLayout(0, 1));
		typePane.setLayout(new GridLayout(0, 1));
		textFieldPane.setLayout(new GridLayout(0, 1));

		JLabel label = new JLabel( "Object name", SwingConstants.CENTER );

		nameTextField = new JTextField(object.getName(), 6);
		labelPane.add(label);
		textFieldPane.add(nameTextField);
		label = new JLabel("(String)");
		typePane.add(label);

		label = new JLabel( "  Input  ", SwingConstants.CENTER );
		inputPane.add(label);
		
		label = new JLabel( "  Goal  ", SwingConstants.CENTER );
		goalsPane.add(label);
		
		label = new JLabel( "  Watch", SwingConstants.CENTER );
		watchPane.add(label);

		JTextField textField;
		JComboBox comboBox;
		JCheckBox watch;
		
		for (ClassField field : object.fields) {
			if (field.isArray()) {
				comboBox = new JComboBox();
				comboBox.setEditable(true);
				comboBox.addActionListener(this);

				if (field.getValue() != "" && field.getValue() != null) {
					String[] split = field.getValue().split(
							ClassField.ARRAY_TOKEN);
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

				watch = new JCheckBox();
				watch.setHorizontalAlignment( SwingConstants.CENTER );
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

				watch = new JCheckBox((String)null, b);
				watch.setHorizontalAlignment( SwingConstants.CENTER );
				watchPane.add(watch);
				watchFields.add(watch);
			}
			
			boolean enable = field.isPrimitiveOrString();//TODO - tmp until combos are refactored
			
			final JCheckBox input = new JCheckBox( (String)null, field.isInput() );
			input.setEnabled( enable );
			input.setHorizontalAlignment( SwingConstants.CENTER );
			inputPane.add(input);
			inputs.add(input);
			
			final JCheckBox goal = new JCheckBox( (String)null, field.isGoal() );
			goal.setEnabled( enable );
			goal.setHorizontalAlignment( SwingConstants.CENTER );
			goalsPane.add(goal);
			goals.add(goal);
			
			ActionListener lst = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if( e.getSource() == input && input.isSelected() ) {
						
						goal.setSelected( false );
						
					} else if( e.getSource() == goal && goal.isSelected() ) {
						
						input.setSelected( false );
						
					}
				}
			};
			
			input.addActionListener(lst);
			goal.addActionListener(lst);
		}
		
		isStatic = new JCheckBox( "Static", object.isStatic() );
		textFieldPane.add(isStatic);
		labelPane.add( new JLabel() );
		watchPane.add( new JLabel() );
		inputPane.add( new JLabel() );
		goalsPane.add( new JLabel() );
		typePane.add( new JLabel() );
		
		JPanel contentPane = new JPanel();
		JScrollPane areaScrollPane = new JScrollPane(contentPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		fullPane.setLayout(new BorderLayout());
		contentPane.setLayout( new BorderLayout() );
		westPanel.add(labelPane);
		centerPanel.add(textFieldPane);
		eastPanel.add(typePane);
		eastPanel.add(inputPane);
		eastPanel.add(goalsPane);
		eastPanel.add(watchPane);
		contentPane.add( westPanel, BorderLayout.WEST );
		contentPane.add( centerPanel, BorderLayout.CENTER );
		contentPane.add( eastPanel, BorderLayout.EAST );
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
		close = new JButton("Close");
		close.addActionListener(this);
		//buttonPane.add(close);
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
			// String newSelection = (String)thisComboBox.getSelectedItem();
			if (thisComboBox != null) {
				thisComboBox.addItem(jtf.getText());
			}
		} else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
			JTextField jtf = (JTextField) (e.getSource());
			JComboBox thisComboBox = getComboBox(jtf);
			if (thisComboBox != null) {
				thisComboBox.removeItem(thisComboBox.getSelectedItem());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		JTextField textField;
		ClassField field;
		boolean inputError = false;
		if (e.getSource() == ok) {
			for (int i = 0; i < textFields.size(); i++) {
				textField = textFields.get(i);
				field = primitiveNameList.get(i);
				if (!textField.getText().equals("")) {
				try {
					getValue(textField.getText(), field
							.getType());
					
				} catch (NumberFormatException nfe) {
					inputError = true;
					JOptionPane.showMessageDialog(this,
						    "The field "+field.getName()+" of type " +field.getType() + 
						    		" cannot have the value \""+textField.getText()+"\"",
						    "Input error",
						    JOptionPane.ERROR_MESSAGE
						    );
					
				}
				}
			}
			if (!inputError) {
				for (int i = 0; i < textFields.size(); i++) {
					textField = textFields.get(i);
					field = primitiveNameList.get(i);
					field.setWatched( watchFields.get(i).isSelected() );
					field.setInput( inputs.get(i).isSelected() );
					field.setGoal( goals.get(i).isSelected() );
					if (!textField.getText().trim().equals("")) {

						field.setValue(textField.getText());
					} else {
						field.setValue(null);
					}
				}
				for (int i = 0; i < comboBoxes.size(); i++) {
					JComboBox comboBox = comboBoxes.get(i);
					field = arrayNameList.get(i);
					//field.setInput( inputs.get(i).isSelected() );
					//field.setGoal( goals.get(i).isSelected() );
					String s = "";
					for (int j = 0; j < comboBox.getItemCount(); j++) {
						s += (String) comboBox.getItemAt(j)
								+ ClassField.ARRAY_TOKEN;
					}
					if (!s.equals("")) {
						field.setValue(s);

					} else {
						field.setValue(null);
					}
				}
				controlledObject.setName(nameTextField.getText());
				controlledObject.setStatic( isStatic.isSelected() );
				controlledObject = null;
				this.dispose();
				canvas.repaint();
			}
		}
		else if (e.getSource() == clear) {
			// Clears object properties except the object name.
			for (int i = 0; i < textFields.size(); i++) {
				textField = textFields.get(i);
				field = controlledObject.fields.get(i);
				watchFields.get(i).setSelected(false);
				inputs.get(i).setSelected(false);
				goals.get(i).setSelected(false);
				field.setWatched(false);
				field.setValue(null);
				textField.setText("");
			}
			for (int i = 0; i < comboBoxes.size(); i++) {
				JComboBox comboBox = comboBoxes.get(i);
				comboBox.removeAllItems();
			}
		}
		else if (e.getSource() == close) {
			dispose();
		}
	}

	private String getValue(String text, String type)
			throws NumberFormatException {
		if (type.equals(TYPE_INT)) {

			int i = Integer.parseInt(text);
			return Integer.toString(i);

		}
		if (type.equals(TYPE_LONG)) {

			long i = Long.parseLong(text);
			return Long.toString(i);

		}
		if (type.equals(TYPE_DOUBLE)) {
			double i = Double.parseDouble(text);
			return Double.toString(i);

		}
		if (type.equals(TYPE_FLOAT)) {
			float i = Float.parseFloat(text);
			return Float.toString(i);

		}
		return text;
		//TODO - add other types
	}

	private JComboBox getComboBox(JTextField jtf) {
		for (JComboBox jcb : comboBoxes) {
			if (jtf == jcb.getEditor().getEditorComponent()) {
				return jcb;
			}
		}
		return null;
	}
	
	

}
