package ee.ioc.cs.vsle.editor;

import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * Scrollable toolbar component inspired by Volker Simonis'
 * "Scrolling on demand -- A scrollable toolbar component".
 */
public class ScrollableBar extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "ScrollableBarUI";
	private JComponent comp;

	static {
		UIManager.put(uiClassID, "ee.ioc.cs.vsle.editor.ScrollableBarUI");
	}

	public ScrollableBar(JComponent comp) {
		this.comp = comp;
		updateUI();
	}

	@Override
	public void updateUI() {
		setUI(UIManager.getUI(this));
		invalidate();
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public JComponent getComponent() {
		return comp;
	}
	
	void destroy() {
	    setUI( null );
	    comp = null;
	}
}
