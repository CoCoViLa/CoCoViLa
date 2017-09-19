/**
 * 
 */
package ee.ioc.cs.vsle.editor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.swing.*;

/**
 * @author pavelg
 *
 */
public class DnDTabbedPane extends JTabbedPane {

    private GlassPane glass;
    private int dragTabIdx = -1;
    
    private final DragSourceListener dragSourceListener = new DragSourceAdapter() {

        @Override
        public void dragExit( DragSourceEvent e ) {
            Point location = e.getLocation();
            try {
                if ( dragTabIdx > -1 ) {
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_ESCAPE);
                    robot.keyRelease(KeyEvent.VK_ESCAPE); 
                    
                    OuterFrame frame = new OuterFrame();
                    
                    frame.tab = DnDTabbedPane.this.getTabComponentAt( dragTabIdx );
                    frame.comp = DnDTabbedPane.this.getComponentAt( dragTabIdx );
                    frame.tabTitle = DnDTabbedPane.this.getTitleAt( dragTabIdx );
                    frame.icon = DnDTabbedPane.this.getIconAt( dragTabIdx );
                    frame.tip = DnDTabbedPane.this.getToolTipTextAt( dragTabIdx );
                    DnDTabbedPane.this.removeTabAt( dragTabIdx );
                    frame.setTitle( frame.tabTitle );
                    frame.getContentPane().add( frame.comp );
                    frame.setSize(frame.comp.getSize());
                    frame.validate();
                    frame.setLocation( location.x - frame.getSize().width/2, location.y-5 );
                    frame.setVisible( true );
                    
                    //keep dragging the frame while mouse button is pressed
                    robot.mouseRelease( InputEvent.BUTTON1_MASK );
                    robot.mouseMove( location.x, location.y );
                    robot.mousePress( InputEvent.BUTTON1_MASK );
                }
            } catch ( AWTException e1 ) {
                e1.printStackTrace();
            }
        }

        @Override
        public void dragEnter( DragSourceDragEvent e ) {
            e.getDragSourceContext().setCursor( DragSource.DefaultMoveDrop );
        }

        @Override
        public void dragDropEnd( DragSourceDropEvent e ) {
            getGlass().setVisible( false );
        }
    };

    private final Transferable transferable = new Transferable() {

        @Override
        public Object getTransferData( DataFlavor flavor )
                throws UnsupportedFlavorException, IOException {
            return null;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return null;
        }

        @Override
        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            return false;
        }

    };

    private final DragGestureListener dragGestureListener = new DragGestureListener() {

        @Override
        public void dragGestureRecognized( DragGestureEvent dge ) {
            if ( getTabCount() < 2 
                    || !SwingUtilities.isLeftMouseButton( (MouseEvent)dge.getTriggerEvent() ) )
                return;

            Point p = dge.getDragOrigin();
            dragTabIdx = indexAtLocation( p.x, p.y );

            if ( dragTabIdx < 0 || !isEnabledAt( dragTabIdx ) ) {
                dragTabIdx = -1;
                return;
            }
            setSelectedIndex( dragTabIdx );
            
            getRootPane().setGlassPane( getGlass() );
            
            getGlass().createImage( dge.getComponent() );

            try {
                //unfortunately "drag image" is OS-specific and is not shown in Windows
                dge.startDrag( DragSource.DefaultMoveDrop, emptyImage, emptyPoint, transferable,
                        dragSourceListener );
                    
            } catch ( InvalidDnDOperationException e ) {
                e.printStackTrace();
            }
        }
    };

    //this is a workaround for the problem on mac osx -- 
    //getting rid of an ugly rectangle
    private final Image emptyImage = new BufferedImage( 1, 1,
            BufferedImage.TYPE_INT_ARGB );
    private final Point emptyPoint = new Point();
    
    private final DropTargetListener dropTargetListener = new DropTargetAdapter() {
        
        @Override
        public void drop( DropTargetDropEvent e ) {
            Point p = e.getLocation();
            int idx = getDropIndex( p );
            if ( idx > -1 && dragTabIdx > -1 && idx != dragTabIdx ) {
                Component tab = DnDTabbedPane.this.getTabComponentAt( dragTabIdx );
                Component comp = DnDTabbedPane.this.getComponentAt( dragTabIdx );
                String title = DnDTabbedPane.this.getTitleAt( dragTabIdx );
                Icon icon = DnDTabbedPane.this.getIconAt( dragTabIdx );
                String tip = DnDTabbedPane.this.getToolTipTextAt( dragTabIdx );
                DnDTabbedPane.this.removeTabAt( dragTabIdx );
                DnDTabbedPane.this.insertTab( title, icon, comp, tip, idx );
                DnDTabbedPane.this.setTabComponentAt( idx, tab );
                DnDTabbedPane.this.setSelectedIndex( idx );
                e.dropComplete( true );
            } else {
                e.dropComplete( false );
            }
            dragTabIdx = -1;
        }

        @Override
        public void dragOver( final DropTargetDragEvent dtde ) {
            getGlass().setDragLocation( dtde.getLocation() );
            getGlass().setVisible( true );
            getGlass().repaint();
        }
    };
    
    /**
     * 
     */
    public DnDTabbedPane() {
        super();
        init();
    }

    private void init() {

        new DropTarget( this, DnDConstants.ACTION_COPY_OR_MOVE, dropTargetListener, true );
        new DragSource().createDefaultDragGestureRecognizer( this,
                DnDConstants.ACTION_COPY_OR_MOVE, dragGestureListener );
    }

    private int getDropIndex( Point p ) {
        int idx = indexAtLocation( p.x, p.y );

        //check if drop location is to the right of the last tab
        if ( idx == -1 && getTabCount() > 0 ) {
            Rectangle tabBounds = getBoundsAt( getTabCount() - 1 );
            if ( ( tabBounds.x + tabBounds.width ) <= p.x
                    && tabBounds.y <= ( p.y + 1 )
                    && p.y <= ( tabBounds.y + tabBounds.height ) ) {

                idx = getTabCount() - 1;

            }
        }
        return idx;
    }

    /**
     * @return the glass
     */
    public GlassPane getGlass() {

        if ( glass == null ) {
            glass = new GlassPane();
        }
        return glass;
    }

    private class GlassPane extends JPanel {

        private final AlphaComposite composite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.5f );

        private Point location;
        private BufferedImage image;

        private GlassPane() {
            setOpaque( false );
        }

        @Override
        public void paintComponent( Graphics g ) {
            if ( location == null )
                return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite( composite );

            int tabOver = getDropIndex( location );
            
            if ( tabOver > -1 && tabOver != dragTabIdx ) {
                Rectangle r = SwingUtilities.convertRectangle(
                        DnDTabbedPane.this, getBoundsAt( tabOver ), this );

                int x = r.x + ( tabOver <= dragTabIdx ? 0 : r.width );
                g.setColor( Color.black );
                g.fillRect( x, r.y, 2, r.height );
            }

            if ( image != null ) {
                Point _location = SwingUtilities.convertPoint( DnDTabbedPane.this, location, this );
                double xx = _location.getX() - ( image.getWidth( this ) / 2d );
                double yy = _location.getY() - ( image.getHeight( this ) / 2d );
                g2.drawImage( image, (int) xx, (int) yy, null );
            }
        }

        /**
         * @param location the location to set
         */
        public void setDragLocation( Point location ) {
            this.location = location;
        }

        /**
         * @param image the image to set
         */
        public void createImage( Component c ) {
            Rectangle rect = getBoundsAt( dragTabIdx );
            this.image = new BufferedImage( 
                    c.getWidth(), c.getHeight(),
                    BufferedImage.TYPE_INT_ARGB );
            c.paint( image.getGraphics() );
            image = image.getSubimage( 
                        rect.x < 0 ? 0 : rect.x, 
                        rect.y < 0 ? 0 : rect.y, 
                        rect.width, rect.height );
        }
    }

    private class OuterFrame extends JFrame {
        
        private Component tab;
        private Component comp;
        private String tabTitle;
        private Icon icon;
        private String tip;
        
        private OuterFrame() {
            super();
            setSize( 200, 300 );
            
            addWindowListener( new WindowAdapter() {

                @Override
                public void windowClosing( WindowEvent e ) {
                  DnDTabbedPane.this.addTab( tabTitle, icon, comp, tip );
                  int idx = DnDTabbedPane.this.indexOfComponent( comp );
                  DnDTabbedPane.this.setTabComponentAt( idx, tab );
                  DnDTabbedPane.this.setSelectedIndex( idx );
                  
                  removeWindowListener( this );
                }
            });
        }
    }
    
    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        JFrame frame = new JFrame();
        JTabbedPane pane = new DnDTabbedPane();

        pane.addTab( "Test1", new JLabel("one") );
        pane.addTab( "Test2", new JLabel("two") );
        pane.addTab( "Test3", new JLabel("three") );

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( new JLabel( "TOP" ), BorderLayout.NORTH );
        panel.add( pane, BorderLayout.CENTER );

        frame.add( panel );

        frame.setSize( new Dimension( 300, 400 ) );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setVisible( true );

        //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
        //        new JTabbedPaneDemo().setVisible(true); 

        //        MainPanel.createAndShowGUI();
    }

}
