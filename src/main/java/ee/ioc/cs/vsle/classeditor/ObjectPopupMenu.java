package ee.ioc.cs.vsle.classeditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import ee.ioc.cs.vsle.editor.CodeViewer;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.editor.ProgramRunnerEvent;
import ee.ioc.cs.vsle.event.EventSystem;
import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.Image;
import ee.ioc.cs.vsle.graphics.Text;
import ee.ioc.cs.vsle.util.TypeUtil;
import ee.ioc.cs.vsle.vclass.ClassObject;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.RelObj;

/**
 * Popup menu that contains actions that can be performed on a scheme object.
 */
public class ObjectPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = 1L;

    // ee.ioc.cs.editor.editor.Menu items displayed in the menu.
    JMenuItem itemProperties;
    JMenuItem itemGroup;
    JMenuItem itemUngroup;
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
    JMenuItem itemOrder;


    JMenu submenuOrder;

    private ClassCanvas canvas;
    private GObj object;
    private static final boolean BB_ALWAYS_ON_TOP_RULE = true;

    /**
     * Build the popup menu by adding menu items and action listeners for the
     * menu items in it.
     * 
     * @param object the object that was clicked
     * @param canvas the canvas
     */
    ObjectPopupMenu( GObj object, ClassCanvas canvas ) {
        super();

        this.canvas = canvas;
        this.object = object;

        this.add(ClassEditor.getInstance().cloneAction);
        this.add(ClassEditor.getInstance().deleteAction);        
        
        if (object != null && object.getShapes() != null && object.getShapes().get(0) instanceof BoundingBox){    
        	
        	this.remove(0); /* This removes Clone action for Bounding Box */
        	
        	itemProperties = new JMenuItem( Menu.CLASS_PROPERTIES , KeyEvent.VK_R ); /* Item Property == ClassProperty for BB*/
            itemProperties.addActionListener( this );
            itemProperties.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK ) );
            this.add( itemProperties );
            
            itemViewCode = new JMenuItem( Menu.VIEWCODE , KeyEvent.  VK_V ); 
            itemViewCode.addActionListener( this );
            itemViewCode.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK ) );
            if ( ClassObject.className != null && ClassObject.componentType != PackageClass.ComponentType.TEMPLATE){
            	enableDisableMenuItem(itemViewCode, true);
            } else enableDisableMenuItem(itemViewCode, false);
            this.add( itemViewCode ); 
        	     	
        	itemOrder = new JMenuItem( Menu.MENU_ORDER );
        	enableDisableMenuItem(itemOrder, false);
        	this.add( itemOrder);
        	return;
        }
        
        itemProperties = new JMenuItem( Menu.PROPERTIES, KeyEvent.VK_R );
        itemProperties.addActionListener( this );
        itemProperties.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK ) );
        this.add( itemProperties );
        
        if (object != null) {
            this.add(makeSubmenuOrder());
        }

        if( object != null ) {
            if ( !object.isStrictConnected() && ! ( object instanceof RelObj ) )
                add( makeSubmenuRotation( object, canvas ) );

            // superclass
//            if ( canvas.canBeSetAsSuperClass( object ) ) {
//                JMenuItem tmp = new JMenuItem( Menu.SET_AS_SUPER, KeyEvent.VK_S );
//                tmp.addActionListener( this );
//                this.add( tmp );
//            } else if ( object.isSuperClass() ) {
//                JMenuItem tmp = new JMenuItem( Menu.UNSET_AS_SUPER, KeyEvent.VK_S );
//                tmp.addActionListener( this );
//                this.add( tmp );
//            }
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

        if (object == null || canvas.getObjectList().getSelectedCount() != 1) {
            enableDisableMenuItem(itemProperties, false);
            enableDisableMenuItem(itemForward, false);
            enableDisableMenuItem(itemToFront, false);
            enableDisableMenuItem(itemBackward, false);
            enableDisableMenuItem(itemToBack, false);
        } else {
            int objectIndex = canvas.getObjectList().indexOf(object);

            // Enable or disable order changing menu items.
            if (objectIndex == canvas.getObjectList().size() - 1) {
                enableDisableMenuItem(itemForward, false);
                enableDisableMenuItem(itemToFront, false);
            }
            if (objectIndex == 0) {
                enableDisableMenuItem(itemBackward, false);
                enableDisableMenuItem(itemToBack, false);
            }            
            if(objectIndex == canvas.getObjectList().size() - 2 && canvas.isBBTop()){
            	 enableDisableMenuItem(itemForward, false);
                 enableDisableMenuItem(itemToFront, false);
            }
        }

        return submenuOrder;
    }

    private Component makeSubmenuRotation( final GObj obj, final ClassCanvas canv ) {
        JMenu menu = new JMenu( Menu.ROTATION );

        JMenuItem itemRotCw = new JMenuItem( Menu.ROTATE_CW );
        itemRotCw.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                obj.setAngle( obj.getAngle() + Math.PI / 2.0 );
                canvas.drawingArea.repaint();
            }

        } );
        menu.add( itemRotCw );

        JMenuItem itemRotCcw = new JMenuItem( Menu.ROTATE_CCW );
        itemRotCcw.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                obj.setAngle( obj.getAngle() - Math.PI / 2.0 );
                canvas.drawingArea.repaint();
            }

        } );
        menu.add( itemRotCcw );

        JMenuItem itemRotAngle = new JMenuItem( Menu.ROTATE_ANGLE );
        itemRotAngle.addActionListener( new ActionListener() {

            @Override
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
    @Override
    public void actionPerformed( ActionEvent e ) {
        String cmd = e.getActionCommand();

        if ( Menu.PROPERTIES.equals( cmd ) ) {        	      	
        	if(this.object.getShapes() != null && this.object.getShapes().get(0) instanceof Text){
        		 new TextDialog( ClassEditor.getInstance(),this.object).setVisible( true );        		
        	} else if (this.object.getShapes() != null && this.object.getShapes().get(0) instanceof Image){
        		  new ImageDialog( ClassEditor.getInstance(), this.object).setVisible( true );
        	} else  new ShapePropertiesDialog(ClassEditor.getInstance(), this.object).setVisible( true );  
//            canvas.openPropertiesDialog( object );
            /*
             * Disabled until needed and reimplemented
             */
//        } else if ( Menu.GROUP.equals( cmd ) ) {
//            canvas.groupObjects();
//        } else if ( Menu.UNGROUP.equals( cmd ) ) {
//            canvas.ungroupObjects();
        } else if ( Menu.HLPORTS.equals( cmd ) ) {
            canvas.hilightPorts();
        } else if ( Menu.SHPORTS.equals( cmd ) ) {
            canvas.drawSelectedPorts();
        } else if ( Menu.BACKWARD.equals( cmd ) ) {
            // MOVE OBJECT BACKWARD IN THE LIST
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjectList().sendBackward( object, 1 );
            canvas.drawingArea.repaint();
        } else if ( Menu.FORWARD.equals( cmd ) ) {
            // MOVE OBJECT FORWARD IN THE LIST
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjectList().bringForward( object, 1 );
            canvas.drawingArea.repaint();
        } else if ( Menu.TOFRONT.equals( cmd ) ) {
            // MOVE OBJECT TO THE FRONT IN THE LIST,
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjectList().bringToFront( object , BB_ALWAYS_ON_TOP_RULE);
            canvas.drawingArea.repaint();
        } else if ( Menu.TOBACK.equals( cmd ) ) {
            // MOVE OBJECT TO THE BACK IN THE LIST
            // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
            canvas.getObjectList().sendToBack( object );
            canvas.drawingArea.repaint();
//        } else if ( Menu.MAKECLASS.equals( cmd ) ) {
//            ClassSaveDialog csd = new ClassSaveDialog( ( (GObjGroup) object ).getSpec( canvas.getConnections() ), canvas );
//            csd.pack();
//            csd.setLocationRelativeTo( canvas );
//            csd.setVisible( true );
        } else if ( Menu.VIEWCODE.equals( cmd ) ) {
        	canvas.openClassCodeViewer( ClassEditor.classObject.getClassName());
            //canvas.openClassCodeViewer( object.getClassName() );
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
        } else if (Menu.CLASS_PROPERTIES.equals(cmd)){
        	new ClassPropertiesDialog( ClassEditor.getInstance().getClassFieldModel(), true );
        	 canvas.updateBoundingBox();        	
        }
    }
}
