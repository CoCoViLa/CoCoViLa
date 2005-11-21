package ee.ioc.cs.vsle.editor;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.synthesize.*;

public class SchemeSettingsDialog extends JDialog {

	public SchemeSettingsDialog( JFrame owner ) {
		super( owner, "Scheme Options" );
		
		init();
		
		setModal( true );
    	pack();
    	setResizable( false );
    	setVisible( true );
    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void init() {
		JPanel spec = new JPanel( );
		spec.setBorder( BorderFactory.createTitledBorder("Specification"));
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
		
		JPanel plan = new JPanel( );
		plan.setBorder( BorderFactory.createTitledBorder("Planner"));
		plan.setLayout(new BoxLayout(plan, BoxLayout.Y_AXIS));
		
		List<IPlanner> plans = PlannerFactory.getInstance().getAllInstances();
		ButtonGroup group2 = new ButtonGroup();
		for (final IPlanner planner : plans) {
			JRadioButton button = new JRadioButton(planner.getDescription());
			button.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					PlannerFactory.getInstance().setCurrentPlanner( planner );
				}});
			group2.add(button);
			plan.add( button );
			if( planner == PlannerFactory.getInstance().getCurrentPlanner() ) {
				button.setSelected( true );
			}
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add( spec );
		panel.add( plan );
		
		getContentPane().add( panel );
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
