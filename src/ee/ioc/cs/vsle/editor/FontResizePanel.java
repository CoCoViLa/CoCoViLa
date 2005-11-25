package ee.ioc.cs.vsle.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;

public class FontResizePanel extends JPanel {
	private JComponent m_area;
	private JSpinner m_spinner = new JSpinner();
	private boolean m_isAjusting = false;
	
	public FontResizePanel(JComponent area) {
		m_area = area;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		for (int i = 0; i < m_spinner.getComponents().length; i++) {
			
			Component comp = m_spinner.getComponents()[i];
			
			if( comp instanceof JButton ) {
				comp.addMouseListener(new MouseAdapter(){
					
					public void mouseReleased(MouseEvent e) {
						m_isAjusting = false;
					}

					public void mousePressed(MouseEvent e) {
						m_isAjusting = true;
					}});
			}
			
		final JTextField jtf = ((JSpinner.DefaultEditor)m_spinner.getEditor()).getTextField();
		jtf.addKeyListener(new KeyAdapter(){

			public void keyPressed(KeyEvent e) {
				m_isAjusting = true;
			}

			public void keyReleased(KeyEvent e) {
				m_isAjusting = false;

				update( ((Integer) m_spinner.getValue()).intValue() );
			}});
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel(new GridLayout(1, 3));
		panel.add(new JLabel("Font Size: "));
		m_spinner.setPreferredSize(new Dimension(50, 20));
		final SpinnerNumberModel model = new SpinnerNumberModel(m_area.getFont().getSize(), 5, 100, 1);
		m_spinner.setModel(model);
		panel.add(m_spinner);
		JCheckBox checkb = new JCheckBox("Bold", (m_area.getFont().isBold()) ? true : false);
		checkb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox c = (JCheckBox) e.getSource();
				Font font = m_area.getFont();
				m_area.setFont(new Font(font.getName(),
					(c.isSelected()) ? Font.BOLD : Font.PLAIN,
					((Integer) m_spinner.getValue()).intValue()));
				RuntimeProperties.font = m_area.getFont();
			}
		});

		panel.add(checkb);
		this.add(panel);

		this.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				model.setValue(new Integer(m_area.getFont().getSize()));
			}
		});

		m_spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				final JSpinner spin = (JSpinner) e.getSource();
				
				update( ((Integer) spin.getValue()).intValue() );
				
			}
		});
	}
	
	private void update( final int value ) {
		
		SwingUtilities.invokeLater(new Runnable(){

			public void run() {
				Font font = m_area.getFont();
				
				if( !m_isAjusting ) {
					m_area.setFont(new Font(font.getName(), font.getStyle(), value ) );
				}
				RuntimeProperties.font = m_area.getFont();
			}});
	}
}
