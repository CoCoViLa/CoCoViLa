/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class AlgorithmVisualizer extends JFrame implements TextEditView {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JTabbedPane jTabbedPane = null;
	
	private FontChangeEvent.Listener fontListener = new FontChangeEvent.Listener() {

        @Override
        public void fontChanged( FontChangeEvent e ) {
            if( e.getElement() == RuntimeProperties.Fonts.ALGORITHM ) {
                for( int i = 0; i < getJTabbedPane().getTabCount(); i++ ) {
                    ((TabPanel)getJTabbedPane().getComponentAt( i )).getJtaAlgorithm().setFont( e.getFont() );
                }
            }
        }
    };
    
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
		}
		return jTabbedPane;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
				AlgorithmVisualizer thisClass = getInstance();
//				thisClass.addNewTab( "test", new ArrayList<Rel>() );
//				thisClass.addNewTab( "test2", new ArrayList<Rel>() );
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	private static AlgorithmVisualizer s_instance = null;
	
	public static AlgorithmVisualizer getInstance() {
		if( s_instance == null ) {
			s_instance = new AlgorithmVisualizer( Editor.getInstance() );
		}
		
		return s_instance;
	}
	
	@Override
	public void dispose() {
		s_instance = null;
		FontChangeEvent.removeFontChangeListener( fontListener );
		super.dispose();
	}

	/**
	 * This is the default constructor
	 */
	private AlgorithmVisualizer( JFrame parent ) {
		super();
		initialize();
	}

	/**
	 * Creates new TabPanel with custom Tab Component
	 * @param title
	 * @param algorithm
	 */
	public void addNewTab( String title, EvaluationAlgorithm algorithm ) {
		
		final TabPanel panel = new TabPanel( algorithm );
		
		getJTabbedPane().addTab( title, panel );
		getJTabbedPane().setSelectedComponent( panel );
		
		JLabel lbl = new JLabel( title );
		
		final JButton btn = new JButton( "x" );
		btn.setHorizontalTextPosition( SwingConstants.CENTER );
		btn.setMargin( new Insets( 0, 0, 0, 0 ) );
		btn.setPreferredSize( new Dimension( 20, 15 ) );
		
		btn.addActionListener( new ActionListener() {

			@Override
            public void actionPerformed(ActionEvent e) {
				getJTabbedPane().remove( panel );
				btn.removeActionListener( this );
			}
		} );
		
		JPanel pnl = new JPanel( new FlowLayout( FlowLayout.CENTER, 5, 0 ) );
		pnl.setOpaque( false );
		
		pnl.add( lbl );
		pnl.add( btn );
		
		getJTabbedPane().setTabComponentAt( getJTabbedPane().getSelectedIndex(), pnl );
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        TextSearchDialog.attachTo(this, this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(new Rectangle(300, 300, 600, 400));
		this.setContentPane(getJContentPane());
		this.setTitle("Algorithm Visualizer");
		this.setVisible(true);
		FontChangeEvent.addFontChangeListener( fontListener );
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	
	private static class TabPanel extends JPanel {
		
		private JTextArea jtaAlgorithm = null;

		private JScrollPane jScrollPane = null;
		
		TabPanel( final EvaluationAlgorithm algorithm ) {
			setLayout(new BorderLayout());
			add(getJScrollPane(), BorderLayout.CENTER);
			
			new Thread() {

				@Override
                public void run() {
					
					final String text = generateAlgorithmText( algorithm );
					
					SwingUtilities.invokeLater( new Runnable() {
						@Override
                        public void run() {
							getJtaAlgorithm().setText( text );
						}
					} );
					
				}
				
			}.start();
			
		}
			
		/**
		 * This method initializes jtaAlgorithm	
		 * 	
		 * @return javax.swing.JTextArea	
		 */
		private JTextArea getJtaAlgorithm() {
			if (jtaAlgorithm == null) {
				jtaAlgorithm = new JTextArea();
				jtaAlgorithm.setFont( RuntimeProperties.getFont( RuntimeProperties.Fonts.ALGORITHM ) );
				jtaAlgorithm.setEditable(false);
				jtaAlgorithm.setText("Please wait...");
			}
			return jtaAlgorithm;
		}

		/**
		 * This method initializes jScrollPane	
		 * 	
		 * @return javax.swing.JScrollPane	
		 */
		private JScrollPane getJScrollPane() {
			if (jScrollPane == null) {
				jScrollPane = new JScrollPane();
				jScrollPane.setViewportView(getJtaAlgorithm());
			}
			return jScrollPane;
		}
		
		private String generateAlgorithmText( EvaluationAlgorithm algorithm ) {
			
			StringBuilder alg = new StringBuilder();

			for ( PlanningResult res : algorithm ) {
			    Rel rel = res.getRel();
			    
				String obj = rel.getParent().getFullName();
				
				if( TypeUtil.TYPE_THIS.equals( obj ) ) {
					obj = "spec";
				}
				
				alg.append(same());
				alg.append(obj);
				alg.append(" : ");
				alg.append(rel.getDeclaration());
				alg.append("\n");

				if (rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK) {

					for (SubtaskRel subtask : rel.getSubtasks()) {
						right();
						alg.append(same());
						alg.append("!Subtask ");
						alg.append(subtask.getDeclaration());
						alg.append(" :\n");
						alg.append(generateAlgorithmText(res.getSubtaskAlgorithm( subtask )));
						left();
					}

					alg.append(same());
					alg.append("!end of: ");
					alg.append(obj);
					alg.append(" : ");
					alg.append(rel.getDeclaration());
					alg.append("\n\n");
				}
			}

			return alg.toString();
		}
		
		private String offset = "";
	    public static final String OT_TAB = "\t";

		
	    private static enum OFFSET { OT_INC, OT_DEC }
	    
	    private String cOT( OFFSET ot, int times ) {

	    	switch( ot )
	    	{
	    	case OT_INC :
	    		for ( int i = 0; i < times; i++ ) {
	    			offset += OT_TAB;
	    		}
	    		break;
	    	case OT_DEC :
	    		for ( int i = 0; i < times; i++ ) {
	    			offset = offset.substring( OT_TAB.length() );
	    		}
	    		break;

	    	}

	    	return offset;
	    }

	    private String left() {
	    	return cOT( OFFSET.OT_DEC, 1 );
	    }
	    
	    private String right() {
	    	return cOT( OFFSET.OT_INC, 1 );
	    }
	    
	    private String same() {
	    	return offset;
	    }
	}

    @Override
    public JTextComponent getTextComponent() {
        
        TabPanel panel = (TabPanel)getJTabbedPane().getSelectedComponent();
        
        if( panel != null ) {
            return panel.getJtaAlgorithm();
        }
        
        return null;
    }

    @Override
    public Window getWindow() {
        return this;
    }
	
}
