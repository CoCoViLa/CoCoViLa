package ee.ioc.cs.vsle.iconeditor;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;

public class TextDialog extends JDialog implements ActionListener {

	private JLabel lblFont = new JLabel("Font:");
	private JLabel lblSize = new JLabel("Size:");
	private JLabel lblColor = new JLabel("Color:");

	private JComboBox cbFont = new JComboBox();

	private Color color = Color.black;

	private JButton bttnColor;
	private JButton bttnOk = new JButton("OK");
	private JButton bttnCancel = new JButton("Cancel");
	private JButton bttnBold;
	private JButton bttnItalic;
	private JButton bttnUnderline;

	private CustomTextArea taText = new CustomTextArea();

	private JPanel pnlMain = new JPanel();
	private JPanel pnlButton = new JPanel();
	private JPanel pnlFont = new JPanel();
	private JPanel pnlText = new JPanel();
	int mouseX, mouseY;

	JScrollPane textScrollPane = new JScrollPane(taText,
		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		JScrollPane.
		HORIZONTAL_SCROLLBAR_AS_NEEDED);

	Spinner spinner = new Spinner(1, 100, 1, 1);

	/**
	 * Booleans defining text BOLD, ITALIC and UNDERLINE
	 * values. Changed at every press on relevant buttons.
	 */
	private boolean isBold = false;
	private boolean isItalic = false;
	private boolean isUnderline = false;

	// IconEditor reference.
	IconEditor editor;

	// Font used for the text. Changed with dialog
	// buttons and other methods and passed back to
	// the calling environment.
	Font font;

	public TextDialog(IconEditor editor, int x, int y) {
		this.editor = editor;
		mouseX = x;
		mouseY = y;

		// Specify the dialog window title.
		setTitle("Text Dialog");

		// Specify smaller font for title and buttons.
		Font f = new Font("Arial", Font.BOLD, 11);

		TitledBorder textBorder = BorderFactory.createTitledBorder("Text to be displayed");
		textBorder.setTitleFont(f);
		textScrollPane.setBorder(textBorder);

		lblFont.setFont(f);
		lblSize.setFont(f);
		lblColor.setFont(f);
		bttnOk.setFont(f);
		bttnCancel.setFont(f);
		cbFont.setFont(f);

		// add color chooser tool
		ImageIcon icon = new ImageIcon("images/colorchooser.gif");
		bttnColor = new JButton(icon);
		bttnColor.setToolTipText("Color chooser");

		icon = new ImageIcon("images/bold.gif");
		bttnBold = new JButton(icon);
		bttnBold.setToolTipText("Bold");

		icon = new ImageIcon("images/italic.gif");
		bttnItalic = new JButton(icon);
		bttnItalic.setToolTipText("Italic");

		icon = new ImageIcon("images/underline.gif");
		bttnUnderline = new JButton(icon);
		bttnUnderline.setToolTipText("Underline");

		pnlFont.setPreferredSize(new Dimension(400, 40));
		pnlFont.setMinimumSize(pnlFont.getPreferredSize());
		pnlFont.setMaximumSize(pnlFont.getPreferredSize());

		cbFont.setPreferredSize(new Dimension(170, 22));
		cbFont.setMinimumSize(cbFont.getPreferredSize());
		cbFont.setMaximumSize(cbFont.getPreferredSize());

		bttnColor.setPreferredSize(new Dimension(22, 22));
		bttnColor.setMinimumSize(bttnColor.getPreferredSize());
		bttnColor.setMaximumSize(bttnColor.getPreferredSize());

		bttnBold.setPreferredSize(new Dimension(22, 22));
		bttnBold.setMinimumSize(bttnBold.getPreferredSize());
		bttnBold.setMaximumSize(bttnBold.getPreferredSize());

		bttnItalic.setPreferredSize(new Dimension(22, 22));
		bttnItalic.setMinimumSize(bttnItalic.getPreferredSize());
		bttnItalic.setMaximumSize(bttnItalic.getPreferredSize());

		// BUTTON DISABLED UNTIL FIGURED OUT HOW TO DISPLAY UNDERLINED TEXT
		bttnUnderline.setPreferredSize(new Dimension(22, 22));
		bttnUnderline.setMinimumSize(bttnUnderline.getPreferredSize());
		bttnUnderline.setMaximumSize(bttnUnderline.getPreferredSize());

		spinner.setPreferredSize(new Dimension(40, 22));
		spinner.setMaximumSize(spinner.getPreferredSize());
		spinner.setBorder(BorderFactory.createEmptyBorder());
		spinner.setValue("10");

		setComboBoxValues(cbFont, getSystemFonts(), "Arial");
		changeTextFont();

		pnlFont.add(lblFont);
		pnlFont.add(cbFont);
		pnlFont.add(bttnBold);
		pnlFont.add(bttnItalic);

		// pnlFont.add(bttnUnderline);
		pnlFont.add(lblSize);
		pnlFont.add(spinner);
		pnlFont.add(lblColor);
		pnlFont.add(bttnColor);

		// Specify text area behaviours.
		taText.setLineWrap(true);
		taText.setWrapStyleWord(true);
		taText.setTextAntialiasing(true);
		taText.setTabSize(4);

		pnlText.setLayout(new BorderLayout());
		pnlText.add(textScrollPane, BorderLayout.CENTER);

		pnlButton.add(bttnOk);
		pnlButton.add(bttnCancel);

		pnlMain.setPreferredSize(new Dimension(500, 300));
		pnlMain.setMinimumSize(pnlMain.getPreferredSize());
		pnlMain.setMaximumSize(pnlMain.getPreferredSize());

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlFont, BorderLayout.NORTH);
		pnlMain.add(pnlText, BorderLayout.CENTER);
		pnlMain.add(pnlButton, BorderLayout.SOUTH);

		getContentPane().add(pnlMain);
		setVisible(true);
		setSize(new Dimension(pnlMain.getPreferredSize()));
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(editor);

		// The default focus is automatically taken by the text area for
		// enabling the user to immediately start typing, without a need
		// to first position the cursor to the text area.
		taText.requestFocus();

		// ACTION LISTENERS AS ANONYMOUS CLASSES

		bttnBold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnBold) {
					if (isBold) {
						isBold = false;
					} else {
						isBold = true;
					}
					changeTextFont();
				}
			} // end actionPerformed
		}); // end bttnBold Action Listener

		bttnItalic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnItalic) {
					if (isItalic) {
						isItalic = false;
					} else {
						isItalic = true;
					}
					changeTextFont();
				}
			} // end actionPerformed
		}); // end bttnItalic Action Listener

		bttnUnderline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnUnderline) {
					if (isUnderline) {
						isUnderline = false;
					} else {
						isUnderline = true;
					}
					changeTextFont();
				}
			} // end actionPerformed
		}); // end bttnUnderline Action Listener

		bttnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnCancel) {
					setVisible(false);
				}
			} // end actionPerformed
		}); // end bttnCancel Action Listener

		bttnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnOk) {
					drawText(mouseX, mouseY);
					setVisible(false);
				}
			} // end actionPerformed
		}); // end bttnOk Action Listener

		bttnColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnColor) {
					openColorChooser();
					changeTextColor();
				}
			} // end actionPerformed
		}); // end bttnColor Action Listener

		cbFont.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent event) {
				if (event.getSource() == cbFont && event.getStateChange() == ItemEvent.SELECTED && cbFont.getItemCount() > 0) {
					changeTextFont();
				}
			}
		}); // end cbFont item listener

		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() == spinner) {
					changeTextFont();
				}
			}
		}); // end spinner change listener

	} // TextDialog

	/**
	 * Open the JColorChooser module and
	 * let the user choose for a new color for
	 * the text.
	 */
	private void openColorChooser() {
		Color col = JColorChooser.showDialog(this, "Choose Color", Color.black);
		if (col != null) this.color = col;
	} // openColorChooser

	/**
	 * Set text on text area. Called externally.
	 * @param s String - text to be put to text area.
	 */
	public void setText(String s) {
		taText.setText(s);
	} // setText

	/**
	 * Set text color. Called externally.
	 * @param col Color - text color.
	 */
	public void setColor(Color col) {
		this.color = col;
		taText.setForeground(this.color);
	} // setColor

	/**
	 * Set text font. Called externally.
	 * @param f Font - text font.
	 */
	public void setFont(Font f) {
		this.font = f;
		isBold = this.font.isBold();
		isItalic = this.font.isItalic();
		this.spinner.setValue(String.valueOf(this.font.getSize()));
		this.cbFont.setSelectedItem(f.getName());
		taText.setFont(this.font);
	} // setFont

	/**
	 * Change font of the text area taking the font name from the fonts
	 * combobox and the size from the spinner.
	 */
	private void changeTextFont() {
		if (cbFont != null && cbFont.getItemCount() > 0 && cbFont.getSelectedItem() != null &&
			spinner != null && spinner.getValue() != null && spinner.getValue().toString().trim().length() > 0) {

			int fontStyle = Font.PLAIN;
			if (isBold) fontStyle = Font.BOLD;
			if (isItalic) fontStyle = fontStyle + Font.ITALIC;

			font = new Font(cbFont.getSelectedItem().toString(), fontStyle, Integer.parseInt(spinner.getValue().toString()));

			taText.setFont(font);

		}
	} // changeTextFont

	/**
	 * Change the color of the text displayed in the text area.
	 */
	private void changeTextColor() {
		taText.setForeground(this.color);
	} // changeTextColor

	/**
	 * Draw displayed text on the drawing area in the IconEditor
	 */
	private void drawText(int x, int y) {
		editor.mListener.drawText(font, color, taText.getText(), x, y);
	} // drawText

	/**
	 * Set values to the combobox by removing the current values and
	 * replacing them with the ones given in the "values" array.
	 * @param cb JComboBox - combobox to put the values into.
	 * @param values Object[] - values to be put into the combobox.
	 * @param defaultSelection - item selected by default.
	 */
	private void setComboBoxValues(JComboBox cb, Object[] values, Object defaultSelection) {
		if (cb != null) {
			while (cb.getItemCount() > 0) cb.removeItemAt(0);
			if (values != null && values.length > 0) {
				for (int i = 0; i < values.length; i++) {
					cb.addItem(values[i]);
				}
				if (defaultSelection != null) {
					cb.setSelectedItem(defaultSelection);
				}
			}
		}
	} // setComboBoxValues

	/**
	 * Returns fonts available in the system.
	 * @return String[] - fonts available in the system.
	 */
	private String[] getSystemFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	} // getSystemFonts

	/**
	 * Action listener.
	 * @param evt ActionEvent - action event.
	 */
	public void actionPerformed(ActionEvent evt) {
	} // actionPerformed

	/**
	 * Main method for module unit testing.
	 * @param args String[] - command line arguments.
	 */
	public static void main(String[] args) {
		new TextDialog(new IconEditor(), 0, 0);
	} // main

} // end of class
