package ee.ioc.cs.vsle.iconeditor;

import javax.swing.JTextArea;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class CustomTextArea extends JTextArea {

	private boolean antialiasingOn;

	public CustomTextArea() {
	} // CustomTextArea

	public void setTextAntialiasing(boolean b) {
		this.antialiasingOn = b;
	} // setTextAntialiasing

	public boolean isTextAntialiased() {
		return this.antialiasingOn;
	} // isTextAntialiased

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// Switch on text antialiasing.
		if (isTextAntialiased()) {
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
				java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		}

		// Paint the default look.
		super.paintComponent(g2);

	} // paintComponent

} // end of class
