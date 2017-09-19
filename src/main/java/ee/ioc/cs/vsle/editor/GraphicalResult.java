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

import java.awt.Color;

import javax.swing.*;

class GraphicalResult extends JFrame {

	GraphicalResult() {
		JPanel drawingArea = new JPanel();
		drawingArea.setBackground(Color.white);

//		drawingArea.addMouseListener(mListener);

//		drawingArea.addMouseMotionListener(mListener);
//		drawingArea.setPreferredSize(drawAreaSize);
//		JScrollPane areaScrollPane = new JScrollPane(drawingArea,
//			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

/*		mainPanel.setLayout(new BorderLayout());
                mainPanel.add(areaScrollPane, BorderLayout.CENTER);

                infoPanel.add(posInfo);

                mainPanel.add(infoPanel, BorderLayout.SOUTH);
                posInfo.setText("-");
                makeMenu();
*/
		getContentPane().add(drawingArea);
		validate();
	}
}
