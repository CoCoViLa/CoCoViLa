package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.synthesize.*;

public class SchemeSettingsDialog extends JDialog {

	public SchemeSettingsDialog( JFrame owner ) {
		super( owner, "Scheme Options", true );
		
		init();
		
    	pack();
    	setResizable( false );
    	setVisible( true );
    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void init() {
		
		setLocationRelativeTo( getParent() );
		
		JPanel spec_flow = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		spec_flow.setBorder( BorderFactory.createTitledBorder("Specification"));
		JPanel spec = new JPanel( );		
		spec.setLayout(new BoxLayout(spec, BoxLayout.Y_AXIS));
		
		List<IFactory> specs = SpecGenFactory.getInstance().getAllInstances();
		ButtonGroup group = new ButtonGroup();
		for (final IFactory factory : specs) {
			JRadioButton button = new JRadioButton(factory.getDescription());
			button.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					SpecGenFactory.getInstance().setCurrentSpecGen((ISpecGenerator)factory.getInstance());
				}});
			group.add(button);
			spec.add( button );
			if( factory.getInstance() == SpecGenFactory.getInstance().getCurrentSpecGen() ) {
				button.setSelected( true );
			}
		}
		spec_flow.add(spec);
		
		JPanel plan_flow = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		plan_flow.setBorder( BorderFactory.createTitledBorder("Planner"));
		JPanel plan = new JPanel( );		
		//plan.setLayout(new BoxLayout(plan, BoxLayout.Y_AXIS));
		
		List<IPlanner> plans = PlannerFactory.getInstance().getAllInstances();
		plan.setLayout( new GridLayout( plans.size(), 0 ) );
		
		ButtonGroup group2 = new ButtonGroup();
		for (final IPlanner planner : plans) {
			final Component opt = planner.getCustomOptionComponent();
			
			final JRadioButton button = new JRadioButton(planner.getDescription());
			button.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					PlannerFactory.getInstance().setCurrentPlanner( planner );
				}});
			group2.add(button);
			if( planner == PlannerFactory.getInstance().getCurrentPlanner() ) {
				button.setSelected( true );
			}
			if( opt != null ) {
				JPanel tmp = new JPanel();
				tmp.setLayout(new BoxLayout(tmp, BoxLayout.X_AXIS));
				tmp.add( button );
				final JButton advanced = new JButton( "Advanced" );
				advanced.addActionListener( new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						new JDialog( SchemeSettingsDialog.this, "Advanced Options", true ) {
							public void setVisible( boolean b ) {
								if( b ) {
									setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
									getContentPane().add( opt );
									pack();
								}
								super.setVisible( b );
							}
						}.setVisible( true );
					}});
				tmp.add( advanced );//
				plan.add( tmp );
				button.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						if( e.getSource() == button ) {
							advanced.setEnabled( button.isSelected() );
						}
					}});
				advanced.setEnabled( button.isSelected() );
			} else {
				plan.add( button );
			}		
		}		
		plan_flow.add( plan );
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add( spec_flow );
		panel.add( plan_flow );
		
		getContentPane().add( panel );
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
