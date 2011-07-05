/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JSlider;

/**
 * @author pavelg
 *
 */
public class ExpertConsultant extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel jpButtons = null;
    private JButton jbNext = null;
    private JButton jbFinish = null;
    private JButton jbBack = null;
    private JButton jbCancel = null;
    private JLabel jlblQuestion = null;
    private JPanel jpNorthQuestion = null;
    private JPanel jpCenter = null;
    private JLabel jlblInputType = null;
    private JSlider jsIntRangeSlider = null;
    /**
     * This method initializes jpButtons	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpButtons() {
        if ( jpButtons == null ) {
            jpButtons = new JPanel();
            jpButtons.setLayout(new FlowLayout());
            jpButtons.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jpButtons.add(getJbCancel(), null);
            jpButtons.add(getJbBack(), null);
            jpButtons.add(getJbNext(), null);
            jpButtons.add(getJbFinish(), null);
        }
        return jpButtons;
    }

    /**
     * This method initializes jbNext	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbNext() {
        if ( jbNext == null ) {
            jbNext = new JButton();
            jbNext.setText("Next");
        }
        return jbNext;
    }

    /**
     * This method initializes jbFinish	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbFinish() {
        if ( jbFinish == null ) {
            jbFinish = new JButton();
            jbFinish.setText("Finish");
            jbFinish.setEnabled(false);
        }
        return jbFinish;
    }

    /**
     * This method initializes jbBack	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbBack() {
        if ( jbBack == null ) {
            jbBack = new JButton();
            jbBack.setText("Back");
        }
        return jbBack;
    }

    /**
     * This method initializes jbCancel	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbCancel() {
        if ( jbCancel == null ) {
            jbCancel = new JButton();
            jbCancel.setText("Cancel");
        }
        return jbCancel;
    }

    /**
     * This method initializes jpNorthQuestion	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpNorthQuestion() {
        if ( jpNorthQuestion == null ) {
            jpNorthQuestion = new JPanel();
            jpNorthQuestion.setLayout(new FlowLayout());
            jpNorthQuestion.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            jpNorthQuestion.add(jlblQuestion, null);
        }
        return jpNorthQuestion;
    }

    /**
     * This method initializes jpCenter	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpCenter() {
        if ( jpCenter == null ) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints1.gridy = 3;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            jlblInputType = new JLabel();
            jlblInputType.setText("Current value: 35");
            jlblInputType.setVisible(true);
            jpCenter = new JPanel();
            jpCenter.setLayout(new GridBagLayout());
            jpCenter.add(jlblInputType, gridBagConstraints);
            jpCenter.add(getJsIntRangeSlider(), gridBagConstraints1);
        }
        return jpCenter;
    }

    /**
     * This method initializes jsIntRangeSlider	
     * 	
     * @return javax.swing.JSlider	
     */
    private JSlider getJsIntRangeSlider() {
        if ( jsIntRangeSlider == null ) {
            jsIntRangeSlider = new JSlider();
            jsIntRangeSlider.setName("");
            jsIntRangeSlider.setPaintTicks(true);
            jsIntRangeSlider.setSnapToTicks(true);
            jsIntRangeSlider.setMinorTickSpacing(5);
            jsIntRangeSlider.setMajorTickSpacing(50);
            jsIntRangeSlider.setPaintLabels(true);
        }
        return jsIntRangeSlider;
    }

    /**
         * @param args
         */
    public static void main( String[] args ) {
        // TODO Auto-generated method stub

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                ExpertConsultant thisClass = new ExpertConsultant();
                thisClass.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                thisClass.setVisible( true );
            }
        } );
    }

    /**
     * This is the default constructor
     */
    public ExpertConsultant() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(385, 215);
        this.setResizable(false);
        this.setContentPane( getJContentPane() );
        this.setTitle("Expert System Consultant");
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if ( jContentPane == null ) {
            jlblQuestion = new JLabel();
            jlblQuestion.setText("Specify the speed of wind (in m/s)");
            jlblQuestion.setToolTipText("");
            jContentPane = new JPanel();
            jContentPane.setLayout( new BorderLayout() );
            jContentPane.add(getJpButtons(), BorderLayout.SOUTH);
            jContentPane.add(getJpNorthQuestion(), BorderLayout.NORTH);
            jContentPane.add(getJpCenter(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
