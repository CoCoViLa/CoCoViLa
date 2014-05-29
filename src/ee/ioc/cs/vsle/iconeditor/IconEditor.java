package ee.ioc.cs.vsle.iconeditor;

import static ee.ioc.cs.vsle.graphics.Shape.*;
import static ee.ioc.cs.vsle.iconeditor.ClassFieldsTableModel.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.vclass.PackageClass.ComponentType;

public class IconEditor extends JFrame {

 
	private static final long serialVersionUID = 1L;
	// Class properties.
    public static String className;
    public static String classDescription;
    private static String classIcon;
    public static ComponentType componentType;
    
    // Package properties
    public static String packageName;
    public static String packageDesc;
    
    int mouseX, mouseY; // Mouse X and Y coordinates.
    BoundingBox boundingbox;
    public IconMouseOps mListener;
    public static DrawingArea drawingArea;
    JMenuBar menuBar;
    JMenu menu;
    JMenu submenu;
    JMenu exportmenu;
    JMenu importmenu;
    JMenuItem menuItem;
    JPanel infoPanel; // Panel for runtime information, mouse coordinates,
                        // selected objects etc.
    public JPanel mainPanel = new JPanel();
    JLabel posInfo; // Label for displaying mouse position information.
    IconPalette palette;
    Dimension drawAreaSize = new Dimension( 700, 500 );
    ShapeGroup shapeList = new ShapeGroup();
    ArrayList<IconClass> packageClassList = new ArrayList<IconClass>();
    ArrayList<ClassField> fields = new ArrayList<ClassField>();
    Shape currentShape;
    ArrayList<IconPort> ports = new ArrayList<IconPort>();
    IconKeyOps keyListener;
    ArrayList<String> packageClassNamesList = new ArrayList<String>();
    boolean newClass = true;
    /**
     * Table model for storing class fields
     */
    private ClassFieldsTableModel dbrClassFields = new ClassFieldsTableModel();

    ChooseClassDialog ccd = new ChooseClassDialog( packageClassNamesList );
    DeleteClassDialog dcd = new DeleteClassDialog( packageClassNamesList );
    ClassImport ci;
    int classX, classY;

    public static final String WINDOW_TITLE = "CoCoViLa - Class Editor";
    public static boolean classParamsOk = false;
    public static boolean packageParamsOk = false;

    private static File packageFile;
    
    public static String prevPackagePath;
    float scale = 1.0f; // zoom level
        
    /**
     * Class constructor [1].
     */
    public IconEditor() {
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        initialize();
        palette = new IconPalette( mListener, this );
        validate();
    }

    /**
     * Application initializer.
     */
    public void initialize() {
        dbrClassFields = new ClassFieldsTableModel();

        setLocationByPlatform( true );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );

        addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );

        keyListener = new IconKeyOps( this );
        mListener = new IconMouseOps( this );

        drawingArea = new DrawingArea();
        drawingArea.setBackground( Color.white );
        drawingArea.setGridVisible( RuntimeProperties.isShowGrid() );

        // Initializes key listeners, for keyboard shortcuts.
        drawingArea.addKeyListener( keyListener );

        infoPanel = new JPanel( new GridLayout( 1, 2 ) );
        posInfo = new JLabel();

        drawingArea.addMouseListener( mListener );

        drawingArea.addMouseMotionListener( mListener );
        drawingArea.setPreferredSize( drawAreaSize );
        JScrollPane areaScrollPane = new JScrollPane( drawingArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add( areaScrollPane, BorderLayout.CENTER );

        infoPanel.add( posInfo );

        mainPanel.add( infoPanel, BorderLayout.SOUTH );
        posInfo.setText( "-" );
        makeMenu();

        classX = 0;
        classY = 0;

        getContentPane().add( mainPanel );

    }

    /**
     * Move object with keys, executed by the KeyOps.
     * 
     * @param moveX int - object x coordinate change.
     * @param moveY int - object y coordinate change.
     */
    public void moveObject( int moveX, int moveY ) {
        moveX = moveX * RuntimeProperties.getNudgeStep();
        moveY = moveY * RuntimeProperties.getNudgeStep();
        for ( int i = 0; i < shapeList.getSelected().size(); i++ ) {
            Shape s = shapeList.getSelected().get( i );
            s.setPosition( moveX, moveY );
        }
        for ( int i = 0; i < ports.size(); i++ ) {
            IconPort p = ports.get( i );
            if ( p.isSelected() ) {
                p.setPosition( moveX, moveY );
            }
        }
        repaint();
    } // moveObject

    /**
     * Build menu.
     */
    public void makeMenu() {
        menuBar = new JMenuBar();
        setJMenuBar( menuBar );

        menu = new JMenu( Menu.MENU_FILE );
        menu.setMnemonic( KeyEvent.VK_F );
        /*
         * menuItem = new JMenuItem(Menu.SAVE_SCHEME, KeyEvent.VK_S);
         * menuItem.addActionListener(mListener);
         * menuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S,
         * ActionEvent.CTRL_MASK)); menu.add(menuItem);
         * 
         * menuItem = new JMenuItem(Menu.LOAD_SCHEME, KeyEvent.VK_O);
         * menuItem.addActionListener(mListener);
         * menuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_O,
         * ActionEvent.CTRL_MASK)); menu.add(menuItem);
         */
        exportmenu = new JMenu( Menu.EXPORT_MENU );
        exportmenu.setMnemonic( KeyEvent.VK_E );

//        menuItem = new JMenuItem( Menu.EXPORT_CLASS, KeyEvent.VK_C );
//        menuItem.addActionListener( mListener );
//        exportmenu.add( menuItem );

        menuItem = new JMenuItem( Menu.EXPORT_TO_PACKAGE, KeyEvent.VK_P );
        menuItem.addActionListener( mListener );
        exportmenu.add( menuItem );

        // Export window graphics
        exportmenu.add(GraphicsExporter.getExportMenu());

        menu.add( exportmenu );

        importmenu = new JMenu( Menu.IMPORT_MENU );
        importmenu.setMnemonic( KeyEvent.VK_I );

        menuItem = new JMenuItem( Menu.IMPORT_FROM_PACKAGE );
        menuItem.addActionListener( mListener );
        importmenu.add( menuItem );

        menu.add( importmenu );

        menuItem = new JMenuItem( Menu.DELETE_FROM_PACKAGE, KeyEvent.VK_D );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.CREATE_PACKAGE, KeyEvent.VK_C );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.SELECT_PACKAGE );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );
        
        menu.addSeparator();

        menuItem = new JMenuItem( Menu.PRINT, KeyEvent.VK_P );
        menuItem.addActionListener( mListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );

        menu.addSeparator();

        menuItem = new JMenuItem( Menu.EXIT, KeyEvent.VK_X );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menuBar.add( menu );

        menu = new JMenu( Menu.MENU_EDIT );
        menu.setMnemonic( KeyEvent.VK_E );

        menuItem = new JMenuItem( Menu.SELECT_ALL, KeyEvent.VK_A );
        menuItem.addActionListener( mListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.CLEAR_ALL, KeyEvent.VK_C );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menuItem = new JCheckBoxMenuItem( Menu.GRID, RuntimeProperties.isShowGrid() );
        menuItem.setMnemonic( 'G' );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menu.addSeparator();

        menuItem = new JMenuItem( Menu.CLASS_PROPERTIES, KeyEvent.VK_P );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menuBar.add( menu );

        menu = new JMenu( Menu.MENU_OPTIONS );
        menu.setMnemonic( KeyEvent.VK_O );

        submenu = new JMenu( Menu.MENU_LAF );
        submenu.setMnemonic( KeyEvent.VK_L );

        Look.getInstance().createMenuItems( submenu );

        menuItem = new JMenuItem( Menu.SETTINGS, KeyEvent.VK_S );
        menuItem.addActionListener( mListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_J, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );

        menu.add( submenu );

        menuBar.add( menu );

        menu = new JMenu( Menu.MENU_HELP );
        menu.setMnemonic( KeyEvent.VK_H );
        menuBar.add( menu );

        menuItem = new JMenuItem( Menu.DOCS, KeyEvent.VK_D );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menu.addSeparator();

        menuItem = new JMenuItem( Menu.LICENSE, KeyEvent.VK_L );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.ABOUT, KeyEvent.VK_A );
        menuItem.addActionListener( mListener );
        menu.add( menuItem );

    }

    public void fixShape() {
        for ( int i = 0; i < shapeList.size(); i++ ) {
            Shape shape = shapeList.get( i );
            if ( shape.isSelected() ) {
                shape.setFixed( !shape.isFixed() );
            }
        }
    } // fixShape

    /**
     * Returns XML representing shapes on the screen.
     * 
     * @param appendXMLtag - append xml formatting or not.
     * @return StringBuilder - XML representing shapes on the screen.
     */
    public StringBuilder getShapesInXML( boolean appendXMLtag ) {
        StringBuilder xmlBuffer = new StringBuilder();
        if ( appendXMLtag ) {
            xmlBuffer.append( "<?xml version='1.0' encoding='utf-8'?>\n" );
            xmlBuffer.append( "\n" );
        }
        xmlBuffer.append( "<class type=\"" ).append( componentType.getXmlName() ).append(  "\">\n" );
        xmlBuffer.append( "	<name>" ).append(  IconEditor.className ).append(  "</name>\n" );
        xmlBuffer.append( "	<description>" ).append(  IconEditor.classDescription ).append(  "</description>\n" );
        String _classIcon = IconEditor.getClassIcon();
//        if ( classIcon != null && classIcon.lastIndexOf( "/" ) >= 0 )
//            classIcon = classIcon.substring( classIcon.lastIndexOf( "/" ) + 1 );
//        if ( classIcon != null && classIcon.lastIndexOf( "\\" ) >= 0 )
//            classIcon = classIcon.substring( classIcon.lastIndexOf( "\\" ) + 1 );
        xmlBuffer.append( "	<icon>" ).append(  _classIcon ).append(  "</icon>\n" );
        xmlBuffer = appendShapes( xmlBuffer );
        xmlBuffer = appendPorts( xmlBuffer );
        xmlBuffer = appendClassFields( xmlBuffer );
        xmlBuffer.append( "</class>\n" );

        return xmlBuffer;
    } // getShapesInXML

    public void createPackage() {
        PackagePropertiesDialog p = new PackagePropertiesDialog();
        p.setVisible( true );
        savePackage();
    } // createPackage

    public void selectPackage() {

        try {

            // Package file chooser.

            File file = selectFile();

            if ( file != null && file.exists()) {
                // Check if the file name ends with a required extension. If
                // not,
                // append the default extension to the file name.
                if ( !file.getAbsolutePath().toLowerCase().endsWith( ".xml" ) ) {
                    file = new File( file.getAbsolutePath() + ".xml" );
                }

                setPackageFile( file );
                
                updateTitle();
                
                // store the last open directory in system properties.
                RuntimeProperties.setLastPath( file.getAbsolutePath() );
                
            }
        } catch ( Exception exc ) {
            exc.printStackTrace();
        }
    }
    
    private void savePackage() {
        StringBuilder sb = new StringBuilder();
        sb.append( "<?xml version=\'1.0\' encoding=\'utf-8\'?>\n" );
        sb.append( "\n" );
        sb.append( "<!DOCTYPE package SYSTEM \"" + RuntimeProperties.PACKAGE_DTD + "\">\n" );
        sb.append( "<package>\n" );
        sb.append( "<name>" + IconEditor.packageName + "</name>\n" );
        sb.append( "<description>" + IconEditor.packageDesc + "</description>\n" );
        sb.append( "</package>" );
        if ( packageParamsOk ) {
            setPackageFile( saveToFile( sb.toString(), "xml" ) );
            updateTitle();
        }
    } // savePackage

    private void updateTitle() {
        if( getPackageFile() != null ) {
            setTitle( getPackageFile().getAbsolutePath() );
        } else {
            setTitle( null );
        }
    }
    /**
     * Save shape to file in XML format.
     * @deprecated
     */
    public void exportShapesToXML() {
        classParamsOk = true;
        validateClassParams();
        if ( classParamsOk ) {
            StringBuilder xmlBuffer = new StringBuilder();

            if ( boundingbox != null ) {
                xmlBuffer = getShapesInXML( true );
                saveToFile( xmlBuffer.toString(), "xml" );
            } else {
                JOptionPane.showMessageDialog( null, "Please define a bounding box.", "Bounding box undefined",
                        JOptionPane.INFORMATION_MESSAGE );
            }
        }
    } // exportShapesToXML

    public void exportShapesToPackage() {
        classParamsOk = true;
        validateClassParams();
        if ( classParamsOk ) {
            if ( boundingbox != null ) {
                saveToPackage();
            } else {
                JOptionPane.showMessageDialog( null, "Please define a bounding box.", "Bounding box undefined",
                        JOptionPane.INFORMATION_MESSAGE );
            }
        }
    } // exportShapesToPackage

    private void validateClassParams() {
        if ( IconEditor.className == null || IconEditor.classDescription == null
                || IconEditor.getClassIcon() == null
                || ( IconEditor.className != null && IconEditor.className.trim().length() == 0 )
                || ( IconEditor.classDescription != null && IconEditor.classDescription.trim().length() == 0 )
                || ( IconEditor.getClassIcon() != null && IconEditor.getClassIcon().trim().length() == 0 ) ) {
            new ClassPropertiesDialog( dbrClassFields, false );
        }
    } // validateClassParams

    private StringBuilder appendShapes( StringBuilder buf ) {
        buf.append( "<graphics>\n" );
        if ( boundingbox != null )
            buf.append( boundingbox.toFile( 0, 0 ) );
        for ( int i = 0; i < shapeList.size(); i++ ) {
            Shape shape = shapeList.get( i );
            if ( ! ( shape instanceof BoundingBox || isSpecialFieldShape(shape) ) ) {
                String shapeXML = null;
                shapeXML = shape.toFile( boundingbox.getX(), boundingbox.getY() );

                if ( shapeXML != null )
                    buf.append( shapeXML );
            }
        }
        buf.append( "</graphics>\n" );
        return buf;
    } // appendShapes

    private StringBuilder appendPorts( StringBuilder buf ) {
        if ( ports != null && ports.size() > 0 ) {
            buf.append( "	<ports>\n" );
            for ( int i = 0; i < ports.size(); i++ ) {
                IconPort p = ports.get( i );

                buf.append( "		<port name=\"" );
                buf.append( p.getName() );
                buf.append( "\" type=\"" );
                buf.append( p.getType() );
                buf.append( "\" x=\"" );
                buf.append( p.getX() - boundingbox.getX() );

                buf.append( "\" y=\"" );
                buf.append( p.getY() - boundingbox.getY() );

                buf.append( "\" portConnection=\"" );

                if ( p.isArea() )
                    buf.append( "area" );
                buf.append( "\" strict=\"" );
                buf.append( p.isStrict() );
                buf.append( "\" " );
                if ( p.isMulti() ) {
                    buf.append( "multi=\"" );
                    buf.append( p.isMulti() );
                    buf.append( "\" " );
                }
                buf.append( "/>\n" );
                /*
                 * if(!RuntimeProperties.classIsRelation) { buf.append("<open>\n");
                 * buf.append("<graphics>\n"); buf.append("<bounds x=\"-5\"
                 * y=\"-5\" width=\"10\" height=\"10\" />\n"); buf.append("</graphics>\n");
                 * buf.append("</open>\n"); buf.append("<closed>\n");
                 * buf.append("<graphics>\n"); buf.append("<rect x=\"-3\"
                 * y=\"-3\" width=\"6\" height=\"6\" colour=\"0\"
                 * filled=\"true\" />\n"); buf.append("</graphics>\n");
                 * buf.append("</closed>\n"); } buf.append("</port>\n");
                 */
            }
            buf.append( "	</ports>\n" );
        }
        return buf;
    } // appendPorts

    public Boolean isSpecialFieldShape(Shape s) {
        if( s instanceof Text) {
            dbrClassFields.removeEmptyRows();
            if ( dbrClassFields != null && dbrClassFields.getRowCount() > 0 ) {
                for ( int i = 0 ; i < dbrClassFields.getRowCount(); i++ ) {
                    Object fieldName = dbrClassFields.getValueAt( i, 0 );
                    if ("*".concat((String)fieldName).equals(((Text)s).getText()))
                        return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public StringBuilder appendClassFields( StringBuilder buf ) {
        dbrClassFields.removeEmptyRows();
        Boolean hasGraphics = false;
        if ( dbrClassFields != null && dbrClassFields.getRowCount() > 0 ) {
            buf.append( "	<fields>\n" );
            for ( int i = 0; i < dbrClassFields.getRowCount(); i++ ) {
                Object fieldName = dbrClassFields.getValueAt( i, 0 );
                Object fieldType = dbrClassFields.getValueAt( i, 1 );
                Object fieldValue = dbrClassFields.getValueAt( i, 2 );

                if ( fieldType == null )
                    fieldType = "";
                if ( fieldValue == null )
                    fieldValue = "";

                for ( int j = 0; j < shapeList.size(); j++ ){
                   if( shapeList.get(j) instanceof Text) {
                       /* "*fieldname" as text shows that it is special text that belongs to field
                        * (i.e field with known graphics)
                        */ 
                       if ("*".concat((String)fieldName).equals(((Text)shapeList.get(j)).getText())) {
                           if (!hasGraphics) {
                              if ( fieldValue.equals( "" ) )
                                  buf.append( "       <field name=\"" + fieldName + "\" type=\"" + fieldType + "\">\n" );
                              else
                                  buf.append( "       <field name=\"" + fieldName + "\" type=\"" + fieldType + "\" value=\"" + fieldValue
                                           + "\">\n" );
                              buf.append( "        <known>\n" );
                              buf.append( "          <graphics>\n" );
                           }
                           ((Text)shapeList.get(j)).setText("*self");
                           buf.append("            " + shapeList.get(j).toFile(boundingbox.getX(), boundingbox.getY()));
                           ((Text)shapeList.get(j)).setText("*".concat((String)fieldName));
                           if (!hasGraphics) {
                               buf.append( "          </graphics>\n" );
                               buf.append( "        </known>\n" );
                               buf.append( "      </field>\n" );
                           }
                           hasGraphics = true;
                       }
                   }
                }

                if ( !hasGraphics ) {
                    if ( fieldValue.equals( "" ) )
                        buf.append( "		<field name=\"" + fieldName + "\" type=\"" + fieldType + "\"/>\n" );
                    else
                        buf.append( "		<field name=\"" + fieldName + "\" type=\"" + fieldType + "\" value=\"" + fieldValue
                               + "\" />\n" );
                }

                hasGraphics = false;
            }
            buf.append( "	</fields>\n" );
        }
        return buf;
    } // appendClassFields

    public void selectShapesInsideBox( int x1, int y1, int x2, int y2 ) {
        for ( int i = 0; i < shapeList.size(); i++ ) {
            Shape shape = shapeList.get( i );
            if ( shape.isInsideRect( x1, y1, x2, y2 ) ) {
                shape.setSelected( true );
            }
        }
        for ( int i = 0; i < ports.size(); i++ ) {
            IconPort port = ports.get( i );
            if ( port.isInsideRect( x1, y1, x2, y2 ) ) {
                port.setSelected( true );
            }
        }
    } // selectShapesInsideBox

    public Shape checkInside( int x, int y ) {
        for ( int i = shapeList.size() - 1; i >= 0; i-- ) {
            Shape shape = shapeList.get( i );
            if ( shape.contains( x, y ) ) {
                return shape;
            }
        }
        return null;
    }

    public DrawingArea getDrawingArea() {
        return drawingArea;
    }

    class DrawingArea extends JPanel {

        private boolean showGrid = false;

        public boolean isGridVisible() {
            return this.showGrid;
        }

        public void setGridVisible( boolean b ) {
            this.showGrid = b;
            repaint();
        }

        protected void drawGrid( Graphics g ) {
            g.setColor( Color.lightGray );
            for ( int i = 0; i < getWidth(); i += RuntimeProperties.getGridStep() ) {
                // draw vertical lines
                g.drawLine( i, 0, i, getHeight() );
                // draw horizontal lines
                g.drawLine( 0, i, getWidth(), i );
            }
        }

        @Override
        protected void paintComponent( Graphics g ) {
            Graphics2D g2 = (Graphics2D) g;
            super.paintComponent( g2 );

            if ( this.showGrid )
                drawGrid( g2 );

            if ( RuntimeProperties.isAntialiasingOn() ) {
                g2.setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
            }

            for ( int i = 0; i < shapeList.size(); i++ ) {
                Shape shape = shapeList.get( i );
                // 2 esimest parameetrit 0,0.0 on offset ehk palju me teda
                // nihutame (oli vajalik kui shape on objekti graafika osa sest
                // siis peame arvestama ka objekti asukohta, antud juhul pole
                // oluline, aga ma ei viitsi meetodeid �mber
                // kirjutada, tulevikus voib seda teha. Kolmas parameeter ehk 1
                // on suurenduskordaja
                shape.draw( 0, 0, 1f, 1f, g2 );
            }

            IconPort port;
            for ( int i = 0; i < ports.size(); i++ ) {
                port = ports.get( i );
                port.draw( 0, 0, 1, g2 );
            }

            if ( mListener.state.equals( State.dragBox ) ) {
                g2.setColor( Color.gray );
                g2.setStroke( new BasicStroke( (float) 1.0 ) );
                int rectX = Math.min( mListener.startX, mouseX );
                int rectY = Math.min( mListener.startY, mouseY );
                int width = Math.abs( mouseX - mListener.startX );
                int height = Math.abs( mouseY - mListener.startY );
                g2.drawRect( rectX, rectY, width, height );
            } else {

                int red = mListener.color.getRed();
                int green = mListener.color.getGreen();
                int blue = mListener.color.getBlue();

                int alpha = mListener.getTransparency();
                g2.setColor( new Color( red, green, blue, alpha ) );

                if ( mListener.lineType > 0 ) {
                    g2.setStroke( new BasicStroke( mListener.strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                            50, new float[] { mListener.lineType, mListener.lineType }, 0 ) );
                } else {
                    g2.setStroke( new BasicStroke( mListener.strokeWidth ) );
                }

                final int width = Math.abs( mouseX - mListener.startX );
                final int height = Math.abs( mouseY - mListener.startY );

                if ( mListener.state.equals( State.drawArc1 ) ) {
                    g.drawRect( mListener.startX, mListener.startY, mListener.arcWidth, mListener.arcHeight );
                    g.drawLine( mListener.startX + mListener.arcWidth / 2, mListener.startY + mListener.arcHeight / 2, mouseX,
                            mouseY );
                } else if ( mListener.state.equals( State.drawArc2 ) ) {
                    if ( mListener.fill ) {
                        g2.fillArc( mListener.startX, mListener.startY, mListener.arcWidth, mListener.arcHeight,
                                mListener.arcStartAngle, mListener.arcAngle );

                    } else {
                        g2.drawArc( mListener.startX, mListener.startY, mListener.arcWidth, mListener.arcHeight,
                                mListener.arcStartAngle, mListener.arcAngle );
                    }
                }
                if ( !mListener.mouseState.equals( "released" ) ) {
                    if ( mListener.state.equals( State.drawRect ) ) {
                        g2.drawRect( Math.min( mListener.startX, mouseX ), Math.min( mListener.startY, mouseY ), width, height );
                    } else if ( mListener.state.equals( State.boundingbox ) ) {
                        g2.setColor( Color.darkGray );
                        g2.drawRect( Math.min( mListener.startX, mouseX ), Math.min( mListener.startY, mouseY ), width, height );
                    } else if ( mListener.state.equals( State.drawFilledRect ) ) {
                        g2.fillRect( Math.min( mListener.startX, mouseX ), Math.min( mListener.startY, mouseY ), width, height );
                    } else if ( mListener.state.equals( State.drawLine ) ) {
                        g2.drawLine( mListener.startX, mListener.startY, mouseX, mouseY );
                    } else if ( mListener.state.equals( State.drawOval ) ) {
                        g2.drawOval( Math.min( mListener.startX, mouseX ), Math.min( mListener.startY, mouseY ), width, height );
                    } else if ( mListener.state.equals( State.drawFilledOval ) ) {
                        g2.fillOval( Math.min( mListener.startX, mouseX ), Math.min( mListener.startY, mouseY ), width, height );
                    } else if ( mListener.state.equals( State.drawArc ) ) {
                        g.drawRect( Math.min( mListener.startX, mouseX ), Math.min( mListener.startY, mouseY ), width, height );
                    } else if ( mListener.state.equals( State.drawFilledArc ) ) {
                        g.drawRect( Math.min( mListener.startX, mouseX ), Math.min( mListener.startY, mouseY ), width, height );
                    }
                }
            }
        }
    }

    /**
     * Display information dialog to application user.
     * 
     * @param title - information dialog title.
     * @param text - text displayed in the information dialog.
     */
    public void showInfoDialog( String title, String text ) {
        JOptionPane.showMessageDialog( null, text, title, JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * Overridden so we can exit when window is closed
     * 
     * @param e - Window Event.
     */
    @Override
    protected void processWindowEvent( WindowEvent e ) {
        // super.processWindowEvent(e); // automatic closing disabled,
        // confirmation asked instead.
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            exitApplication();
        }
    }

    /**
     * Close application.
     */
    public void exitApplication() {

        int confirmed = JOptionPane.showConfirmDialog( null, "Exit Application?", Menu.EXIT, JOptionPane.OK_CANCEL_OPTION );
        switch ( confirmed ) {
        case JOptionPane.CANCEL_OPTION:
            break;
        case JOptionPane.OK_OPTION:
            System.exit( 0 );
        }
    }

    /**
     * Removes all objects.
     */
    public void clearObjects() {
        mListener.state = State.selection;
        shapeList = new ShapeGroup();
        ports = new ArrayList<IconPort>();
        palette.boundingbox.setEnabled( true );
        boundingbox = null;
        repaint();
    }

    /**
     * Upon platform, use OS-specific methods for opening the URL in required
     * browser.
     * 
     * @param url - URL to be opened in a browser. Capable of browsing local
     *                documentation as well if path is given with file://
     */
    public static void openInBrowser( String url ) {
        try {
            // Check if URL is defined, otherwise there is no reason for opening
            // the browser in the first place.
            if ( url != null && url.trim().length() > 0 ) {
                // Get OS type.
                String osType = getOsType();
                // Open URL with OS-specific methods.
                if ( osType != null && osType.equalsIgnoreCase( "Windows" ) ) {
                    Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + url );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Check if Operating System type is Windows.
     * 
     * @param osType - Operating System type.
     * @return boolean - Operating System belongs to the Windows family or not.
     */
    public static boolean isWin( String osType ) {
        if ( osType != null && osType.startsWith( "Windows" ) ) {
            return true;
        }
        return false;
    }

    /**
     * Return operating system type. Uses isWin, isMac, isUnix methods for
     * deciding on Os type and returns always the internally defined Os Type
     * (WIN,MAC or UNIX).
     * 
     * @return String - internally defined OS TYPE.
     */
    public static String getOsType() {
        Properties sysProps = System.getProperties();
        try {
            if ( sysProps != null ) {
                String osType = sysProps.getProperty( "os.name" );
                if ( isWin( osType ) ) {
                    return "Windows";
                }
                return "NotWindows";
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    } // getOsType

    /**
     * Open application options dialog.
     */
    public void openOptionsDialog() {
        OptionsDialog o = new OptionsDialog( IconEditor.this );
        o.setVisible( true );
        repaint();
    } // openOptionsDialog

    public void saveScheme() {

        JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );
        CustomFileFilter txtFilter = new CustomFileFilter( CustomFileFilter.EXT.TXT );

        fc.setFileFilter( txtFilter );
        int returnVal = fc.showSaveDialog( null );

        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File file = fc.getSelectedFile();

            // [Aulo] 11.02.2004
            // Check if the file name ends with a required extension. If not,
            // append the default extension to the file name.
            if ( !file.getAbsolutePath().toLowerCase().endsWith( CustomFileFilter.EXT.TXT.getExtension() ) ) {
                file = new File( file.getAbsolutePath() + "." + CustomFileFilter.EXT.TXT.getExtension() );
            }

            // store the last open directory in system properties.
            RuntimeProperties.setLastPath( file.getAbsolutePath() );
            boolean valid = true;

            // [Aulo] 04.01.2004
            // Check if file with a predefined name already exists.
            // If file exists, confirm file overwrite, otherwise leave
            // file as it is.
            if ( file.exists() ) {

                if ( JOptionPane.showConfirmDialog( null, "File exists.\nOverwrite file?", "Confirm Save",
                        JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.CANCEL_OPTION ) {
                    valid = false;
                }
            }
            if ( valid ) {
                // Save scheme.
                try {
                    StringBuilder xml = new StringBuilder();

                    xml.append( getGraphicsToString().toString() );
                    xml.append( getClassPropsToString().toString() );

                    FileOutputStream out = new FileOutputStream( new File( file.getAbsolutePath() ) );
                    out.write( xml.toString().getBytes() );
                    out.flush();
                    out.close();
                    JOptionPane.showMessageDialog( null, "Saved to: " + file.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE );

                } catch ( Exception exc ) {
                    exc.printStackTrace();
                }
            }
        }

    } // saveScheme

    /**
     * Load the previously saved scheme and display it on the drawing canvas.
     */
    public void loadScheme() {
        JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );
        CustomFileFilter filter = new CustomFileFilter( CustomFileFilter.EXT.TXT );

        fc.setFileFilter( filter );
        int returnVal = fc.showOpenDialog( null );

        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File file = fc.getSelectedFile();
            RuntimeProperties.setLastPath( file.getAbsolutePath() );
            try {
                mListener.state = State.selection;
                shapeList = new ShapeGroup();
                ports = new ArrayList<IconPort>();
                palette.boundingbox.setEnabled( true );
                loadGraphicsFromFile( file );
            } catch ( Exception exc ) {
                exc.printStackTrace();
            }
        }
    } // loadScheme

    // Print the drawing canvas.
    public void print() {
        PrintUtilities.printComponent( getDrawingArea() );
    } // print

    /**
     * Method for deleting selected objects.
     */
    public void deleteObjects() {
        shapeList.remove( currentShape );

        // Enable the bounding box button if we deleted a bounding box.
        if ( boundingbox != null && boundingbox.isSelected() ) {
            palette.boundingbox.setEnabled( true );
            boundingbox = null;
        }
        currentShape = null;

        // Remove all selected shapes.
        ArrayList<Shape> removableShapes = new ArrayList<Shape>();
        for ( int i = 0; i < shapeList.size(); i++ ) {
            Shape shape = shapeList.get( i );
            if ( shape.isSelected() ) {
                removableShapes.add( shape );
            }
        }
        shapeList.removeAll( removableShapes );

        // Remove all selected ports.
        ArrayList<IconPort> removablePorts = new ArrayList<IconPort>();
        for ( int i = 0; i < ports.size(); i++ ) {
            IconPort port = ports.get( i );
            if ( port.isSelected() ) {
                removablePorts.add( port );
            }
        }
        ports.removeAll( removablePorts );

        // Refresh the drawing canvas.
        repaint();
    } // deleteObjects

    /**
     * Method for grouping objects.
     */
    public void groupObjects() {
        ArrayList<Shape> selected = shapeList.getSelected();

        Shape shape;

        for ( int i = 0; i < selected.size(); i++ ) {
            shape = selected.get( i );
            shape.setSelected( false );
        }
        ShapeGroup sg = new ShapeGroup( selected );
        shapeList.removeAll( selected );
        shapeList.add( sg );
        repaint();
    }

    /**
     * Method for ungrouping objects.
     */
    public void ungroupObjects() {
        Shape shape;
        for ( int i = 0; i < shapeList.getSelected().size(); i++ ) {
            shape = shapeList.getSelected().get( i );
            if ( shape.getName() != null && shape.getName().startsWith( "GROUP" ) ) {
                shapeList.addAll( ( (ShapeGroup) shape ).getShapes() );
                shapeList.remove( shape );
                shape = null;
                currentShape = null;
            }
        }
        repaint();
    }

    public static javax.swing.filechooser.FileFilter getFileFilter( final String... formats ) {
        if ( formats != null && formats.length > 0 ) {
            javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
                @Override
                public String getDescription() {
                    StringBuilder fst = new StringBuilder();
                    StringBuilder snd = new StringBuilder();
                    for ( String format : formats ) {
                        fst.append( format.toUpperCase() ).append( " " );
                        snd.append( "*." ).append( format.toLowerCase() ).append( " " );
                    }
                    return fst + "files (" + snd + ")";
                }

                @Override
                public boolean accept( java.io.File f ) {
                    if( f.isDirectory() ) return true;
                    for ( String format : formats ) {
                        if( f.getName().toLowerCase().endsWith( "." + format.toLowerCase() ) ) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            return filter;
        }
        return null;
    }

    /**
     * Saves any input string into a file.
     * 
     * @param content - file content.
     * @param format - file format (also the default file extension).
     */
    public File saveToFile( String content, String format ) {
        try {
            if ( format != null ) {
                format = format.toLowerCase();
            } else {
                throw new Exception( "File format unspecified." );
            }
            JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );

            // [Aulo] 11.02.2004
            // Set custom file filter.
            fc.setFileFilter( getFileFilter( format ) );

            int returnVal = fc.showSaveDialog( null );
            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                File file = fc.getSelectedFile();

                // [Aulo] 11.02.2004
                // Check if the file name ends with a required extension. If
                // not,
                // append the default extension to the file name.
                if ( !file.getAbsolutePath().toLowerCase().endsWith( "." + format ) ) {
                    file = new File( file.getAbsolutePath() + "." + format );
                }

                // store the last open directory in system properties.
                RuntimeProperties.setLastPath( file.getAbsolutePath() );
                boolean valid = true;

                // [Aulo] 04.01.2004
                // Check if file with a predefined name already exists.
                // If file exists, confirm file overwrite, otherwise leave
                // file as it is.
                if ( file.exists() ) {
                    if ( JOptionPane.showConfirmDialog( null, "File exists.\nOverwrite file?", "Confirm Save",
                            JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.CANCEL_OPTION ) {
                        valid = false;
                    }
                }
                if ( valid ) {
                    // Save scheme.
                    try {
                        FileOutputStream out = new FileOutputStream( file );
                        out.write( content.getBytes() );
                        out.flush();
                        out.close();
                        JOptionPane.showMessageDialog( null, "Saved to: " + file.getName(), "Saved",
                                JOptionPane.INFORMATION_MESSAGE );
                        return file;

                    } catch ( Exception exc ) {
                        exc.printStackTrace();
                    }

                }
            }
        } catch ( Exception exc ) {
            exc.printStackTrace();
        }
        
        return null;
    }

    boolean checkPackage() {
        if( getPackageFile() == null ) {
            int result = JOptionPane.showConfirmDialog( this, "Select package and continue?", "Package not selected!", 
                    JOptionPane.YES_NO_OPTION );
            
            if( result == JOptionPane.YES_OPTION ) {
                selectPackage();
            }
            if( getPackageFile() == null ) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Saves any input string into a file.
     */
    public void saveToPackage() {
        boolean inPackage = false;
        String _className = IconEditor.className;
        
        if( !checkPackage() ) {
            JOptionPane.showMessageDialog(this,
                    "No package selected. Aborting.",
                    "Package not selected!", JOptionPane.WARNING_MESSAGE);
            return;
        }
       
        File iconFile = new File( getPackageFile().getParent() + File.separator + getClassIcon());
        
        /* See if icon file exists */
        File prevIconFile = new File( prevPackagePath + File.separator + getClassIcon());
        
        if ( (IconEditor.getClassIcon() == null) && !prevIconFile.exists() ) {
            IconEditor.setClassIcon( "default.gif" );
        } else if (prevIconFile.exists() && (prevIconFile.compareTo(iconFile) != 0)) {
        	FileFuncs.copyImageFile(prevIconFile, iconFile);        	
        }
                
        // See if class already exists in package
        ci = new ClassImport( getPackageFile(), packageClassNamesList, packageClassList );
        for ( int i = 0; i < packageClassList.size(); i++ ) {

            // class exists, move changed class to the end

            if ( IconEditor.className.equalsIgnoreCase( packageClassList.get( i ).getName() ) ) {
                inPackage = true;
                classX = 0 - classX;
                classY = 0 - classY;
                // shift everything back to where it was when first
                // loaded
                shapeList.shift( classX, classY );
                // set values to those on screen
                packageClassList.get( i ).setBoundingbox( boundingbox );
                packageClassList.get( i ).setDescription( IconEditor.classDescription );
                if ( IconEditor.getClassIcon() == null ) {
                    packageClassList.get( i ).setIconName( "default.gif" );
                } else {
                    packageClassList.get( i ).setIconName( IconEditor.getClassIcon() );
                }
                packageClassList.get( i ).setComponentType( IconEditor.componentType );
                packageClassList.get( i ).setName( IconEditor.className );
                packageClassList.get( i ).setPorts( ports );
                packageClassList.get( i ).shiftPorts( classX, classY );
                packageClassList.get( i ).setShapeList( shapeList );

                if ( dbrClassFields != null && dbrClassFields.getRowCount() > 0 ) {
                    fields.clear();
                    for ( int j = 0; j < dbrClassFields.getRowCount(); j++ ) {
                        String fieldName = dbrClassFields.getValueAt( j, iNAME );
                        String fieldType = dbrClassFields.getValueAt( j, iTYPE );
                        String fieldValue = dbrClassFields.getValueAt( j, iVALUE );
                        ClassField field = new ClassField( fieldName, fieldType, fieldValue );
                        fields.add( field );
                    }
                }
                packageClassList.get( i ).setFields( fields );
                packageClassList.add( packageClassList.get( i ) );
                packageClassList.remove( i );
                // assume that we only have one class with that name
                break;
            }
        }
        try {
            // Read the contents of the package, escaping the package
            // end that
            // will be appended later.
            BufferedReader in = new BufferedReader( new FileReader( getPackageFile() ) );
            String str;
            StringBuilder content = new StringBuilder();

            // Read file contents to be appended to.
            while ( ( str = in.readLine() ) != null ) {

                if ( inPackage && str.trim().startsWith( "<class" ) ) {
                    break;

                    // class is not in package, just write everything to
                    // file
                } else if ( !inPackage ) {
                    if ( str.equalsIgnoreCase( "</package>" ) )
                        break;
                    content.append( str + "\n" );

                } else if ( inPackage )
                    content.append( str + "\n" );
            }

            // if class is not in package, append the xml of current
            // drawing.
            if ( !inPackage ) {
                content.append( getShapesInXML( false ) );
            } else {
                // write all classes
                for ( int i = 0; i < packageClassList.size(); i++ ) {

                    classX = 0;
                    classY = 0;

                    makeClass( packageClassList.get( i ) );
                    content.append( getShapesInXML( false ) );
                }
            }
            content.append( "</package>" );

            in.close();

            /* See if .java file exists */
            File javaFile = new File( getPackageFile().getParent() + File.separator + _className + ".java" );
            /* See if previews .java file exists */
            File prevJavaFile = new File( prevPackagePath + File.separator + _className + ".java" );
            

            /* If file exists show confirmation dialog */
            int overwriteFile = JOptionPane.YES_OPTION;
            if ( javaFile.exists() ) {

                overwriteFile = JOptionPane.showConfirmDialog( null, "Java class already exists. Overwrite?" );
            }

            if ( overwriteFile != JOptionPane.CANCEL_OPTION ) {

                FileOutputStream out = new FileOutputStream( new File( getPackageFile().getAbsolutePath() ) );
                out.write( content.toString().getBytes() );
                out.flush();
                out.close();
                
                
                if ( overwriteFile == JOptionPane.YES_OPTION ) {
                	String fileText = null;
                	if (prevJavaFile.exists()) {
                		fileText = FileFuncs.getFileContents(prevJavaFile);
                	} else {
                		fileText = "class " + _className + " {";
                		fileText += "\n    /*@ specification " + _className + " {\n";

                		for ( int i = 0; i < dbrClassFields.getRowCount(); i++ ) {
                			String fieldName = dbrClassFields.getValueAt( i, iNAME );
                			String fieldType = dbrClassFields.getValueAt( i, iTYPE );
                			// String fieldValue =
                			// RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[2],
                			// i);

                			if ( fieldType != null ) {
                				fileText += "    " + fieldType + " " + fieldName + ";\n";
                			}
                		}
                		fileText += "    }@*/\n \n}";
                	}
                	FileFuncs.writeFile( javaFile, fileText );
                }

                JOptionPane.showMessageDialog( null, "Saved to package: " + getPackageFile().getName(), "Saved",
                        JOptionPane.INFORMATION_MESSAGE );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }


    } // saveToPackage

    /**
     * Sets all objects selected or unselected, depending on the method
     * parameter value.
     * 
     * @param b - select or unselect shapes.
     */
    public void selectAllObjects( boolean b ) {
        if ( shapeList != null && shapeList.size() > 0 ) {
            for ( int i = 0; i < shapeList.size(); i++ ) {
                Shape shape = shapeList.get( i );
                shape.setSelected( b );
            }
            repaint();
        }
    } // selectAllObjects

    /**
     * Sets all ports selected or unselected, depending on the method parameter
     * value.
     * 
     * @param b boolean - select or unselect ports.
     */
    public void selectAllPorts( boolean b ) {
        if ( ports != null && ports.size() > 0 ) {
            for ( int i = 0; i < ports.size(); i++ ) {
                IconPort port = ports.get( i );
                port.setSelected( b );
            }
            repaint();
        }
    } // selectAllPorts

    /**
     * Clones the currently selected object.
     */
    public void cloneObject() {

        currentShape = null;
        shapeList = shapeList.getCopy();
        repaint();
    } // cloneObject

    /**
     * Returns the selected shape if any shapes selected, otherwise returns
     * null. Called externally.
     * 
     * @return Shape - selected shape.
     */
    public Shape getSelectedShape() {
        if ( shapeList != null && shapeList.size() > 0 ) {
            for ( int i = 0; i < shapeList.size(); i++ ) {
                Shape s = shapeList.get( i );
                if ( s.isSelected() )
                    return s;
            }
        }
        return null;
    } // getSelectedShape

    /**
     * Load graphics.
     * 
     * @param f - package file to be loaded.
     */
    public void loadGraphicsFromFile( File f ) {
        try {
            emptyClassFields();
            BufferedReader in = new BufferedReader( new FileReader( f ) );
            String str;
            while ( ( str = in.readLine() ) != null ) {
                processShapes( str );
            }
            in.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    } // loadGraphicsFromFile

    /**
     * Empty the class fields table.
     */
    private void emptyClassFields() {
        if ( dbrClassFields != null )
            dbrClassFields.setRowCount( 0 );
    } // emptyClassFields

    /**
     * Returns a StringBuilder with all class properties for saving on a disk in
     * text format.
     * 
     * @return StringBuilder - class properties for saving on a disk in text
     *         format.
     */
    public StringBuilder getClassPropsToString() {
        StringBuilder sb = new StringBuilder();

        // Add class name.
        if ( IconEditor.className != null && IconEditor.className.trim().length() > 0 ) {
            sb.append( "CLASSNAME:" + IconEditor.className.trim() + "\n" );
        }

        // Add class decription.
        if ( IconEditor.classDescription != null && IconEditor.classDescription.trim().length() > 0 ) {
            sb.append( "CLASSDESCRIPTION:" + IconEditor.classDescription.trim() + "\n" );
        }

        // Add class icon.
        if ( IconEditor.getClassIcon() != null && IconEditor.getClassIcon().trim().length() > 0 ) {
            sb.append( "CLASSICON:" + IconEditor.getClassIcon().trim() + "\n" );
        }

        // Add boolean value representing if the class is a relation.
        sb.append( "COMPONENTTYPE:" + IconEditor.componentType.getXmlName() + "\n" );
        dbrClassFields.removeEmptyRows();
        // Add class fields.
        if ( dbrClassFields != null && dbrClassFields.getRowCount() > 0 ) {
            for ( int i = 0; i < dbrClassFields.getRowCount(); i++ ) {
                String fieldName = dbrClassFields.getValueAt( i, iNAME );
                String fieldType = dbrClassFields.getValueAt( i, iTYPE );
                String fieldValue = dbrClassFields.getValueAt( i, iVALUE );

                if ( fieldType == null )
                    fieldType = "";
                if ( fieldValue == null )
                    fieldValue = "";
                sb.append( "CLASSFIELD:" + fieldName + ":" + fieldType + ":" + fieldValue + "\n" );
            } // end for.
        } // end adding class fields.
        return sb;
    } // getClassPropsToString

    /**
     * Returns a StringBuilder with all drawn class graphics for saving on a disk
     * in text format.
     * 
     * @return StringBuilder - class graphics for saving on a disk in text
     *         format.
     */
    public StringBuilder getGraphicsToString() {
        StringBuilder sb = new StringBuilder();

        for ( int i = 0; i < shapeList.size(); i++ ) {
            Shape shape = shapeList.get( i );
            sb.append( shape.toText() );
            sb.append( "\n" );
        } // end for.

        for ( int i = 0; i < ports.size(); i++ ) {
            IconPort port = ports.get( i );
            sb.append( port.toText() );
            sb.append( "\n" );
        } // end for.

        return sb;
    } // getGraphicsToString

    public void processShapes( String str ) {

        if ( str != null ) {
            if ( str.startsWith( "LINE:" ) ) {
                str = str.substring( 5 );
                int x1 = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y1 = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int x2 = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y2 = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int colorInt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int strokeW = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int lt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int transp = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fixed = Boolean.valueOf( str ).booleanValue();

                Line line = new Line( x1, y1, x2, y2, createColor( colorInt, transp ), strokeW, lt );
                line.setFixed( fixed );
                shapeList.add( line );
            } else if ( str.startsWith( "ARC:" ) ) {
                str = str.substring( 4 );
                int x = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int width = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int height = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int startAngle = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int arcAngle = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int colorInt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fill = Boolean.valueOf( str.substring( 0, str.indexOf( ":" ) ) ).booleanValue();
                str = str.substring( str.indexOf( ":" ) + 1 );
                int strokeW = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int lt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int transp = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fixed = Boolean.valueOf( str ).booleanValue();

                Arc arc = new Arc( x, y, width, height, startAngle, arcAngle, createColor( colorInt, transp ), fill, strokeW, lt );
                arc.setFixed( fixed );
                shapeList.add( arc );
            } else if ( str.startsWith( "BOUNDS:" ) ) {
                str = str.substring( 7 );
                int x = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int width = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int height = Integer.parseInt( str );
                BoundingBox b = new BoundingBox( x, y, width, height );
                this.boundingbox = b;
                shapeList.add( b );
                palette.boundingbox.setEnabled( false );
            } else if ( str.startsWith( "DOT:" ) ) {
                str = str.substring( 4 );
                int x = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                // int width = Integer.parseInt(str.substring(0,
                // str.indexOf(":")));
                str = str.substring( str.indexOf( ":" ) + 1 );
                // int height = Integer.parseInt(str.substring(0,
                // str.indexOf(":")));
                str = str.substring( str.indexOf( ":" ) + 1 );
                int colorInt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int strokeW = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int transp = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fixed = Boolean.valueOf( str ).booleanValue();

                Dot dot = new Dot( x, y, createColor( colorInt, transp ), strokeW );
                dot.setFixed( fixed );
                shapeList.add( dot );
            } else if ( str.startsWith( "OVAL:" ) ) {
                str = str.substring( 5 );
                int x = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int width = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int height = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int colorInt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fill = Boolean.valueOf( str.substring( 0, str.indexOf( ":" ) ) ).booleanValue();
                str = str.substring( str.indexOf( ":" ) + 1 );
                int strokeW = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int lt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int transp = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fixed = Boolean.valueOf( str ).booleanValue();

                Oval oval = new Oval( x, y, width, height, createColor( colorInt, transp ), fill, strokeW, lt );
                oval.setFixed( fixed );
                shapeList.add( oval );
            } else if ( str.startsWith( "RECT:" ) ) {
                str = str.substring( 5 );
                int x = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int width = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int height = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int colorInt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fill = Boolean.valueOf( str.substring( 0, str.indexOf( ":" ) ) ).booleanValue();
                str = str.substring( str.indexOf( ":" ) + 1 );
                int strokeW = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int lt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int transp = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean fixed = Boolean.valueOf( str ).booleanValue();

                Rect rect = new Rect( x, y, width, height, createColor( colorInt, transp ), fill, strokeW, lt );
                rect.setFixed( fixed );
                shapeList.add( rect );
            } else if ( str.startsWith( "TEXT:" ) ) {
                str = str.substring( 5 );
                int x = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int colorInt = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                String fontName = str.substring( 0, str.indexOf( ":" ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                String fontStyle = str.substring( 0, str.indexOf( ":" ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int fontSize = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int transp = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );

                Font font = null;

                if ( fontStyle.equalsIgnoreCase( "0" ) )
                    font = new Font( fontName, Font.PLAIN, fontSize );
                else if ( fontStyle.equalsIgnoreCase( "1" ) )
                    font = new Font( fontName, Font.BOLD, fontSize );
                else if ( fontStyle.equalsIgnoreCase( "2" ) )
                    font = new Font( fontName, Font.ITALIC, fontSize );
                if ( font != null ) {
                    Text text = new Text( x, y, font, createColor( colorInt, transp ), str );
                    shapeList.add( text );
                }

            } else if ( str.startsWith( "PORT:" ) ) {
                str = str.substring( 5 );
                int x = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                int y = Integer.parseInt( str.substring( 0, str.indexOf( ":" ) ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean isAreaConn = Boolean.valueOf( str.substring( 0, str.indexOf( ":" ) ) ).booleanValue();
                str = str.substring( str.indexOf( ":" ) + 1 );
                boolean isStrict = Boolean.valueOf( str.substring( 0, str.indexOf( ":" ) ) ).booleanValue();
                str = str.substring( str.indexOf( ":" ) + 1 );

                IconPort port = new IconPort( str, x, y, isAreaConn, isStrict, false );
                ports.add( port );
            } else if ( str.startsWith( "CLASSNAME:" ) ) {
                IconEditor.className = str.substring( 10 );
            } else if ( str.startsWith( "CLASSDESCRIPTION:" ) ) {
                IconEditor.classDescription = str.substring( 17 );
            } else if ( str.startsWith( "CLASSICON:" ) ) {
                IconEditor.setClassIcon( str.substring( 10 ) );
            } else if ( str.startsWith( "COMPONENTTYPE:" ) ) {
                str = str.substring( "COMPONENTTYPE:".length() );
                IconEditor.componentType = ComponentType.getType( str );
            } else if ( str.startsWith( "CLASSFIELD:" ) ) {
                str = str.substring( 11 );
                String fieldName = str.substring( 0, str.indexOf( ":" ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                String fieldType = str.substring( 0, str.indexOf( ":" ) );
                str = str.substring( str.indexOf( ":" ) + 1 );
                String fieldValue = str;
                String[] classFields = { fieldName, fieldType, fieldValue };
                dbrClassFields.addRow( classFields );
            }
        }
        repaint();
    } // processShapes

    /**
     * Main method for running Class Editor.
     * 
     * @param args command line arguments
     */
    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                createAndInitGUI( args );
            }
        } );
    }

    static void createAndInitGUI( String[] args ) {
        assert SwingUtilities.isEventDispatchThread();

        Editor.checkWebStart( args );

        RuntimeProperties.init();

        Look.getInstance().initDefaultLnF();

        Editor.extractPackages();

        IconEditor window = new IconEditor();
        window.setTitle( null );
        window.setSize( 775, 600 );
        window.setVisible( true );

    }

    @Override
    public void setTitle( String title ) {
        
        if( title == null || title.length() == 0 ) {
            super.setTitle( WINDOW_TITLE );
        } else {
            super.setTitle( title + " - " + WINDOW_TITLE );
        }
    }

    public void setScale(float scale) {
        float oldScale = this.scale;
        this.scale = scale;

        // This code needs work and should probably be unified with Scheme Editor
        for ( int i = 0; i < shapeList.size(); i++ ) {
            Shape s = shapeList.get( i );
            s.setMultSize(scale * 100f, oldScale * 100f);
        }
        for ( int i = 0; i < ports.size(); i++ ) {
            IconPort p = ports.get( i );
            p.setMultSize(scale * 100f, oldScale * 100f);
        }

        recalcPreferredSize();
        drawingArea.repaint();
    } // zoom

    public void recalcPreferredSize() {
        int maxx = Integer.MIN_VALUE;
        int maxy = Integer.MIN_VALUE;

        for (int i = 0; i < shapeList.size(); i++) {
            Shape s = shapeList.get(i);
            int tmp = s.getX() + s.getRealWidth();
            if (tmp > maxx) {
                maxx = tmp;
            }

            tmp = s.getY() + s.getRealHeight();
            if (tmp > maxy) {
                maxy = tmp;
            }
        }

        for (int i = 0; i < ports.size(); i++) {
            IconPort p = ports.get(i);
            int tmp = p.getX() + p.getWidth();
            if (tmp > maxx) {
                maxx = tmp;
            }
            tmp = p.getY() + p.getHeight();
            if (tmp > maxy) {
                maxy = tmp;
            }
        }
    
        drawAreaSize.width = Math.round(
            // scale *
            (maxx > 0 ? maxx + RuntimeProperties.getGridStep() : drawAreaSize.width /* this.scale */));

        drawAreaSize.height = Math.round(// scale *
            (maxy > 0 ? maxy + RuntimeProperties.getGridStep() : drawAreaSize.height /* this.scale*/));

        drawingArea.setPreferredSize(drawAreaSize);
        drawingArea.revalidate();
    }

    public void loadClass() {
        JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );
        CustomFileFilter filter = new CustomFileFilter( CustomFileFilter.EXT.XML );

        fc.setFileFilter( filter );
        int returnVal = fc.showOpenDialog( null );

        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File file = fc.getSelectedFile();
            RuntimeProperties.setLastPath( file.getAbsolutePath() );
            try {
                mListener.state = State.selection;
                shapeList = new ShapeGroup();
                dbrClassFields.setRowCount( 0 );
                ports.clear();
                fields.clear();
                palette.boundingbox.setEnabled( true );
                importClassFromPackage( file );
            } catch ( Exception exc ) {
                exc.printStackTrace();
            }
        }
    }

    /*
     * Show a list of classes that can be imported from package
     */
    public void importClassFromPackage( File f ) {
    	prevPackagePath = f.getParent();
    	ci = new ClassImport( f, packageClassNamesList, packageClassList );
        // opens dialog with list of class names
        ccd.newJList( packageClassNamesList );
        ccd.setLocationRelativeTo( rootPane );
        ccd.setVisible( true );
        ccd.repaint();
        String selection = ccd.getSelectedValue();
        newClass = false;
        for ( int i = 0; i < packageClassList.size(); i++ ) {
            if ( ( packageClassList.get( i ) ).getName().equals( selection ) ) {
                makeClass( packageClassList.get( i ) );
            }
        }

        repaint();
    }

    /*
     * Make class
     */
    public void makeClass( IconClass icon ) {
        // find coordinates for the class
        classX = ( drawingArea.getWidth() / 2 ) - icon.getMaxWidth() / 2;
        classY = ( drawingArea.getHeight() / 2 ) - icon.getMaxHeight() / 2;
        shapeList = icon.getShapeList();
        icon.shiftPorts( classX, classY );
        shapeList.shift( classX, classY );
        ports = icon.getPorts();
        fields = icon.getFields();
        emptyClassFields();
        for ( int i = 0; i < fields.size(); i++ ) {

            String[] row = { ( fields.get( i ) ).getName(), ( fields.get( i ) ).getType(), ( fields.get( i ) ).getValue() };
            dbrClassFields.addRow( row );
        }
        IconEditor.className = icon.getName();
        IconEditor.classDescription = icon.getDescription();
        IconEditor.setClassIcon( icon.getIconName() );
        IconEditor.componentType = icon.getComponentType();
        palette.boundingbox.setEnabled( false );
        boundingbox = icon.getBoundingbox();
    }

    public File selectFile() {
        JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );
        fc.setFileFilter( getFileFilter( "xml" ) );
        fc.setDialogTitle( "Choose package" );

        int returnVal = fc.showOpenDialog( null );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File file = fc.getSelectedFile();

            // Check if the file name ends with a required extension. If not,
            // append the default extension to the file name.
            if ( !file.getAbsolutePath().toLowerCase().endsWith( ".xml" ) ) {
                file = new File( file.getAbsolutePath() + ".xml" );
            }
            RuntimeProperties.setLastPath( file.getAbsolutePath() );
            return file;
        }
        return null;
    }

    public void deleteClass() {
        File f = selectFile();
        if ( f != null )
            deleteClassFromPackage( f );

    }

    public void deleteClassFromPackage( File f ) {
        BufferedReader in;
        String str;
        StringBuilder content = new StringBuilder();
        String currentClass = IconEditor.className;
        try {
            in = new BufferedReader( new FileReader( f ) );

            ci = new ClassImport( f, packageClassNamesList, packageClassList );
            dcd.newJList( packageClassNamesList );
            dcd.setLocationRelativeTo( rootPane );
            dcd.setVisible( true );
            dcd.repaint();
            String selection = dcd.getSelectedValue();
            if ( selection == null )
                return;
            boolean deleteJavaClass = dcd.deleteClass();

            while ( ( str = in.readLine() ) != null ) {
                if ( str.trim().startsWith( "<class" ) ) {
                    break;
                }
                content.append( str + "\n" );
            }
            for ( int i = 0; i < packageClassList.size(); i++ ) {

                if ( ! ( ( packageClassList.get( i ) ).getName().equals( selection ) ) ) {
                    classX = 0;
                    classY = 0;
                    makeClass( packageClassList.get( i ) );
                    content.append( getShapesInXML( false ) );
                }
            }
            content.append( "</package>" );

            if ( ( currentClass == null ) || ( currentClass.equals( selection ) ) ) {
                clearObjects();
            }
            in.close();
            FileOutputStream out = new FileOutputStream( new File( f.getAbsolutePath() ) );
            out.write( content.toString().getBytes() );
            out.flush();
            out.close();
            if ( deleteJavaClass ) {
                File javaFile = new File( f.getParent() + File.separator + selection + ".java" );
                javaFile.delete();
            }
            JOptionPane.showMessageDialog( null, "Deleted " + selection + " from package: " + f.getName(), "Deleted",
                    JOptionPane.INFORMATION_MESSAGE );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the class field table model
     * 
     * @return the class field table model
     */
    public ClassFieldsTableModel getClassFieldModel() {
        return dbrClassFields;
    }

    /**
     * @param packageFile the packageFile to set
     */
    void setPackageFile( File packageFile ) {
        IconEditor.packageFile = packageFile;
    }

    /**
     * @return the packageFile
     */
    static File getPackageFile() {
        return packageFile;
    }

    /**
     * @param classIcon the classIcon to set
     */
    public static void setClassIcon( String classIcon ) {
        IconEditor.classIcon = classIcon;
    }

    /**
     * @return the classIcon
     */
    public static String getClassIcon() {
        return classIcon;
    }

} // end of class
