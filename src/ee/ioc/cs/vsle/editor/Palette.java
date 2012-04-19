package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * Menubar that contains buttons for choosing tools
 * and classes from the current package.
 */
public class Palette extends PaletteBase {

    protected Canvas canvas;
    private ScrollableBar bar;
    private JPanel controlPanel;
    private PropertyChangeListener propLst;
    private Map<String, AbstractButton> propsToButtons;

    public Palette(Canvas canv) {
        super();
        this.canvas = canv;
        init();
    }

    private void init() {

        // Cannot use JToolBars here because it does not play nicely with changing
        // L'n'F and scrollable toolbar. Hence buttons will look different 
        // in CE and SE for now...
        toolBar = new JPanel();

        toolBar.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        toolBar.setOpaque(false); // there is transparent space between buttons
        toolBar.setBorder(new EmptyBorder(2, 0, 2, 0));

        c.gridx = 0;
        toolBar.add(createToolPanel(), c);

        c.gridx = 2;
        if( canvas.isCtrlPanelVisible() ) {
            toolBar.add( controlPanel = createControlPanel(), c);
            c.gridx++;
        }
        
        toolBar.add(createZoomPanel(), c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(0, PANEL_SPACE.width, 0, PANEL_SPACE.width);
        toolBar.add(bar = new ScrollableBar(createClassPanel(canvas.getPackage())), c);

        canvas.add(toolBar, BorderLayout.PAGE_START);
        canvas.revalidate();
    }
    
    private JComponent createClassPanel(VPackage vPackage) {
        JPanel classPanel = new JPanel();

        classPanel.setOpaque(false);
        classPanel.setBorder(new EmptyBorder(2, 0, 2, 0));
        classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.LINE_AXIS));

        // read package info and add it to the palette
        for (int i = 0; i < vPackage.getClasses().size(); i++) {
            PackageClass pClass = vPackage.getClasses().get(i);

            ImageIcon icon;
            if ("default.gif".equals(pClass.getIcon())) {
                icon = FileFuncs.getImageIcon("images/default.gif", false);
            } else {
                icon = FileFuncs.getImageIcon(canvas.getWorkDir()
                        + pClass.getIcon(), true);
            }

            String actionCmd;
            if (pClass.getComponentType() == PackageClass.ComponentType.REL) {
                // to denote a class which is a relation
                actionCmd = State.addRelObjPrefix + pClass.getName();
            } else {
                actionCmd = pClass.getName();
            }

            JToggleButton button = createButton(icon, pClass.getName() + " "
                    + pClass.getDescription(), actionCmd);

            classPanel.add(button);

            if (i < vPackage.getClasses().size() - 1) {
                classPanel.add(Box.createRigidArea(BUTTON_SPACE));
            }
        }
        return classPanel;
    }

    private JComponent createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.LINE_AXIS));
        toolPanel.setOpaque(false);
        toolPanel.setBorder(new EmptyBorder(2, 0, 2, 0));

        JToggleButton selection = createButton("images/mouse.gif",
                "Select / Drag", State.selection);

        selection.setSelected(true);
        toolPanel.add(selection);

        toolPanel.add(Box.createRigidArea(BUTTON_SPACE));

        JToggleButton relation = createButton("images/rel.gif",
                "Relation", State.addRelation);

        toolPanel.add(relation);

        return toolPanel;
    }

    
    @Override
    protected MouseListener getButtonMouseListener() {
        if (mouseListener == null) {
            mouseListener = new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int i = buttons.lastIndexOf(e.getSource());

                        if (i < 0) {
                            return;
                        }

                        String action = buttons.get(i).getActionCommand();

                        if (State.isAddObject(action) 
                                || State.isAddRelClass(action)) {

                            canvas.viewPackageComponent(
                                    State.getClassName(action ));
                        }
                    }
                }
            };
        }
        return mouseListener;
    }

    @Override
    protected ActionListener getZoomListener() {
        return new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int i = ((JComboBox) e.getSource()).getSelectedIndex();
                canvas.setScale(ZOOM_LEVELS[i]);
            }

        };
    }

    @Override
    protected void setState(String state) {
        canvas.mListener.setState(state);
        canvas.drawingArea.grabFocus();
        canvas.drawingArea.repaint();
    }
    
    public static enum CtrlButton {
        RUN( "images/control/run_scheme.png", Menu.RUN ), 
        RUNSAME( "images/control/run_same_app.png", "RUNSAME" ),
        STOP( "images/control/stop_app.png", "STOP" ),
        PROPAGATE( "images/control/propagate.png", Menu.PROPAGATE_VALUES, RuntimeProperties.PROPAGATE_VALUES, true ), 
        GOAL( "images/control/compute_goal.png", Menu.COMPUTE_GOAL, RuntimeProperties.COMPUTE_GOAL, true ), 
        SPEC( "images/control/scheme_spec.png", Menu.SPECIFICATION ), 
        EXTEND( "images/control/scheme_extend.png", Menu.EXTEND_SPEC ), 
        RELOADSCHEME( "images/control/scheme_reload.png", Menu.RELOAD_SCHEME ), 
        RELOADPACKAGE( "images/control/package_reload.png", Menu.RELOAD  );

        private Icon icon;
        private boolean isToggle;
        private String actionCmd;
        private String propertyName;
        
        CtrlButton( String iconPath, String actionCmd ) {
            this( iconPath, actionCmd, null, false );
        }
        
        CtrlButton( String iconPath, String actionCmd, String propertyName, boolean isToggle ) {
            this.icon = FileFuncs.getImageIcon( iconPath, false );
            this.isToggle = isToggle;
            this.actionCmd = actionCmd;
            this.propertyName = propertyName;
        }

        /**
         * @return the icon
         */
        private Icon getIcon() {
            return icon;
        }

        /**
         * @return the isToggle
         */
        private boolean isToggle() {
            return isToggle;
        }

        /**
         * @return the actionCmd
         */
        String getActionCmd() {
            return actionCmd;
        }

        /**
         * @return the propertyName
         */
        private String getPropertyName() {
            return propertyName;
        }
        
    }
    
    protected JPanel createControlPanel() {
        
        JPanel _controlPanel = new JPanel();
        _controlPanel.setLayout(new BoxLayout(_controlPanel, BoxLayout.LINE_AXIS));
        _controlPanel.setOpaque(false);

        propsToButtons = new LinkedHashMap<String, AbstractButton>();
        
        for ( CtrlButton ctrlBtn : CtrlButton.values() ) {
            _controlPanel.add( prepareButton( ctrlBtn ) );
        }

        _controlPanel.add( Box.createHorizontalStrut( 5 ) );
        
        propLst = new PropertyChangeListener() {
            
            @Override
            public void propertyChange( PropertyChangeEvent evt ) {
                String propName = evt.getPropertyName();
                if( propsToButtons.containsKey( propName ) ) {
                    updateSelection( (JToggleButton)propsToButtons.get( propName ), propName );
                }
            }
        };
        
        RuntimeProperties.addPropertyChangeListener( propLst );
        return _controlPanel;
    }
    
    private void updateSelection( JToggleButton btn, String propName ) {
        if( propName == RuntimeProperties.COMPUTE_GOAL ) {
            btn.setSelected( RuntimeProperties.isComputeGoal() );
        } else if ( propName == RuntimeProperties.PROPAGATE_VALUES ) {
            btn.setSelected( RuntimeProperties.isPropagateValues() );
        }
    }
    
    private AbstractButton prepareButton( CtrlButton ctrlBtn ) {
        Icon icon = ctrlBtn.getIcon();
        AbstractButton btn;
        if( ctrlBtn.isToggle() ) {
            btn = new JToggleButton(icon);
            btn.setSelectedIcon(icon);
            updateSelection( (JToggleButton)btn, ctrlBtn.getPropertyName() );
        } else {
            btn = new JButton( icon );
        }
        btn.setActionCommand(ctrlBtn.getActionCmd());
        btn.setToolTipText(ctrlBtn.name());//TODO
        
        btn.setMargin(BUTTON_BORDER);
        btn.addActionListener( Editor.getInstance().getActionListener() );
        
        if( ctrlBtn.getPropertyName() != null ) {
            propsToButtons.put( ctrlBtn.getPropertyName(), btn );
        }
        return btn;
    }

    @Override
    public void destroy() {
        canvas.remove( toolBar );
        super.destroy();
        bar.destroy();
        bar.removeAll();
        bar = null;
        toolBar.removeAll();
        toolBar = null;
        if( controlPanel != null ) {
            controlPanel.removeAll();
            RuntimeProperties.removePropertyChangeListener( propLst );
            propLst = null;
            propsToButtons.clear();
            propsToButtons = null;
        }
        canvas = null;
    }
}
