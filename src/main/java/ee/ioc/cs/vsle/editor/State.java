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

/**
 * Central repository of all states used by the Mouse Operators and listened by the Listeners.
 *
 * Module: ee.ioc.cs.editor.editor.State
 * User: AASMAAUL
 * Date: 15.01.2004
 * Time: 12:07:30
 */
public class State {
    public static final String addRelation = "?addRelation";
    public static final String chooseColor = "?chooseColor";
    public static final String dragBreakPoint = "?dragBreakPoint";
    public static final String drag = "?drag";
    public static final String dragBox = "?dragBox";
    public static final String drawArc2 = "?drawArc2";
    public static final String drawArc1 = "?drawArc1";
    public static final String drawArc = "?drawArc";
    public static final String drawFilledArc = "?drawFilledArc";
    public static final String drawLine = "?drawLine";
    public static final String drawRect = "?drawRect";
    public static final String drawFilledRect = "?drawFilledRect";
    public static final String drawOval = "?drawOval";
    public static final String drawFilledOval = "?drawFilledOval";
    public static final String drawText = "?text";
    public static final String eraser = "?eraser";
    public static final String freehand = "?freehand";
    public static final String magnifier = "?magnifier";
    public static final String resize = "?resize";
    public static final String selection = "?selection";
    public static final String boundingbox = "?boundingbox";
    public static final String addPort = "?addPort";
    public static final String insertImage = "?image";
    public static final String cloneDrawing = "?cloneDrawing";

    static final String addRelObjPrefix = "??";
    static final String statePrefix = "?";

    /**
     * Returns true if a relation class is being added in the specified state.
     * @param state the state
     * @return true if a relation class is being added, false otherwise
     */
    public static boolean isAddRelClass(String state) {
        return state.startsWith(addRelObjPrefix);
    }

    /**
     * Returns the class name encoded in the specified state.
     * @param state the state
     * @return class name
     */
    public static String getClassName(String state) {
        return isAddRelClass(state) ? state.substring(2) : state; 
    }

    /**
     * Returns true if an object is added in the specified state.
     * @param state the state
     * @return true if and object is added
     */
    public static boolean isAddObject(String state) {
        return !state.startsWith(statePrefix);
    }
}
