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
import java.awt.event.*;

import javax.swing.*;

/**
 * @author pavelg
 * 
 */
public class ComponentResizer extends ComponentAdapter {

	public static final int CARE_FOR_MINIMUM = 0;

	public static final int CARE_FOR_PREFERRED = 1;

	private int token = -1;

	public ComponentResizer(int token) {

		if (token > CARE_FOR_PREFERRED || token < CARE_FOR_MINIMUM) {
			throw new IllegalArgumentException();
		}

		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {

		Component comp = e.getComponent();

		Container cont = (comp instanceof Container) ? (Container) comp
				: SwingUtilities.getAncestorOfClass(Container.class, comp);

		LayoutManager lmngr = cont.getLayout();

		Dimension d;

		switch (token) {

		case CARE_FOR_MINIMUM:
			d = lmngr.minimumLayoutSize(cont);
			break;
		case CARE_FOR_PREFERRED:
			d = lmngr.preferredLayoutSize(cont);
			break;
		default:
			return;
		}

		int minWidth = d.width;
		int minHeight = d.height;
		int w = comp.getWidth();
		int h = comp.getHeight();

		if ((w < minWidth) && (h < minHeight)) {
			cont.setSize(minWidth, minHeight);
		} else if (w < minWidth) {
			cont.setSize(minWidth, h);
		} else if (h < minHeight) {
			cont.setSize(w, minHeight);
		}

		comp.repaint();
	}

}
