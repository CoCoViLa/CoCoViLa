package ee.ioc.cs.vsle.iconeditor;

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

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Class Editor toolbar.
 */
public class IconPalette extends PaletteBase {

    IconEditor editor;

    // Labels
    JLabel lblLineWidth = new JLabel(" Size: ");
    JLabel lblLineType;
    JLabel lblTransparency;
    JLabel lblZoom;

    // Spinners
    Spinner spinnerLineWidth = new Spinner(1, 10, 1,1);
    Spinner spinnerTransparency = new Spinner(1, 255, 1,255);
    Spinner spinnerLineType = new Spinner(0,100,1, 0);

    // Buttons
    JToggleButton selection;
    JToggleButton boundingbox;
    JToggleButton text;
    JToggleButton line;
    JToggleButton arc;
    JToggleButton filledarc;
    JToggleButton rectangle;
    JToggleButton filledrectangle;
    JToggleButton oval;
    JToggleButton filledoval;
    JToggleButton freehand;
    JToggleButton eraser;
    JToggleButton colors;
    JToggleButton addport;
    JToggleButton image;

    public IconPalette(IconMouseOps mListener, IconEditor ed) {
        super();
        toolBar = new JToolBar();

        this.editor = ed;

        spinnerLineWidth.setPreferredSize(new Dimension(40, 20));
        spinnerLineWidth.setMaximumSize(spinnerLineWidth.getPreferredSize());
        spinnerLineWidth.setBorder(BorderFactory.createLineBorder(java.awt.Color.white,0));

        spinnerTransparency.setPreferredSize(new Dimension(45, 20));
        spinnerTransparency.setMaximumSize(spinnerTransparency.getPreferredSize());
        spinnerTransparency.setBorder(BorderFactory.createLineBorder(java.awt.Color.white,0));

        spinnerLineType.setPreferredSize(new Dimension(40,20));
        spinnerLineType.setMaximumSize(spinnerLineType.getPreferredSize());
        spinnerLineType.setBorder(BorderFactory.createLineBorder(java.awt.Color.white,0));

        // Action listener added as anonymous class.
        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SpinnerModel source = (SpinnerModel) e.getSource();
                try {
                    editor.mListener.changeStrokeWidth(Float.parseFloat(String.valueOf(source.getValue())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Action listener added as anonymous class.
        ChangeListener transpListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SpinnerModel source = (SpinnerModel) e.getSource();
                try {
                    editor.mListener.changeTransparency(Integer.parseInt(String.valueOf(source.getValue())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        // Action listener added as anonymous class.
        ChangeListener lineTypeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SpinnerModel source = (SpinnerModel) e.getSource();
                try {
                    editor.mListener.changeLineType(Integer.parseInt(String.valueOf(source.getValue())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // add action listeners to spinners.
        spinnerLineWidth.getModel().addChangeListener(listener);
        spinnerTransparency.getModel().addChangeListener(transpListener);
        spinnerLineType.getModel().addChangeListener(lineTypeListener);

        selection = createButton("images/mouse.gif", "Select", State.selection);
        boundingbox = createButton("images/boundingbox.gif", "Bounding Box", State.boundingbox);
        addport = createButton("images/port.gif", "Add Port", State.addPort);
        image = createButton("images/image.png", "Insert Image", State.insertImage);
        text = createButton("images/text.gif", "Text", State.drawText);
        line = createButton("images/line.gif", "Line", State.drawLine);
        arc = createButton("images/arc.gif", "Arc", State.drawArc);
        filledarc = createButton("images/fillarc.gif", "Filled arc", State.drawFilledArc);
        rectangle = createButton("images/rect.gif", "Rectangle", State.drawRect);
        filledrectangle = createButton("images/fillrect.gif", "Filled rectangle", State.drawFilledRect);
        oval = createButton("images/oval.gif", "Oval", State.drawOval);
        filledoval = createButton("images/filloval.gif", "Filled oval", State.drawFilledOval);
        freehand = createButton("images/freehand.gif", "Freehand drawing", State.freehand);
        eraser = createButton("images/eraser.gif", "Eraser", State.eraser);
        colors = createButton("images/colorchooser.gif", "Color chooser", State.chooseColor);

        for (JToggleButton b : buttons) {
            toolBar.add(b);
        }

        lblLineWidth.setToolTipText("Line width or point size of a selected tool");
        toolBar.add(lblLineWidth);
        toolBar.add(spinnerLineWidth);

        Icon icon = FileFuncs.getImageIcon("images/transparency.gif", false);
        lblTransparency = new JLabel(icon);
        lblTransparency.setToolTipText("Object transparency percentage");

        icon = FileFuncs.getImageIcon("images/zoom.gif", false);
        lblZoom = new JLabel(icon);
        lblZoom.setToolTipText("Object zoom percentage");

        icon = FileFuncs.getImageIcon("images/linetype.gif", false);
        lblLineType = new JLabel(icon);
        lblLineType.setToolTipText("Dash style");

        toolBar.add(lblTransparency);
        toolBar.add(spinnerTransparency);

        toolBar.add(lblZoom);
        toolBar.add(createZoomPanel());

        toolBar.add(lblLineType);
        toolBar.add(spinnerLineType);

        editor.mainPanel.add(toolBar, BorderLayout.NORTH);
    }

    @Override
    protected ActionListener getZoomListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = ((JComboBox) e.getSource()).getSelectedIndex();
                editor.setScale(ZOOM_LEVELS[i]);
            }
        };
    }

    @Override
    protected void setState(String state) {
        editor.mListener.setState(state);
    }
} // end of class
