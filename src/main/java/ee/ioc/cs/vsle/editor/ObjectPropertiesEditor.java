package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

/**
 * Editor window for editing properties (field values, object name, etc)
 * of visual class instances on the scheme.
 */
public class ObjectPropertiesEditor extends JFrame implements ActionListener,
		KeyListener {
	
    private static final long           serialVersionUID  = 1L;
    private ArrayList<JTextField>       textFields        = new ArrayList<JTextField>();
    private ArrayList<JCheckBox>        goalInputs            = new ArrayList<JCheckBox>();
    private ArrayList<JCheckBox>        goalOutputs             = new ArrayList<JCheckBox>();
    private ArrayList<JComboBox>        comboBoxes        = new ArrayList<JComboBox>();
    private ArrayList<ClassField>       primitiveNameList = new ArrayList<ClassField>();
    private ArrayList<ClassField>       arrayNameList     = new ArrayList<ClassField>();
    private GObj                        controlledObject;
    private JTextField                  nameTextField;
    private JButton                     clear, ok, apply, close;
    private Canvas                      canvas;
    private JCheckBox                   isStatic;
    private ComponentListener           lst;
    
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
		
		lst =  new ComponentAdapter() {

            @Override
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
        };
		
		addComponentListener( lst );
		
		JPanel labelPane = new JPanel();
		JPanel inputPane = new JPanel();
		JPanel outputPane = new JPanel();
		JPanel textFieldPane = new JPanel();
		JPanel typePane = new JPanel();

		labelPane.setLayout(new GridLayout(0, 1));
		inputPane.setLayout(new GridLayout(0, 1));
		outputPane.setLayout(new GridLayout(0, 1));
		typePane.setLayout(new GridLayout(0, 1));
		textFieldPane.setLayout(new GridLayout(0, 1));

		JLabel label = new JLabel( "Object name", SwingConstants.CENTER );

		nameTextField = new JTextField(controlledObject.getName(), 6);
		labelPane.add(label);
		textFieldPane.add(nameTextField);
		label = new JLabel("(String)");
		typePane.add(label);

		label = new JLabel( "  Input  ", SwingConstants.CENTER );
		inputPane.add(label);
		
		label = new JLabel( "  Output  ", SwingConstants.CENTER );
		outputPane.add(label);

		JTextField textField;
		JComboBox comboBox;
		
		for (ClassField field : controlledObject.getFields()) {

            if ( field.isHidden() )
                continue;
		    
            String name = field.isAlias() ? "alias " : "";
            name += field.getName();
            label = new JLabel(name, SwingConstants.CENTER);
            label.setToolTipText(field.getDescription());
            labelPane.add(label);
            label = new JLabel("(" + field.getType() + ")");
            typePane.add(label);

            if (field.isArray() && !field.isAlias()) {
				comboBox = new JComboBox();
				comboBox.setEditable(true);
				comboBox.addActionListener(this);

				if (field.getValue() != "" && field.getValue() != null) {
					String[] split = field.getValue().split(
							TypeUtil.ARRAY_TOKEN);
					for (int j = 0; j < split.length; j++) {
						comboBox.addItem(split[j]);
					}
				}
				Component jtf = comboBox.getEditor().getEditorComponent();
				jtf.addKeyListener(this);
				comboBoxes.add(comboBox);
				arrayNameList.add(field);
				textFieldPane.add(comboBox);
			} else if (field.isPrimitiveOrString()) {
				textField = new JTextField();
				textField.addKeyListener(this);
				textField.setName(field.getName());
				textField.setText(field.getValue());
                textFieldPane.add(textField);
				primitiveNameList.add(field);
				textFields.add(textField);
			} else {
			    textField = new JTextField();
			    textField.setEditable( false );
                textField.setName(field.getName());
                textFieldPane.add(textField);
			}
			
			final JCheckBox input = new JCheckBox( (String)null, field.isInput() );
			//TODO only input for primitives and strings are supported by the gui
			input.setEnabled( field.isPrimitiveOrString() );
			input.setHorizontalAlignment( SwingConstants.CENTER );
			inputPane.add(input);
			goalInputs.add(input);
			
			final JCheckBox output = new JCheckBox( (String)null, field.isGoal() );
			output.setEnabled( true );
			output.setHorizontalAlignment( SwingConstants.CENTER );
			outputPane.add(output);
			goalOutputs.add(output);
			
			ActionListener chkBoxlst = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if( e.getSource() == input && input.isSelected() ) {
						
						output.setSelected( false );
						
					} else if( e.getSource() == output && output.isSelected() ) {
						
						input.setSelected( false );
						
					}
				}
			};
			
			input.addActionListener(chkBoxlst);
			output.addActionListener(chkBoxlst);
		}
		
		isStatic = new JCheckBox( "Static", controlledObject.isStatic() );
		textFieldPane.add(isStatic);
		labelPane.add( new JLabel() );
		inputPane.add( new JLabel() );
		outputPane.add( new JLabel() );
		typePane.add( new JLabel() );
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout( new BorderLayout() );
		JPanel westPanel = new JPanel();
		westPanel.setLayout( new BoxLayout( westPanel, BoxLayout.X_AXIS ) );
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.X_AXIS ) );
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout( new BoxLayout( eastPanel, BoxLayout.X_AXIS ) );
		JPanel buttonPane = new JPanel();
		westPanel.add(labelPane);
		centerPanel.add(textFieldPane);
		JPanel goalPane = new JPanel();
		goalPane.setLayout( new BoxLayout( goalPane, BoxLayout.X_AXIS ) );
		goalPane.setBorder( BorderFactory.createTitledBorder( "Goal" ) );
		goalPane.add(inputPane);
		goalPane.add(outputPane);
		eastPanel.add(goalPane);
		JPanel propsPane = new JPanel(new BorderLayout());
		propsPane.setBorder( BorderFactory.createTitledBorder( "Properties" ) );
		propsPane.add( westPanel, BorderLayout.WEST );
		propsPane.add( centerPanel, BorderLayout.CENTER );
		propsPane.add( typePane, BorderLayout.EAST );
		contentPane.add( propsPane, BorderLayout.CENTER );
		contentPane.add( eastPanel, BorderLayout.EAST );
		ok = new JButton("OK");
		ok.addActionListener(this);
		buttonPane.add(ok);
		close = new JButton("Close");
		close.addActionListener(this);
		buttonPane.add(close);
		apply = new JButton("Apply");
		apply.addActionListener(this);
		buttonPane.add(apply);
		clear = new JButton("Clear all");
		clear.addActionListener(this);
		if (controlledObject.getFields().size() == 0) {
			clear.setEnabled(false);
		} else {
			clear.setEnabled(true);
		}
		buttonPane.add(clear);
		JScrollPane areaScrollPane = new JScrollPane(contentPane,
		        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel fullPane = new JPanel();
        fullPane.setLayout(new BorderLayout());
		fullPane.add(areaScrollPane, BorderLayout.CENTER);
		fullPane.add(buttonPane, BorderLayout.SOUTH);
		setContentPane(fullPane);
		validate();
	}

	public void keyTyped(KeyEvent e) {
	    // ignored
	}

	public void keyReleased(KeyEvent e) {
	    // ignored
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

	private boolean validateAndApply() {
		JTextField textField;
		ClassField field;
		boolean inputError = false;
		
		// Validate object name: must be unique and valid Java identifier
		String objectName = nameTextField.getText();
		if (!StringUtil.isJavaIdentifier(objectName)) {
			inputError = true;
			JOptionPane.showMessageDialog(this, "The name of the object"
					+ " is not a valid Java identifier.",
					"Input error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			if (!canvas.getObjectList().isUniqueName(objectName,
					controlledObject)) {
				inputError = true;
				JOptionPane.showMessageDialog(this, "The name of the"
						+ " object is not unique.",
						"Input error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

    final List<String> textFieldValues = new ArrayList<String>(textFields.size());
    for (int i = 0; i < textFields.size(); i++) {
      textField = textFields.get(i);
      field = primitiveNameList.get(i);
      if (!textField.getText().equals("")) {
        try {
          final String value = getValue(textField.getText(), field.getType());
          textFieldValues.add(value);
        }
        catch (IllegalArgumentException e) {
          inputError = true;
          JOptionPane.showMessageDialog(this,
                  "The field "+field.getName()+" of type " +field.getType() +
                          " cannot have the value \""+textField.getText()+"\"",
                  "Input error",
                  JOptionPane.ERROR_MESSAGE
          );

        }
      }
      else {
        textFieldValues.add(null);
      }
    }
		if (!inputError) {

		    // Apply checkbox values to class fields.
		    // Indexes of textFields and checkboxes do not match
		    // in case there are array fields.  Therefore, separate
		    // indexes txtIdx and i are needes. 
		    int i = 0;
		    int txtIdx = 0;

		    for (ClassField fld : controlledObject.getFields()) {
		        //TODO this is soo ugly, need to refactor
		        if ( fld.isHidden() )
	                continue;
		        fld.setInput(goalInputs.get(i).isSelected());
		        fld.setGoal(goalOutputs.get(i).isSelected());
		            
		        if (fld.isPrimitiveOrString()) { 
                fld.setValue(textFieldValues.get(txtIdx));
		            txtIdx++;
		        }
		        i++;
		    }

		    for (i = 0; i < comboBoxes.size(); i++) {
				JComboBox comboBox = comboBoxes.get(i);
				field = arrayNameList.get(i);
				//field.setInput( inputs.get(i).isSelected() );
				//field.setGoal( goals.get(i).isSelected() );
				String s = "";
				for (int j = 0; j < comboBox.getItemCount(); j++) {
					s += (String) comboBox.getItemAt(j)
							+ TypeUtil.ARRAY_TOKEN;
				}
				if (!s.equals("")) {
					field.setValue(s);

				} else {
					field.setValue(null);
				}
			}
			controlledObject.setName(objectName);
			controlledObject.setStatic( isStatic.isSelected() );
			
			canvas.repaint();
		}
		return !inputError;
	}
	
    @Override
    public void dispose() {
		super.dispose();
		
		if( lst != null ) {
		    removeComponentListener( lst );
		    lst = null;
		}
		controlledObject = null;
		canvas = null;
	}

	public void actionPerformed(ActionEvent e) {
		JTextField textField;
		ClassField field;
		if (e.getSource() == ok) {
			
			if( validateAndApply() ) {
				this.dispose();
			}
		}
		else if (e.getSource() == close) {
			
			this.dispose();
		}
		else if (e.getSource() == apply) {
			
			validateAndApply();
		}
		else if (e.getSource() == clear) {
			// Clears object properties except the object name.
			for (int i = 0; i < textFields.size(); i++) {
				textField = textFields.get(i);
				field = primitiveNameList.get(i);
//				watchFields.get(i).setSelected(false);
				goalInputs.get(i).setSelected(false);
				goalOutputs.get(i).setSelected(false);
				field.setValue(null);
				textField.setText("");
			}
			for (int i = 0; i < comboBoxes.size(); i++) {
				JComboBox comboBox = comboBoxes.get(i);
				comboBox.removeAllItems();
			}
		}
	}

  private String getValue(String text, String type) {
    if (type.equals(TYPE_INT)) {
      int i = Integer.parseInt(text);
      return Integer.toString(i);
    }
    if (type.equals(TYPE_LONG)) {
      long i = Long.parseLong(text);
      return Long.toString(i) + "L";
    }
    if (type.equals(TYPE_DOUBLE)) {
      double i = Double.parseDouble(text);
      return Double.toString(i) + "D";
    }
    if (type.equals(TYPE_FLOAT)) {
      float i = Float.parseFloat(text);
      return Float.toString(i) + "F";
    }
    if (type.equals(TYPE_SHORT)) {
      final short s = Short.parseShort(text);
      return Short.toString(s);
    }
    if (type.equals(TYPE_BYTE)) {
      final byte b = Byte.parseByte(text);
      return Byte.toString(b);
    }
    if (type.equals(TYPE_BOOLEAN)) {
      boolean b = Boolean.parseBoolean(text);
      return Boolean.toString(b);
    }
    if (type.equals(TYPE_CHAR)) {
      if(text.length() != 1) {
        throw new IllegalArgumentException("Cannot convert String \"" + text + "\" to a char");
      }
      return "'" + text.charAt(0) + "'";
    }
    return text;
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
