package ee.ioc.cs.vsle.editor;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

class GraphicalResult extends JFrame {

	GraphicalResult() {
		JPanel drawingArea = new JPanel();
		drawingArea.setBackground(Color.white);

//		drawingArea.addMouseListener(mListener);

//		drawingArea.addMouseMotionListener(mListener);
//		drawingArea.setPreferredSize(drawAreaSize);
		JScrollPane areaScrollPane = new JScrollPane(drawingArea,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
