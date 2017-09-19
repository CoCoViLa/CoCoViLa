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

import java.awt.Window;

import javax.swing.text.JTextComponent;

/**
 * Interface for text editor windows.
 * Using this interface a client can query the current active text area
 * and other properties of the bound text editor window.
 */
public interface TextEditView {

    /**
     * Returns the current active text editor component.  This method may
     * be constant when there is only one text editor area in the window
     * and it is always active.  In case of tabbed windows the method can
     * return a different value on each call.
     * @return the active editor component or null
     */
    public JTextComponent getTextComponent();

    /**
     * Returns the editor window.  This method should be constant and not null.
     * @return the editor window
     */
    public Window getWindow();

}
