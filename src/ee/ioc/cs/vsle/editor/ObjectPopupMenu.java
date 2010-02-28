package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * Popup menu that contains actions that can be performed on a scheme object.
 */
public class ObjectPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = 1L;

    // ee.ioc.cs.editor.editor.Menu items displayed in the menu.
    JMenuItem itemProperties;
    // JMenuItem itemGroup;
    // JMenuItem itemUngroup;
    JMenuItem itemClone;
    JMenuItem itemHLPorts;
    JMenuItem itemDrawPorts;
    JMenuItem itemBackward;
    JMenuItem itemForward;
    JMenuItem itemToFront;
    JMenuItem itemToBack;
    JMenuItem itemMakeClass;
    JMenuItem itemViewCode;
    JMenuItem itemShowValues;

    JMenu submenuOrder;

    private Canvas canvas;
    private GObj object;

    /**
     * Build the popup menu by adding menu items and action listeners for the
     * menu items in it.
     * 
     * @param object the object that was clicked
     * @param canvas the canvas
     */
    ObjectPopupMenu( GObj object, Canvas canvas ) {
        super();

        this.canvas = canvas;
        this.object = object;

        this.add(Editor.getInstance().cloneAction);
        this.add(Editor.getInstance().deleteAction);

        /*
         * See canvas.groupObjects()
         */
        /*
         * itemGroup = new JMenuItem(Menu.GROUP, KeyEvent.VK_G);
         * itemGroup.addActionListener(this);
         * itemGroup.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_G,
         * InputEvent.CTRL_DOWN_MASK)); this.add(itemGroup);
         * 
         * itemUngroup = new JMenuItem(Menu.UNGROUP, KeyEvent.VK_U);
         * itemUngroup.addActionListener(this);
         * itemUngroup.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_U,
         * InputEvent.CTRL_DOWN_MASK)); this.add(itemUngroup);
         */

        itemProperties = new JMenuItem( Menu.PROPERTIES, KeyEvent.VK_R );
        itemProperties.addActionListener( this );
        itemProperties.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK ) );
        this.add( itemProperties );

        itemHLPorts = new JMenuItem( Menu.HLPORTS, KeyEvent.VK_H );
        itemHLPorts.addActionListener( this );
        itemHLPorts.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK ) );
        this.add( itemHLPorts );

        itemDrawPorts = new JMenuItem( Menu.SHPORTS );
        itemDrawPorts.addActionListener( this );
        this.add( itemDrawPorts );

        if (object != null && object.getClassName() != null) {
            itemViewCode = new JMenuItem( Menu.VIEWCODE );
            itemViewCode.addActionListener( this );
            itemViewCode.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK ) );
            this.add( itemViewCode );
        }

        itemShowValues = new JMenuItem( Menu.SHOW_VALUES );
        itemShowValues.addActionListener( this );
        itemShowValues.setEnabled( canvas.getLastProgramRunnerID() > 0 );
        this.add( itemShowValues );

        // itemMakeClass = new JMenuItem( Menu.MAKECLASS );
        // itemMakeClass.addActionListener( this );
        // itemMakeClass.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK ) );
        // this.add( itemMakeClass );

        if (object != null && object.getClassName() != null) {
            this.add(makeSubmenuOrder());
        }

        if( object != null ) {
            if ( !object.isStrictConnected() && ! ( object instanceof RelObj ) )
                add( makeSubmenuRotation( object, canvas ) );

            // superclass
            if ( canvas.canBeSetAsSuperClass( object ) ) {
                JMenuItem tmp = new JMenuItem( Menu.SET_AS_SUPER, KeyEvent.VK_S );
                tmp.addActionListener( this );
                this.add( tmp );
            } else if ( object.isSuperClass() ) {
                JMenuItem tmp = new JMenuItem( Menu.UNSET_AS_SUPER, KeyEvent.VK_S );
                tmp.addActionListener( this );
                this.add( tmp );
            }
        }
    }

    private Component makeSubmenuOrder() {
        submenuOrder = new JMenu( Menu.MENU_ORDER );

        itemBackward = new JMenuItem( Menu.BACKWARD, KeyEvent.VK_B );
        itemBackward.addActionListener( this );
        itemBackward.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_MINUS, 0 ) );
        submenuOrder.add( itemBackward );

        itemForward = new JMenuItem( Menu.FORWARD, KeyEvent.VK_F );
        itemForward.addActionListener( this );
        itemForward.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PLUS, 0 ) );
        submenuOrder.add( itemForward );

        itemToFront = new JMenuItem( Menu.TOFRONT, KeyEvent.VK_R );
        itemToFront.addActionListener( this );
        itemToFront.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK ) );
        submenuOrder.add( itemToFront );

        itemToBack = new JMenuItem( Menu.TOBACK, KeyEvent.VK_A );
        itemToBack.addActionListener( this );
        itemToBack.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK ) );
        submenuOrder.add( itemToBack );
        submenuOrder.setMnemonic( 'O' );

        if (object == null || canvas.getObjects().getSelectedCount() != 1) {
            enableDisableMenuItem(itemProperties, false);
            enableDisableMenuItem(itemForward, false);
            enableDisableMenuItem(itemToFront, false);
            enableDisableMenuItem(itemBackward, false);
            enableDisableMenuItem(itemToBack, false);
        } else {
            int objectIndex = canvas.getObjects().indexOf(object);

            // Enable or disable order changing menu items.
            if (objectIndex == canvas.getObjects().size() - 1) {
                enableDisableMenuItem(itemForward, false);
                enableDisableMenuItem(itemToFront, false);
            }
            if (objectIndex == 0) {
                enableDisableMenuItem(itemBackward, false);
                enableDisableMenuItem(itemToBack, false);
            }
        }

        return submenuOrder;
    }

    private Component makeSubmenuRotation( final GObj obj, final Canvas canv ) {
        JMenu menu = new JMenu( Menu.ROTATION );

        JMenuItem itemRotCw = new JMenuItem( Menu.ROTATE_CW );
        itemRotCw.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                obj.setAngle( obj.getAngle() + Math.PI / 2.0 );
                canvas.drawingArea.repaint();
            }

        } );
        menu.add( itemRotCw );

        JMenuItem itemRotCcw = new JMenuItem( Menu.ROTATE_CCW );
        itemRotCcw.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                obj.setAngle( obj.getAngle() - Math.PI / 2.0 );
                canvas.drawingArea.repaint();
            }

        } );
        menu.add( itemRotCcw );

        JMenuItem itemRotAngle = new JMenuItem( Menu.ROTATE_ANGLE );
        itemRotAngle.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                String value = JOptionPane.showInputDialog( "Input the angle in degrees:" );
                try {
                    obj.setAngle( Math.toRadians( Double.parseDouble( value ) ) );
                    canv.drawingArea.repaint();
                } catch ( Exception ex ) {
                    // ignore NumberFormatException and do nothing
                }
            }

        } );
        menu.add( itemRotAngle );

        return menu;
    }

    /**
     * Method for enabling or disabling menu items.
     * 
     * @param item - menu item to be enabled or disabled.
     * @param b - enable or disable the menu item.
     */
    void enableDisableMenuItem( JMenuItem item, boolean b ) {
        item.setEnabled( b );
    } // enableDisableMenuItem

    /**
     * Handles actions generated by object popup menu.
     * 
     * @param e Action Event
     */
    public void actionPerformed( ActionEvent e ) {
        String cmd = e.getActionCommand();

        if ( Menu.PROPERTIES.equals( cmd ) ) {
            canvas.openPropertiesDialog( object );
            /*
             * Disabled until needed and reimplemented
             */
            // } else if (Menu.GROUP.equals(cmd)) {
            // canvas.groupObjects();
            // } else if (Menu.UNGROUP.equals(cmd)) {
            // canvas.ungroupObjects();
        } else if ( Menu.HLPORTS.equals( cmd ) ) {
            canvas.hilightPorts();
        } else if ( Menu.SHPORTS.equals( cmd ) ) {
            canvas.drawSelectedPorts();
        } else if ( Menu.BACKWARD.equals( cmd ) ) {
            // MOVE OBJECT BACKWARD IN THE LIST
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjects().sendBackward( object, 1 );
            canvas.drawingArea.repaint();
        } else if ( Menu.FORWARD.equals( cmd ) ) {
            // MOVE OBJECT FORWARD IN THE LIST
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjects().bringForward( object, 1 );
            canvas.drawingArea.repaint();
        } else if ( Menu.TOFRONT.equals( cmd ) ) {
            // MOVE OBJECT TO THE FRONT IN THE LIST,
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjects().bringToFront( object );
            canvas.drawingArea.repaint();
        } else if ( Menu.TOBACK.equals( cmd ) ) {
            // MOVE OBJECT TO THE BACK IN THE LIST
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjects().sendToBack( object );
            canvas.drawingArea.repaint();
        } else if ( Menu.MAKECLASS.equals( cmd ) ) {
            ClassSaveDialog csd = new ClassSaveDialog( ( (GObjGroup) object ).getSpec( canvas.getConnections() ), canvas );
            csd.pack();
            csd.setLocationRelativeTo( canvas );
            csd.setVisible( true );
        } else if ( Menu.VIEWCODE.equals( cmd ) ) {
            canvas.openClassCodeViewer( object.getClassName() );
        } else if ( Menu.OBJ_SPEC.equals( cmd ) ) {
            new CodeViewer( object );
        } else if ( Menu.SHOW_VALUES.equals( cmd ) ) {
            ProgramRunnerEvent event = new ProgramRunnerEvent( this, canvas.getLastProgramRunnerID(), ProgramRunnerEvent.SHOW_VALUES );
            event.setObjectName( object.isSuperClass() ? TypeUtil.TYPE_THIS : object.getName() );
            EventSystem.queueEvent( event );
        } else if ( Menu.SET_AS_SUPER.equals( cmd ) ) {
            canvas.setAsSuperClass( object, true );
        } else if ( Menu.UNSET_AS_SUPER.equals( cmd ) ) {
            canvas.setAsSuperClass( object, false );
        }
    }
}
