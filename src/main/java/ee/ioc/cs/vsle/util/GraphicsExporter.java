package ee.ioc.cs.vsle.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import ee.ioc.cs.vsle.editor.Editor;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for exporting application window graphics as vector (SVG, EPS, ...)
 * and raster (PNG, JPEG, ...) images using third party libraries.
 * This implementation currently supports the VectorGraphics package of
 * the FreeHEP Java Library:
 * http://java.freehep.org/vectorgraphics/index.html.
 * The package is Free software and not currently included in CoCoViLa.
 * To use the functionality, download the FreeHEP VectorGraphics package,
 * extract the archive and add all the jar-files in directory lib into
 * CoCoViLas CLASSPATH.<br/>
 * $ cp vectorgraphics/lib/*.jar cocovila/lib/<br/>
 * $ cd cocovila && java -cp classes`for f in $(ls lib/*.jar); do echo -n ":$f"; done` ee.ioc.cs.vsle.editor.Editor</br>
 * (or something similar).
 */
public final class GraphicsExporter {

    private static final Logger logger = LoggerFactory.getLogger(GraphicsExporter.class);

    private static JMenu exportMenu;

    /**
     * Creates and returns a JMenu that knows how to load libraries 
     * and find windows to export. The same instance is returned on
     * subsequent calls.
     * @return graphics export menu
     */
    public static JMenu getExportMenu() {
        assert SwingUtilities.isEventDispatchThread();

        if (exportMenu == null) {
            exportMenu = new JMenu(Menu.EXPORT_WINDOW_GRAPHICS);
            exportMenu.getPopupMenu().addPopupMenuListener(
                    new ExportPopupListener(exportMenu));
        }

        return exportMenu;
    }

    /**
     * A lazy menu that attempts to load FreeHEP libraries when requested.
     * When the libraries are found a menu item is presented for each window.
     */
    static class ExportPopupListener implements PopupMenuListener {

        private static final String EXPORT_DIALOG_CLASS =
            "org.freehep.util.export.ExportDialog";

        private Class<?> exportDialogClass;
        private boolean initDone;
        private ActionListener subMenuListener;
        private JMenu menu;
        private Map<Object, Window> menuItemFrameMap;

        public ExportPopupListener(JMenu menu) {
            if (menu == null) {
                throw new IllegalArgumentException("Menu cannot be null");
            }
            this.menu = menu;
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            if (menuItemFrameMap != null) {
                menuItemFrameMap.clear();
            }
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // ignored
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (exportDialogClass == null && !initDone) {
                init();
            }

            if (exportDialogClass != null) {
                menu.removeAll();
                menuItemFrameMap.clear();

                Window[] ws = Window.getWindows();
                for (Window w : ws) {
                    // Probably should hide some strange windows such as
                    // ###overrideRedirect### etc.  How to figure out the
                    // useful windows?  Remember that packages can also create
                    // useful frames.
                    JMenuItem item = new JMenuItem(
                            w instanceof Frame
                                ? ((Frame) w).getTitle()
                                : w.getName());

                    item.addActionListener(subMenuListener);
                    menu.add(item);
                    menuItemFrameMap.put(item, w);
                }
            }
        }

        // Called only once when the menu is first displayed.
        private void init() {
            if (initDone) {
                return;
            }

            try {
                // Dynamic loading is used to avoid compile- and link-time
                // dependencies.
                exportDialogClass = Class.forName(EXPORT_DIALOG_CLASS);
                menuItemFrameMap = new HashMap<Object, Window>();
                subMenuListener = new ExportActionListener(exportDialogClass,
                        menuItemFrameMap);
            } catch (ClassNotFoundException ex) {
                logger.error(null, ex);
                menu.removeAll();
                menu.add(new JLabel("(function not available)"));
            } finally {
                initDone = true;
            }
        }
    }

    /**
     * Listener that actually exports a window selected from the popup list.
     */
    static class ExportActionListener implements ActionListener {

        private static final String EXPORT_METHOD = "showExportDialog";
        private static final String DIALOG_TITLE = "Export Graphics";

        Class<?> exportDialogClass;
        Map<Object, Window> menuMap;

        public ExportActionListener(Class<?> exportDialogClass,
                Map<Object, Window> menuMap) {

            if (exportDialogClass == null || menuMap == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            this.exportDialogClass = exportDialogClass;
            this.menuMap = menuMap;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            Window target = menuMap.get(ev.getSource());
            if (target == null) {
                return;
            }
            try {
                // Avoid compile-time deps
                Object o = exportDialogClass.newInstance();
                Method m = exportDialogClass.getMethod(EXPORT_METHOD,
                        Component.class, String.class, Component.class,
                        String.class);
                m.invoke(o, Editor.getInstance(), DIALOG_TITLE,
                        target, FileFuncs.toFileName(
                                target instanceof Frame
                                    ? ((Frame) target).getTitle()
                                    : target.getName()));
            } catch (SecurityException e) {
                logger.error(null, e);
            } catch (NoSuchMethodException e) {
                logger.error(null, e);
            } catch (IllegalArgumentException e) {
                logger.error(null, e);
            } catch (IllegalAccessException e) {
                logger.error(null, e);
            } catch (InvocationTargetException e) {
                logger.error(null, e);
            } catch (InstantiationException e) {
                logger.error(null, e);
            } finally {
                menuMap.clear();
            }
        }
    }
}
