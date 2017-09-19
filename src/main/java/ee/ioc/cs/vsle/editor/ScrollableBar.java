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
